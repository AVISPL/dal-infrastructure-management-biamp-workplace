/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Invitation;

/**
 * Represents the possible statuses of an {@link Invitation}.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum InvitationStatus {
	NOT_AVAILABLE(Constant.NOT_AVAILABLE),
	PENDING("Pending"),
	EXPIRED("Expired"),
	DECLINED("Declined"),
	ACCEPTED("Accepted");

	private final String value;

	InvitationStatus(String value) {
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
