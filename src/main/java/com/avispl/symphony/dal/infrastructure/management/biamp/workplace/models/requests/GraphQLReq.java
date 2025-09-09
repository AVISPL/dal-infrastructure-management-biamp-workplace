package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.requests;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.avispl.symphony.api.common.error.InvalidArgumentException;
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

	/**
	 * Creates a {@link GraphQLReq} instance for fetching device information by organization IDs.
	 * <p>
	 * This method loads the GraphQL query from the <code>allDevices.graphql</code> file,
	 * and initializes the request with query variables including:
	 * <ul>
	 *   <li>{@code limit}: fixed to 200</li>
	 *   <li>{@code offset}: fixed to 0</li>
	 *   <li>{@code filter}: a filter containing the provided organization IDs</li>
	 * </ul>
	 * </p>
	 *
	 * @param organizationIds the list of organization IDs to filter devices by;
	 * must not be {@code null} or empty
	 * @return a {@link GraphQLReq} containing the devices query and variables
	 * @throws InvalidArgumentException if {@code organizationIds} is {@code null} or empty
	 */
	public static GraphQLReq getDevices(String... organizationIds) {
		String query = Util.readQueryFromGraphQLFile(ResponseType.DEVICES.getFieldName());
		Map<String, Object> variables = new HashMap<>();
		variables.put("limit", 200);
		variables.put("offset", 0);
		variables.put("filter", Collections.singletonMap("organizations", Arrays.asList(organizationIds)));

		return new GraphQLReq(query, variables);
	}

	/**
	 * Creates a {@link GraphQLReq} instance for fetching the next firmware information
	 * of a device by its ID, current firmware version, and public key.
	 * <p>
	 * This method loads the GraphQL query from the <code>nextFirmware.graphql</code> file
	 * and initializes the request with query variables including:
	 * <ul>
	 *   <li>{@code deviceId}: the ID of the target device</li>
	 *   <li>{@code fwrev}: the current firmware version of the device</li>
	 *   <li>{@code pubkey}: the public key associated with the device</li>
	 * </ul>
	 * </p>
	 *
	 * @param deviceId the ID of the device
	 * @param firmwareVersion the current firmware version of the device
	 * @param publicKey the public key of the device
	 * @return a {@link GraphQLReq} containing the next firmware query and variables
	 */
	public static GraphQLReq getNextFirmware(String deviceId, String firmwareVersion, String publicKey) {
		String query = Util.readQueryFromGraphQLFile(ResponseType.NEXT_FIRMWARE.getFieldName());
		Map<String, Object> variables = new HashMap<>();
		variables.put("deviceId", deviceId);
		variables.put("fwrev", firmwareVersion);
		variables.put("pubkey", publicKey);

		return new GraphQLReq(query, variables);
	}

	/**
	 * Creates a {@link GraphQLReq} instance for rebooting a device within an organization.
	 * <p>
	 * This method loads the GraphQL query from the <code>rebootDevice.graphql</code> file
	 * and initializes the request with query variables including:
	 * <ul>
	 *   <li>{@code orgId}: the ID of the organization containing the device</li>
	 *   <li>{@code deviceId}: the ID of the device to reboot</li>
	 * </ul>
	 * </p>
	 *
	 * @param orgId the ID of the organization
	 * @param deviceId the ID of the device to reboot
	 * @return a {@link GraphQLReq} containing the reboot device query and variables
	 */
	public static GraphQLReq rebootDevice(String orgId, String deviceId) {
		String query = Util.readQueryFromGraphQLFile(ResponseType.REBOOT_DEVICE.getFieldName());
		Map<String, Object> variables = new HashMap<>();
		variables.put("orgId,", orgId);
		variables.put("deviceId", deviceId);

		return new GraphQLReq(query, variables);
	}
}