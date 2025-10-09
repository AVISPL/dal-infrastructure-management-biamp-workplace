/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.bases.BaseProperty;

/**
 * Represents profile properties of an aggregator device.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum UserProfileProperty implements BaseProperty {
	ID("ID"),
	NAME("Name"),
	EMAIL("Email"),
	IS_SUPER_ADMIN("IsSuperAdmin");

	private final String name;

	UserProfileProperty(String name) {
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
