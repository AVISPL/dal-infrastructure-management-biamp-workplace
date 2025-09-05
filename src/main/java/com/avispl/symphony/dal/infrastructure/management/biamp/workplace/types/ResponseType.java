/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

import com.fasterxml.jackson.databind.JsonNode;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.Authentication;

/**
 * Defines different response types and their associated model classes.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum ResponseType {
	AUTHENTICATION(Authentication.class);

	private final Class<?> clazz;

	ResponseType(Class<?> clazz) {
		this.clazz = clazz;
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
	 * Extracts paths from the given JSON root node according to the response type.
	 *
	 * @param root the JSON root node
	 * @return the JSON node extracted based on the response type
	 */
	public JsonNode getPaths(JsonNode root) {
		switch (this) {
			default:
				return root;
		}
	}
}
