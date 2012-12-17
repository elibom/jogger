package org.jogger;

/**
 * Helper class. Provides methods to check the environment in which Jogger is working. Remember that the environment is set
 * using a system (i.e. System.setProperty method) or environment (i.e. System.setenv method) variable.
 * 
 * @author German Escobar
 */
public class Environment {

	/**
	 * Retrieves the environment in which Jogger is working.
	 * 
	 * @return a String object representing the environment.
	 */
	public static String get() {

		String env = System.getProperty("JOGGER_ENV");
		if (env == null) {
			env = System.getenv("JOGGER_ENV");
		}

		if (env == null) {
			return "dev";
		}

		return env;
	}

	/**
	 * Checks if Jogger is working in testing mode.
	 * 
	 * @return true if working in testing mode, false otherwise.
	 */
	public static boolean isTesting() {
		return get().equals("test");
	}

	/**
	 * Checks if Jogger is working in development mode.
	 * 
	 * @return true if working in development mode, false otherwise.
	 */
	public static boolean isDevelopment() {
		return get().equals("dev");
	}

	/**
	 * Checks if Jogger is working in production mode.
	 * 
	 * @return true if working in production mode, false otherwise.
	 */
	public static boolean isProduction() {
		return get().equals("prod");
	}

}
