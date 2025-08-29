/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.security.auth.login.FailedLoginException;
import org.apache.commons.collections.CollectionUtils;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.RequestStateHandler;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.ApiConstant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.Authentication;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Device;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Membership;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Organization;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Profile;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests.AuthenticationReq;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests.GraphQLReq;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.ResponseType;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator.GeneralProperty;
import com.avispl.symphony.dal.util.StringUtils;


/**
 * BiampWorkplaceCommunicator
 *
 * @author Harry / Symphony Dev Team
 * @since 1.0.0
 */
public class BiampWorkplaceCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {
	private final ReentrantLock reentrantLock;
	private final ObjectMapper objectMapper;

	/**
	 * Device adapter instantiation timestamp.
	 */
	private Long adapterInitializationTimestamp;
	/**
	 * Duration (in milliseconds) of the last monitoring cycle.
	 */
	private Long lastMonitoringCycleDuration;
	private RequestStateHandler requestStateHandler;
	private ExtendedStatistics localExtendedStatistics;
	private Properties versionProperties;
	private Authentication authentication;
	private Profile profile;
	private List<Device> devices;

	private Set<String> historicalProperties;

	public BiampWorkplaceCommunicator() {
		this.reentrantLock = new ReentrantLock();
		this.objectMapper = new ObjectMapper();

		this.adapterInitializationTimestamp = System.currentTimeMillis();
		this.lastMonitoringCycleDuration = 0L;
		this.localExtendedStatistics = new ExtendedStatistics();
		this.requestStateHandler = new RequestStateHandler();
		this.versionProperties = new Properties();
		this.authentication = new Authentication();
		this.profile = new Profile();
		this.devices = new ArrayList<>();

		this.historicalProperties = new HashSet<>();
	}

	/**
	 * Retrieves {@link #historicalProperties}
	 *
	 * @return value of {@link #historicalProperties}
	 */
	public String getHistoricalProperties() {
		return String.join(Constant.COMMA, this.historicalProperties);
	}

	/**
	 * Sets {@link #historicalProperties} value
	 *
	 * @param historicalProperties new value of {@link #historicalProperties}
	 */
	public void setHistoricalProperties(String historicalProperties) {
		this.historicalProperties.clear();
		Arrays.asList(historicalProperties.split(Constant.COMMA))
				.forEach(propertyName -> this.historicalProperties.add(propertyName.trim()));
	}

	@Override
	protected void internalInit() throws Exception {
		this.logger.info(Constant.INITIAL_INTERNAL_INFO + this);
		this.setTrustAllCertificates(true);
		this.setAuthenticationScheme(AuthenticationScheme.None);
		this.loadProperties(this.versionProperties);
		super.internalInit();
	}

	@Override
	protected void authenticate() throws Exception {
		if (StringUtils.isNullOrEmpty(this.getLogin()) || StringUtils.isNullOrEmpty(this.getPassword())) {
			throw new FailedLoginException(Constant.LOGIN_FAILED);
		}
		if (this.authentication.isInvalid()) {
			this.authentication.setClientId(this.getLogin());
			this.authentication.setRefreshToken(this.getPassword());
		}
	}

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		this.reentrantLock.lock();
		try {
			this.setupData();
		} finally {
			this.reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		return new ArrayList<>();
	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
		return retrieveMultipleStatistics()
				.stream()
				.filter(aggregatedDevice -> list.contains(aggregatedDevice.getDeviceId()))
				.collect(Collectors.toList());
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		this.reentrantLock.lock();
		try {

		} finally {
			this.reentrantLock.unlock();
		}
	}

