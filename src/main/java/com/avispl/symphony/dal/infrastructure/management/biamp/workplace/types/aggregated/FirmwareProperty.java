/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.bases.BaseProperty;

/**
 * Represents firmware properties of an aggregated device.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum FirmwareProperty implements BaseProperty {
	ASSIGNED_FIRMWARE("AssignedFirmware"),
	FIRMWARE("Firmware"),
	FIRMWARE_CHANNEL("FirmwareChannel"),
	LATEST_FIRMWARE("LatestFirmware"),
	NEXT_FIRMWARE("NextFirmware");

	private final String name;

	FirmwareProperty(String name) {
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
