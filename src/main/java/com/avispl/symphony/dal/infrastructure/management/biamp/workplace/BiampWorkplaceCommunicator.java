/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

import java.io.IOException;
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
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils.ControllerUtil;
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
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Main adapter class for Biamp Workplace Aggregator.
 * Responsible for generating monitoring, controllable, and aggregated devices.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class BiampWorkplaceCommunicator extends RestCommunicator implements Monitorable, Controller, Aggregator {
	private static final Set<String> DEFAULT_GRAPH_PROPERTIES = new HashSet<>(Arrays.asList(
			GeneralProperty.LAST_MONITORING_CYCLE_DURATION.getName(),
			GeneralProperty.MONITORED_DEVICES_TOTAL.getName()
	));

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

			extendedStatistics.setStatistics(statistics);
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

				Map<String, String> statistics = new HashMap<>();
				statistics.putAll(this.getOverviewProperties(device));
				statistics.putAll(this.getFirmwareProperties(device));
				statistics.putAll(this.getStatusProperties(device));

				List<AdvancedControllableProperty> controllableProperties = this.getOverviewControllers(device);
				Optional.of(controllableProperties).filter(List::isEmpty).ifPresent(l -> l.add(Constant.DUMMY_CONTROLLER));

				aggregatedDevice.setProperties(statistics);
				aggregatedDevice.setControllableProperties(controllableProperties);
				aggregatedDevices.add(aggregatedDevice);
			});
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

				if (response == null) {
					throw new IllegalStateException(Constant.REBOOT_DEVICE_FAILED);
				}
				if (!response.isSuccess()) {
					throw new IllegalStateException(Optional.ofNullable(response.getErrorMessage()).orElse(Constant.REBOOT_DEVICE_FAILED));
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
			AuthenticationReq requestBody = new AuthenticationReq(this.getLogin(), this.authentication.getRefreshToken());
			this.authentication = this.sendRequest(ApiConstant.GET_TOKEN_ENDPOINT, requestBody.toFormData(), ResponseType.AUTHENTICATION);
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
	 * Generates overview control properties for an aggregated device.
	 * <p>
	 * This method creates a list of {@link AdvancedControllableProperty} objects
	 * that allow interaction with the device's overview-level controls, such as reboot.
	 * </p>
	 *
	 * @return a list of controllable properties for the device overview
	 */
	private List<AdvancedControllableProperty> getOverviewControllers(Device device) {
		List<AdvancedControllableProperty> controllableProperties = new ArrayList<>();
		if (Util.isDeviceOnline(device.getState())) {
			controllableProperties.add(ControllerUtil.generateControllableButton(
					OverviewProperty.REBOOT.getName(), OverviewProperty.REBOOT.getName(), "Rebooting", 0L
			));
		}

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
