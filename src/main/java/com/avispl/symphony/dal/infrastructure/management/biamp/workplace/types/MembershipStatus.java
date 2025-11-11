package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;

/**
 *
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum MembershipStatus {
	NOT_AVAILABLE(Constant.NOT_AVAILABLE),
	REQUESTED("Requested"),
	INVITED("Invited"),
	ACTIVE("Active"),
	DEACTIVATED("Deactivated");

	private final String value;

	MembershipStatus(String value) {
		this.value = value;
	}

	/**
	 * Retrieves {@link #value}
	 *
	 * @return value of {@link #value}
	 */
	public String getValue() {
		return value;
	}
}
