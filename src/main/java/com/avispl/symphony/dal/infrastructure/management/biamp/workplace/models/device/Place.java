/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 
 * 
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Place {
	private String id;
	private List<Hierarchy> hierarchy;

	public Place() {
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
	 * Retrieves {@link #hierarchy}
	 *
	 * @return value of {@link #hierarchy}
	 */
	public List<Hierarchy> getHierarchy() {
		return hierarchy;
	}

	/**
	 * Sets {@link #hierarchy} value
	 *
	 * @param hierarchy new value of {@link #hierarchy}
	 */
	public void setHierarchy(List<Hierarchy> hierarchy) {
		this.hierarchy = hierarchy;
	}

	/**
 * 
 * 
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
	public static class Hierarchy {
		private String id;
		private String name;

		public Hierarchy() {
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
	}
}
