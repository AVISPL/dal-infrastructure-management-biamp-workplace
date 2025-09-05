/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Represents an authentication request for refreshing an access token.
 * This request uses the OAuth 2.0 {@code refresh_token} grant type.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class AuthenticationReq {
	private final String grantType;
	private final String clientId;
	private final String refreshToken;

	public AuthenticationReq(String clientId, String refreshToken) {
		this.grantType = "refresh_token";
		this.clientId = clientId;
		this.refreshToken = refreshToken;
	}

	/**
	 * Converts this authentication request into a form-encoded key-value map,
	 * suitable for use in an HTTP POST request to an OAuth 2.0 token endpoint.
	 *
	 * @return a {@link MultiValueMap} containing the request parameters
	 */
	public MultiValueMap<String, String> toFormData() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", this.grantType);
		map.add("client_id", this.clientId);
		map.add("refresh_token", this.refreshToken);

		return map;
	}
}
