/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents an OAuth2 authentication response containing access and refresh tokens.
 * <p>
 * This class is mapped from a JSON response (using Jackson annotations) and
 * includes token metadata such as issue time and expiration duration.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Authentication {
	@JsonProperty("access_token")
	private String accessToken;
	@JsonProperty("refresh_token")
	private String refreshToken;
	private Long issuedAt;
	@JsonProperty("expires_in")
	private Long expiresIn;

	public Authentication() {
		this.issuedAt = System.currentTimeMillis();
	}

	/**
	 * Retrieves {@link #accessToken}
	 *
	 * @return value of {@link #accessToken}
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Sets {@link #accessToken} value
	 *
	 * @param accessToken new value of {@link #accessToken}
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Retrieves {@link #refreshToken}
	 *
	 * @return value of {@link #refreshToken}
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * Sets {@link #refreshToken} value
	 *
	 * @param refreshToken new value of {@link #refreshToken}
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Retrieves {@link #issuedAt}
	 *
	 * @return value of {@link #issuedAt}
	 */
	public Long getIssuedAt() {
		return issuedAt;
	}

	/**
	 * Sets {@link #issuedAt} value
	 *
	 * @param issuedAt new value of {@link #issuedAt}
	 */
	public void setIssuedAt(Long issuedAt) {
		this.issuedAt = issuedAt;
	}

	/**
	 * Retrieves {@link #expiresIn}
	 *
	 * @return value of {@link #expiresIn}
	 */
	public Long getExpiresIn() {
		return expiresIn;
	}

	/**
	 * Sets {@link #expiresIn} value
	 *
	 * @param expiresIn new value of {@link #expiresIn}
	 */
	public void setExpiresIn(Long expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * Checks whether this authentication response is invalid.
	 * <p>
	 * An authentication is considered invalid if:
	 * <ul>
	 *   <li>Any of {@link #accessToken}, or {@link #refreshToken} is {@code null}</li>
	 *   <li>{@link #issuedAt} or {@link #expiresIn} is {@code null}</li>
	 *   <li>The token has expired, i.e. {@code issuedAt + expiresIn * 1000 <= current time}</li>
	 * </ul>
	 *
	 * @return {@code true} if the authentication is invalid, otherwise {@code false}
	 */
	public boolean isInvalid() {
		boolean isAuthenticated = this.accessToken != null && this.refreshToken != null;
		boolean isActivated = this.issuedAt != null && this.expiresIn != null
				&& (this.issuedAt + this.expiresIn * 1000) > System.currentTimeMillis();

		return !isAuthenticated || !isActivated;
	}
}
