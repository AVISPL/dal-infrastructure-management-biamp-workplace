/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils.Util;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated.OverviewProperty;

/**
 * Unit tests for the {@link BiampWorkplaceCommunicator} class.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
class BiampWorkplaceCommunicatorTest {
	private ExtendedStatistics extendedStatistics;
	private BiampWorkplaceCommunicator communicator;

	@BeforeEach
	void setUp() throws Exception {
		this.communicator = new BiampWorkplaceCommunicator();
		this.communicator.setHost("");
		this.communicator.setPort(443);
		this.communicator.setLogin("");
		this.communicator.setPassword("");
		this.communicator.init();
		this.communicator.connect();
	}

	@AfterEach
	void destroy() throws Exception {
		this.communicator.disconnect();
		this.communicator.destroy();
	}

	@Test
	void testGetMultipleStatistics() throws Exception {
		this.communicator.setOrganizationIds("7d188efc-9a08-4bc9-9b61-431c823db38b");
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = this.extendedStatistics.getStatistics();
		List<AdvancedControllableProperty> controllableProperties = this.extendedStatistics.getControllableProperties();
		Map<String, String> dynamicStatistics = this.extendedStatistics.getDynamicStatistics();

		this.verifyStatistics(statistics);
		this.verifyStatistics(dynamicStatistics);
		controllableProperties.forEach(Assertions::assertNotNull);
	}

	@Test
	void testRetrieveMultipleStatistics() throws Exception {
		this.communicator.setHistoricalProperties("Status#Temperature(C), Status#CPUUtilization(%)");
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		this.communicator.retrieveMultipleStatistics();
		Util.delayExecution(5000);
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		this.communicator.retrieveMultipleStatistics();
		Util.delayExecution(5000);
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		List<AggregatedDevice> aggregatedDevices = this.communicator.retrieveMultipleStatistics();

		aggregatedDevices.forEach(aggregatedDevice -> {
			Map<String, String> statistics = aggregatedDevice.getProperties();
			List<AdvancedControllableProperty> controllableProperties = aggregatedDevice.getControllableProperties();
			Map<String, String> dynamicStatistics = aggregatedDevice.getDynamicStatistics();

			this.verifyStatistics(statistics);
			this.verifyStatistics(dynamicStatistics);
			controllableProperties.forEach(Assertions::assertNotNull);
		});
	}

	@Test
	void testRebootForAggregatedDevice() throws Exception {
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		ControllableProperty controllableProperty = new ControllableProperty(
				OverviewProperty.REBOOT.getName(),
				Constant.NOT_AVAILABLE,
				"60643bb4-89e5-4e21-b0d3-a169d6d037f9"
		);
		this.communicator.controlProperty(controllableProperty);
		Util.delayExecution(1000);
		Exception exception = Assertions.assertThrows(IllegalStateException.class, () -> this.communicator.controlProperty(controllableProperty));
		Assertions.assertEquals("target device is offline", exception.getMessage());
	}

	private void verifyStatistics(Map<String, String> statistics) {
		Map<String, Map<String, String>> groups = new LinkedHashMap<>();
		groups.put("General", this.filterGroupStatistics(statistics, null));

		for (Map<String, String> initGroup : groups.values()) {
			for (Map.Entry<String, String> initStatistics : initGroup.entrySet()) {
				Assertions.assertNotNull(initStatistics.getValue(), "Value is null with property: " + initStatistics.getKey());
			}
		}
	}

	private Map<String, String> filterGroupStatistics(Map<String, String> statistics, String groupName) {
		return statistics.entrySet().stream()
				.filter(e -> {
					if (groupName == null) {
						return !e.getKey().contains("#");
					}
					return e.getKey().startsWith(groupName);
				})
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
