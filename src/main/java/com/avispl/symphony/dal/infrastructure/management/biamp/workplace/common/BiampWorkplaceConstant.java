/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common;

/**
 * DisruptiveTechnologiesConstant class used in monitoring
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 22/10/2024
 * @since 1.0.0
 */
public class BiampWorkplaceConstant {
	public static final String HASH = "#";
	public static final String MODEL_MAPPING_AGGREGATED_DEVICE = "dt_sensor/model-mapping.yml";
	public static final String REQUEST_BODY = "assertion=%s&grant_type=urn%%3Aietf%%3Aparams%%3Aoauth%%3Agrant-type%%3Ajwt-bearer";
	public static final String NONE = "None";
	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String GENERIC = "GeneralInformation";
	public static final String DEFAULT_FORMAT_DATETIME_WITH_MILLIS = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";
	public static final String TARGET_FORMAT_DATETIME = "MMM d, yyyy, h:mm a";
	public static final String ACCESS_TOKEN = "access_token";
	public static final String GRANT_TYPE = "refresh_token";
	public static final String EXPIRES_IN = "expires_in";
	public static final String REPORTED = "reported";
	public static final String NAME = "name";
	public static final String COLON = ":";
	public static final String DATA = "data";
	public static final String PROFILE = "profile";
	public static final String DEVICES = "device";
	/**
	 * Token timeout is 29 minutes, as this case reserve 1 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 28;
}
