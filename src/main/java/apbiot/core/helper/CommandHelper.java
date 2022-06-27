package apbiot.core.helper;

import java.security.SecureRandom;

public class CommandHelper {
	
	public static final String ID_SEPARATOR = "|";
	
	/**
	 * Generate a random ID used to identified commands
	 * @return a random ID
	 */
	public static String generateRandomID() {
		SecureRandom rdm = new SecureRandom();
		
		String currentMillis = String.valueOf(System.currentTimeMillis()).substring(6);
		
		return rdm.nextInt(20)+""+(char)(rdm.nextInt(26)+'a')+"-"+currentMillis+""+(char)(rdm.nextInt(26)+'a');
	}
	
	/**
	 * Get the specific id for a component (without the command id)
	 * @param componentID - the id received
	 * @return the component id
	 */
	public static String getComponentID(String componentID) {
		int separator = componentID.indexOf("|");
		return separator != -1 && componentID.length() > 1 ? componentID.substring(separator+1) : null;
	}
	
}
