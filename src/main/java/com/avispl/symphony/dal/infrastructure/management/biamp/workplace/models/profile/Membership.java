/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.MembershipRole;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.MembershipStatus;

/**
 * Represents a membership of a user within an organization.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Membership {
	private String id;
	private String userId;
	private MembershipRole role;
	private MembershipStatus status;
	private String orgId;
	private Organization organization;

	public Membership() {
		//	Default constructor required for JSON deserialization.
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
	 * Retrieves {@link #userId}
	 *
	 * @return value of {@link #userId}
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets {@link #userId} value
	 *
	 * @param userId new value of {@link #userId}
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Retrieves {@link #role}
	 *
	 * @return value of {@link #role}
	 */
	public MembershipRole getRole() {
		return role;
	}

	/**
	 * Sets {@link #role} value
	 *
	 * @param role new value of {@link #role}
	 */
	public void setRole(MembershipRole role) {
		this.role = role;
	}

	/**
	 * Retrieves {@link #status}
	 *
	 * @return value of {@link #status}
	 */
	public MembershipStatus getStatus() {
		return status;
	}

	/**
	 * Sets {@link #status} value
	 *
	 * @param status new value of {@link #status}
	 */
	public void setStatus(MembershipStatus status) {
		this.status = status;
	}

	/**
	 * Retrieves {@link #orgId}
	 *
	 * @return value of {@link #orgId}
	 */
	public String getOrgId() {
		return orgId;
	}

	/**
	 * Sets {@link #orgId} value
	 *
	 * @param orgId new value of {@link #orgId}
	 */
	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	/**
	 * Retrieves {@link #organization}
	 *
	 * @return value of {@link #organization}
	 */
	public Organization getOrganization() {
		return organization;
	}

	/**
	 * Sets {@link #organization} value
	 *
	 * @param organization new value of {@link #organization}
	 */
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
}
