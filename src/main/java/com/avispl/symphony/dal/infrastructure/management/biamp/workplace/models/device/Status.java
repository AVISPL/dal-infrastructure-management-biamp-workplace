/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * 
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Status {
	private String timestamp;
	private int temperature;
	private String firmware;
	private Object uptime;
	private Object presence;
	private Object cpuUtilization;

	public Status() {
		//	Default constructor required for JSON deserialization.
	}

	/**
	 * Retrieves {@link #timestamp}
	 *
	 * @return value of {@link #timestamp}
	 */
	public String getTimestamp() {
		return timestamp;
	}

	/**
	 * Sets {@link #timestamp} value
	 *
	 * @param timestamp new value of {@link #timestamp}
	 */
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Retrieves {@link #temperature}
	 *
	 * @return value of {@link #temperature}
	 */
	public int getTemperature() {
		return temperature;
	}

	/**
	 * Sets {@link #temperature} value
	 *
	 * @param temperature new value of {@link #temperature}
	 */
	public void setTemperature(int temperature) {
		this.temperature = temperature;
	}

	/**
	 * Retrieves {@link #firmware}
	 *
	 * @return value of {@link #firmware}
	 */
	public String getFirmware() {
		return firmware;
	}

	/**
	 * Sets {@link #firmware} value
	 *
	 * @param firmware new value of {@link #firmware}
	 */
	public void setFirmware(String firmware) {
		this.firmware = firmware;
	}

	/**
	 * Retrieves {@link #uptime}
	 *
	 * @return value of {@link #uptime}
	 */
	public Object getUptime() {
		return uptime;
	}

	/**
	 * Sets {@link #uptime} value
	 *
	 * @param uptime new value of {@link #uptime}
	 */
	public void setUptime(Object uptime) {
		this.uptime = uptime;
	}

	/**
	 * Retrieves {@link #presence}
	 *
	 * @return value of {@link #presence}
	 */
	public Object getPresence() {
		return presence;
	}

	/**
	 * Sets {@link #presence} value
	 *
	 * @param presence new value of {@link #presence}
	 */
	public void setPresence(Object presence) {
		this.presence = presence;
	}

	/**
	 * Retrieves {@link #cpuUtilization}
	 *
	 * @return value of {@link #cpuUtilization}
	 */
	public Object getCpuUtilization() {
		return cpuUtilization;
	}

	/**
	 * Sets {@link #cpuUtilization} value
	 *
	 * @param cpuUtilization new value of {@link #cpuUtilization}
	 */
	public void setCpuUtilization(Object cpuUtilization) {
		this.cpuUtilization = cpuUtilization;
	}
}
