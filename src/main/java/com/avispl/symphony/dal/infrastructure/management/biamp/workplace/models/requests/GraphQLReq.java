package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests;

import java.util.Map;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils.Util;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.ResponseType;

/**
 * Represents a GraphQL request payload.
 * Contains the GraphQL query string and optional variables map used to execute the request against a GraphQL endpoint.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
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

	/**
	 * Creates a {@link GraphQLReq} instance for fetching profile information.
	 * The method loads the GraphQL query from the <code>profile.graphql</code> file
	 * and initializes a request without variables.
	 *
	 * @return a {@link GraphQLReq} containing the profile query
	 */
	public static GraphQLReq getProfile() {
		String query = Util.readQueryFromGraphQLFile(ResponseType.PROFILE.getFieldName());

		return new GraphQLReq(query, null);
	}
}