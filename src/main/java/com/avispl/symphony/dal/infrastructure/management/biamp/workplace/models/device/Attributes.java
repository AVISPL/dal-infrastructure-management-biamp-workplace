package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Attributes {
	private String productModel;

	public Attributes() {
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
