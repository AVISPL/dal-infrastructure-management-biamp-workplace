	/*
	 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
	 */

	package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

	import java.io.IOException;
	import java.net.URLEncoder;
	import java.nio.charset.StandardCharsets;
	import java.util.ArrayList;
	import java.util.Arrays;
	import java.util.Collections;
	import java.util.HashMap;
	import java.util.HashSet;
	import java.util.List;
	import java.util.Map;
	import java.util.Optional;
	import java.util.Set;
	import java.util.concurrent.ExecutorService;
	import java.util.concurrent.Executors;
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
	import org.apache.http.HttpResponse;
	import org.apache.http.client.HttpClient;
	import org.apache.http.client.methods.HttpPost;
	import org.apache.http.entity.StringEntity;
	import org.apache.http.impl.client.HttpClients;
	import org.apache.http.util.EntityUtils;

	import com.avispl.symphony.api.dal.control.Controller;
	import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
	import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
	import com.avispl.symphony.api.dal.dto.monitor.Statistics;
	import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
	import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
	import com.avispl.symphony.api.dal.monitor.Monitorable;
	import com.avispl.symphony.api.dal.monitor.aggregator.Aggregator;
	import com.avispl.symphony.dal.aggregator.parser.AggregatedDeviceProcessor;
	import com.avispl.symphony.dal.communicator.RestCommunicator;
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
	 *    <li> Cloud Connector </li>
	 *  <ul>
	 *
	 * Subscription Group:
	 * <ul>
	 * <li> - CreationDate</li>
	 * <li> - ExpireDate</li>
	 * <li> - LastModifiedDate</li>
	 * <li> - Level</li>
	 * </ul>
	 *
	 * General Info Aggregated Device:
	 * <ul>
	 *   Monitoring with sensors:
	 *   <li> CO2 </li>
	 *   <li> Contact </li>
	 *   <li> Counting Proximity </li>
	 *   <li> Counting Touch </li>
	 *   <li> Desk Occupancy </li>
	 *   <li> Humidity </li>
	 *   <li> Motion </li>
	 *   <li> Proximity </li>
	 *   <li> Temperature </li>
	 *   <li> Touch </li>
	 *   <li> Water Detector </li>
	 * </ul>
	 *
	 * @author Harry / Symphony Dev Team<br>
	 * Created on 23/10/2024
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
		 * ID of project
		 */
		private String projectID;

		/**
		 * Private variable representing the local extended statistics.
		 */
		private ExtendedStatistics localExtendedStatistics;

		/**
		 * A cache that maps route names to their corresponding values.
		 */
		private final Map<String, String> cacheValue = new HashMap<>();

		/**
		 * A set containing cloud info.
		 */
		private Set<String> allCloudSet = new HashSet<>();

		/**
		 * A set containing sensor info.
		 */
		private Set<String> allSensorNameSet = new HashSet<>();

		/**
		 * List of aggregated device
		 */
		private List<AggregatedDevice> aggregatedDeviceList = Collections.synchronizedList(new ArrayList<>());

		/**
		 * An instance of the AggregatedDeviceProcessor class used to process and aggregate device-related data.
		 */
		private AggregatedDeviceProcessor aggregatedDeviceProcessor;

		/**
		 * List of token Response
		 */
		private RefreshToken objToken = new RefreshToken();

		/**
		 * A JSON node containing the response from an aggregator.
		 */
		private List<JsonNode> profileData = Collections.synchronizedList(new ArrayList<>());

		private JsonNode profile;

		private String refreshToken;
		private String accessToken;


		/**
		 * A mapper for reading and writing JSON using Jackson library.
		 * ObjectMapper provides functionality for converting between Java objects and JSON.
		 * It can be used to serialize objects to JSON format, and deserialize JSON data to objects.
		 */
		ObjectMapper objectMapper = new ObjectMapper();

		/**
		 * cache data for aggregated
		 */
		private List<AggregatedDevice> cachedData = Collections.synchronizedList(new ArrayList<>());

		/**
		 * A JSON node containing the response from an aggregator.
		 */
		private JsonNode aggregatedResponse;

		class BiampWorkplaceCloudDataLoader implements Runnable {
			private volatile boolean inProgress;
			private volatile boolean dataFetchCompleted = false;

			public BiampWorkplaceCloudDataLoader() {
				inProgress = true;
			}

			@Override
			public void run() {
//				loop:
//				while (inProgress) {
//					long startCycle = System.currentTimeMillis();
//					try {
//						try {
//							TimeUnit.MILLISECONDS.sleep(500);
//						} catch (InterruptedException e) {
//							logger.info(String.format("Sleep for 0.5 second was interrupted with error message: %s", e.getMessage()));
//						}
//
//						if (!inProgress) {
//							break loop;
//						}
//
//						// next line will determine whether DT Studio monitoring was paused
//						updateAggregatorStatus();
//						if (devicePaused) {
//							continue loop;
//						}
//						if (logger.isDebugEnabled()) {
//							logger.debug("Fetching other than aggregated device list");
//						}
//
//						while (nextDevicesCollectionIterationTimestamp > System.currentTimeMillis()) {
//							try {
//								TimeUnit.MILLISECONDS.sleep(1000);
//							} catch (InterruptedException e) {
//								logger.info(String.format("Sleep for 1 second was interrupted with error message: %s", e.getMessage()));
//							}
//						}
//
//						if (!inProgress) {
//							break loop;
//						}
//						if (dataFetchCompleted) {
//							nextDevicesCollectionIterationTimestamp = System.currentTimeMillis() + 30000;
//							lastMonitoringCycleDuration = (System.currentTimeMillis() - startCycle) / 1000;
//							logger.debug("Finished collecting devices statistics cycle at " + new Date() + ", total duration: " + lastMonitoringCycleDuration);
//							dataFetchCompleted = false;
//						}
//
//						if (logger.isDebugEnabled()) {
//							logger.debug("Finished collecting devices statistics cycle at " + new Date());
//						}
//					} catch (Exception e) {
//						logger.error("Unexpected error occurred during main device collection cycle", e);
//					}
//				}
//				logger.debug("Main device collection loop is completed, in progress marker: " + inProgress);
				// Finished collecting
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
			this.setTrustAllCertificates(false);
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

		private List<AggregatedDevice> cloneAndPopulateAggregatedDeviceList() {
			aggregatedDeviceList.clear();
			Map<String, String> stats = new HashMap<>();
			mapDataMockup1();
			return aggregatedDeviceList;
		};

		private void mapDataMockup1() {
			AggregatedDevice aggregatedDevice = new AggregatedDevice();
			aggregatedDevice.setDeviceId("emucrbc449qbmpf547g7dc0");
			aggregatedDevice.setDeviceModel("1234SXZ");
			aggregatedDevice.setDeviceName("Temperature Sensor 1");
			aggregatedDevice.setDeviceOnline(true);
			aggregatedDevice.setProperties(populateDeviceAggregated());
			cachedData.add(aggregatedDevice);
			aggregatedDeviceList.add(aggregatedDevice);
		}

		public Map<String, String> populateDeviceAggregated() {
			Map<String, String> stats = new HashMap<>();
			stats.put("Name", "projects/crbc2sna9j4j6igdjkvg/devices/emucrbc449qbmpf547g7dc0");
			stats.put("Type", "temperature");
			stats.put("ProductNumber", "None");
			stats.put("LabelsName", "Temperature Sensor 1");
			stats.put("LabelsCustom", "Not Found in Response");

			stats.put("NetworkStatus#SignalStrength(%)", "100");
			stats.put("NetworkStatus#RSSI", "-50");
			stats.put("NetworkStatus#UpdateTime", "Sep 03, 2024, 7:09 AM");
			stats.put("NetworkStatus#TransmissionMode", "LOW_POWER_STANDARD_MODE");

			stats.put("BatteryStatus#Value(%)", "None");
			stats.put("BatteryStatus#UpdateTime", "None");

			stats.put("Temperature#Value", "16");
			stats.put("Temperature#UpdateTime", "Sep 03, 2024, 7:09 AM");

			stats.put("Touch#UpdateTime", "Sep 03, 2024, 7:09 AM");
			stats.put("ConnectionStatus", "None");
			return stats;
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
				retrieveSystemInfo();
				populateDeviceInfo(stats);
				populateSystemInfo(stats);
				extendedStatistics.setStatistics(stats);
				extendedStatistics.setDynamicStatistics(dynamicStatistics);
				localExtendedStatistics = extendedStatistics;
			} finally {
				reentrantLock.unlock();
			}
			return Collections.singletonList(localExtendedStatistics);
		}

		private void populateSystemInfo(Map<String, String> stats) {
			System.out.println("profile" + profile.asText());
//			List<String> siteNameList = profileData.stream().map(node -> node.get(BiampWorkplaceConstant.NAME).asText()).collect(Collectors.toList());
		}

		private void retrieveSystemInfo() throws Exception {
			JsonNode response = this.doPost(BiampWorkplaceCommand.BIAMP_QUERY_URL, BiampWorkplaceQuery.PROFILE_QUERY, JsonNode.class);

			if (!response.has(BiampWorkplaceConstant.DATA)) {
				throw new ResourceNotReachableException("Error when retrieve system information.");
			}
			profileData.clear();
			for (JsonNode item : response.get(BiampWorkplaceConstant.DATA).get(BiampWorkplaceConstant.DEVICES)) {
				profileData.add(item);
				profile = item;
			}
		}

		private void populateDeviceInfo(Map<String, String> stats) throws Exception {
			stats.put("CountDevices", "1");
			stats.put("Role", "ORG_ADMIN");
			stats.put("OrganizationId", "7d188efc-9a08-4bc9-9b61-431c823db38b");
			stats.put("Organization", "AVI-SPL-LAB");
			stats.put("UserStatus", "ACTIVE");
		}

		/**
		 * {@inheritDoc}
		 * set API Key into Header of Request
		 */
		@Override
		protected HttpHeaders putExtraRequestHeaders(HttpMethod httpMethod, String uri, HttpHeaders headers) throws Exception {
			if(uri.contains(BiampWorkplaceCommand.GET_TOKEN)){
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
		 * Check API token validation
		 * If the token expires, we send a request to get a new token
		 */
		private void checkValidApiToken() throws Exception {
			if (StringUtils.isNullOrEmpty(this.getLogin()) || StringUtils.isNullOrEmpty(this.getPassword())) {
				throw new FailedLoginException("Client_Id or Refresh token field is empty. Please check device credentials");
			}

			if (this.loginInfo.isTimeout() || StringUtils.isNullOrEmpty(this.loginInfo.getToken())) {
				System.out.println("Token expired or missing. Retrieving new token...");
				getToken();
			}
		}


		private void getToken() throws Exception {
				try{
					refreshToken = Optional.ofNullable(objToken.getRefreshToken()).orElse(this.getPassword());

					if (StringUtils.isNullOrEmpty(refreshToken)) {
						throw new RuntimeException("No valid refresh token available. Login might be required.");
					}

//					MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
//					requestBody.put("grant_type", Collections.singletonList(BiampWorkplaceConstant.GRANT_TYPE));
//					requestBody.put("client_id", Collections.singletonList(this.getLogin()));
//					requestBody.put("refresh_token", Collections.singletonList(refreshToken));
//					JsonNode result = doPost(BiampWorkplaceCommand.GET_TOKEN, requestBody, JsonNode.class);
					JsonNode result = post(BiampWorkplaceCommand.GET_TOKEN, BiampWorkplaceConstant.GRANT_TYPE, this.getLogin(), refreshToken);

					ObjectMapper objectMapper = new ObjectMapper();
//					objToken = objectMapper.treeToValue(result, RefreshToken.class);
					objToken = objectMapper.treeToValue(result.get("response"), RefreshToken.class);
					this.loginInfo.setToken(objToken.getAccessToken());
					this.accessToken = objToken.getAccessToken();
					this.loginInfo.setLoginDateTime(System.currentTimeMillis());

					System.out.println("New access token retrieved successfully: " + objToken.getAccessToken());
					System.out.println("New refresh token saved: " + objToken.getRefreshToken());

				} catch (Exception e) {
					throw new RuntimeException("Can not retrieve refresh token", e);
				}
		}

		public JsonNode post(String url, String grantType, String clientId, String refreshToken) throws Exception {
			HttpClient client = this.obtainHttpClient(true);
			HttpPost httpPost = new HttpPost(url);

			String requestBody = "grant_type=" + grantType
					+ "&client_id=" + clientId
					+ "&refresh_token=" + URLEncoder.encode(refreshToken, StandardCharsets.UTF_8.toString());

			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
			httpPost.setEntity(new StringEntity(requestBody, StandardCharsets.UTF_8));

			HttpResponse response = client.execute(httpPost);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new IOException("Unexpected response code: " + response.getStatusLine().getStatusCode());
			}

			String responseBody = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(responseBody);
		}

	}
