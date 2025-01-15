/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common;

/**
 * AggregatorInformation class represents information about the aggregator.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 10/1/2025
 * @since 1.0.0
 */
public enum AggregatorInformation {
	USER_ROLE("userRole", ""),
	USER_STATUS("userStatus", ""),
	ORGANIZATION_ID("organizationId", ""),
	ORGANIZATION_NAME("organizationName", ""),
	DEVICE_COUNT("deviceCount", ""),
	;
	private final String name;
	private final String group;

	/**
	 * Constructor for AggregatorInformation.
	 *
	 * @param name The name representing the system information category.
	 * @param group The corresponding value associated with the category.
	 */
	AggregatorInformation(String name, String group) {
		this.name = name;
		this.group = group;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #group}
	 *
	 * @return value of {@link #group}
	 */
	public String getGroup() {
		return group;
	}
}
