/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;

/**
 *
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum MembershipRole {
	NOT_AVAILABLE(Constant.NOT_AVAILABLE),
	UNSPECIFIED("Unspecified"),
	OWNER("Owner"),
	ORG_ADMIN("OrgAdmin"),
	DESK_ADMIN("DeskAdmin"),
	USER("User");

	private final String value;

	MembershipRole(String value) {
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
