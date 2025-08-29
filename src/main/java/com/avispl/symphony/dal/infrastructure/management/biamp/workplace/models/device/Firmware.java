package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Firmware {
	private String id;
	private String version;

	public Firmware() {
	}

	/**
	 * Retrieves {@link #id}
	 *
	 * @return value of {@link #id}
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets {@link #id} value
	 *
	 * @param id new value of {@link #id}
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Retrieves {@link #version}
	 *
	 * @return value of {@link #version}
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets {@link #version} value
	 *
	 * @param version new value of {@link #version}
	 */
	public void setVersion(String version) {
		this.version = version;
	}
}
