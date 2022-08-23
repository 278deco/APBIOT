package apbiot.core.helper;

import java.text.Normalizer;
import java.util.List;
import java.util.Random;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class StringHelper {
	
	/**
	 * Used to convert a list (in case of the .split(" ")) into a string
	 * @param list - the list which contains all the words
	 * @return the list converted into string
	 */
	public static String listToString(List<String> list, String separator) {
		StringBuilder sb = new StringBuilder();
		for(String str : list) {
			sb.append(str).append(separator);
		}
		if(!list.isEmpty()) sb.setLength(sb.length() - separator.length());
		return sb.toString();
	}
	
	/**
	 * Used to convert an array into a string
	 * @param list - the array which contains all the words
	 * @return the array converted into string
	 */
	public static String listToString(String[] list, String separator) {
		StringBuilder sb = new StringBuilder();
		for(String str : list) {
			sb.append(str).append(separator);
		}
		if(list.length != 0) sb.setLength(sb.length() - separator.length());
		return sb.toString();
	}
	
	/**
	 * Return a random element from a list
	 * @param list - A list of String
	 * @return an element from the list
	 */
	public static String getRandomElement(List<String> list) {
		return list.get(new Random().nextInt(list.size()));
	}
	
	/**
	 * Return a random element from an array
	 * @param array - An array of String
	 * @return an element from the array
	 */
	public static String getRandomElement(String[] array) {
		return array[new Random().nextInt(array.length)];
	}
	
	/**
	 * Used to convert a message containing an unrecognizable mention to an message containing an usable mention 
	 * @param message - the message which contain the mention
	 * @param guild - the guild of the member
	 * @return a string which contain a formatted discord mention
	 */
	public static String getFormattedDiscordMention(String message, Guild guild) {
		String[] messageArray = message.split(" ");
		
		int index = 0;
		
		for(String msg : messageArray) {
			msg = msg.trim();
			if(msg.startsWith("@")) {
				for(User u : guild.getMembers().toIterable()) {
					if(msg.contains(u.getUsername())) {
						messageArray[index] = u.getMention();
					}
				}
			}else if(msg.startsWith("<")) {
				String userM = getFormattedDiscordID(msg);
				messageArray[index] = guild.getMemberById(Snowflake.of(userM)).block().getMention();
				messageArray[index] = messageArray[index].trim();
			}
			
			index+=1;
		}
		
		return listToString(messageArray, " ");
	}
	
	/**
	 * Used to know if an specified message contains a valid discord ID
	 * @param message - the message send by the user
	 * @return if the message contains a valid discord ID
	 * @see discord4j.core.object.util.Snowflake
	 */
	public static boolean isValidUserDiscordID(String message) {
		if(message.isEmpty() || !message.contains("<") && !message.contains(">")) {
			return false;
		}else if(message.contains("&") || !message.contains("@") || message.contains("#")) {
			return false;
		}else {
			String testID = getFormattedDiscordID(message);
			try {
				Long.valueOf(testID);
			}catch(NumberFormatException e) {
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Used to know if an specified message contains a valid discord ID
	 * @param message - the message send by the user
	 * @return if the message contains a valid discord ID
	 * @see discord4j.core.object.util.Snowflake
	 */
	public static boolean isValidChannelDiscordID(String message) {
		if(message.isEmpty() || !message.contains("<") && !message.contains(">")) {
			return false;
		}else if(message.contains("&") || message.contains("@") || !message.contains("#")) {
			return false;
		}else {
			String testID = getFormattedDiscordID(message);
			try {
				Long.valueOf(testID);
			}catch(NumberFormatException e) {
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Used to know if an specified message contains a valid discord ID
	 * @param message - the message send by the user
	 * @return if the message contains a valid discord ID
	 * @see discord4j.core.object.util.Snowflake
	 */
	public static boolean isValidRoleDiscordID(String message) {
		if(message.isEmpty() || !message.contains("<") && !message.contains(">")) {
			return false;
		}else if(!message.contains("&") || message.contains("#")) {
			return false;
		}else {
			String testID = getFormattedDiscordID(message);
			try {
				Long.valueOf(testID);
			}catch(NumberFormatException e) {
				return false;
			}
			return true;
		}
	}
	
	/**
	 * The function will remove unrequired character for getting user's id
	 * @param message - the message send by the user
	 * @return the converted message
	 */
	public static String getFormattedDiscordID(String message) {
		if(message.isEmpty()) return "";
		
		String[] splitMsg = message.split(" ");
		
		if(!splitMsg[0].isEmpty() && splitMsg[0].contains("<") && splitMsg[0].contains(">")) {
			if(splitMsg[0].contains("@&")) splitMsg[0] = splitMsg[0].replace("@&", "");
			if(splitMsg[0].contains("!")) splitMsg[0] = splitMsg[0].replace("!", "");
			if(splitMsg[0].contains("@")) splitMsg[0] = splitMsg[0].replace("@", "");
			if(splitMsg[0].contains("#")) splitMsg[0] = splitMsg[0].replace("#", "");
			splitMsg[0] = splitMsg[0].replace("<", "").replace(">", "");
		}
		
		return listToString(splitMsg, " ");
	}
	
	/**
	 * convert a text containing the ID of a role to a role object
	 * @param text - the text containg the ID
	 * @param guild - the guild where the role is
	 * @return a mono containing the role
	 */
	public static Mono<Role> getRoleFromRawString(String text, Guild guild) {
		if(isValidRoleDiscordID(text)) {
			return guild.getRoleById(Snowflake.of(getFormattedDiscordID(text)));
		}
		
		return null;
	}
	
	/**
	 * @deprecated since 4.0
	 * @see apbiot.core.helper.StringHelper#getParsedDiscordID(String, boolean)
	 */
	public static long getParsedDiscordID(String discordID) {
		discordID = getFormattedDiscordID(discordID);
		long id = -1L;
		try {
			id = Long.valueOf(discordID);
		}catch(NumberFormatException e) {
			return id;
		}
		return id;
	}
	
	/**
	 * The function will return a parsed discord ID
	 * @param discordID - the discord id get from a message
	 * @param formatID - set it to true if the function need to format the ID before parsing it
	 * @return a converted discord id
	 */
	public static long getParsedDiscordID(String discordID, boolean formatID) {
		if(formatID) discordID = getFormattedDiscordID(discordID);
		long id = -1L;
		try {
			id = Long.valueOf(discordID);
		}catch(NumberFormatException e) {
			return id;
		}
		return id;
	}
	
	/**
	 * @deprecated since 4.0
	 * @see apbiot.core.helper.ArgumentHelper#formatCommandArguments(boolean, String)
	 */
	public static void formatCommandArguments() { }
	
	/**
	 * Create a random string ID 
	 * ID Format: !!!!!%-%%%%%!
	 * ! represent letters, % represent numbers
	 *  
	 * @param charNumber - the number of character to be present in the first part of the ID
	 * @param maxNumbers - the maximum of number to be present in the first part of the ID
	 * @param maxIDNumber - the maximum of character to be present in the second part of the ID
	 * @return the constructed string
	 */
	public static String getRandomIDString(int charNumber, int maxNumbers, int maxIDNumber, boolean uppercase) {
		char[] alphabet = uppercase ? "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray() : "abcdefghijklmnopqrstuvwxyz".toCharArray();
		String ret = "";
		
		for(int i = 0; i < charNumber; i++) {
			ret+=alphabet[new Random().nextInt(alphabet.length)];
		}
		
		for(int i = 0; i <= (maxNumbers*2); i++) {
			if(i == maxNumbers) ret+="-";
			else ret+=new Random().nextInt(9);
		}
		
		ret+=alphabet[new Random().nextInt(alphabet.length)];
		
		return ret;
	}
	
	/**
	 * Used to remove all accents, space, and other characters except numbers and letters
	 * @param text - a random string
	 * @return the converted string
	 */
	public static String getRawCharacterString(String text) {
		return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\\W]|_", "");
	}
	
	/**
	 * Used to remove all excess blank spaces
	 * @param text - a random string
	 * @return the converted string
	 */
	public static String deleteBlankSpaceExcess(String text) {
		return text.trim().replaceAll("  +", " ");
	}
	
	/**
	 * Format a string by replacing all _ with blank space and making all letters, after a blank space, upper case 
	 * @param text - the text to format
	 * @return the formatted text
	 */
	public static String formatString(String text) {
		text = text.replace("_", " ");
		String[] letterAdjustement = text.split(" ");
		for(int i = 0; i < letterAdjustement.length; i++) {
			if(!letterAdjustement[i].isEmpty()) letterAdjustement[i] = letterAdjustement[i].substring(0, 1).toUpperCase() + letterAdjustement[i].substring(1);
		}
		
		return listToString(letterAdjustement, " ");
	}
}
