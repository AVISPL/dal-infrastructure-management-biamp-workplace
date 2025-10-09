/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

import java.time.Duration;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.security.auth.login.FailedLoginException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.aggregator.AggregatedDevice;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.ApiConstant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils.Util;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated.OverviewProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated.StatusProperty;
import com.avispl.symphony.dal.util.StringUtils;

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
	void testConfigManagementProperty() throws Exception {
		this.communicator.setConfigManagement(true);
		this.communicator.getMultipleStatistics();
		this.communicator.retrieveMultipleStatistics();
		Util.delayExecution(Duration.ofSeconds(20).toMillis());
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = this.extendedStatistics.getStatistics();
		Map<String, String> dynamicStatistics = this.extendedStatistics.getDynamicStatistics();
		List<AggregatedDevice> aggregatedDevices = this.communicator.retrieveMultipleStatistics();

		this.verifyStatistics(statistics);
		Assertions.assertEquals(2, dynamicStatistics.size());
		if (this.communicator.isConfigManagement()) {
			aggregatedDevices.forEach(aggregatedDevice -> Assertions.assertFalse(aggregatedDevice.getControllableProperties().isEmpty()));
		} else {
			aggregatedDevices.forEach(aggregatedDevice -> Assertions.assertTrue(aggregatedDevice.getControllableProperties().isEmpty()));
		}
	}

	@Test
	void testHistoricalPropertiesProperty() throws Exception {
		this.communicator.setHistoricalProperties(String.join(",", new String[] {
				//	Optional graphs for aggregated devices
				String.format(Constant.PROPERTY_FORMAT, Constant.STATUS_GROUP, StatusProperty.TEMPERATURE.getName()),
				String.format(Constant.PROPERTY_FORMAT, Constant.STATUS_GROUP, StatusProperty.CPU_UTILIZATION.getName())
		}));
		this.communicator.getMultipleStatistics();
		this.communicator.retrieveMultipleStatistics();
		Util.delayExecution(Duration.ofSeconds(20).toMillis());
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = this.extendedStatistics.getStatistics();
		Map<String, String> dynamicStatistics = this.extendedStatistics.getDynamicStatistics();
		List<AggregatedDevice> aggregatedDevices = this.communicator.retrieveMultipleStatistics();

		this.verifyStatistics(statistics);
		Assertions.assertEquals(2, dynamicStatistics.size());
		aggregatedDevices.forEach(aggregatedDevice -> Assertions.assertEquals(2, aggregatedDevice.getDynamicStatistics().size()));
	}

	@Test
	void testOauthHostnameProperty() throws Exception {
		this.communicator.setOauthHostname("google.com");

		if (StringUtils.isNotNullOrEmpty(this.communicator.getOauthHostname())
				&& !this.communicator.getOauthHostname().equals(ApiConstant.OAUTH_HOSTNAME)) {
			Assertions.assertThrows(FailedLoginException.class, () -> this.communicator.getMultipleStatistics());
		} else {
			this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
			Map<String, String> statistics = this.extendedStatistics.getStatistics();
			Map<String, String> dynamicStatistics = this.extendedStatistics.getDynamicStatistics();

			Assertions.assertEquals(ApiConstant.OAUTH_HOSTNAME, this.communicator.getOauthHostname());
			this.verifyStatistics(statistics);
			Assertions.assertEquals(2, dynamicStatistics.size());
		}
	}

	@Test
	void testOrganizationIdsProperty() throws Exception {
		this.communicator.setOrganizationIds("7d188efc-9a08-4bc9-9b61-431c823db38b");
		this.communicator.getMultipleStatistics();
		this.communicator.retrieveMultipleStatistics();
		Util.delayExecution(Duration.ofSeconds(20).toMillis());
		Set<String> organizationIds = Arrays.stream(this.communicator.getOrganizationIds().split(","))
				.map(String::trim).collect(Collectors.toSet());
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		Map<String, String> statistics = this.extendedStatistics.getStatistics();
		Map<String, String> dynamicStatistics = this.extendedStatistics.getDynamicStatistics();
		List<AggregatedDevice> aggregatedDevices = this.communicator.retrieveMultipleStatistics();

		this.verifyStatistics(statistics);
		Assertions.assertEquals(2, dynamicStatistics.size());
		if (StringUtils.isNotNullOrEmpty(this.communicator.getOrganizationIds())) {
			aggregatedDevices.forEach(aggregatedDevice -> {
				String organizationId = aggregatedDevice.getProperties().get(OverviewProperty.ORGANIZATION_ID.getName());
				Assertions.assertTrue(organizationIds.contains(organizationId));
			});
		}
	}

	@Test
	void testRetrieveMultipleStatistics() throws Exception {
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		this.communicator.retrieveMultipleStatistics();
		Util.delayExecution(Duration.ofSeconds(5).toMillis());
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		this.communicator.retrieveMultipleStatistics();
		Util.delayExecution(Duration.ofSeconds(5).toMillis());
		this.extendedStatistics = (ExtendedStatistics) this.communicator.getMultipleStatistics().get(0);
		List<AggregatedDevice> aggregatedDevices = this.communicator.retrieveMultipleStatistics();

		aggregatedDevices.forEach(aggregatedDevice -> {
			Map<String, String> statistics = aggregatedDevice.getProperties();
			List<AdvancedControllableProperty> controllableProperties = aggregatedDevice.getControllableProperties();
			Map<String, String> dynamicStatistics = aggregatedDevice.getDynamicStatistics();

			this.verifyStatistics(statistics);
			this.verifyStatistics(dynamicStatistics);
			if (!controllableProperties.isEmpty()) {
				controllableProperties.forEach(Assertions::assertNotNull);
			}
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
		groups.put(Constant.ORGANIZATION_GROUPS, this.filterGroupStatistics(statistics, Constant.ORGANIZATION_GROUPS));
		groups.put(Constant.FIRMWARE_GROUP, this.filterGroupStatistics(statistics, Constant.FIRMWARE_GROUP));
		groups.put(Constant.STATUS_GROUP, this.filterGroupStatistics(statistics, Constant.STATUS_GROUP));

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
