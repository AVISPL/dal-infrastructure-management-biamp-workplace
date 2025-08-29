package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Organization {
	private String id;
	private String domain;
	private String name;

	public Organization() {
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
}
