package apbiot.core.utils;

public class References {

	public static final String LIBRARY_VERSION = "5.0";
	public static final String DISCORD_SLASH_PREFIX = "/";
	
	/* 
	 * Check if the environment is production or development. 
	 * If the ENVIRONMENT environment variable is not set to "DEV" or doesn't exist, then it is production.
	 */
	public static final boolean PROD_ENVIRONMENT = !"DEV".equals(System.getenv("ENVIRONMENT"));

}
