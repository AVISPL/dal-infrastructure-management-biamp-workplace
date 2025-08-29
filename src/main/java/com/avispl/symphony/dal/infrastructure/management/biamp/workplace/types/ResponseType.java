package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

import java.util.List;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.ApiConstant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.Authentication;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Device;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Profile;

public enum ResponseType {
	AUTHENTICATION(Authentication.class, false),
	PROFILE(Profile.class, false),
	DEVICES(Device.class, true);

	private final Class<?> clazz;
	private final boolean isList;

	ResponseType(Class<?> clazz, boolean isList) {
		this.clazz = clazz;
		this.isList = isList;
	}

	/**
	 * Retrieves {@link #clazz}
	 *
	 * @return value of {@link #clazz}
	 */
	public Class<?> getClazz() {
		return clazz;
	}

	public JavaType getTypeRef(ObjectMapper mapper) {
		if (!this.isList) {
			throw new IllegalStateException("This instance is not used for a list response");
		}
		return mapper.getTypeFactory().constructCollectionType(List.class, this.clazz);
	}

	/**
	 * Retrieves {@link #isList}
	 *
	 * @return value of {@link #isList}
	 */
	public boolean isList() {
		return isList;
	}

	public JsonNode getPaths(JsonNode root) {
		switch (this) {
			case PROFILE:
				return root.path(ApiConstant.DATA_FIELD).path("profile");
			case DEVICES:
				return root.path(ApiConstant.DATA_FIELD).path("allDevices").path("devices");
			default:
				return root;
		}
	}
}
