/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.bases.BaseProperty;

/**
 * Represents status properties of an aggregated device.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum StatusProperty implements BaseProperty {
	CPU_UTILIZATION("CPUUtilization(%)"),
	TEMPERATURE("Temperature(C)"),
	TIMESTAMP("Timestamp(UTC)");

	private final String name;

	StatusProperty(String displayName) {
		this.name = displayName;
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
