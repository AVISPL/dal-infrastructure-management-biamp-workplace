/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Device;

/**
 * Represents the possible states of a {@link Device}.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum DeviceState {
	NOT_AVAILABLE(Constant.NOT_AVAILABLE),
	UNPROVISIONED("Unprovisioned"),
	PROVISIONED("Provisioned"),
	ONLINE("Online"),
	OFFLINE("Offline"),
	MISSING("Missing");

	private final String value;

	DeviceState(String value) {
		this.value = value;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}
}
