/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */

package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.query;

/**
 * Class containing GraphQL queries used in Biamp Workplace communication.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 25/12/2024
 * @since 1.0.0
 */
public class BiampWorkplaceQuery {
	public static final String DEVICE_QUERY = "{\"query\":\"query Device { "
			+ "device(id: null) { "
			+ "id "
			+ "serial "
			+ "firmwarePublicKey "
			+ "firmwareVariant "
			+ "placeId "
			+ "deskId "
			+ "roomId "
			+ "language "
			+ "timezone "
			+ "orgName "
			+ "orgId "
			+ "createdAt "
			+ "updatedAt "
			+ "state "
			+ "} "
			+ "}\"}";

	public static final String PROFILE_QUERY = "{\"query\":\"query Profile { "
			+ "profile { "
			+ "id "
			+ "name "
			+ "firstName "
			+ "lastName "
			+ "email "
			+ "eula "
			+ "verified "
			+ "superAdmin "
			+ "createdAt "
			+ "updatedAt "
			+ "} "
			+ "}\"}";

}
