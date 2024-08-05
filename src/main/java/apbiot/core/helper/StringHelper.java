package apbiot.core.helper;

import java.nio.ByteBuffer;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import reactor.core.publisher.Mono;

public class StringHelper {
	
	/**
	 * Used to convert a list (in case of the .split(" ")) into a string
	 * @param list The list which contains all the words
	 * @return the list converted into string
	 * @since 1.0
	 */
	public static String listToString(List<String> list, String separator) {
		final StringBuilder sb = new StringBuilder();
		list.forEach(str -> sb.append(str).append(separator));

		if(!list.isEmpty()) sb.setLength(sb.length() - separator.length());
		return sb.toString();
	}
	
	/**
	 * Used to convert an array into a string
	 * @param list The array which contains all the words
	 * @return the array converted into string
	 * @since 1.0
	 */
	public static String listToString(String[] list, String separator) {
		final StringBuilder sb = new StringBuilder();
		for(String str : list) {
			sb.append(str).append(separator);
		}
		
		if(list.length != 0) sb.setLength(sb.length() - separator.length());
		return sb.toString();
	}
	
	/**
	 * Used to convert a string representation of an array into a list
	 * @param str The string representation of an array
	 * @return a list of string
	 */
	public static List<String> stringToList(String str) {
		if(str == null || str.isEmpty() || str.isBlank() || str.equals("[]")) return Collections.emptyList();
		str = str.replaceAll("[\\[\\]\\\\]", "");
		
		final List<String> splitted = Arrays.asList(str.split("\"\\ *,\\ *\""));
		for (int i = 0; i < splitted.size(); i++) splitted.set(i, splitted.get(i).replaceAll("\"", ""));
		
		return splitted;
	}
	
	/**
	 * Return a random element from a list
	 * @param list A list of String
	 * @return an element from the list
	 * @since 1.0
	 */
	public static Optional<String> getRandomElement(List<String> list, Random random) {
		Objects.requireNonNull(list);
		return list.size() > 0 ? Optional.ofNullable(list.get(random.nextInt(list.size()))) : Optional.empty();
	}
	
	/**
	 * Return a random element from a list
	 * @param list A list of String
	 * @return an element from the list
	 * @since 1.0
	 * @deprecated since 5.0
	 * @see #getRandomElement(List, Random)
	 */
	public static String getRandomElement(List<String> list) {
		Objects.requireNonNull(list);
		return list.size() > 0 ? list.get(new Random().nextInt(list.size())) : null;
	}
	
	/**
	 * Return a random element from an array
	 * @param array An array of String
	 * @return an element from the array
	 */
	public static Optional<String> getRandomElement(String[] array, Random random) {
		Objects.requireNonNull(array);
		return array.length > 0 ? Optional.ofNullable(array[random.nextInt(array.length)]) : Optional.empty();
	}
	
	/**
	 * Return a random element from an array
	 * @param array An array of String
	 * @return an element from the array
	 * @deprecated since 5.0
	 * @see #getRandomElement(String[], Random)
	 */
	public static String getRandomElement(String[] array) {
		Objects.requireNonNull(array);
		return array.length > 0 ? array[new Random().nextInt(array.length)] : null;
	}
	
	/**
	 * Used to convert a message containing an unrecognizable mention to an message containing an usable mention 
	 * @param message The message which contain the mention
	 * @param guild The guild of the member
	 * @return a string which contain a formatted discord mention
	 */
	public static String getFormattedDiscordMention(String message, Guild guild) {
		final String[] messageArray = message.split(" ");
		
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
				final String userM = getFormattedDiscordID(msg);
				messageArray[index] = guild.getMemberById(Snowflake.of(userM)).block().getMention();
				messageArray[index] = messageArray[index].trim();
			}
			