	@Override
	public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
		if (CollectionUtils.isEmpty(controllableProperties)) {
			if (this.logger.isWarnEnabled()) {
				this.logger.warn(Constant.CONTROLLABLE_PROPS_EMPTY_WARNING);
			}
			return;
		}
		for (ControllableProperty controllableProperty : controllableProperties) {
			this.controlProperty(controllableProperty);
		}
	}

	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		if (uri.equals(ApiConstant.GET_TOKEN_ENDPOINT)) {
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		} else {
			headers.setContentType(MediaType.APPLICATION_JSON);
//			headers.setBearerAuth(this.authentication.getAccessToken());
			headers.setBearerAuth(
					"eyJhbGciOiJSUzI1NiIsImtpZCI6IjMzNTQwODY3MTQ2MDY4Nzg4NSIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsiMjk0NDA3NjI5NzQzMTYxMzk5IiwiMjY5Mzk5NTA1Njg0NzU4NTQyQHBsYXRmb3JtIiwiMjY5Mzk5NzY2MTUxMDM2OTQyQHBsYXRmb3JtIiwiMjg2MzY0NTk3Mzk1NDg4ODIxQHBsYXRmb3JtIiwiMjk0MzEwNzAyMDc4ODUzMTc1IiwiMjgzNTkwNTkxMDEzNTUyMTg1QHBsYXRmb3JtIiwiMjY5Mzk5NzY2NDY5ODA0MDQ2QHBsYXRmb3JtIiwiMjY5Mzk5NzY1OTQ5NzEwMzUwQHBsYXRmb3JtIiwiMjY5Mzk5MTYxODE4ODczODcwIl0sImNsaWVudF9pZCI6IjI4MzU5MDU5MTAxMzU1MjE4NUBwbGF0Zm9ybSIsImV4cCI6MTc1NjQ3OTAyOSwiaWF0IjoxNzU2NDM1ODI5LCJpc3MiOiJodHRwczovL2lhbS53b3JrcGxhY2UuYmlhbXAuYXBwIiwianRpIjoiVjJfMzM1MzA0NzA0Mjc3ODcyNjUzLWF0XzMzNTQ0MzM3MDAxNDk5ODU0MSIsIm5iZiI6MTc1NjQzNTgyOSwic3ViIjoiMjk4NTEwNzc5ODU3MDEwNzgxIiwidXJuOnppdGFkZWw6aWFtOnVzZXI6bWV0YWRhdGEiOnsiZXdwX3VzZXJfaWQiOiJZbUV3TVdaak5qa3RZak0yTlMwME9XWmhMVGsyTmpNdFlXSTBZMlJsWXpBMVltUTUiLCJld3BfdXNlcl90eXBlIjoiZFhObGNnIn19.ZC2sFxksUcmKzva7-ITUaRkPjaT1gxuBaIPFasYiQWuOi7AopKvMpfGtxvzBlb1O1hwpKoxakxKAWCnX5--W79ykzmWCi1VtGPh16uSQ2MJ48UBOrrdFAkmeOdmGiIRR2F3rigX6sKVAaAXIilBczKYHUfDOBqimZapdqLz3ZJq5DxArsna-VqRQ9zjjZ3P-1PbHeMUXiFVqeYtqRiuaw7B17kxsGhNwq15WZ3x8lNv6jNpkRvH_jiIbzTt0S7xK6C6YvCLhUdOlNaWpR2WAp-wwbSO06a885q2hWoIFJv6YhjxTHJespxanGOA13ocTyOrvEm4AFRQHi-g4D-wbHg"
			);
		}
		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	@Override
	protected void internalDestroy() {
		this.logger.info(Constant.DESTROY_INTERNAL_INFO + this);
		this.historicalProperties = null;
		this.devices = null;
		this.profile = null;
		this.authentication = null;
		this.versionProperties = null;
		this.requestStateHandler = null;
		this.localExtendedStatistics = null;
		this.adapterInitializationTimestamp = 0L;
		this.lastMonitoringCycleDuration = 0L;
		super.internalDestroy();
	}

	/**
	 * Loads version properties and sets initial values used to create general properties
	 * for the aggregator device.
	 *
	 * @param properties the properties to load and update
	 */
	private void loadProperties(Properties properties) {
		try {
			properties.load(this.getClass().getResourceAsStream("/version.properties"));
			properties.setProperty(GeneralProperty.ADAPTER_UPTIME.getProperty(), String.valueOf(this.adapterInitializationTimestamp));
			properties.setProperty(GeneralProperty.LAST_MONITORING_CYCLE_DURATION.getProperty(), "0");
			properties.setProperty(GeneralProperty.MONITORED_DEVICES_TOTAL.getProperty(), "0");
		} catch (IOException e) {
			this.logger.error(Constant.READ_PROPERTIES_FILE_FAILED + e.getMessage());
		}
	}

	private void setupData() throws Exception {
		this.requestStateHandler.clearRequests();
		this.devices.clear();
		if (this.authentication.isInvalid()) {
			AuthenticationReq requestBody = new AuthenticationReq(this.getLogin(), this.getPassword());
//			this.authentication = this.sendRequest(ApiConstant.GET_TOKEN_ENDPOINT, requestBody.toFormData(), ResponseType.AUTHENTICATION);
		}
		this.profile = this.sendRequest(ApiConstant.GRAPHQL_ENDPOINT, GraphQLReq.getProfile(), ResponseType.PROFILE);
		if (this.profile != null && CollectionUtils.isNotEmpty(this.profile.getMemberships())) {
			List<String> orgIds = this.profile.getMemberships().stream()
					.map(Membership::getOrganization).filter(Objects::nonNull)
					.map(Organization::getId).filter(Objects::nonNull)
					.collect(Collectors.toList());
			for (String orgId : orgIds) {
				List<String> mappedId = Collections.singletonList(orgId);
				List<Device> fetchedDevices = this.sendRequest(ApiConstant.GRAPHQL_ENDPOINT, GraphQLReq.getDevices(mappedId), ResponseType.DEVICES);
				if (CollectionUtils.isNotEmpty(fetchedDevices)) {
					this.devices.addAll(fetchedDevices);
				}
			}
		}

		this.requestStateHandler.verifyRequestState();
	}

	private <T> T sendRequest(String endpoint, Object request, ResponseType responseType) throws Exception {
		String responseClassName = responseType.getClazz().getSimpleName();
		try {
			this.requestStateHandler.pushRequest(endpoint);
			String jsonResponse = super.doPost(endpoint, request, String.class);
			JsonNode responseNode = responseType.getPaths(this.objectMapper.readTree(jsonResponse));
			@SuppressWarnings("unchecked")
			T response = responseType.isList()
					? (T) this.objectMapper.convertValue(responseNode, responseType.getTypeRef(this.objectMapper))
					: (T) this.objectMapper.treeToValue(responseNode, responseType.getClazz());

			if (response == null && this.logger.isWarnEnabled()) {
				this.logger.warn(String.format(Constant.SENT_REQUEST_NULL_WARNING, endpoint, responseClassName));
			}
			this.requestStateHandler.resolveError(endpoint);

			return response;
		} catch (FailedLoginException e) {
			throw e;
		} catch (ResourceNotReachableException e) {
			throw new ResourceNotReachableException(e.getMessage(), e);
		} catch (Exception e) {
			this.requestStateHandler.pushError(endpoint, e);
			this.logger.error(String.format(Constant.FETCH_DATA_FAILED, endpoint, responseClassName), e);
			return null;
		}
	}
}
