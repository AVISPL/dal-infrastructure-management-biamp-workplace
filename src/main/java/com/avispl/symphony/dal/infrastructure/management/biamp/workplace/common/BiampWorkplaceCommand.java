/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common;

/**
 * DisruptiveTechnologiesCommand
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 22/10/2024
 * @since 1.0.0
 */
public class BiampWorkplaceCommand {
//	public static final String GET_TOKEN = "https://iam.workplace.biamp.app/oauth/v2/token";
	public static final String GET_TOKEN = "http://127.0.0.1:8088/oauth/v2/token";
	public static final String AUTH_URL = "oauth/v2/authorize";

	public static final String BIAMP_QUERY_URL = "/graphql/get-device";
	public static final String GET_ALL_PROJECT = "/v2/projects";
	public static final String GET_SINGLE_PROJECT = "/v2/projects/%s";
	public static final String AUTHENTICATION_PARAM = "{\"refresh_token\":\"%s\", \"grant_type\":\"%s\", \"client_id\":\"%s\"}";
}
