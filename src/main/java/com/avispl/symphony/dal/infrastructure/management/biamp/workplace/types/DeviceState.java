package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

public enum DeviceState {
	UNPROVISIONED("Unprovisioned"),
	PROVISIONED("Provisioned"),
	ONLINE("Online"),
	OFFLINE("Offline"),
	MISSING("Missing");

	private final String name;

	DeviceState(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}
}
