/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.models.device.Device;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.types.DeviceState;

/**
 * Utility class for this adapter. This class includes helper methods to extract and convert properties.
 * <p>This class is non-instantiable and provides only static utility methods.</p>
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class Util {
	private static final Log LOGGER = LogFactory.getLog(Util.class);

	private Util() {
		// Prevent instantiation
	}

	/**
	 * Reads the contents of a GraphQL query file from the classpath resources.
	 * <p>
	 * The method automatically appends the {@link Constant#GRAPHQL_EXTENSION} extension if missing,
	 * searches under the {@link Constant#GRAPHQL_FOLDER} folder in resources, and returns the file
	 * contents as a UTF-8 string.
	 * </p>
	 * If the file does not exist or an error occurs while reading, an error is logged
	 * and {@code null} is returned.
	 *
	 * @param fileName the query file name, with or without the {@link Constant#GRAPHQL_EXTENSION} extension
	 * @return the file contents as a string, or {@code null} if not found or unreadable
	 */
	public static String readQueryFromGraphQLFile(String fileName) {
		String verifiedFileName = fileName.endsWith(Constant.GRAPHQL_EXTENSION) ? fileName : fileName + Constant.GRAPHQL_EXTENSION;
		String resourcePath = Constant.GRAPHQL_FOLDER + verifiedFileName;

		try (InputStream graphqlInput = Util.class.getClassLoader().getResourceAsStream(resourcePath)) {
			if (graphqlInput == null) {
				throw new FileNotFoundException(Constant.FIND_GRAPHQL_FOLDER_FAILED + resourcePath);
			}
			return new BufferedReader(new InputStreamReader(graphqlInput, StandardCharsets.UTF_8)).lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (Exception e) {
			LOGGER.error(Constant.READ_GRAPHQL_QUERY_FAILED + fileName, e);
			return null;
		}
	}

	/**
	 * Delays execution for a specified duration.
	 *
	 * @param milliseconds The duration in milliseconds.
	 */
	public static void delayExecution(long milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			LOGGER.error(e.getMessage(), e);
		}
	}

	/**
	 * Checks whether a {@link Device} is currently online.
	 *
	 * @param state the {@link DeviceState} of the device, may be {@code null}
	 * @return {@code true} if the state is {@link DeviceState#ONLINE}, otherwise {@code false}
	 */
	public static boolean isDeviceOnline(DeviceState state) {
		return DeviceState.ONLINE.equals(state);
	}
}
