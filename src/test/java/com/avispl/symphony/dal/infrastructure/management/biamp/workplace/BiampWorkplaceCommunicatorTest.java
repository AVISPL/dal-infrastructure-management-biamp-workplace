/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
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
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;

/**
 * BiampWorkplaceCommunicatorTest
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 25/10/2024
 * @since 1.0.0
 */
public class BiampWorkplaceCommunicatorTest {
	private ExtendedStatistics extendedStatistic;
	private BiampWorkplaceCommunicator disruptiveTechnologiesCommunicator;

	@BeforeEach
	void setUp() throws Exception {
		disruptiveTechnologiesCommunicator = new BiampWorkplaceCommunicator();
//		disruptiveTechnologiesCommunicator.setHost("api.evoko.app/graphql");
		disruptiveTechnologiesCommunicator.setHost("127.0.0.1");
		disruptiveTechnologiesCommunicator.setLogin("283590591013552185@platform");
		disruptiveTechnologiesCommunicator.setPassword("jFZucJSyFv79C2vmaYKT63NI3K-A87rR6hWqKYtcqam5G6DidM8fjUGcbpMBMI6zT0OojLMJquPO-7HQF_Qy8nJyqpBcHt65sJKFwIQ7");
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
	void testGetAggregatorData() throws Exception {
		extendedStatistic = (ExtendedStatistics) disruptiveTechnologiesCommunicator.getMultipleStatistics().get(0);
		Map<String, String> stats = extendedStatistic.getStatistics();
		Assertions.assertEquals("AVI-SPL-LAB Inventory", stats.get("Generic#ProjectName"));
		Assertions.assertEquals("AVI-SPL-LAB", stats.get("Generic#OrganizationName"));
		Assertions.assertEquals("12", stats.get("Generic#MonitoredSensorCount"));
	}

	@Test
	void testGetAggregatedData() throws Exception {
		disruptiveTechnologiesCommunicator.getMultipleStatistics();
		disruptiveTechnologiesCommunicator.retrieveMultipleStatistics();
		Thread.sleep(20000);
		List<AggregatedDevice> aggregatedDeviceList = disruptiveTechnologiesCommunicator.retrieveMultipleStatistics();
		System.out.println("aggregatedDeviceList " + aggregatedDeviceList);
		String sensorId = "emucrbc5qpqbmpf547g7dh0";
		Optional<AggregatedDevice> aggregatedDevice = aggregatedDeviceList.stream().filter(item -> item.getDeviceId().equals(sensorId)).findFirst();
		if (aggregatedDevice.isPresent()) {
			Map<String, String> stats = aggregatedDevice.get().getProperties();
			Assertions.assertEquals("CO2", stats.get("Type"));
			Assertions.assertEquals("CO2 Sensor 1", stats.get("LabelName"));
		}
	}

	@Test
	void testGetMultipleStatisticsWithHistoricalProperties()throws Exception {
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
