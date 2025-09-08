/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants;

/**
 * Utility class that defines constant values used across the application.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class Constant {
	private Constant() {
		// Prevent instantiation
	}

	//	Formats
	public static final String GROUP_FORMAT = "%s_%02d";
	public static final String PROPERTY_FORMAT = "%s#%s";

	//	Special characters
	public static final String COMMA = ",";

	//	Groups
	public static final String ORGANIZATION_GROUPS = "Organization";

	//	Values
	public static final String NOT_AVAILABLE = "N/A";
	public static final String REFRESH_TOKEN_INVALID_MESSAGE = "Errors.OIDCSession.RefreshTokenInvalid";
	public static final String GRAPHQL_FOLDER = "graphql/";
	public static final String GRAPHQL_EXTENSION = ".graphql";

	//	Info messages
	public static final String INITIAL_INTERNAL_INFO = "Initialing internal state of instance: ";
	public static final String DESTROY_INTERNAL_INFO = "Destroying internal state of instance: ";
	public static final String REFRESHING_TOKENS_INFO = "Authentication is invalid or expired, refreshing tokens";

	//	Warning messages
	public static final String SENT_REQUEST_NULL_WARNING = "Sent request is null. Endpoint: %s, ResponseClass: %s";
	public static final String ORGANIZATIONS_EMPTY_WARNING = "The organizations is empty, returning empty map.";
	public static final String OBJECT_NULL_WARNING = "%s object is null, returning null value.";
	public static final String UNSUPPORTED_PROPERTY_WARNING = "Unsupported %s with property %s.";

	//	Fail messages
	public static final String REQUEST_APIS_FAILED = "Unable to process requested API sections: [%s], error reported: [%s]";
	public static final String READ_PROPERTIES_FILE_FAILED = "Failed to load properties file: ";
	public static final String LOGIN_FAILED = "Failed to login, please check the credentials";
	public static final String FETCH_DATA_FAILED = "Exception while fetching data. Endpoint: %s, ResponseClass: %s";
	public static final String FIND_GRAPHQL_FOLDER_FAILED = "Can not find the GraphQL folder: ";
	public static final String READ_GRAPHQL_QUERY_FAILED = "Can not read query from graphql file: ";
	public static final String MAP_TO_UPTIME_FAILED = "Failed to mapToUptime with uptime: ";
	public static final String MAP_TO_UPTIME_MIN_FAILED = "Failed to mapToUptimeMin with uptime: ";
}
