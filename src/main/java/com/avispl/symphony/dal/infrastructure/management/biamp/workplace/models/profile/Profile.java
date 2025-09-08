/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a user profile.
 * A profile contains basic user information along with memberships and invitations related to organizations.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile {
	private String id;
	private String name;
	private String email;
	private List<Membership> memberships;
	private List<Invitation> invitations;

	public Profile() {
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
	 * Retrieves {@link #email}
	 *
	 * @return value of {@link #email}
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets {@link #email} value
	 *
	 * @param email new value of {@link #email}
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Retrieves {@link #memberships}
	 *
	 * @return value of {@link #memberships}
	 */
	public List<Membership> getMemberships() {
		return memberships;
	}

	/**
	 * Sets {@link #memberships} value
	 *
	 * @param memberships new value of {@link #memberships}
	 */
	public void setMemberships(List<Membership> memberships) {
		this.memberships = memberships;
	}

	/**
	 * Retrieves {@link #invitations}
	 *
	 * @return value of {@link #invitations}
	 */
	public List<Invitation> getInvitations() {
		return invitations;
	}

	/**
	 * Sets {@link #invitations} value
	 *
	 * @param invitations new value of {@link #invitations}
	 */
	public void setInvitations(List<Invitation> invitations) {
		this.invitations = invitations;
	}
}
