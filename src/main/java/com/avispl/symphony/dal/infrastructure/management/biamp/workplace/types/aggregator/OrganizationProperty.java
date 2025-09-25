/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.bases.BaseProperty;

/**
 *
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum OrganizationProperty implements BaseProperty {
	ID("ID"),
	INVITATION_STATUS("InvitationStatus"),
	MEMBERSHIP_STATUS("MembershipStatus"),
	NAME("Name"),
	USER_ROLE("UserRole");

	private final String name;

	OrganizationProperty(String name) {
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
