package apbiot.core.helper;

import java.util.UUID;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.objects.Tuple;
import discord4j.core.object.component.ActionComponent;

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
	 * Generate a {@link String} id for a {@link ActionComponent}.
	 * <p>
	 * The id is generated using this following model:<br>
	 * <code> Command's id + COMMAND_ID_SEPARATOR + Component's name</code>
	 * 
	 * @param commandID The {@link AbstractCommandInstance}'s uuid
	 * @param componentName The component's name
	 * @return A correct component's id
	 * @since 6.0.0
	 */
	public static String generateComponentID(UUID commandID, String componentName) {
		return StringHelper.shortenUUIDToBase64(commandID) + COMMAND_ID_SEPARATOR + componentName;
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
	 * @deprecated As of release 6.3.0, replaced by {@link #getCommandFromUserInput(String, String)}
	 * @see #getCommandFromUserInput(String, String)
	 */
	public static Tuple<String, Boolean> getCommandFromUserInputOld(String[] userMessage, String botPrefix) {
		if(userMessage.length > 1 && userMessage[0].equals(botPrefix)) { //If the prefix is separated from the command name itself
			return Tuple.of(userMessage[1], true);
		}else if(userMessage[0].contains(botPrefix) && userMessage.length >= 1) {
			return Tuple.of(userMessage[0].substring(1), false);
		}
		
		return Tuple.empty();
	}
	
	/**
	 * Get the command send by the user and its argument(s).<br/>
	 * 
	 * The method will also normalize the input by removing extra spaces and trimming the message.<br/><br/>
	 * For example, if the user sends "   ;   hello   world   ", with a bot prefix of ";", the method will return ("hello world", true).
	 * 
	 * @param userMessage The message sent by the user
	 * @param botPrefix The bot prefix used to identify commands
	 * @return a tuple containing the command and if the command was separate from the prefix
	 * @since 6.3.0
	 */
	public static Tuple<String, Boolean> getCommandFromUserInput(String userMessage, String botPrefix) {
        userMessage = userMessage.strip();

        final StringBuilder sb = new StringBuilder(userMessage.length());
        boolean lastWasSpace = false;
        for (int i = 0; i < userMessage.length(); i++) {
            char c = userMessage.charAt(i);
            if (Character.isWhitespace(c)) {
                if (!lastWasSpace) {
                    sb.append(' ');
                    lastWasSpace = true;
                }
            } else {
                sb.append(c);
                lastWasSpace = false;
            }
        }

        final String[] parts = sb.toString().split(" ", 2);

        if (parts.length > 1 && parts[0].equals(botPrefix)) {
            return Tuple.of(parts[1], true);
        } else if (parts[0].startsWith(botPrefix)) {
            return Tuple.of(parts[0].substring(botPrefix.length()), false);
        }

        return Tuple.empty();
    }
	
}
