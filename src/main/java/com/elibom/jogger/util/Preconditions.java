package com.elibom.jogger.util;

/**
 * This class provides helper methods to check preconditions throwing an exception when not met.
 *
 * @author German Escobar
 */
public final class Preconditions {

	/**
	 * Hide constructor.
	 */
	private Preconditions() {}

	/**
	 * Checks that an object is not null.
	 *
	 * @param object the object to be tested
	 * @param message the message for the exception in case the object is null.
	 *
	 * @throws IllegalArgumentException if the object is null.
	 */
	public static void notNull(Object object, String message) throws IllegalArgumentException {
		if (object == null) {
			throw new IllegalArgumentException("A precondition failed: " + message);
		}
	}

	/**
	 * Checks that a string is not null or empty.
	 *
	 * @param value the string to be tested.
	 * @param message the message for the exception in case the string is empty.
	 *
	 * @throws IllegalArgumentException if the string is empty.
	 */
	public static void notEmpty(String value, String message) throws IllegalArgumentException {
		if (value == null || "".equals(value.trim())) {
			throw new IllegalArgumentException("A precondition failed: " + message);
		}
	}
}
