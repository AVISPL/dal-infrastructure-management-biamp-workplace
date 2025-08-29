package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.avispl.symphony.api.common.error.InvalidArgumentException;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.Util;
import com.avispl.symphony.dal.util.StringUtils;

public class GraphQLReq {
	private String query;
	private Map<String, Object> variables;

	public GraphQLReq() {
	}

	private GraphQLReq(String query, Map<String, Object> variables) {
		this.query = query;
		this.variables = variables;
	}

	/**
	 * Retrieves {@link #query}
	 *
	 * @return value of {@link #query}
	 */
	public String getQuery() {
		return query;
	}

	/**
	 * Sets {@link #query} value
	 *
	 * @param query new value of {@link #query}
	 */
	public void setQuery(String query) {
		this.query = query;
	}

	/**
	 * Retrieves {@link #variables}
	 *
	 * @return value of {@link #variables}
	 */
	public Map<String, Object> getVariables() {
		return variables;
	}

	/**
	 * Sets {@link #variables} value
	 *
	 * @param variables new value of {@link #variables}
	 */
	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public static GraphQLReq getProfile() {
		String query = Util.readQueryFromGraphQLFile("profile");

		return new GraphQLReq(query, null);
	}

	public static GraphQLReq getDevices(List<String> organizationIds) {
		if (CollectionUtils.isEmpty(organizationIds)) {
			throw new InvalidArgumentException("The organizationIds is empty");
		}
		String query = Util.readQueryFromGraphQLFile("allDevices");
		Map<String, Object> variables = new HashMap<>();
		variables.put("limit", 200);
		variables.put("offset", 0);
		variables.put("filter", Collections.singletonMap("organizations", organizationIds));

		return new GraphQLReq(query, variables);
	}

	public static GraphQLReq rebootDevice(String orgId, String deviceId) {
		if (StringUtils.isNullOrEmpty(orgId) || StringUtils.isNullOrEmpty(deviceId)) {
			throw new InvalidArgumentException("The orgId or deviceId is empty");
		}
		String query = Util.readQueryFromGraphQLFile("rebootDevice");
		Map<String, Object> variables = new HashMap<>();
		variables.put("orgId", orgId);
		variables.put("deviceId", deviceId);

		return new GraphQLReq(query, variables);
	}
}
