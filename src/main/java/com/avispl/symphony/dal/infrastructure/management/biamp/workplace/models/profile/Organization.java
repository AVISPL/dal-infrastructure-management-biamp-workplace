/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.InvitationStatus;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.MembershipRole;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.MembershipStatus;

/**
 * Represents an organization entity, used to store the data of organizations.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization {
	private String id;
	private String domain;
	private String name;
	private MembershipRole membershipRole;
	private MembershipStatus membershipStatus;
	private InvitationStatus invitationStatus;

	public Organization() {
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
	 * Retrieves {@link #domain}
	 *
	 * @return value of {@link #domain}
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * Sets {@link #domain} value
	 *
	 * @param domain new value of {@link #domain}
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets {@link #name} value
	 *
	 * @param name new value of {@link #name}
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retrieves {@link #membershipRole}
	 *
	 * @return value of {@link #membershipRole}
	 */
	public MembershipRole getMembershipRole() {
		return membershipRole;
	}

	/**
	 * Sets {@link #membershipRole} value
	 *
	 * @param membershipRole new value of {@link #membershipRole}
	 */
	public void setMembershipRole(MembershipRole membershipRole) {
		this.membershipRole = membershipRole;
	}

	/**
	 * Retrieves {@link #membershipStatus}
	 *
	 * @return value of {@link #membershipStatus}
	 */
	public MembershipStatus getMembershipStatus() {
		return membershipStatus;
	}

	/**
	 * Sets {@link #membershipStatus} value
	 *
	 * @param membershipStatus new value of {@link #membershipStatus}
	 */
	public void setMembershipStatus(MembershipStatus membershipStatus) {
		this.membershipStatus = membershipStatus;
	}

	/**
	 * Retrieves {@link #invitationStatus}
	 *
	 * @return value of {@link #invitationStatus}
	 */
	public InvitationStatus getInvitationStatus() {
		return invitationStatus;
	}

	/**
	 * Sets {@link #invitationStatus} value
	 *
	 * @param invitationStatus new value of {@link #invitationStatus}
	 */
	public void setInvitationStatus(InvitationStatus invitationStatus) {
		this.invitationStatus = invitationStatus;
	}
}
