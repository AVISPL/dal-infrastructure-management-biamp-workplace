	/*
	 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
	 */

	package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

	import java.io.IOException;
	import java.time.OffsetDateTime;
	import java.time.ZoneOffset;
	import java.time.format.DateTimeFormatter;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Collections;
	import java.util.Date;
	import java.util.HashMap;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	import java.util.Properties;
	import java.util.Set;
	import java.util.concurrent.ExecutorService;
	import java.util.concurrent.Executors;
	import java.util.concurrent.TimeUnit;
	import java.util.concurrent.locks.ReentrantLock;
	import java.util.stream.Collectors;

	import org.springframework.http.HttpHeaders;
	import org.springframework.http.HttpMethod;
	import org.springframework.util.CollectionUtils;
	import org.springframework.util.LinkedMultiValueMap;
	import org.springframework.util.MultiValueMap;

	import com.fasterxml.jackson.databind.JsonNode;
	import com.fasterxml.jackson.databind.ObjectMapper;
	import javax.security.auth.login.FailedLoginException;

	import com.avispl.symphony.api.dal.control.Controller;
	import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
	import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
	import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
	import com.avispl.symphony.api.dal.dto.monitor.Statistics;
	import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
	import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
	import com.avispl.symphony.api.dal.monitor.Monitorable;
	import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
	import com.avispl.symphony.dal.aggregator.parser.AggregatedDeviceProcessor;
	import com.avispl.symphony.dal.aggregator.parser.PropertiesMapping;
	import com.avispl.symphony.dal.aggregator.parser.PropertiesMappingParser;
	import com.avispl.symphony.dal.communicator.RestCommunicator;
	import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.AggregatedInformation;
	import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.AggregatorInformation;
	import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.BiampWorkplaceCommand;
	import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.BiampWorkplaceConstant;
	import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.LoginInfo;
	import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.dto.RefreshToken;
	import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.query.BiampWorkplaceQuery;
	import com.avispl.symphony.dal.util.StringUtils;


	/**
	 * BiampWorkplaceCommunicator
	 * Supported features are:
	 * Monitoring Aggregator Device:
	 *  <ul>
	 *    <li> Generic </li>
	 *    <li> User Status </li>
	 *    <li> User Role </li>
	 *    <li> Device Count </li>
	 *    <li> Organization Id </li>
	 *    <li> Organization Name </li>
	 *  <ul>
	 *
	 * Monitoring aggregated device:
	 * <ul>
	 *     <li>ProductFamily</li>
	 *     <li>ProductModel</li>
	 *     <li>ProductRevision</li>
	 *     <li>SerialNumber</li>
	 *     <li>Description</li>
	 *
	 *     <li>Architecture</li>
	 *     <li>DeviceCreatedAt</li>
	 *     <li>DeviceUpdatedAt</li>
	 *     <li>DeviceState</li>
	 *     <li>DeviceType</li>
	 *     <li>DeviceLanguage</li>
	 *     <li>Timezone</li>
	 *     <li>LastTimestamp</li>
	 *     <li>Uptime(Seconds)</li>
	 *
	 *     <li>Firmware
	 *         <ul>
	 *             <li>CurrentVersion</li>
	 *             <li>Channel</li>
	 *             <li>LatestVersion</li>
	 *             <li>NextVersion</li>
	 *         </ul>
	 *     </li>
	 *
	 *     <li>Statistics
	 *         <ul>
	 *             <li>CpuUtilization(%)</li>
	 *             <li>PresenceDetected</li>
	 *             <li>Temperature</li>
	 *         </ul>
	 *     </li>
	 *
	 *     <li>Workplace_Information
	 *         <ul>
	 *             <li>OrganizationId</li>
	 *             <li>OrganizationName</li>
	 *             <li>WorkplaceDeskID</li>
	 *             <li>WorkplaceDeskName</li>
	 *             <li>WorkplacePlaceName</li>
	 *             <li>WorkplacePlaceID</li>
	 *             <li>WorkplaceRoomID</li>
	 *             <li>WorkplaceRoomName</li>
	 *         </ul>
	 *     </li>
	 * </ul>
	 *
	 * @author Harry / Symphony Dev Team<br>
	 * Created on 02/1/2025
	 * @since 1.0.0
	 */
	public class BiampWorkplaceCommunicator extends RestCommunicator implements Aggregator, Monitorable, Controller {
		/**
		 * Process that is running constantly and triggers collecting data from Biamp Workplace API endpoints, based on the given timeouts and thresholds.
		 *
		 * @author Harry
		 * @since 1.0.0
		 */

		/**
		 * How much time last monitoring cycle took to finish
		 */
		private Long lastMonitoringCycleDuration;

		/* Adapter metadata properties - adapter version and build date /
		private Properties adapterProperties;

		/**
		 * Device adapter instantiation timestamp.
		 */
		private long adapterInitializationTimestamp;

		/**
		 * Indicates whether a device is considered as paused.
		 * True by default so if the system is rebooted and the actual value is lost -> the device won't start stats
		 * collection unless the {@link BiampWorkplaceCommunicator#retrieveMultipleStatistics()} method is called which will change it
		 * to a correct value
		 */
		private volatile boolean devicePaused = true;

		/**
		 * We don't want the statistics to be collected constantly, because if there's not a big list of devices -
		 * new devices' statistics loop will be launched before the next monitoring iteration. To avoid that -
		 * this variable stores a timestamp which validates it, so when the devices' statistics is done collecting, variable
		 * is set to currentTime + 30s, at the same time, calling {@link #retrieveMultipleStatistics()} and updating the
		 */
		private long nextDevicesCollectionIterationTimestamp;

		/**
		 * This parameter holds timestamp of when we need to stop performing API calls
		 * It used when device stop retrieving statistic. Updated each time of called #retrieveMultipleStatistics
		 */
		private volatile long validRetrieveStatisticsTimestamp;

		/**
		 * Aggregator inactivity timeout. If the {@link BiampWorkplaceCommunicator#retrieveMultipleStatistics()}  method is not
		 * called during this period of time - device is considered to be paused, thus the Cloud API
		 * is not supposed to be called
		 */
		private static final long retrieveStatisticsTimeOut = 3 * 60 * 1000;

		/**
		 * Update the status of the device.
		 * The device is considered as paused if did not receive any retrieveMultipleStatistics()
		 * calls during {@link BiampWorkplaceCommunicator}
		 */
		private synchronized void updateAggregatorStatus() {
			devicePaused = validRetrieveStatisticsTimestamp < System.currentTimeMillis();
		}

		/**
		 * Uptime time stamp to valid one
		 */
		private synchronized void updateValidRetrieveStatisticsTimestamp() {
			validRetrieveStatisticsTimestamp = System.currentTimeMillis() + retrieveStatisticsTimeOut;
			updateAggregatorStatus();
		}

		/**
		 * Executor that runs all the async operations, that is posting and
		 */
		private ExecutorService executorService;

		/**
		 * the login info
		 */
		private LoginInfo loginInfo;

		/**
		 * A private field that represents an instance of the BiampWorkplaceCloudLoader class, which is responsible for loading device data for BiampWorkplaceCloud
		 */
		private BiampWorkplaceCloudDataLoader deviceDataLoader;

		/**
		 * A private final ReentrantLock instance used to provide exclusive access to a shared resource
		 * that can be accessed by multiple threads concurrently. This lock allows multiple reentrant
		 * locks on the same shared resource by the same thread.
		 */
		private final ReentrantLock reentrantLock = new ReentrantLock();

		/**
		 * Represents the refresh token used for obtaining a new access token
		 * when the current access token expires.
		 */
		private String refresh_Token;

		/**
		 * Private variable representing the local extended statistics.
		 */
		private ExtendedStatistics localExtendedStatistics;

		/**
		 * A cache that maps route names to their corresponding values.
		 */
		private final Map<String, String> cacheValue = new HashMap<>();

		/**
		 * List of aggregated device
		 */
		private List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

		/**
		 * cache data for aggregated
		 */
		private List<AggregatedDevice> cachedData = Collections.synchronizedList(new ArrayList<>());

		/**
		 * An instance of the AggregatedDeviceProcessor class used to process and aggregate device-related data.
		 */
		private AggregatedDeviceProcessor aggregatedDeviceProcessor;

		/**
		 * List of token Response
		 */
		private RefreshToken objToken = new RefreshToken();

		/** Adapter metadata properties - adapter version and build date */
		private Properties adapterProperties;

		/**
		 * A JSON node containing the response from an aggregator.
		 */
		private JsonNode aggregatorResponse;

		/**
		 * Represents the access token used to authenticate and authorize requests
		 * to protected resources or APIs.
		 */
		private String accessToken;


		/**
		 * A mapper for reading and writing JSON using Jackson library.
		 * ObjectMapper provides functionality for converting between Java objects and JSON.
		 * It can be used to serialize objects to JSON format, and deserialize JSON data to objects.
		 */
		ObjectMapper objectMapper = new ObjectMapper();

		/**
		 * A private field that represents an instance of the BiampWorkplaceCloudDataLoader class, which is responsible for loading device data for BiampWorkplaceCloud
		 */
		class BiampWorkplaceCloudDataLoader implements Runnable {
			private volatile boolean inProgress;
			private volatile boolean dataFetchCompleted = false;
			private volatile boolean flag = false;

			public BiampWorkplaceCloudDataLoader() {
				inProgress = true;
			}

			@Override
			public void run() {
				loop:
				while (inProgress) {
					long startCycle = System.currentTimeMillis();
					try {
						try {
							TimeUnit.MILLISECONDS.sleep(500);
						} catch (InterruptedException e) {
							logger.info(String.format("Sleep for 0.5 second was interrupted with error message: %s", e.getMessage()));
						}

						if (!inProgress) {
							break loop;
						}

						// next line will determine whether DT Studio monitoring was paused
						updateAggregatorStatus();
						if (devicePaused) {
							continue loop;
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Fetching other than aggregated device list");
						}

						long currentTimestamp = System.currentTimeMillis();
						if (!flag && nextDevicesCollectionIterationTimestamp <= currentTimestamp) {
							populateDeviceAggregated();
							flag = true;
						}

						while (nextDevicesCollectionIterationTimestamp > System.currentTimeMillis()) {
							try {
								TimeUnit.MILLISECONDS.sleep(1000);
							} catch (InterruptedException e) {
								logger.info(String.format("Sleep for 1 second was interrupted with error message: %s", e.getMessage()));
							}
						}

						if (!inProgress) {
							break loop;
						}
						if (dataFetchCompleted) {
							nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 30000;
							lastMonitoringCycleDuration = (System.currentTimeMillis() - startCycle) / 1000;
							logger.debug("Finished collecting devices statistics cycle at " + new Date() + ", total duration: " + lastMonitoringCycleDuration);
							dataFetchCompleted = false;
						}

						if (logger.isDebugEnabled()) {
							logger.debug("Finished collecting devices statistics cycle at " + new Date());
						}
					} catch (Exception e) {
						logger.error("Unexpected error occurred during main device collection cycle", e);
					}
				}
				logger.debug("Main device collection loop is completed, in progress marker: " + inProgress);
			}

			/**
			 * Triggers main loop to stop
			 */
			public void stop() {
				inProgress = false;
			}
		}

		/**
		 * Configurable property for historical properties, comma separated values kept as set locally
		 */
		private Set<String> historicalProperties = new HashSet<>();

		/**
		 * Retrieves {@link #historicalProperties}
		 *
		 * @return value of {@link #historicalProperties}
		 */
		public String getHistoricalProperties() {
			return String.join(",", this.historicalProperties);
		}

		/**
		 * Sets {@link #historicalProperties} value
		 *
		 * @param historicalProperties new value of {@link #historicalProperties}
		 */
		public void setHistoricalProperties(String historicalProperties) {
			this.historicalProperties.clear();
			Arrays.asList(historicalProperties.split(",")).forEach(propertyName -> {
				this.historicalProperties.add(propertyName.trim());
			});
		}

		/**
		 * Constructs a new instance of BiampWorkplaceCommunicator.
		 *
		 * @throws IOException If an I/O error occurs while loading the properties mapping YAML file.
		 */
		public BiampWorkplaceCommunicator() throws IOException {
			Map<String, PropertiesMapping> mapping = new PropertiesMappingParser().loadYML(BiampWorkplaceConstant.MODEL_MAPPING_AGGREGATED_DEVICE, getClass());
			aggregatedDeviceProcessor = new AggregatedDeviceProcessor(mapping);
			adapterProperties = new Properties();
			adapterProperties.load(getClass().getResourceAsStream("/version.properties"));
			this.setTrustAllCertificates(true);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void controlProperty(ControllableProperty controllableProperty) throws Exception {}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void controlProperties(List<ControllableProperty> controllableProperties) throws Exception {
			if (CollectionUtils.isEmpty(controllableProperties)) {
				throw new IllegalArgumentException("ControllableProperties can not be null or empty");
			}
			for (ControllableProperty p : controllableProperties) {
				try {
					controlProperty(p);
				} catch (Exception e) {
					logger.error(String.format("Error when control property %s", p.getProperty()), e);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<AggregatedDevice> retrieveMultipleStatistics(List<String> list) throws Exception {
			return retrieveMultipleStatistics()
					.stream()
					.filter(aggregatedDevice -> list.contains(aggregatedDevice.getDeviceId()))
					.collect(Collectors.toList());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<AggregatedDevice> retrieveMultipleStatistics() throws Exception {
			if (executorService == null) {
				executorService = Executors.newFixedThreadPool(1);
				executorService.submit(deviceDataLoader = new BiampWorkplaceCloudDataLoader());
			}
			nextDevicesCollectionIterationTimestamp = System.currentTimeMillis();
			updateValidRetrieveStatisticsTimestamp();
			return cloneAndPopulateAggregatedDeviceList();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public List<Statistics> getMultipleStatistics() throws Exception {
			reentrantLock.lock();
			try {
				if (loginInfo == null) {
					loginInfo = new LoginInfo();
				}
				checkValidApiToken();
				Map<String, String> stats = new HashMap<>();
				Map<String, String> dynamicStatistics = new HashMap<>();
				ExtendedStatistics extendedStatistics = new ExtendedStatistics();
				retrieveAggregatorInfo();
				populateDeviceInfo(stats);
				retrieveMetadata(stats, dynamicStatistics);
				extendedStatistics.setStatistics(stats);
				extendedStatistics.setDynamicStatistics(dynamicStatistics);
				localExtendedStatistics = extendedStatistics;
			} finally {
				reentrantLock.unlock();
			}
			return Collections.singletonList(localExtendedStatistics);
		}

		/**
		 * {@inheritDoc}
		 * set API Key into Header of Request
		 */
		@Override
		protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
			if(uri.contains(BiampWorkplaceCommand.SIMULATOR_GET_TOKEN)){
				headers.set("Content-Type", "application/x-www-form-urlencoded");
			} else {
				headers.setBearerAuth(this.accessToken);
			}
			return super.putExtraRequestHeaders(httpMethod, uri, headers);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void authenticate() throws Exception {
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void internalInit() throws Exception {
			if (logger.isDebugEnabled()) {
				logger.debug("Internal init is called.");
			}
			adapterInitializationTimestamp = System.currentTimeMillis();
			executorService = Executors.newFixedThreadPool(1);
			executorService.submit(deviceDataLoader = new BiampWorkplaceCloudDataLoader());
			super.internalInit();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void internalDestroy() {
			if (logger.isDebugEnabled()) {
				logger.debug("Internal destroy is called.");
			}
			if (deviceDataLoader != null) {
				deviceDataLoader.stop();
				deviceDataLoader = null;
			}
			if (executorService != null) {
				executorService.shutdownNow();
				executorService = null;
			}
			if (localExtendedStatistics != null && localExtendedStatistics.getStatistics() != null && localExtendedStatistics.getControllableProperties() != null) {
				localExtendedStatistics.getStatistics().clear();
				localExtendedStatistics.getControllableProperties().clear();
			}
			cacheValue.clear();
			loginInfo = null;
			nextDevicesCollectionIterationTimestamp = 0;
			aggregatedDeviceList.clear();
			cachedData.clear();
			super.internalDestroy();
		}

		/**
		 * Retrieves metadata information and updates the provided statistics and dynamic map.
		 *
		 * @param stats the map where statistics will be stored
		 * @param dynamicStatistics the map where dynamic statistics will be stored
		 * @throws Exception if there is an error during the retrieval process
		 */
		private void retrieveMetadata(Map<String, String> stats, Map<String, String> dynamicStatistics) throws Exception {
			try {
				if (lastMonitoringCycleDuration != null) {
					dynamicStatistics.put(BiampWorkplaceConstant.MONITORING_CYCLE_DURATION, String.valueOf(lastMonitoringCycleDuration));
				}
				stats.put(BiampWorkplaceConstant.ADAPTER_VERSION,
						getDefaultValueForNullData(adapterProperties.getProperty("aggregator.version")));
				stats.put(BiampWorkplaceConstant.ADAPTER_BUILD_DATE,
						getDefaultValueForNullData(adapterProperties.getProperty("aggregator.build.date")));

				long adapterUptime = System.currentTimeMillis() - adapterInitializationTimestamp;
				stats.put(BiampWorkplaceConstant.ADAPTER_UPTIME_MIN, String.valueOf(adapterUptime / (1000 * 60)));
				stats.put(BiampWorkplaceConstant.ADAPTER_UPTIME, formatUpTime(String.valueOf(adapterUptime / 1000)));
			} catch (Exception e) {
				logger.error("Failed to populate metadata information", e);
			}
		}

		/**
		 * Populates device information into the provided stats map by retrieving data from the response info endpoint.
		 *
		 * @param stats a map to store device information as key-value pairs
		 */
		private void populateDeviceInfo(Map<String, String> stats) {
			JsonNode userProfileInfo = aggregatorResponse.get(BiampWorkplaceConstant.PROFILE).get(BiampWorkplaceConstant.MEMBERSHIPS);
			JsonNode deviceInfo = aggregatorResponse.get("allDevices");
			for(AggregatorInformation property : AggregatorInformation.values()){
				switch (property) {
					case USER_ROLE:
						stats.put(uppercaseFirstCharacter(property.getName()), uppercaseFirstCharacter(userProfileInfo.get(BiampWorkplaceConstant.ROLE).asText()));
						break;
					case USER_STATUS:
						stats.put(uppercaseFirstCharacter(property.getName()), uppercaseFirstCharacter(userProfileInfo.get(BiampWorkplaceConstant.STATUS).asText()));
						break;
					case ORGANIZATION_ID:
						stats.put(uppercaseFirstCharacter(property.getName()), uppercaseFirstCharacter(deviceInfo.get(BiampWorkplaceConstant.DEVICES).get("orgId").asText()));
						break;
					case ORGANIZATION_NAME:
						stats.put(uppercaseFirstCharacter(property.getName()), uppercaseFirstCharacter(deviceInfo.get(BiampWorkplaceConstant.DEVICES).get("orgName").asText()));
						break;
					case DEVICE_COUNT:
						stats.put(uppercaseFirstCharacter(property.getName()), uppercaseFirstCharacter(deviceInfo.get(BiampWorkplaceConstant.TOTAL_COUNT).asText()));
						break;
					default:
						stats.put(uppercaseFirstCharacter(property.getName()), getDefaultValueForNullData(deviceInfo.get(property.name()).asText()));
						break;
				}
			}
		}

		/**
		 * Retrieves aggregator information by sending a POST request and processing the response.
		 *
		 * @throws FailedLoginException if the login fails during token retrieval.
		 */
		private void retrieveAggregatorInfo() throws FailedLoginException {
			try{
				JsonNode response = this.doPost(BiampWorkplaceCommand.SIMULATOR_GET_AGGREGATOR, BiampWorkplaceQuery.PROFILE_QUERY, JsonNode.class);
				if (!response.has(BiampWorkplaceConstant.DATA)) {
					throw new ResourceNotReachableException("Error when retrieve system information.");
				}
				aggregatorResponse = response.get(BiampWorkplaceConstant.DATA);
			} catch (Exception e){
				throw new FailedLoginException("Token has been expired. Please renew the token");
			}
		}

		/**
		 * Clones and populates a new list of aggregated devices with mapped monitoring properties.
		 *
		 * @return A new list of {@link AggregatedDevice} objects with mapped monitoring properties.
		 */
		private List<AggregatedDevice> cloneAndPopulateAggregatedDeviceList() {
			aggregatedDeviceList.clear();
			synchronized (cachedData) {
				for (AggregatedDevice item : cachedData) {
					AggregatedDevice aggregatedDevice = new AggregatedDevice();
					Map<String, String> cachedValue = item.getProperties();
					aggregatedDevice.setDeviceId(item.getDeviceId());
					aggregatedDevice.setDeviceOnline(item.getDeviceOnline());
					aggregatedDevice.setDeviceModel(item.getDeviceModel());
					Map<String, String> stats = new HashMap<>();
					List<AdvancedControllableProperty> advancedControllableProperties = new ArrayList<>();
					mapMonitoringProperty(cachedValue, stats);
					mapControllableProperty(stats, advancedControllableProperties);
					aggregatedDevice.setProperties(stats);
					aggregatedDevice.setControllableProperties(advancedControllableProperties);
					aggregatedDeviceList.add(aggregatedDevice);
				}
			}
			return aggregatedDeviceList;
		}

		/**
		 * Maps monitoring properties from cached values to statistics and advanced control properties.
		 *
		 * @param cachedValue The cached values map containing raw monitoring data.
		 * @param stats The statistics map to store mapped monitoring properties.
		 */
		private void mapMonitoringProperty(Map<String, String> cachedValue, Map<String, String> stats) {
			for (AggregatedInformation property : AggregatedInformation.values()) {
				String name = property.getName();
				String propertyName = property.getGroup() + name;
				String value = getDefaultValueForNullData(cachedValue.get(propertyName));
				switch (property) {
					case DEVICE_CREATED_AT:
					case DEVICE_UPDATED_AT:
					case LAST_TIMESTAMP:
						stats.put(propertyName, convertDateTimeFormat(value));
						break;
					case UPTIME:
						stats.put(propertyName, formatUpTime(value));
						break;
					default:
						stats.put(propertyName, value);
						break;
				}
			}
		}

		/**
		 * Maps controllable properties to the provided stats and advancedControllableProperties lists.
		 * This method adds buttons for "Reboot Player" and "Reboot with Crash Report" to the advanced controllable properties.
		 *
		 * @param stats A map containing the statistics to be populated with controllable properties.
		 * @param advancedControllableProperties A list of AdvancedControllableProperty objects to be populated with controllable properties.
		 */
		private void mapControllableProperty(Map<String, String> stats, List<AdvancedControllableProperty> advancedControllableProperties) {
			addAdvancedControlProperties(advancedControllableProperties, stats, createButton(BiampWorkplaceConstant.REBOOT, "Reboot", "Rebooting", 0), BiampWorkplaceConstant.NONE);
		}

		/**
		 * Populates detailed information for each device in the aggregated response.
		 * This method iterates over all devices in the response
		 * @throws FailedLoginException if the login fails during token retrieval.
		 */
		private void populateDeviceAggregated() throws FailedLoginException {
			reentrantLock.lock();
			try{
				JsonNode response = this.doPost(BiampWorkplaceCommand.SIMULATOR_BIAMP_QUERY_URL, BiampWorkplaceQuery.DEVICE_QUERY, JsonNode.class);
				if (!response.has(BiampWorkplaceConstant.DATA)) {
					throw new ResourceNotReachableException("Error when retrieve device information.");
				}
				JsonNode device = response.get(BiampWorkplaceConstant.DATA).get(BiampWorkplaceConstant.DEVICE);
				JsonNode node = objectMapper.createArrayNode().add(device);
				String id = device.get(BiampWorkplaceConstant.ID).asText();
				cachedData.removeIf(item -> item.getDeviceId().equals(id));
				cachedData.addAll(aggregatedDeviceProcessor.extractDevices(node));
			} catch (Exception e){
				throw new FailedLoginException("Token has been expired. Please renew the token");
			} finally {
				reentrantLock.unlock();
			}
		}

		/**
		 * Check API token validation
		 * If the token expires, we send a request to get a new token
		 * @throws Exception if there is an error during the retrieval process
		 */
		private void checkValidApiToken() throws Exception {
			 if (StringUtils.isNullOrEmpty(this.getLogin()) || StringUtils.isNullOrEmpty(this.getPassword())) {
			 throw new FailedLoginException("Client_Id or Refresh token field is empty. Please check device credentials");
			 }
			if (this.loginInfo.isTimeout() || StringUtils.isNullOrEmpty(this.loginInfo.getToken())) {
				logger.info("Token expired or missing. Retrieving new token...");
				String[] passwordField = this.getPassword().split(BiampWorkplaceConstant.SPACE);
				if (passwordField.length == 2) {
					this.accessToken = passwordField[0];
					refresh_Token = passwordField[1];
				} else {
					throw new FailedLoginException("The format of Password field is incorrect. Please check again");
				}
			}
		}

		/**
		 * Retrieves a new access token using the provided or available refresh token.
		 *
		 * @param refresh_Token The refresh token to be used for obtaining a new access token.
		 *                      If null, the method attempts to use an existing refresh token
		 *                      from the {@code objToken} object.
		 * @throws FailedLoginException if the token retrieval fails due to invalid credentials or other errors.
		 */
		private void retrieveToken(String refresh_Token) throws FailedLoginException {
				try{
					String refreshToken = Optional.ofNullable(objToken.getRefreshToken()).orElse(refresh_Token);

					if (StringUtils.isNullOrEmpty(refreshToken)) {
						throw new RuntimeException("No valid refresh token available. Login might be required.");
					}
					MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
					requestBody.put(BiampWorkplaceConstant.GRANT_TYPE, Collections.singletonList(BiampWorkplaceConstant.REFRESH_TOKEN));
					requestBody.put(BiampWorkplaceConstant.CLIENT_ID, Collections.singletonList(this.getLogin()));
					requestBody.put(BiampWorkplaceConstant.REFRESH_TOKEN, Collections.singletonList(refreshToken));
					JsonNode result = doPost(BiampWorkplaceCommand.SIMULATOR_GET_TOKEN, requestBody, JsonNode.class);

					objToken = objectMapper.treeToValue(result.get("response"), RefreshToken.class);
					this.loginInfo.setToken(objToken.getAccessToken());
					this.accessToken = objToken.getAccessToken();
					this.loginInfo.setLoginDateTime(System.currentTimeMillis());

				} catch (Exception e) {
					throw new FailedLoginException("Invalid token");
				}
		}

		/**
		 * Create a button.
		 *
		 * @param name name of the button
		 * @param label label of the button
		 * @param labelPressed label of the button after pressing it
		 * @param gracePeriod grace period of button
		 * @return This returns the instance of {@link AdvancedControllableProperty} type Button.
		 */
		private AdvancedControllableProperty createButton(String name, String label, String labelPressed, long gracePeriod) {
			AdvancedControllableProperty.Button button = new AdvancedControllableProperty.Button();
			button.setLabel(label);
			button.setLabelPressed(labelPressed);
			button.setGracePeriod(gracePeriod);
			return new AdvancedControllableProperty(name, new Date(), button, BiampWorkplaceConstant.EMPTY);
		}

		/**
		 * Add addAdvancedControlProperties if advancedControllableProperties different empty
		 *
		 * @param advancedControllableProperties advancedControllableProperties is the list that store all controllable properties
		 * @param stats store all statistics
		 * @param property the property is item advancedControllableProperties
		 * @throws IllegalStateException when exception occur
		 */
		private void addAdvancedControlProperties(List<AdvancedControllableProperty> advancedControllableProperties, Map<String, String> stats, AdvancedControllableProperty property, String value) {
			if (property != null) {
				advancedControllableProperties.removeIf(controllableProperty -> controllableProperty.getName().equals(property.getName()));

				String propertyValue = StringUtils.isNotNullOrEmpty(value) ? value : BiampWorkplaceConstant.EMPTY;
				stats.put(property.getName(), propertyValue);

				advancedControllableProperties.add(property);
			}
		}

		/**
		 * check value is null or empty
		 *
		 * @param value input value
		 * @return value after checking
		 */
		private String getDefaultValueForNullData(String value) {
			return StringUtils.isNotNullOrEmpty(value) && !"null".equalsIgnoreCase(value) ? uppercaseFirstCharacter(value) : BiampWorkplaceConstant.NONE;
		}

		/**
		 * capitalize the first character of the string
		 *
		 * @param input input string
		 * @return string after fix
		 */
		private String uppercaseFirstCharacter(String input) {
			return Character.toUpperCase(input.charAt(0)) + input.substring(1);
		}

		/**
		 * Formats uptime from a string representation "hh:mm:ss" into "X hour(s) Y minute(s)" format.
		 *
		 * @param time the uptime string to format
		 * @return formatted uptime string or "None" if input is invalid
		 */
		private String formatUpTime(String time) {
			int seconds = Integer.parseInt(time);
			if (seconds < 0) {
				return BiampWorkplaceConstant.NONE;
			}

			int days = seconds / (24 * 3600);
			seconds %= 24 * 3600;
			int hours = seconds / 3600;
			seconds %= 3600;
			int minutes = seconds / 60;
			seconds %= 60;

			StringBuilder result = new StringBuilder();
			if (days > 0) {
				result.append(days).append(" day(s) ");
			}
			if (hours > 0) {
				result.append(hours).append(" hour(s) ");
			}
			if (minutes > 0) {
				result.append(minutes).append(" minute(s) ");
			}
			if (seconds > 0) {
				result.append(seconds).append(" second(s) ");
			}

			if (result.length() == 0) {
				return "0 second(s)";
			}
			return result.toString().trim();
		}


		/**
		 * Converts a date-time string from the default format to the target format with GMT timezone.
		 *
		 * @param inputDateTime The input date-time string in the default format.
		 * @return The date-time string after conversion to the target format with GMT timezone.
		 * Returns {@link BiampWorkplaceConstant#NONE} if there is an error during conversion.
		 */
		private String convertDateTimeFormat(String inputDateTime) {
			if (BiampWorkplaceConstant.NONE.equals(inputDateTime)) {
				return inputDateTime;
			}
			try {
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(BiampWorkplaceConstant.DEFAULT_FORMAT_DATETIME_WITHOUT_MILLIS)
						.withZone(ZoneOffset.UTC);
				DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(BiampWorkplaceConstant.TARGET_FORMAT_DATETIME)
						.withZone(ZoneOffset.UTC);

				OffsetDateTime date = OffsetDateTime.parse(inputDateTime, inputFormatter);
				return date.format(outputFormatter);
			} catch (Exception e) {
				logger.warn("Can't convert the date time value");
				return BiampWorkplaceConstant.NONE;
			}
		}

	}
