/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.bases.BaseProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;

/**
 * Represents general properties of an aggregated device.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum OverviewProperty implements BaseProperty {
	MODEL("Model"),
	REBOOT(Constant.REBOOT),
	STATE("State"),
	TYPE("Type"),
	LOCATION_ID("LocationID"),
	LOCATION_NAME("LocationName"),
	ORGANIZATION_ID("OrganizationID"),
	ORGANIZATION_NAME("OrganizationName");

	private final String name;

	OverviewProperty(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	@Override
	public String getName() {
		return name;
	}
}