			index+=1;
		}
		
		return listToString(messageArray, " ");
	}
	
	/**
	 * Used to know if an specified message contains a valid discord ID
	 * @param message The message send by the user
	 * @return if the message contains a valid discord ID
	 * @see discord4j.core.object.util.Snowflake
	 * @since 1.0
	 */
	public static boolean isValidUserDiscordID(String message) {
		if(message.isEmpty() || message.isBlank() || (!message.contains("<") && !message.contains(">"))) {
			return false;
		}else if(message.contains("&") || !message.contains("@") || message.contains("#")) {
			return false;
		}else {
			try {
				Long.valueOf(getFormattedDiscordID(message));
			}catch(NumberFormatException e) {
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Used to know if an specified message contains a valid discord ID
	 * @param message The message send by the user
	 * @return if the message contains a valid discord ID
	 * @see discord4j.core.object.util.Snowflake
	 * @since 1.0
	 */
	public static boolean isValidChannelDiscordID(String message) {
		if(message.isEmpty() || message.isBlank() || (!message.contains("<") && !message.contains(">"))) {
			return false;
		}else if(message.contains("&") || message.contains("@") || !message.contains("#")) {
			return false;
		}else {
			try {
				Long.valueOf(getFormattedDiscordID(message));
			}catch(NumberFormatException e) {
				return false;
			}
			return true;
		}
	}
	
	/**
	 * Used to know if an specified message contains a valid discord ID
	 * @param message The message send by the user
	 * @return if the message contains a valid discord ID
	 * @see discord4j.core.object.util.Snowflake
	 * @since 1.0
	 */
	public static boolean isValidRoleDiscordID(String message) {
		if(message.isEmpty() || message.isBlank() || (!message.contains("<") && !message.contains(">"))) {
			return false;
		}else if(!message.contains("&") || message.contains("#")) {
			return false;
		}else {
			try {
				Long.valueOf(getFormattedDiscordID(message));
			}catch(NumberFormatException e) {
				return false;
			}
			return true;
		}
	}
	
	/**
	 * The function will remove unrequired character for getting discord's snowflake id
	 * @param message The message send by the user
	 * @return the converted message
	 * @since 1.0
	 */
	public static String getFormattedDiscordID(String message) {
		if(message.isEmpty() || message.isBlank()) return "";
		
		final String[] splitMsg = message.split(" ");
		splitMsg[0] = splitMsg[0].replaceAll("[<>@&#!]", "");
		
		return listToString(splitMsg, " ");
	}
	
	/**
	 * Convert a text containing the ID of a role to a role object
	 * @param text The text containg the ID
	 * @param guild The guild where the role is
	 * @return a mono containing the role
	 * @since 1.0
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
	 * @param discordID The discord id get from a message
	 * @param formatID Set it to true if the function need to format the ID before parsing it
	 * @return a converted discord id
	 * @since 1.0
	 */
	public static long getParsedDiscordID(String discordID, boolean formatID) {
		if(formatID) discordID = getFormattedDiscordID(discordID);
		try {
			return Long.valueOf(discordID);
		}catch(NumberFormatException e) {
			return -1L;
		}
	}
	
	/**
	 * @deprecated since 4.0
	 * @see apbiot.core.helper.ArgumentHelper#formatCommandArguments(boolean, String)
	 */
	public static void formatCommandArguments() { }
	
	/**
	 * Create a random string ID based on the following pattern<br/>
	 * <strong>ID Format:</strong> LLLLLN-NNNNNL<br/>
	 *<i>L represent letters, N represent numbers</i><br/>
	 * @param charNumber The number of character to be present in the first part of the ID
	 * @param maxNumbers The maximum of number to be present in the first part of the ID
	 * @param maxIDNumber The maximum of character to be present in the second part of the ID
	 * @param uppercase Are the letter going to be uppercases or lowercases
	 * @return the newly created ID
	 * @since 2.0
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
	 * Compare two strings together and check their equality 
	 * without taking into account the case and any accents.
	 * <p>
	 * Examples:
     * <blockquote><pre>
     * StringHelper.equalIgnoreCaseAccent("Hello", "heLlO") returns true
     * StringHelper.equalIgnoreCaseAccent("Wôrld", "World") returns true
     * StringHelper.equalIgnoreCaseAccent("Foo,bar!", "Foobar") returns false 
     * </pre></blockquote>
     *
	 * @param str1 The first string to be compared
	 * @param str2 The second string to be compared
	 * @return If the two string are equals 
	 */
	public static boolean equalIgnoreCaseAccent(String str1, String str2) {
		final String normalizedStr1 = Normalizer.isNormalized(str1, Normalizer.Form.NFKD) ?
			Normalizer.normalize(str1, Normalizer.Form.NFKD).replaceAll("\\p{M}", "") : str1;
		
		final String normalizedStr2 = Normalizer.isNormalized(str2, Normalizer.Form.NFKD) ?
				Normalizer.normalize(str2, Normalizer.Form.NFKD).replaceAll("\\p{M}", "") : str2;
		
		return normalizedStr1.equalsIgnoreCase(normalizedStr2);
	}
	
	/**
	 * Used to remove all accents, space, and other characters except numbers and letters
	 * @param text A random string
	 * @return The converted string
	 * @since 2.0
	 */
	public static String getRawCharacterString(String text) {
		return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("[\\W]|_", "");
	}
	
	/**
	 * Used to remove all excess blank spaces
	 * @param text A random string
	 * @return the converted string
	 * @since 2.0
	 */
	public static String deleteBlankSpaceExcess(String text) {
		return text.trim().replaceAll("  +", " ");
	}
	
	/**
	 * Change the first letter of a string to be uppercase
	 * @param text The text to be formatted
	 * @param allLowerCase Make the rest of the string lowercase
	 * @return The formatted string
	 * @since 2.0
	 */
	public static String capitalize(String text, boolean allLowerCase) {
		if(text == null || text == "" || text.isEmpty() || text.isBlank()) return "";
		return text.substring(0,1).toUpperCase() + (allLowerCase ? text.substring(1).toLowerCase() : text.substring(1));
	}
	
	/**
	 * Format a string by replacing all _ with blank space and making all letters, after a blank space, upper case 
	 * @param text The text to be formatted
	 * @return the formatted text
	 * @since 2.0
	 */
	public static String formatString(String text) {
		text = text.replace("_", " ");
		final String[] letterAdjustement = text.split(" ");
		for(int i = 0; i < letterAdjustement.length; i++) {
			if(!letterAdjustement[i].isEmpty()) letterAdjustement[i] = letterAdjustement[i].substring(0, 1).toUpperCase() + letterAdjustement[i].substring(1);
		}
		
		return listToString(letterAdjustement, " ");
	}
	
	public static String shortenUUIDToBase64(UUID uuid) {
		final ByteBuffer buf = ByteBuffer.wrap(new byte[16]);
		buf.putLong(uuid.getMostSignificantBits());
		buf.putLong(uuid.getLeastSignificantBits());
		
		return Base64.getEncoder().encodeToString(buf.array()).replace("=", "");
	}
	
	public static UUID base64ToUUID(String encoded) {
		if(!encoded.endsWith("=")) {
			encoded = encoded + "=="; //	To avoid illegal base64 character exception, add padding to the string			
		}
		
		final ByteBuffer buf = ByteBuffer.wrap(Base64.getDecoder().decode(encoded));
		return new UUID(buf.getLong(), buf.getLong());
	}
	
	public static Optional<UUID> optionalUUIDFromString(String uuidStr) {
		if(uuidStr == null || uuidStr.isEmpty() || uuidStr.isBlank()) return Optional.empty();
		try {
			return Optional.of(UUID.fromString(uuidStr));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Replace the last occurrence of a string in a text
	 * @param text The text to be formatted
	 * @param regex The regular expression to which this string is to be matched
	 * @param replacement The string to be substituted for the first match
	 * @return The formatted text
	 */
	public static String replaceLast(String text, String regex, String replacement) {
        return text.replaceFirst("(?s)(.*)" + regex, "$1" + replacement);
    }
}
