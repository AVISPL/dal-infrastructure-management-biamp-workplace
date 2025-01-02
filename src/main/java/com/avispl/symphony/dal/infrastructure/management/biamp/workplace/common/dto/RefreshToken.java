/*
 *  Copyright (c) 2024 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a RefreshToken object used for handling OAuth2 authentication tokens.
 * This class is deserialized from a JSON response containing token details.
 *
 * @author Harry / Symphony Dev Team<br>
 * Created on 24/12/2024
 * @since 1.0.0
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RefreshToken {

	/**
	 * The access token used for authenticated API requests.
	 */
	@JsonProperty("access_token")
	private String accessToken;

	/**
	 * The type of token, typically "Bearer".
	 */
	@JsonProperty("token_type")
	private String tokenType;

	/**
	 * The refresh token used to obtain new access tokens.
	 */
	@JsonProperty("refresh_token")
	private String refreshToken;

	/**
	 * The number of seconds until the access token expires.
	 */
	@JsonProperty("expires_in")
	private int expiresIn;

	/**
	 * The ID token, often used in OpenID Connect for identity information.
	 */
	@JsonProperty("id_token")
	private String idToken;

	/**
	 * Gets the access token.
	 *
	 * @return the access token.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Sets the access token.
	 *
	 * @param accessToken the access token to set.
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Gets the token type.
	 *
	 * @return the token type.
	 */
	public String getTokenType() {
		return tokenType;
	}

	/**
	 * Sets the token type.
	 *
	 * @param tokenType the token type to set.
	 */
	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	/**
	 * Gets the refresh token.
	 *
	 * @return the refresh token.
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	/**
	 * Sets the refresh token.
	 *
	 * @param refreshToken the refresh token to set.
	 */
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	/**
	 * Gets the number of seconds until the access token expires.
	 *
	 * @return the expiration time in seconds.
	 */
	public int getExpiresIn() {
		return expiresIn;
	}

	/**
	 * Sets the number of seconds until the access token expires.
	 *
	 * @param expiresIn the expiration time in seconds to set.
	 */
	public void setExpiresIn(int expiresIn) {
		this.expiresIn = expiresIn;
	}

	/**
	 * Gets the ID token.
	 *
	 * @return the ID token.
	 */
	public String getIdToken() {
		return idToken;
	}

	/**
	 * Sets the ID token.
	 *
	 * @param idToken the ID token to set.
	 */
	public void setIdToken(String idToken) {
		this.idToken = idToken;
	}

	@Override
	public String toString() {
		return "TokenResponseDTO{" +
				"accessToken='" + accessToken + '\'' +
				", tokenType='" + tokenType + '\'' +
				", refreshToken='" + refreshToken + '\'' +
				", expiresIn=" + expiresIn +
				", idToken='" + idToken + '\'' +
				'}';
	}
}
