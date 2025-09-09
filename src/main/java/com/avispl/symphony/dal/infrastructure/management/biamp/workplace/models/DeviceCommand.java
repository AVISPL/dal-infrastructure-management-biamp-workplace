package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents the response of a GraphQL device command mutation.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeviceCommand {
	boolean success;
	String errorMessage;

	public DeviceCommand() {
		//	Default constructor required for JSON deserialization.
	}

	/**
	 * Retrieves {@link #success}
	 *
	 * @return value of {@link #success}
	 */
	public boolean isSuccess() {
		return success;
	}

	/**
	 * Sets {@link #success} value
	 *
	 * @param success new value of {@link #success}
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}

	/**
	 * Retrieves {@link #errorMessage}
	 *
	 * @return value of {@link #errorMessage}
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * Sets {@link #errorMessage} value
	 *
	 * @param errorMessage new value of {@link #errorMessage}
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}
