/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.ApiConstant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.Authentication;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Profile;

/**
 * Defines different response types and their associated model classes.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum ResponseType {
	AUTHENTICATION(Authentication.class, null),
	PROFILE(Profile.class, "profile");

	private final Class<?> clazz;
	private final String fieldName;

	ResponseType(Class<?> clazz, String fieldName) {
		this.clazz = clazz;
		this.fieldName = fieldName;
	}

	/**
	 * Retrieves {@link #clazz}
	 *
	 * @return value of {@link #clazz}
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	/**
	 * Retrieves {@link #fieldName} from an API response
	 *
	 * @return value of {@link #fieldName} from an API response
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * Extracts paths from the given JSON root node according to the response type.
	 *
	 * @param root the JSON root node
	 * @return the JSON node extracted based on the response type
	 */
	public JsonNode getPaths(JsonNode root) {
		switch (this) {
			case PROFILE:
				return root.path(ApiConstant.DATA_FIELD).path(this.fieldName);
			default:
				return root;
		}
	}
}
