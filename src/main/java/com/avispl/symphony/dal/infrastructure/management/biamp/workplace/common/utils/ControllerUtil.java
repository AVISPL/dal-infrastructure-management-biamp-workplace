/*
 * Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.utils;

import java.util.Date;

import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty.Button;
import com.avispl.symphony.dal.infrastructure.management.biamp.workplace.common.constants.Constant;

/**
 * Utility class providing helper methods for controllable property.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public class ControllerUtil {
	private ControllerUtil() {
		// Prevent instantiation
	}

	/**
	 * Generates an {@link AdvancedControllableProperty} of type Button with the specified name, labels, and grace period.
	 *
	 * @param name the name of the button control property
	 * @param label the label to display on the button
	 * @param labelPressed the label to display when the button is pressed
	 * @param gracePeriod the time in milliseconds before the button can be pressed again
	 * @return an {@link AdvancedControllableProperty} configured as a button control
	 */
	public static AdvancedControllableProperty generateControllableButton(String name, String label, String labelPressed, long gracePeriod) {
		Button button = new Button();
		button.setLabel(label);
		button.setLabelPressed(labelPressed);
		button.setGracePeriod(gracePeriod);

		return new AdvancedControllableProperty(name, new Date(), button, Constant.NOT_AVAILABLE);
	}
}
