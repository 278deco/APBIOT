package apbiot.core.helper;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.objects.Tuple;

public class CommandHelper {
	
	public static final String COMMAND_ID_SEPARATOR = "@";
	
	/**
	 * Generate an id for a component using the model:<br> {@code Command's id + COMMAND_ID_SEPARATOR + Component's name}
	 * @param commandInstance The {@link AbstractCommandInstance} which need a component
	 * @param componentName The component's name
	 * @return a correct component's id
	 * @since 3.0
	 */
	public static String generateComponentID(AbstractCommandInstance commandInstance, String componentName) {
		return StringHelper.shortenUUIDToBase64(commandInstance.getID())+COMMAND_ID_SEPARATOR+componentName;
	}
	
	/**
	 * Get the specific id for a component (without the command id)
	 * @param componentID The id received
	 * @return the component id or null if the id wasn't properly written
	 * @since 3.0
	 */
	public static String getComponentID(String componentID) {
		final int separator = componentID.indexOf(COMMAND_ID_SEPARATOR);
		return separator != -1 && componentID.length() > 1 ? componentID.substring(separator+1) : null;
	}
	
	/**
	 * Get the command send by the user and its argument(s)
	 * @param userMessage The message sent by the user
	 * @return a tuple containing the command and if the command was separate from the prefix
	 * @since 3.0
	 */
	public static Tuple<String, Boolean> getCommandFromUserInput(String[] userMessage, String botPrefix) {
		if(userMessage.length > 1 && userMessage[0].equals(botPrefix)) { //If the prefix is separated from the command name itself
			return Tuple.of(userMessage[1], true);
		}else if(userMessage[0].contains(botPrefix) && userMessage.length >= 1) {
			return Tuple.of(userMessage[0].substring(1), false);
		}
		
		return Tuple.empty();
	}
	
}
