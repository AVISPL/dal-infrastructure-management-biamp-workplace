/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.bases.BaseProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Attributes;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Channel;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Device;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Firmware;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Status;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Type;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.profile.Organization;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.DeviceState;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated.FirmwareProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregated.OverviewProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator.GeneralProperty;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.aggregator.OrganizationProperty;
import com.avispl.symphony.dal.util.StringUtils;

/**
 * Utility class providing helper methods for monitoring property.
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
	 * Maps a general aggregator property to its string representation.
	 * <p>Handles various value types. Returns {@link Constant#NOT_AVAILABLE} if the property is null or unsupported.</p>
	 *
	 * @param versionProperties the {@link Properties} object containing version-related data
	 * @param property the {@link GeneralProperty} to map
	 * @return the string representation of the property, or {@link Constant#NOT_AVAILABLE} if unavailable
	 */
	public static String mapToGeneralProperty(Properties versionProperties, GeneralProperty property) {
		if (property == null) {
			LOGGER.warn(String.format(Constant.OBJECT_NULL_WARNING, "VersionProperties"));
			return null;
		}

		switch (property) {
			case ADAPTER_BUILD_DATE:
			case ADAPTER_VERSION:
			case MONITORED_DEVICES_TOTAL:
				return mapToValue(versionProperties.getProperty(property.getProperty()));
			case ADAPTER_UPTIME:
				return mapToUptime(versionProperties.getProperty(property.getProperty()));
			case ADAPTER_UPTIME_MIN:
				return mapToUptimeMin(versionProperties.getProperty(property.getProperty()));
			case LAST_MONITORING_CYCLE_DURATION:
				return mapToMonitoringCycleDuration(versionProperties.getProperty(property.getProperty()));
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_PROPERTY_WARNING, "mapToGeneralProperty()", property));
				return null;
		}
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
			LOGGER.warn(String.format(Constant.OBJECT_NULL_WARNING, "Organization"));
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
				LOGGER.warn(String.format(Constant.UNSUPPORTED_PROPERTY_WARNING, "mapToOrganizationProperty()", property));
				return null;
		}
	}

	/**
	 * Maps a {@link Device} instance to a string value based on the given {@link FirmwareProperty}.
	 *
	 * @param device the device to extract firmware values from; may be {@code null}
	 * @param property the firmware property to map
	 * @return a string value of the requested property, or {@code null} if the device
	 * is {@code null} or the property is not supported
	 */
	public static String mapToFirmwareProperty(Device device, FirmwareProperty property) {
		if (device == null) {
			LOGGER.warn(String.format(Constant.OBJECT_NULL_WARNING, "Device"));
			return null;
		}

		switch (property) {
			case ASSIGNED_FIRMWARE:
				Firmware assignedFirmware = Optional.ofNullable(device.getAssignedFirmware()).orElse(new Firmware());
				return mapToValue(assignedFirmware.getVersion());
			case FIRMWARE:
				Status status = Optional.ofNullable(device.getStatus()).orElse(new Status());
				return mapToValue(status.getFirmware());
			case FIRMWARE_CHANNEL:
				Channel channel = Optional.ofNullable(device.getChannel()).orElse(new Channel());
				return mapToValue(channel.getName());
			case LATEST_FIRMWARE:
				Firmware lastFirmware = Optional.ofNullable(device.getLatestFirmware()).orElse(new Firmware());
				return mapToValue(lastFirmware.getVersion());
			case NEXT_FIRMWARE:
				Firmware nextFirmware = Optional.ofNullable(device.getNextFirmware()).orElse(new Firmware());
				return mapToValue(nextFirmware.getVersion());
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_PROPERTY_WARNING, "mapToFirmwareProperty()", property));
				return null;
		}
	}

	/**
	 * Maps a {@link Device} instance to a string value based on the given {@link OverviewProperty}.
	 *
	 * @param device the device to extract values from; may be {@code null}
	 * @param property the property to map
	 * @return a string value of the requested property, or {@code null} if the device is {@code null}
	 * or the property is not supported
	 */
	public static String mapToOverviewProperty(Device device, OverviewProperty property) {
		if (device == null) {
			LOGGER.warn(String.format(Constant.OBJECT_NULL_WARNING, "Device"));
			return null;
		}

		switch (property) {
			case MODEL:
				return removeAccents(mapToValue(getDeviceAttributes(device).getProductModel()));
			case REBOOT:
				return Util.isDeviceOnline(device.getState()) ? Constant.NOT_AVAILABLE : Constant.REBOOT;
			case STATE:
				DeviceState state = Optional.ofNullable(device.getState()).orElse(DeviceState.NOT_AVAILABLE);
				return state.getValue();
			case TYPE:
				return removeAccents(mapToValue(getDeviceType(device).getName()));
			default:
				LOGGER.warn(String.format(Constant.UNSUPPORTED_PROPERTY_WARNING, "mapToOverviewProperty()", property));
				return null;
		}
	}

	/**
	 * Generates a human-readable name for the given device by combining its type and model.
	 * <p>
	 * The device type and model are converted to title case.
	 * If both values are null or empty, {@link Constant#NOT_AVAILABLE} is returned.
	 * </p>
	 *
	 * @param device the {@link Device} object to generate the name for; must not be {@code null}
	 * @return a string representing the device name in the format "Type Model",
	 * or {@link Constant#NOT_AVAILABLE} if both type and model are missing
	 */
	public static String mapToDeviceName(Device device) {
		String typeName = removeAccents(toTitleCase(getDeviceType(device).getName()));
		String modelName = removeAccents(toTitleCase(getDeviceAttributes(device).getProductModel()));
		String deviceName = Stream.of(typeName, modelName).filter(s -> s != null && !s.isEmpty()).collect(Collectors.joining(Constant.SPACE));

		return deviceName.isEmpty() ? Constant.NOT_AVAILABLE : deviceName;
	}

	/**
	 * Removes all diacritical marks (accents) from the input string.
	 *
	 * @param input the string to normalize
	 * @return the normalized string without accents, or null if input is null
	 */
	public static String removeAccents(String input) {
		if (input == null) {
			return null;
		}
		String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

		return normalized.replaceAll("\\p{M}", "");
	}

	/**
	 * Retrieves the {@link Type} of the given {@link Device}.
	 *
	 * @param device the device from which to retrieve the type; may be {@code null}
	 * @return the device type, or a new {@link Type} if none is defined
	 */
	private static Type getDeviceType(Device device) {
		return Optional.ofNullable(device.getType()).orElse(new Type());
	}

	/**
	 * Retrieves the {@link Attributes} of the given {@link Device}.
	 *
	 * @param device the device from which to retrieve the attributes; may be {@code null}
	 * @return the device attributes, or a new {@link Attributes} if none are defined
	 */
	private static Attributes getDeviceAttributes(Device device) {
		return Optional.ofNullable(device.getAttributes()).orElse(new Attributes());
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

	/**
	 * Returns the elapsed uptime between the current system time and the given timestamp in milliseconds.
	 * <p>
	 * The input timestamp represents the start time in milliseconds (typically from {@link System#currentTimeMillis()}).
	 * The returned string represents the absolute duration in the format:
	 * "X day(s) Y hour(s) Z minute(s) W second(s)", omitting any zero-value units except seconds.
	 *
	 * @param uptime the start time in milliseconds as a string (e.g., "1717581000000")
	 * @return a formatted duration string like "2 day(s) 3 hour(s) 15 minute(s) 42 second(s)", or null if parsing fails
	 */
	private static String mapToUptime(String uptime) {
		try {
			if (StringUtils.isNullOrEmpty(uptime)) {
				return null;
			}

			long uptimeSecond = (System.currentTimeMillis() - Long.parseLong(uptime)) / 1000;
			long seconds = uptimeSecond % 60;
			long minutes = uptimeSecond % 3600 / 60;
			long hours = uptimeSecond % 86400 / 3600;
			long days = uptimeSecond / 86400;
			StringBuilder rs = new StringBuilder();
			if (days > 0) {
				rs.append(days).append(" day(s) ");
			}
			if (hours > 0) {
				rs.append(hours).append(" hour(s) ");
			}
			if (minutes > 0) {
				rs.append(minutes).append(" minute(s) ");
			}
			rs.append(seconds).append(" second(s)");

			return rs.toString().trim();
		} catch (Exception e) {
			LOGGER.error(Constant.MAP_TO_UPTIME_FAILED + uptime, e);
			return null;
		}
	}

	/**
	 * Returns the elapsed uptime in **whole minutes** between the current system time and the given timestamp in milliseconds.
	 * <p>
	 * The input timestamp represents the start time in milliseconds (typically from {@link System#currentTimeMillis()}).
	 * The returned string is the total number of minutes that have elapsed, excluding seconds.
	 *
	 * @param uptime the start time in milliseconds as a string (e.g., "1717581000000")
	 * @return a string representing the total number of elapsed minutes (e.g., "125"), or null if parsing fails
	 */
	private static String mapToUptimeMin(String uptime) {
		try {
			if (StringUtils.isNullOrEmpty(uptime)) {
				return null;
			}

			long uptimeSecond = (System.currentTimeMillis() - Long.parseLong(uptime)) / 1000;
			long minutes = uptimeSecond / 60;

			return String.valueOf(minutes);
		} catch (Exception e) {
			LOGGER.error(Constant.MAP_TO_UPTIME_MIN_FAILED + uptime, e);
			return null;
		}
	}

	/**
	 * Converts a duration in milliseconds to seconds.
	 * If >= 1000ms, returns integer seconds; otherwise, returns a decimal with 2 digits.
	 *
	 * @param value duration in milliseconds as string
	 * @return duration in seconds as string, or {@link Constant#NOT_AVAILABLE} if input is null or empty
	 */
	private static String mapToMonitoringCycleDuration(String value) {
		if (StringUtils.isNullOrEmpty(value)) {
			return Constant.NOT_AVAILABLE;
		}
		long duration = Long.parseLong(value);
		return duration == 0 || duration >= 1000
				? String.valueOf((int) (duration / 1000))
				: String.format("%.2f", Math.round(duration / 1000.0 * 100) / 100.0);
	}
}
