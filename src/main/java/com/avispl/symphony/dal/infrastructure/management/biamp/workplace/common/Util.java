package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;

public class Util {
	private static final Log LOGGER = LogFactory.getLog(Util.class);
	private static final URL RESOURCE_URL = Util.class.getClassLoader().getResource("graphql");

	private Util() {
	}

	public static String readQueryFromGraphQLFile(String fileName) {
		try {
			if (RESOURCE_URL == null) {
				throw new FileNotFoundException("Can not find the GraphQL folder");
			}

			String verifiedFileName = fileName.endsWith(Constant.GRAPHQL_EXTENSION) ? fileName : fileName + Constant.GRAPHQL_EXTENSION;
			String resourcePath = Paths.get(RESOURCE_URL.toURI()).toAbsolutePath().toString();
			String filePath = MessageFormat.format("{0}\\{1}", resourcePath, verifiedFileName);

			return new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (Exception e) {
			LOGGER.error("Can not read query from graphql file: " + fileName, e);
			return null;
		}
	}
}
