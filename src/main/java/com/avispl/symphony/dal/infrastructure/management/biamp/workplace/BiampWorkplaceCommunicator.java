/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.security.auth.login.FailedLoginException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
import com.avispl.symphony.dal.communicator.RestCommunicator;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.RequestStateHandler;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.ApiConstant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils.MonitoringUtil;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils.Util;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.Authentication;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.DeviceCommand;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Device;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Invitation;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Organization;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Profile;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests.AuthenticationReq;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests.GraphQLReq;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.InvitationStatus;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.ResponseType;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated.FirmwareProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated.OverviewProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated.StatusProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator.GeneralProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator.OrganizationProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator.UserProfileProperty;
import com.avispl.symphony.dal.util.ControllablePropertyFactory;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Main adapter class for Biamp Workplace Aggregator.
 * Responsible for generating monitoring, controllable, and aggregated devices.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class BiampWorkplaceCommunicator extends RestCommunicator implements Monitorable, Controller, Aggregator {
	/** Default graph properties used for the aggregator. */
	private static final Set<String> DEFAULT_GRAPH_PROPERTIES = new HashSet<>(Arrays.asList(
			GeneralProperty.LAST_MONITORING_CYCLE_DURATION.getName(),
			GeneralProperty.MONITORED_DEVICES_TOTAL.getName()
	));
	/** Historical properties of aggregated devices for graphing. */
	private static final Set<String> AGGREGATED_HISTORICAL_PROPERTIES = new HashSet<>(Arrays.asList(
			String.format(Constant.PROPERTY_FORMAT, Constant.STATUS_GROUP, StatusProperty.TEMPERATURE.getName()),
			String.format(Constant.PROPERTY_FORMAT, Constant.STATUS_GROUP, StatusProperty.CPU_UTILIZATION.getName())
	));
	/** Reboot duration (ms) of an aggregated device. */
	private static final long REBOOT_AGGREGATED_TIME = Duration.ofMinutes(4).toMillis();

	/** Lock for thread-safe operations. */
	private final ReentrantLock reentrantLock;
	/** Application configuration loaded from {@code version.properties}. */
	private final Properties versionProperties;
	/** Jackson object mapper for JSON serialization and deserialization. */
	private final ObjectMapper objectMapper;
	/** Handles request status tracking and error detection. */
	private final RequestStateHandler requestStateHandler;

	/** Device adapter instantiation timestamp. */
	private Long adapterInitializationTimestamp;
	/** Duration (in milliseconds) of the last monitoring cycle. */
	private Long lastMonitoringCycleDuration;
	/** Stores extended statistics to be sent to the aggregator. */
	private ExtendedStatistics localExtendedStatistics;
	/** Executes asynchronous tasks for data loader. */
	private ExecutorService executorService;
	/** Loads data from APIs for aggregated devices. */
	private BiampWorkplaceDataLoader dataLoader;
	/** Stores local representations of aggregated devices. */
	private List<AggregatedDevice> localAggregatedDevices;
	/** The current authentication, including tokens and expiry information. */
	private Authentication authentication;
	/** The profile information of the currently {@link #authentication} user. */
	private Profile profile;
	/** The list of organizations associated with the {@link #profile} user. */
	private List<Organization> organizations;
	/** The list of devices from all associated {@link #organizations}. */
	private final List<Device> devices;

	/** The property used to filter the aggregated devices by organizationId(s) */
	private List<String> organizationIds;
	/** The properties used to display historical graphs for an aggregated device */
	private List<String> historicalProperties;
	/** Indicates whether control properties are visible; defaults to false. */
	private boolean configManagement;

	public BiampWorkplaceCommunicator() {
		this.reentrantLock = new ReentrantLock();
		this.versionProperties = new Properties();
		this.objectMapper = new ObjectMapper();
		this.requestStateHandler = new RequestStateHandler();

		this.adapterInitializationTimestamp = System.currentTimeMillis();
		this.lastMonitoringCycleDuration = 0L;
		this.localExtendedStatistics = new ExtendedStatistics();
		this.localAggregatedDevices = new ArrayList<>();
		this.authentication = new Authentication();
		this.profile = new Profile();
		this.organizations = new ArrayList<>();
		this.devices = Collections.synchronizedList(new ArrayList<>());

		this.organizationIds = new ArrayList<>();
		this.historicalProperties = new ArrayList<>();
		this.configManagement = false;
	}

	/**
	 * Retrieves {@link #organizationIds}
	 *
	 * @return value of {@link #organizationIds}
	 */
	public String getOrganizationIds() {
		return String.join(Constant.COMMA, this.organizationIds);
	}

	/**
	 * Sets {@link #organizationIds} value
	 *
	 * @param organizationIds new value of {@link #organizationIds}
	 */
	public void setOrganizationIds(String organizationIds) {
		this.organizationIds.clear();
		if (StringUtils.isNullOrEmpty(organizationIds)) {
			return;
		}
		Arrays.stream(organizationIds.split(Constant.COMMA)).map(String::trim)
				.filter(organizationId -> !organizationId.isEmpty())
				.forEach(organizationId -> this.organizationIds.add(organizationId));
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
		if (StringUtils.isNullOrEmpty(historicalProperties)) {
			return;
		}
		Arrays.stream(historicalProperties.split(Constant.COMMA)).map(String::trim)
				.filter(historicalProperty -> !historicalProperty.isEmpty())
				.forEach(historicalProperty -> this.historicalProperties.add(historicalProperty));
	}

	/**
	 * Retrieves {@link #configManagement}
	 *
	 * @return value of {@link #configManagement}
	 */
	public boolean isConfigManagement() {
		return configManagement;
	}

	/**
	 * Sets {@link #configManagement} value
	 *
	 * @param configManagement new value of {@link #configManagement}
	 */
	public void setConfigManagement(boolean configManagement) {
		this.configManagement = configManagement;
	}

	/**
	 * Sets {@link #lastMonitoringCycleDuration} value
	 *
	 * @param lastMonitoringCycleDuration new value of {@link #lastMonitoringCycleDuration}
	 */
	public void setLastMonitoringCycleDuration(Long lastMonitoringCycleDuration) {
		this.lastMonitoringCycleDuration = lastMonitoringCycleDuration;
	}

	@Override
	protected void internalInit() throws Exception {
		this.logger.info(Constant.INITIAL_INTERNAL_INFO + this.getClass().getSimpleName());
		this.setTrustAllCertificates(true);
		this.setAuthenticationScheme(AuthenticationScheme.None);
		this.loadProperties(this.versionProperties);
		this.authenticate();
		super.internalInit();
	}

	@Override
	protected void authenticate() throws Exception {
		if (StringUtils.isNullOrEmpty(this.getLogin()) || StringUtils.isNullOrEmpty(this.getPassword())) {
			throw new FailedLoginException(Constant.LOGIN_FAILED);
		}
		if (this.authentication.isInvalid()) {
			this.authentication.setRefreshToken(this.getPassword());
		}
	}

	@Override
	protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
		if (uri.equals(ApiConstant.GET_TOKEN_ENDPOINT)) {
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		} else {
			headers.setContentType(MediaType.APPLICATION_JSON);
			headers.setBearerAuth(this.authentication.getAccessToken());
		}

		return super.putExtraRequestHeaders(httpMethod, uri, headers);
	}

	@Override
	public List<Statistics> getMultipleStatistics() throws Exception {
		this.reentrantLock.lock();
		try {
			this.setupData();
			ExtendedStatistics extendedStatistics = new ExtendedStatistics();
			Map<String, String> statistics = new HashMap<>();
			statistics.putAll(this.getGeneralProperties());
			statistics.putAll(this.getOrganizationProperties());
			statistics.putAll(this.getProfileProperties());

			extendedStatistics.setStatistics(statistics);
			extendedStatistics.setDynamicStatistics(this.getDynamicStatistics(statistics));
			this.localExtendedStatistics = extendedStatistics;
		} finally {
			this.reentrantLock.unlock();
		}
		return Collections.singletonList(localExtendedStatistics);
	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
		this.setupDataLoader();
		List<AggregatedDevice> aggregatedDevices = new ArrayList<>();
		synchronized (this.devices) {
			this.devices.forEach(device -> {
				AggregatedDevice aggregatedDevice = new AggregatedDevice();
				aggregatedDevice.setDeviceId(device.getId());
				aggregatedDevice.setDeviceName(MonitoringUtil.mapToDeviceName(device));
				aggregatedDevice.setDeviceOnline(Util.isDeviceOnline(device.getState()));
				aggregatedDevice.setSerialNumber(device.getSerial());
				aggregatedDevice.setTimestamp(System.currentTimeMillis());

				Map<String, String> statistics = new HashMap<>();
				statistics.putAll(this.getOverviewProperties(device));
				statistics.putAll(this.getFirmwareProperties(device));
				statistics.putAll(this.getStatusProperties(device));

				List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();
				if (this.configManagement) {
					controllableProperties.addAll(this.getOverviewControllers());
				}
				Optional.of(controllableProperties).filter(List::isEmpty).ifPresent(l -> l.add(Constant.DUMMY_CONTROLLER));

				aggregatedDevice.setProperties(statistics);
				aggregatedDevice.setControllableProperties(controllableProperties);
				aggregatedDevice.setDynamicStatistics(this.getRealtimeStatistics(statistics));
				aggregatedDevices.add(aggregatedDevice);
			});
		}
		//	Filter by organization id(s)
		if (CollectionUtils.isNotEmpty(this.organizationIds)) {
			String organizationName = OverviewProperty.ORGANIZATION_ID.getName();
			aggregatedDevices.removeIf(aggregatedDevice -> !this.organizationIds.contains(aggregatedDevice.getProperties().get(organizationName)));
		}
		this.localAggregatedDevices = aggregatedDevices;
		this.versionProperties.setProperty(GeneralProperty.LAST_MONITORING_CYCLE_DURATION.getProperty(), String.valueOf(this.lastMonitoringCycleDuration));
		this.versionProperties.setProperty(GeneralProperty.MONITORED_DEVICES_TOTAL.getProperty(), String.valueOf(this.localAggregatedDevices.size()));
		return this.localAggregatedDevices;
	}

	@Override
	public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
		return this.retrieveMultipleStatistics().stream()
				.filter(aggregatedDevice -> list.contains(aggregatedDevice.getDeviceId()))
				.collect(Collectors.toList());
	}

	@Override
	public void controlProperty(ControllableProperty controllableProperty) throws Exception {
		this.reentrantLock.lock();
		try {
			if (OverviewProperty.REBOOT.getName().equals(controllableProperty.getProperty())) {
				Device device = this.devices.stream()
						.filter(d -> d.getId().equals(controllableProperty.getDeviceId())).findFirst()
						.orElseThrow(() -> new IllegalStateException(Constant.DETERMINE_DEVICE_FAILED + controllableProperty.getDeviceId()));
				GraphQLReq query = GraphQLReq.rebootDevice(device.getOrgId(), device.getId());
				DeviceCommand response = this.sendRequest(ApiConstant.GRAPHQL_ENDPOINT, query, ResponseType.REBOOT_DEVICE);

				if (response == null || !response.isSuccess()) {
					throw new IllegalStateException(Constant.REBOOT_DEVICE_FAILED);
				}
				this.disconnect();
			}
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
	protected void internalDestroy() {
		this.logger.info(Constant.DESTROY_INTERNAL_INFO + this.getClass().getSimpleName());

		this.historicalProperties = null;
		this.organizationIds = null;

		this.devices.clear();
		this.organizations = null;
		this.profile = null;
		this.authentication = null;
		this.localAggregatedDevices = null;
		this.localExtendedStatistics = null;
		this.lastMonitoringCycleDuration = null;
		this.adapterInitializationTimestamp = null;

		if (this.dataLoader != null) {
			this.dataLoader.stop();
			this.dataLoader = null;
		}
		if (this.executorService != null) {
			this.executorService.shutdownNow();
			this.executorService = null;
		}
		this.requestStateHandler.clearRequests();

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
			this.logger.error(Constant.READ_PROPERTIES_FILE_FAILED, e);
		}
	}

	private void setupData() throws Exception {
		this.requestStateHandler.clearRequests();
		this.organizations.clear();
		this.devices.clear();

		if (this.authentication.isInvalid()) {
			this.logger.info(Constant.REFRESHING_TOKENS_INFO);
			AuthenticationReq authRequest = new AuthenticationReq(this.getLogin(), this.authentication.getRefreshToken());
			Authentication authResponse = this.sendRequest(ApiConstant.GET_TOKEN_ENDPOINT, authRequest.toFormData(), ResponseType.AUTHENTICATION);
			this.authentication = Optional.ofNullable(authResponse).orElse(new Authentication());
			this.authentication.setIssuedAt(System.currentTimeMillis());
		}
		this.profile = this.sendRequest(ApiConstant.GRAPHQL_ENDPOINT, GraphQLReq.getProfile(), ResponseType.PROFILE);
		if (this.profile != null && CollectionUtils.isNotEmpty(this.profile.getMemberships())) {
			//	Collect data for this.organizations
			this.profile.getMemberships().forEach(membership -> {
				Organization organization = membership.getOrganization();
				InvitationStatus invitationStatus = this.profile.getInvitations().stream()
						.filter(invitation -> invitation.getOrgId().equals(organization.getId()))
						.map(Invitation::getStatus).findFirst().orElse(InvitationStatus.NOT_AVAILABLE);
				organization.setMembershipRole(membership.getRole());
				organization.setMembershipStatus(membership.getStatus());
				organization.setInvitationStatus(invitationStatus);

				this.organizations.add(organization);
			});
			//	Collect data for this.devices
			for (Organization organization : this.organizations) {
				List<Device> fetchedDevices = this.sendRequest(ApiConstant.GRAPHQL_ENDPOINT, GraphQLReq.getDevices(organization.getId()), ResponseType.DEVICES);
				if (CollectionUtils.isNotEmpty(fetchedDevices)) {
					this.devices.addAll(fetchedDevices);
				}
			}
			//	Update the this.organizationIds
			if (Boolean.FALSE.equals(this.profile.getSuperAdmin())
					&& CollectionUtils.isNotEmpty(this.organizationIds) && this.organizationIds.size() > 1) {
				this.organizationIds.subList(1, this.organizationIds.size()).clear();
			}
		}

		this.requestStateHandler.verifyRequestState();
	}

	/**
	 * Sets up the data loader to collect and update data for aggregated devices.
	 * <p>
	 * This method initializes a single-thread executor and submits a {@link BiampWorkplaceDataLoader}
	 * task if not already initialized. It also updates the next collection time for data retrieval
	 * and refreshes the timestamp used to validate collected statistics.
	 * </p>
	 */
	private void setupDataLoader() {
		if (this.executorService == null) {
			this.executorService = Executors.newFixedThreadPool(1);
			this.dataLoader = new BiampWorkplaceDataLoader(this, this.devices);
			this.executorService.submit(this.dataLoader);
		}
		this.dataLoader.setNextCollectionTime(System.currentTimeMillis());
		this.dataLoader.updateValidRetrieveStatisticsTimestamp();
	}

	/**
	 * Retrieves general properties related to the adapter's version and status.
	 * <p>Uses {@link MonitoringUtil#mapToGeneralProperty(Properties, GeneralProperty)} to map each property.</p>
	 *
	 * @return a map of general property names and their corresponding values
	 */
	private Map<String, String> getGeneralProperties() {
		return MonitoringUtil.generateProperties(
				GeneralProperty.values(),
				null,
				property -> MonitoringUtil.mapToGeneralProperty(this.versionProperties, property)
		);
	}

	/**
	 * Retrieves profile properties for the aggregator.
	 * <p>Uses {@link MonitoringUtil#mapToProfileProperty(Profile, UserProfileProperty)} to map each property.</p>
	 *
	 * @return a map of profile property names and their corresponding values
	 */
	private Map<String, String> getProfileProperties() {
		return MonitoringUtil.generateProperties(
				UserProfileProperty.values(),
				Constant.USER_PROFILE_GROUP,
				property -> MonitoringUtil.mapToProfileProperty(this.profile, property)
		);
	}

	/**
	 * Generates monitoring properties for all available organizations.
	 * <p>
	 * Each organization is assigned a group name based on its index and
	 * expanded into key-value property entries using {@link MonitoringUtil}.
	 * </p>
	 * Returns an empty map (with a warning) if no organizations exist.
	 *
	 * @return map of organization property keys and values
	 */
	private Map<String, String> getOrganizationProperties() {
		if (CollectionUtils.isEmpty(this.organizations)) {
			this.logger.warn(Constant.ORGANIZATIONS_EMPTY_WARNING);
			return Collections.emptyMap();
		}
		Map<String, String> properties = new HashMap<>();
		for (int i = 0; i < this.organizations.size(); i++) {
			Organization organization = this.organizations.get(i);
			String groupName = String.format(Constant.GROUP_FORMAT, Constant.ORGANIZATION_GROUPS, i + 1);
			properties.putAll(MonitoringUtil.generateProperties(
					OrganizationProperty.values(), groupName, property -> MonitoringUtil.mapToOrganizationProperty(organization, property)
			));
		}

		return properties;
	}

	/**
	 * Returns dynamic statistics for the aggregator.
	 * <p>
	 * If the input map is empty, logs a warning and returns an empty map.
	 * Otherwise, builds a map of {@code DEFAULT_GRAPHS} with values from
	 * {@code statistics}, or {@link Constant#NOT_AVAILABLE} if missing.
	 * </p>
	 *
	 * @param statistics the input statistics
	 * @return a map of default graphs and their values, or an empty map if none
	 */
	private Map<String, String> getDynamicStatistics(Map<String, String> statistics) {
		if (MapUtils.isEmpty(statistics)) {
			this.logger.warn(Constant.STATISTICS_EMPTY_WARNING);
			return Collections.emptyMap();
		}

		Map<String, String> dynamicStatistic = new HashMap<>();
		DEFAULT_GRAPH_PROPERTIES.forEach(defaultGraph -> {
			String statisticValue = Optional.ofNullable(statistics.get(defaultGraph)).orElse(Constant.NOT_AVAILABLE);

			dynamicStatistic.put(defaultGraph, statisticValue);
		});

		return dynamicStatistic;
	}

	/**
	 * Generates general properties for an aggregated device.
	 * <p>
	 * This method uses {@link MonitoringUtil} to map each {@link OverviewProperty}
	 * to its corresponding value from the provided {@link Device}.
	 * </p>
	 *
	 * @param device the device for which overview properties are generated; must not be {@code null}
	 * @return a map of overview property keys and values for the specified device
	 */
	private Map<String, String> getOverviewProperties(Device device) {
		return MonitoringUtil.generateProperties(
				OverviewProperty.values(),
				null,
				property -> MonitoringUtil.mapToOverviewProperty(device, property)
		);
	}

	/**
	 * Generates firmware properties for an aggregated device.
	 * <p>
	 * This method uses {@link MonitoringUtil} to map each {@link FirmwareProperty}
	 * to its corresponding value from the provided {@link Device}.
	 * </p>
	 *
	 * @param device the device for which firmware properties are generated; must not be {@code null}
	 * @return a map of firmware property keys and values for the specified device
	 */
	private Map<String, String> getFirmwareProperties(Device device) {
		return MonitoringUtil.generateProperties(
				FirmwareProperty.values(),
				Constant.FIRMWARE_GROUP,
				property -> MonitoringUtil.mapToFirmwareProperty(device, property)
		);
	}

	/**
	 * Generates status properties for an aggregated device.
	 * <p>
	 * This method uses {@link MonitoringUtil} to map each {@link StatusProperty}
	 * to its corresponding value from the provided {@link Device}.
	 * </p>
	 *
	 * @param device the device for which status properties are generated; must not be {@code null}
	 * @return a map of status property keys and values for the specified device
	 */
	private Map<String, String> getStatusProperties(Device device) {
		return MonitoringUtil.generateProperties(
				StatusProperty.values(),
				Constant.STATUS_GROUP,
				property -> MonitoringUtil.mapToStatusProperty(device, property)
		);
	}

	/**
	 * Returns dynamic statistics for the aggregated device.
	 * <p>
	 * If the input map is empty, logs a warning and returns an empty map.
	 * Otherwise, builds a map of {@link #AGGREGATED_HISTORICAL_PROPERTIES} with values from
	 * {@code statistics}, or {@link Constant#NOT_AVAILABLE} if missing.
	 * </p>
	 *
	 * @param aggregatedStatistics the input statistics of aggregated device
	 * @return a map of historical graphs and their values, or an empty map if none
	 */
	private Map<String, String> getRealtimeStatistics(Map<String, String> aggregatedStatistics) {
		if (MapUtils.isEmpty(aggregatedStatistics)) {
			this.logger.warn(Constant.AGGREGATED_STATISTICS_EMPTY_WARNING);
			return Collections.emptyMap();
		}
		if (CollectionUtils.isEmpty(this.historicalProperties)) {
			this.logger.warn(Constant.HISTORICAL_PROPERTIES_EMPTY_WARNING);
			return Collections.emptyMap();
		}

		Map<String, String> dynamicStatistic = new HashMap<>();
		this.historicalProperties.forEach(historicalProperty -> {
			if (AGGREGATED_HISTORICAL_PROPERTIES.contains(historicalProperty)) {
				String statisticValue = Optional.ofNullable(aggregatedStatistics.get(historicalProperty)).orElse(Constant.NOT_AVAILABLE);
				dynamicStatistic.put(historicalProperty, statisticValue);
			}
		});

		return dynamicStatistic;
	}

	/**
	 * Generates overview control properties for an aggregated device.
	 * <p>
	 * This method creates a list of {@link AdvancedControllableProperty} objects
	 * that allow interaction with the device's overview-level controls, such as reboot.
	 * </p>
	 *
	 * @return a list of controllable properties for the device overview
	 */
	private List<AdvancedControllableProperty> getOverviewControllers() {
		List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();
		controllableProperties.add(ControllablePropertyFactory.createButton(
				OverviewProperty.REBOOT.getName(), OverviewProperty.REBOOT.getName(), "Rebooting", REBOOT_AGGREGATED_TIME
		));

		return controllableProperties;
	}

	/**
	 * Sends a POST request to the given endpoint and maps the JSON response into the specified type.
	 * <p>
	 * The request is tracked by {@link RequestStateHandler}, and errors are logged
	 * or rethrown depending on their type. If the response is {@code null}, a warning is logged.
	 *
	 * @param <T> the expected response type
	 * @param endpoint the target endpoint URL
	 * @param request the request body to send
	 * @param responseType the type of response to deserialize into
	 * @return the mapped response object, or {@code null} if deserialization failed
	 * @throws Exception if an unrecoverable error occurs while sending the request
	 */
	public <T> T sendRequest(String endpoint, Object request, ResponseType responseType) throws Exception {
		String responseClassName = responseType.getClazz().getSimpleName();
		try {
			this.requestStateHandler.pushRequest(endpoint);
			String jsonResponse = super.doPost(endpoint, request, String.class);
			JsonNode responseNode = responseType.getPaths(this.objectMapper.readTree(jsonResponse));
			@SuppressWarnings("unchecked")
			T response = responseType.isCollection()
					? (T) this.objectMapper.convertValue(responseNode, responseType.getTypeRef(this.objectMapper))
					: (T) this.objectMapper.treeToValue(responseNode, responseType.getClazz());

			if (response == null) {
				this.logger.warn(String.format(Constant.SENT_REQUEST_NULL_WARNING, endpoint, responseClassName));
			}
			this.requestStateHandler.resolveError(endpoint);

			return response;
		} catch (Exception e) {
			this.requestStateHandler.pushError(endpoint, e);
			this.logger.error(String.format(Constant.FETCH_DATA_FAILED, endpoint, responseClassName), e);
			return null;
		}
	}
}
