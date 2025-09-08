
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.bases.BaseProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Organization;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator.OrganizationProperty;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Utility class providing helper methods for monitoring and property mapping.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class MonitoringUtil {
	private static final Log LOGGER = LogFactory.getLog(MonitoringUtil.class);

	private MonitoringUtil() {
		// Prevent instantiation
	}

	/**
	 * Generates a map of property names and their corresponding values.
	 * <p>
	 * Each property name can be optionally prefixed with a group name using a predefined format.
	 * The values are derived using the provided mapping function, with {@link Constant#NOT_AVAILABLE} as a fallback for null results.
	 * </p>
	 *
	 * @param <T> the enum type that extends {@link BaseProperty}
	 * @param properties the array of enum constants to be processed; if null, an empty map is returned
	 * @param groupName optional group name used to prefix each property's name; can be null
	 * @param mapper a function that maps each property to its corresponding string value;
	 * if null or if the result is null, {@link Constant#NOT_AVAILABLE} is used as the value
	 * @return a map where keys are (optionally grouped) property names and values are mapped strings or {@link Constant#NOT_AVAILABLE}
	 */
	public static <T extends Enum<T> & BaseProperty> Map<String, String> generateProperties(T[] properties, String groupName, Function<T, String> mapper) {
		if (properties == null || mapper == null) {
			return Collections.emptyMap();
		}
		return Arrays.stream(properties).collect(Collectors.toMap(
				property -> Objects.isNull(groupName) ? property.getName() : String.format(Constant.PROPERTY_FORMAT, groupName, property.getName()),
				property -> Optional.ofNullable(mapper.apply(property)).orElse(Constant.NOT_AVAILABLE)
		));
	}

	/**
	 * Maps an {@link Organization} instance to a string value based on the given {@link OrganizationProperty}.
	 *
	 * @param organization the organization to extract values from; may be {@code null}
	 * @param property the property to map
	 * @return a string value of the requested property, or {@code null} if the organization
	 * is {@code null} or the property is not supported
	 */
	public static String mapToOrganizationProperty(Organization organization, OrganizationProperty property) {
		if (organization == null) {
			LOGGER.warn("Organization object is null, returning null value");
			return null;
		}

		switch (property) {
			case ID:
				return mapToValue(organization.getId());
			case INVITATION_STATUS:
				return organization.getInvitationStatus().getValue();
			case MEMBERSHIP_STATUS:
				return organization.getMembershipStatus().getValue();
			case NAME:
				return mapToValue(organization.getName());
			case ROLE:
				return organization.getMembershipRole().getValue();
			default:
				return null;
		}
	}

	/**
	 * Converts the given value to a String:
	 * <ul>
	 *   <li>Returns the value itself if it is a non-null, non-empty String.</li>
	 *   <li>Returns String representation if value is Boolean or Integer.</li>
	 *   <li>Returns null otherwise.</li>
	 * </ul>
	 *
	 * @param value input value to convert
	 * @return String value or null
	 */
	private static String mapToValue(Object value) {
		if (value instanceof String) {
			String str = (String) value;
			if (str.equals("true") || str.equals("false")) {
				return str;
			}
			return StringUtils.isNotNullOrEmpty(str) ? toTitleCase(str) : null;
		}
		if (value instanceof Boolean || value instanceof Integer) {
			return value.toString();
		}
		return null;
	}

	/**
	 * Capitalizes the first character of the input string.
	 * <p>
	 * If the input is {@code null}, empty, or the literal string {@code "null"}, this method returns {@code null}.
	 * If the input is {@code "true"} or {@code "false"}, the method returns the input unchanged.
	 * Otherwise, it returns the input string with its first character converted to uppercase.
	 * </p>
	 *
	 * @param value the input string to convert
	 * @return a string with the first character capitalized, or {@code null} if the input is invalid
	 */
	private static String toTitleCase(String value) {
		if (StringUtils.isNullOrEmpty(value) || value.equals("null")) {
			return null;
		}
		if (value.equals("true") || value.equals("false")) {
			return value;
		}

		return Character.toUpperCase(value.charAt(0)) + value.substring(1);
	}
}
