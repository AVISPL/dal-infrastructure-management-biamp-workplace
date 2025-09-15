/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.bases.BaseProperty;

/**
 * Represents workplace properties of an aggregated device.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum WorkplaceProperty implements BaseProperty {
	DESK_ID("DeskID"),
	DESK_NAME("DeskName"),
	LOCATION_ID("LocationID"),
	LOCATION_NAME("LocationName"),
	ORGANIZATION_ID("OrganizationID"),
	ORGANIZATION_NAME("OrganizationName"),
	ROOM_ID("RoomID"),
	ROOM_NAME("RoomName");

	private final String name;

	WorkplaceProperty(String name) {
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
