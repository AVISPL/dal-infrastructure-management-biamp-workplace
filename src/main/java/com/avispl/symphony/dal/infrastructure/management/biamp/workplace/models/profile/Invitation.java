package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Invitation {
	private String id;
	private String orgId;
	private String email;
	private String status;

	public Invitation() {
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
	 * Retrieves {@link #status}
	 *
	 * @return value of {@link #status}
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * Sets {@link #status} value
	 *
	 * @param status new value of {@link #status}
	 */
	public void setStatus(String status) {
		this.status = status;
	}
}
