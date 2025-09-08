/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants;

/**
 * Utility class that defines API endpoint paths and URI patterns.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class ApiConstant {
	private ApiConstant() {
		// Prevent instantiation
	}

	//	Endpoints
	public static final String GET_TOKEN_ENDPOINT = "https://iam.workplace.biamp.app/oauth/v2/token";
	public static final String GRAPHQL_ENDPOINT = "graphql";

	//	Fields
	public static final String DATA_FIELD = "data";
}
