package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class AuthenticationReq {
	private final String grantType;
	private final String clientId;
	private final String refreshToken;

	public AuthenticationReq(String clientId, String refreshToken) {
		this.grantType = "refresh_token";
		this.clientId = clientId;
		this.refreshToken = refreshToken;
	}

	public MultiValueMap<String, String> toFormData() {
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("grant_type", this.grantType);
		map.add("client_id", this.clientId);
		map.add("refresh_token", this.refreshToken);

		return map;
	}
}
