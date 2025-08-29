/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants;

/**
 * BiampWorkplaceConstant class used in monitoring
 *
 * @author Harry / Symphony Dev Team
 * @since 1.0.0
 */
public class Constant {
	private Constant() {
	}

	/**
	 * Token timeout is 29 minutes, as this case reserve 1 minutes to make sure we never failed because of the timeout
	 */
	public static final long TIMEOUT = 28;

	//	Special characters
	public static final String COMMA = ",";

	//	Values
	public static final String NOT_AVAILABLE = "N/A";
	public static final String GRAPHQL_EXTENSION = ".graphql";

	//	Info messages
	public static final String INITIAL_INTERNAL_INFO = "Initialing internal state of instance: ";
	public static final String DESTROY_INTERNAL_INFO = "Destroying internal state of instance: ";

	//	Warning messages
	public static final String CONTROLLABLE_PROPS_EMPTY_WARNING = "ControllableProperties list is null or empty, skipping control operation";
	public static final String SENT_REQUEST_NULL_WARNING = "Sent request is null. Endpoint: %s, ResponseClass: %s";

	//	Fail messages
	public static final String READ_PROPERTIES_FILE_FAILED = "Failed to load properties file: ";
	public static final String LOGIN_FAILED = "Failed to login, please check the credentials";
	public static final String REQUEST_APIS_FAILED = "Unable to process requested API sections: [%s], error reported: [%s]";
	public static final String FETCH_DATA_FAILED = "Exception while fetching data. Endpoint: %s, ResponseClass: %s";
}
