/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;

/**
 * BiampWorkplaceCommunicatorTest
 *
 * @author Harry / Symphony Dev Team
 * @since 1.0.0
 */
class BiampWorkplaceCommunicatorTest {
	private ExtendedStatistics extendedStatistic;
	private BiampWorkplaceCommunicator biampWorkplaceCommunicator;

	@BeforeEach
	void setUp() throws Exception {
		biampWorkplaceCommunicator = new BiampWorkplaceCommunicator();
		biampWorkplaceCommunicator.setHost("api.evoko.app");
		biampWorkplaceCommunicator.setPort(443);
		biampWorkplaceCommunicator.setLogin("283590591013552185@platform");
		biampWorkplaceCommunicator.setPassword("7ZN7bj5YcMY5LT0Ip4ZNK2su9TXpjrRyyVRyiLl7RlZPhUumvpsKZhTJHU0EMr0h4nW3m6kbTX53mcIr0R3DLSxyABb0BvlGG5DYKl8G");
		biampWorkplaceCommunicator.init();
		biampWorkplaceCommunicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		biampWorkplaceCommunicator.disconnect();
		biampWorkplaceCommunicator.destroy();
	}

	@Test
	void testGetMultipleStatisticsWithHistoricalProperties() throws Exception {
		biampWorkplaceCommunicator.setHistoricalProperties("SensorData#ObjectPresentCount, CO2(ppm), Temperature(C)");
		biampWorkplaceCommunicator.getMultipleStatistics();
		System.out.println("passed");
	}
}
