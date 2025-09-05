/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace;

import java.time.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils.Util;

/**
 * This class implements a data loader that periodically collects settings data
 * from a list of Biamp devices via the {@link BiampWorkplaceDataLoader}.
 * This class is thread-safe with the use of {@code volatile} for key flags.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class BiampWorkplaceDataLoader implements Runnable {
	private static final long POLLING_CYCLE_INTERVAL = Duration.ofMinutes(1).toMillis();
	private static final long RETRIEVE_STATISTICS_TIMEOUT = Duration.ofMinutes(3).toMillis();

	private final Log logger = LogFactory.getLog(this.getClass());
	private final BiampWorkplaceCommunicator communicator;

	private volatile boolean inProgress;
	private volatile boolean devicePaused;
	private volatile long validRetrieveStatisticsTimestamp;
	private volatile boolean flag;
	private volatile long nextCollectionTime;

	public BiampWorkplaceDataLoader(BiampWorkplaceCommunicator communicator) {
		this.communicator = communicator;

		this.inProgress = true;
		this.devicePaused = true;
		this.nextCollectionTime = System.currentTimeMillis();
		this.flag = false;
	}

	/**
	 * Sets {@link #nextCollectionTime} value
	 *
	 * @param nextCollectionTime new value of {@link #nextCollectionTime}
	 */
	public void setNextCollectionTime(long nextCollectionTime) {
		this.nextCollectionTime = nextCollectionTime;
	}

	@Override
	public void run() {
		while (this.inProgress) {
			long startCycle = System.currentTimeMillis();
			Util.delayExecution(500);
			if (!this.inProgress) {
				this.logger.debug("Main data collection thread is not in progress, breaking.");
				break;
			}
			this.updateAggregatorStatus();
			if (this.devicePaused) {
				this.logger.debug("The device communicator is paused, data collector is not active.");
				continue;
			}

			long currentTimestamp = System.currentTimeMillis();
			if (!this.flag && this.nextCollectionTime < currentTimestamp) {
				this.collectAggregatedDeviceData();
				this.flag = true;
			}

			if (!this.inProgress) {
				this.logger.debug("Main data collection thread is not in progress, breaking.");
				break;
			}
			while (this.nextCollectionTime > System.currentTimeMillis()) {
				Util.delayExecution(1000);
			}
			if (this.flag) {
				this.nextCollectionTime = System.currentTimeMillis() + POLLING_CYCLE_INTERVAL;
				this.communicator.setLastMonitoringCycleDuration(System.currentTimeMillis() - startCycle);
				this.flag = false;
			}
		}
	}

	/**
	 * Stops the data collection process.
	 */
	public void stop() {
		this.inProgress = false;
	}

	/**
	 * Updates the {@code validRetrieveStatisticsTimestamp}.
	 */
	public synchronized void updateValidRetrieveStatisticsTimestamp() {
		this.validRetrieveStatisticsTimestamp = System.currentTimeMillis() + RETRIEVE_STATISTICS_TIMEOUT;
		this.updateAggregatorStatus();
	}

	/**
	 * Collects and updates settings data for all registered devices.
	 */
	private void collectAggregatedDeviceData() {

	}

	/**
	 * Updates the aggregator status based on the current timestamp.
	 */
	private synchronized void updateAggregatorStatus() {
		this.devicePaused = this.validRetrieveStatisticsTimestamp > 0L && this.validRetrieveStatisticsTimestamp < System.currentTimeMillis();
	}
}
