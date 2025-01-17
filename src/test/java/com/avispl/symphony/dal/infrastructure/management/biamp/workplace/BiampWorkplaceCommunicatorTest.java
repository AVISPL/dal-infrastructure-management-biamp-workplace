/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;

/**
 * BiampWorkplaceCommunicatorTest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 02/01/2025
 * @since 1.0.0
 */
public class BiampWorkplaceCommunicatorTest {
	private ExtendedStatistics extendedStatistic;
	private BiampWorkplaceCommunicator disruptiveTechnologiesCommunicator;

	@BeforeEach
	void setUp() throws Exception {
		disruptiveTechnologiesCommunicator = new BiampWorkplaceCommunicator();
		disruptiveTechnologiesCommunicator.setHost("");
		disruptiveTechnologiesCommunicator.setLogin("");
		disruptiveTechnologiesCommunicator.setPassword("");
		disruptiveTechnologiesCommunicator.setPort(8088);
		disruptiveTechnologiesCommunicator.init();
		disruptiveTechnologiesCommunicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		disruptiveTechnologiesCommunicator.disconnect();
		disruptiveTechnologiesCommunicator.destroy();
	}

	@Test
	void testLoginSuccess() throws Exception {
		disruptiveTechnologiesCommunicator.getMultipleStatistics();
		Thread.sleep(10000);
		disruptiveTechnologiesCommunicator.getMultipleStatistics();
	}

	@Test
	void testGetMetadata() throws Exception {
		disruptiveTechnologiesCommunicator.getMultipleStatistics();
		Thread.sleep(30000);
		List<Statistics> statistics = disruptiveTechnologiesCommunicator.getMultipleStatistics();
		ExtendedStatistics stats = (ExtendedStatistics) statistics.get(0);
		Map<String, String> dsMap = stats.getDynamicStatistics();
		Map<String, String> esMap = stats.getStatistics();
		Assertions.assertNotNull(statistics);
		Assertions.assertNotNull(stats);
		Assertions.assertNotNull(esMap.get("AdapterBuildDate"));
		Assertions.assertNotNull(esMap.get("AdapterUptime"));
		Assertions.assertNotNull(esMap.get("AdapterUptime(min)"));
		Assertions.assertNotNull(esMap.get("AdapterVersion"));
		Assertions.assertNotNull(dsMap.get("LastMonitoringCycleDuration(s)"));
	}

	@Test
	void testGetAggregatorData() throws Exception {
		extendedStatistic = (ExtendedStatistics) disruptiveTechnologiesCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistic.getStatistics();
		Assertions.assertEquals("Tech Solutions Ltd.", stats.get("OrganizationName"));
		Assertions.assertEquals("ORG_ADMIN", stats.get("UserRole"));
		Assertions.assertEquals("1", stats.get("DeviceCount"));
	}

	@Test
	void testGetAggregatedData() throws Exception {
		disruptiveTechnologiesCommunicator.getMultipleStatistics();
		disruptiveTechnologiesCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		List<AggregatedDevice> aggregatedDeviceList = disruptiveTechnologiesCommunicator.retrieveMultipleStatistics();
		Optional<AggregatedDevice> aggregatedDevice = aggregatedDeviceList.stream().findFirst();
		if (aggregatedDevice.isPresent()) {
			Map<String, String> stats = aggregatedDevice.get().getProperties();
			Assertions.assertEquals("Desk Controller", stats.get("DeviceType"));
			Assertions.assertEquals("ARM64", stats.get("Architecture"));
		}
	}

	@Test
	void testGetMultipleStatisticsWithHistoricalProperties() throws Exception {
		disruptiveTechnologiesCommunicator.setHistoricalProperties("SensorData#ObjectPresentCount, CO2(ppm), Temperature(C)");
		disruptiveTechnologiesCommunicator.getMultipleStatistics();
		disruptiveTechnologiesCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		List<AggregatedDevice> aggregatedDeviceList = disruptiveTechnologiesCommunicator.retrieveMultipleStatistics();
		System.out.println("aggregatedDeviceList: " + aggregatedDeviceList);
		Assert.assertEquals(12, aggregatedDeviceList.size());
	}

	@Test
	void testGetNumberOfDevices() throws Exception {
		disruptiveTechnologiesCommunicator.getMultipleStatistics();
		disruptiveTechnologiesCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		List<AggregatedDevice> aggregatedDeviceList = disruptiveTechnologiesCommunicator.retrieveMultipleStatistics();
		Assert.assertEquals(11, aggregatedDeviceList.size());
	}

	@Test
	void testTimeLogin() throws Exception {
		long exp = System.currentTimeMillis() / 1000 + 3600;
		long iat = System.currentTimeMillis() / 1000;
		System.out.println(exp);
		System.out.println(iat);
	}

}
