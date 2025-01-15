/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common;

/**
 * Enum AggregatedInformation represents various pieces of aggregated information about a device.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 07/1/2025
 * @since 1.0.0
 */
public enum AggregatedInformation {
	PRODUCT_FAMILY("ProductFamily", ""),
	PRODUCT_MODEL("ProductModel", ""),
	PRODUCT_REVISION("ProductRevision", ""),
	SERIAL_NUMBER("SerialNumber", ""),
	DESCRIPTION("Description", ""),

	ARCHITECTURE("Architecture", ""),
	DEVICE_CREATED_AT("DeviceCreatedAt", ""),
	DEVICE_UPDATED_AT("DeviceUpdatedAt", ""),
	DEVICE_STATE("DeviceState", ""),
	DEVICE_TYPE("DeviceType", ""),
	DEVICE_LANGUAGE("DeviceLanguage", ""),
	TIMEZONE("Timezone", ""),
	LAST_TIMESTAMP("LastTimestamp", ""),
	UPTIME("Uptime(Seconds)", ""),

	/** Firmware **/
	CURRENT_VERSION("CurrentVersion", "Firmware#"),
	CHANNEL("Channel", "Firmware#"),
	LATEST_VERSION("LatestVersion", "Firmware#"),
	NEXT_VERSION("NextVersion", "Firmware#"),

	/** Statistics **/
	CPU_UTILIZATION("CpuUtilization(%)", "Statistics#"),
	PRESENCE_DETECTED("PresenceDetected", "Statistics#"),
	TEMPERATURE("Temperature", "Statistics#"),

	/** Workplace **/
	ORGANIZATION_ID("OrganizationId", "Workplace_Information#"),
	ORGANIZATION_NAME("OrganizationName", "Workplace_Information#"),
	DESK_ID("WorkplaceDeskID", "Workplace_Information#"),
	DESK_NAME("WorkplaceDeskName", "Workplace_Information#"),
	PLACE_NAME("WorkplacePlaceName", "Workplace_Information#"),
	PLACE_ID("WorkplacePlaceID", "Workplace_Information#"),
	ROOM_ID("WorkplaceRoomID", "Workplace_Information#"),
	ROOM_NAME("WorkplaceRoomName", "Workplace_Information#"),

	;

	private final String name;
	private final String group;

	/**
	 * Constructor for AggregatedInformation.
	 *
	 * @param name The name representing the system information category.
	 * @param group The group associated with the category.
	 */
	AggregatedInformation(String name, String group) {
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
