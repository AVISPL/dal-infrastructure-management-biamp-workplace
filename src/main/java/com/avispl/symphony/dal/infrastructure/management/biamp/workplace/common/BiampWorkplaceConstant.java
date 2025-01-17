/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common;

/**
 * DisruptiveTechnologiesConstant class used in monitoring
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 30/12/2024
 * @since 1.0.0
 */
public class BiampWorkplaceConstant {
	public static final String HASH = "#";
	public static final String MODEL_MAPPING_AGGREGATED_DEVICE = "biamp_workplace/model-mapping.yml";
	public static final String NONE = "None";
	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String ID = "id";
	public static final String DEFAULT_FORMAT_DATETIME_WITHOUT_MILLIS  = "yyyy-MM-dd'T'HH:mm:ss'Z'";
	public static final String TARGET_FORMAT_DATETIME = "MMM d, yyyy, h:mm a";
	public static final String REFRESH_TOKEN = "refresh_token";
	public static final String GRANT_TYPE = "grant_type";
	public static final String CLIENT_ID = "client_id";
	public static final String DATA = "data";
	public static final String PROFILE = "profile";
	public static final String DEVICE = "device";
	public static final String REBOOT = "Reboot";
	public static final String MEMBERSHIPS = "memberships";
	public static final String DEVICES = "devices";
	public static final String STATUS = "status";
	public static final String ROLE = "role";
	public static final String TOTAL_COUNT = "totalCount";
	public static final String MONITORING_CYCLE_DURATION = "LastMonitoringCycleDuration(s)";
	public static final String ADAPTER_VERSION = "AdapterVersion";
	public static final String ADAPTER_BUILD_DATE = "AdapterBuildDate";
	public static final String ADAPTER_UPTIME_MIN = "AdapterUptime(min)";
	public static final String ADAPTER_UPTIME = "AdapterUptime";
	/**
	 * Token timeout is 29 minutes, as this case reserve 1 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 28;
}
