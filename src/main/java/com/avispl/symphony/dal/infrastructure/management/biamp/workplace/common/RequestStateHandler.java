/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.security.auth.login.FailedLoginException;

import com.avispl.symphony.api.dal.error.CommandFailureException;
import com.avispl.symphony.api.dal.error.ResourceNotReachableException;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;

/**
 * Handler is responsible for storing and processing api errors reported by the aggregator.
 * Whenever an important part of the API fails, aggregator should call {@link #pushError(String, Throwable)},
 * when the error is resolved - {@link #resolveError(String)}
 *
 * Then, {@link #verifyRequestState()} is called after the data processing, and if there are errors - the RuntimeException is thrown
 * with the details about the failed API sections and top error cause.
 *
 * @author Kevin/Symphony Team
 * @since 1.0.0
 */
public class RequestStateHandler {
	/**
	 * Map of api sections and corresponding instances of {@link Throwable}
	 */
	private final Map<String, Throwable> apiErrors = new ConcurrentHashMap<>();
	private final Set<String> sentRequests = ConcurrentHashMap.newKeySet();

	public void pushRequest(String endpoint) {
		this.sentRequests.add(endpoint);
	}

	public void clearRequests() {
		this.sentRequests.clear();
	}

	/**
	 * Handles and records API errors by either rethrowing specific exceptions or storing them in {@link #apiErrors}.
	 * <p>
	 * If the given {@code error} is an instance of:
	 * <ul>
	 *   <li>{@link ResourceNotReachableException} – it is rethrown as a new {@code ResourceNotReachableException}.</li>
	 *   <li>{@link FailedLoginException} – it is rethrown as a new {@code FailedLoginException}.</li>
	 *   <li>{@link CommandFailureException} – if it contains {@link Constant#REFRESH_TOKEN_INVALID_MESSAGE}, it is rethrown as a {@code FailedLoginException}.</li>
	 * </ul>
	 * Otherwise, the error is added to {@link #apiErrors} under the given {@code apiSection}.
	 *
	 * @param apiSection the identifier of the API section (property group) where the error occurred
	 * @param error the exception instance to handle
	 * @throws ResourceNotReachableException if the error is a {@code ResourceNotReachableException}
	 * @throws FailedLoginException if the error is a {@code FailedLoginException} or an invalid refresh token case
	 */
	public void pushError(String apiSection, Exception error) throws ResourceNotReachableException, FailedLoginException {
		if (error instanceof ResourceNotReachableException) {
			throw new ResourceNotReachableException(error.getMessage(), error);
		}
		boolean invalidRefreshToken = error instanceof CommandFailureException && error.getMessage().contains(Constant.REFRESH_TOKEN_INVALID_MESSAGE);
		if (error instanceof FailedLoginException || invalidRefreshToken) {
			throw new FailedLoginException(error.getMessage());
		}

		this.apiErrors.put(apiSection, error);
	}

	/**
	 * Remove an error from {@link #apiErrors}
	 *
	 * @param apiSection API section name to remove from {@link #apiErrors}
	 */
	public void resolveError(String apiSection) {
		this.apiErrors.remove(apiSection);
	}

	/**
	 * Checks {@link #apiErrors} and throws an exception if all requests failed.
	 *
	 * @throws ResourceNotReachableException if {@link #apiErrors} is not empty and matches sent requests
	 */
	public void verifyRequestState() {
		if (this.apiErrors.isEmpty() || this.apiErrors.size() != this.sentRequests.size()) {
			return;
		}

		String apiSections = String.join(Constant.COMMA, this.apiErrors.keySet());
		Throwable error = this.apiErrors.values().iterator().next();
		String errorText = error != null ? error.getMessage() : Constant.NOT_AVAILABLE;
		throw new ResourceNotReachableException(String.format(Constant.REQUEST_APIS_FAILED, apiSections, errorText));
	}
}