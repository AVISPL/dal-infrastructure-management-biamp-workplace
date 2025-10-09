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
public class Attributes {
	private String productModel;

	public Attributes() {
		//	Default constructor required for JSON deserialization.
	}

	/**
	 * Retrieves {@link #productModel}
	 *
	 * @return value of {@link #productModel}
	 */
	public String getProductModel() {
		return productModel;
	}

	/**
	 * Sets {@link #productModel} value
	 *
	 * @param productModel new value of {@link #productModel}
	 */
	public void setProductModel(String productModel) {
		this.productModel = productModel;
	}
}
