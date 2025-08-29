package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Authentication {
	private String clientId;
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
	 * Retrieves {@link #clientId}
	 *
	 * @return value of {@link #clientId}
	 */
	public String getClientId() {
		return clientId;
	}

	/**
	 * Sets {@link #clientId} value
	 *
	 * @param clientId new value of {@link #clientId}
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
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

	public boolean isInvalid() {
		boolean isAuthenticated = this.clientId != null && this.accessToken != null && this.refreshToken != null;
		boolean isActivated = this.issuedAt != null && this.expiresIn != null
				&& (this.issuedAt + this.expiresIn * 1000) > System.currentTimeMillis();

		return !isAuthenticated || !isActivated;
	}
}
