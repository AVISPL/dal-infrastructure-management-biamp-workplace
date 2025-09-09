/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.ApiConstant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.Authentication;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.DeviceCommand;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Device;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Firmware;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Profile;

/**
 * Defines different response types and their associated model classes.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum ResponseType {
	AUTHENTICATION(Authentication.class, null),
	PROFILE(Profile.class, "profile"),
	DEVICES(Device.class, "devices"),
	NEXT_FIRMWARE(Firmware.class, "nextFirmware"),
	REBOOT_DEVICE(DeviceCommand.class, "deviceCommand");

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
	 * Determines whether the response type represents a collection of items.
	 *
	 * @return {@code true} if this response type is a collection, {@code false} otherwise
	 */
	public boolean isCollection() {
		return this.equals(DEVICES);
	}

	/**
	 * Checks whether this response type represents a controller operation.
	 *
	 * @return {@code true} if this response type is a controller, {@code false} otherwise
	 */
	public boolean isController() {
		return this.equals(REBOOT_DEVICE);
	}

	/**
	 * Returns the Jackson {@link JavaType} representing a collection of the target class.
	 * <p>
	 * This method is intended for enum constants that represent list responses.
	 * If the current instance does not represent a collection, an {@link IllegalStateException} is thrown.
	 * </p>
	 *
	 * @param mapper the {@link ObjectMapper} used to construct the type reference
	 * @return a {@link JavaType} representing a {@link List} of the target class
	 * @throws IllegalStateException if this instance does not represent a collection response
	 */
	public JavaType getTypeRef(ObjectMapper mapper) {
		if (!this.isCollection()) {
			throw new IllegalStateException("This instance is not marked as a collection type");
		}
		return mapper.getTypeFactory().constructCollectionType(List.class, this.clazz);
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
			case DEVICES:
				return root.path(ApiConstant.DATA_FIELD).path("allDevices").path(this.fieldName);
			case NEXT_FIRMWARE:
				return root.path(ApiConstant.DATA_FIELD).path("device").path(this.fieldName);
			case REBOOT_DEVICE:
				return root.path(ApiConstant.DATA_FIELD);
			default:
				return root;
		}
	}
}
