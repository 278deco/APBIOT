package apbiot.core.helper;

import java.security.SecureRandom;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.objects.Tuple;

public class CommandHelper {
	
	public static final String ID_SEPARATOR = "|";
	
	/**
	 * Generate a random ID used to identified commands
	 * @return a random ID
	 */
	@Deprecated
	public static String generateRandomID() {
		SecureRandom rdm = new SecureRandom();
		
		String currentMillis = String.valueOf(System.currentTimeMillis()).substring(6);
		
		return rdm.nextInt(20)+""+(char)(rdm.nextInt(26)+'a')+"-"+currentMillis+""+(char)(rdm.nextInt(26)+'a');
	}
	
	/**
	 * Generate an id for a component using the model {@code Command's id + ID_SEPARATOR + Component's name}
	 * @param commandInstance The {@link AbstractCommandInstance} which need a component
	 * @param componentName The component's name
	 * @return a correct component's id
	 */
	public static String generateComponentID(AbstractCommandInstance commandInstance, String componentName) {
		return commandInstance.getID()+ID_SEPARATOR+componentName;
	}
	
	/**
	 * Get the specific id for a component (without the command id)
	 * @param componentID The id received
	 * @return the component id
	 */
	public static String getComponentID(String componentID) {
		int separator = componentID.indexOf("|");
		return separator != -1 && componentID.length() > 1 ? componentID.substring(separator+1) : null;
	}
	
	/**
	 * Get the command send by the user and the argument(s)
	 * @param userMessage The message sent by the user
	 * @return a tuple containing the command and if the command was separate from the prefix
	 */
	public static Tuple<String, Boolean> getCommandFromUserInput(String[] userMessage, String botPrefix) {
		if(userMessage.length > 1 && userMessage[0].equals(botPrefix)) {
			return Tuple.of(userMessage[1], true);
		}else if(userMessage[0].contains(botPrefix) && userMessage.length >= 1) {
			return Tuple.of(userMessage[0].substring(1), false);
		}
		return Tuple.empty();
	}
	
}
