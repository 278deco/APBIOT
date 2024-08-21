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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import apbiot.core.objects.enums.DiscordMentionType;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildChannel;
import reactor.core.publisher.Mono;

public class StringHelper {
	
	public static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
	
	private static final Pattern UUID_V1_PATTERN = Pattern.compile("(?i)^[0-9A-F]{8}-[0-9A-F]{4}-[1][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$");
	private static final Pattern UUID_V4_PATTERN = Pattern.compile("(?i)^[0-9A-F]{8}-[0-9A-F]{4}-[4][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$");
	private static final Pattern UUID_V5_PATTERN = Pattern.compile("(?i)^[0-9A-F]{8}-[0-9A-F]{4}-[5][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$");
	private static final Pattern UUID_V7_PATTERN = Pattern.compile("(?i)^[0-9A-F]{8}-[0-9A-F]{4}-[7][0-9A-F]{3}-[89AB][0-9A-F]{3}-[0-9A-F]{12}$");
	
	private static final Pattern DISCORD_USERNAME_PATTERN = 
			Pattern.compile("^(?=.{2,32}$)(?=(?:(?!discord|@|#|:|```).)*$)(?=(?:(?!\\.{2,}).)*$)(?!(?:everyone|here)$)[a-z0-9_.]+$");
	
	private static final int MINIMUM_SNOWFLAKE_SIZE = 17;
	
	/**
	 * Used to convert a list (in case of the .split(" ")) into a string
	 * 
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
	 * 
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
	 * Returns a random element from a list
	 * 
	 * @implNote This method creates its own {@link Random} generator and 
	 * uses the current time in milliseconds as a seed for the random
	 * @param list A list of String
	 * @return an element from the list
	 * @since 6.0.0
	 */
	public static Optional<String> getRandomElement(List<String> list) {
		return getRandomElement(list, new Random(System.currentTimeMillis()));
	}
	
	/**
	 * Returns a random element from a list
	 * 
	 * @param list A list of String
	 * @return an element from the list
	 * @since 1.0
	 */
	public static Optional<String> getRandomElement(List<String> list, Random random) {
		Objects.requireNonNull(list);
		return list.size() > 0 ? Optional.ofNullable(list.get(random.nextInt(list.size()))) : Optional.empty();
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
	 * @deprecated since 6.0.0
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
	 * Checks if a string is a valid Discord's username.
	 * <p>
	 * The function will check if the username is not null, not empty, not blank and match the pattern of a Discord's username.<br/>
	 * A Discord's username can only contain alphanumeric characters, underscores, and periods.<br/>
	 * The username must also be between 2 and 32 characters long and cannot be 'everyone' or 'here'.
	 * 
	 * @param username The string username to be checked
	 * @return If the username is valid by Discord standards
	 * @see <a href="https://discord.com/developers/docs/resources/user#user-object-username">Discord's username documentation</a>
	 * @see <a href="https://support.discord.com/hc/en-us/articles/12620128861463-New-Usernames-Display-Names#h_01GXPQAGG6W477HSC5SR053QG1">Discord 'New Usernames' article</a>
	 */
	public static final boolean isValidDiscordUsername(String username) {
		if(username == null || username.isBlank()) return false;
		return DISCORD_USERNAME_PATTERN.matcher(username).matches();
	}
	
	/**
	 * Check if the mention is valid for the specified type provided.<br/>
	 * The function will check if the mention is not null, not empty, not blank and match the pattern of the type.<br/>
	 * It will also check if the {@link Snowflake} ID is a valid number.
	 * 
	 * @param type The type of the mention
	 * @param mention The string containing the mention
	 * @return If the mention is valid by Discord standards
	 * @since 6.0.0
	 * @see DiscordMentionType
	 */
	public static final boolean isValidDiscordMention(DiscordMentionType type, String mention) {
		if(mention == null || mention.isBlank()) return false;
		
		final Matcher matcher = type.getPattern().matcher(mention);
		if(!matcher.matches()) return false;
		
		try {
			final String snowflakeId = matcher.group("id");
			
            Long.parseUnsignedLong(snowflakeId);
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * Get the {@link Snowflake} ID from a user's mention string.<br/>
	 * The function will check if the mention is not null, not empty, not blank and
	 * match the pattern before converting the ID.<br/>
	 * 
	 * @param mention The string containing the user's mention
	 * @return An {@link Optional} containing the {@link Snowflake} or an empty {@link Optional} if the mention is invalid
	 * @since 6.0.0
	 * @see #getSnowflakeFromMention(DiscordMentionType, String)
	 */
	public static final Optional<Snowflake> getUserSnowflakeFromMention(String mention) {
		return getSnowflakeFromMention(DiscordMentionType.USER, mention);
	}
	
	/**
	 * Get the {@link Snowflake} ID from a mention string based on the type provided.<br/>
	 * The function will check if the mention is not null, not empty, not blank and match 
	 * the pattern of the type before converting the ID.
	 * 
	 * @param type The type of the mention
	 * @param mention The string containing the mention
	 * @return An {@link Optional} containing the {@link Snowflake} or an empty {@link Optional} if the mention is invalid
	 * @since 6.0.0
	 * @see DiscordMentionType
	 */
	public static final Optional<Snowflake> getSnowflakeFromMention(DiscordMentionType type, String mention) {
		if(mention == null || mention.isBlank()) return Optional.empty();
		
		final Matcher matcher = type.getPattern().matcher(mention);
		if(!matcher.matches()) return Optional.empty();
		
		try {
			final String idStr = matcher.group("id");
			if(idStr.length() < MINIMUM_SNOWFLAKE_SIZE) return Optional.empty();
			
			return Optional.of(Snowflake.of(idStr));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}		
	}
	
	/**
	 * Get the {@link Snowflake} ID from a mention string.<br/>
	 * The function does not check the formatting of the mention, only removes all non-digit characters 
	 * and then convert it, if possible, to a {@link Snowflake}.
	 * 
	 * @param mention The string containing the mention
	 * @return An {@link Optional} containing the {@link Snowflake} or an empty {@link Optional} if the mention is invalid
	 * @since 6.0.0
	 * @see Snowflake
	 */
	public static final Optional<Snowflake> getSnowflakeFromMention(String mention) {
		if(mention == null || mention.isBlank()) return Optional.empty();
		
		try {
			final String idStr = mention.replaceAll("[^\\d]", "");
			if(idStr.length() < MINIMUM_SNOWFLAKE_SIZE) return Optional.empty();

			return Optional.of(Snowflake.of(idStr));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Get an {@link Optional} containing the {@link Mono} of a {@link Member} from a mention string.<br/>
	 * The function will check if the mention is valid and then try to get the member from the {@link Guild}.<br/>
	 * If the mention is invalid or the member doesn't exist in the guild, the optional will be empty.
	 * 
	 * @param mention The string containing the member's mention
	 * @param guild The guild where the member is located
	 * @return An {@link Optional} containing the {@link Mono} of the {@link Member} or an empty {@link Optional} if an error occurs
	 */
	public static final Optional<Mono<Member>> getMemberFromMention(String mention, Guild guild) {
		final Optional<Snowflake> snowflake = getSnowflakeFromMention(DiscordMentionType.USER, mention);
		return snowflake.map(guild::getMemberById);
	}
	
	/**
	 * Get an {@link Optional} containing the {@link Mono} of a {@link User} from a mention string.<br/>
	 * The function will check if the mention is valid and then try to get the user from the {@link GatewayDiscordClient}.<br/>
	 * If the mention is invalid or the user doesn't exist, the optional will be empty.
	 * 
	 * @param mention The string containing the user's mention
	 * @param client The client to get the user from
	 * @return An {@link Optional} containing the {@link Mono} of the {@link User} or an empty {@link Optional} if an error occurs
	 */
	public static final Optional<Mono<User>> getUserFromMention(String mention, GatewayDiscordClient client) {
		final Optional<Snowflake> snowflake = getSnowflakeFromMention(DiscordMentionType.USER, mention);
		return snowflake.map(client::getUserById);
	}
	
	/**
	 * Get an {@link Optional} containing the {@link Mono} of a {@link Role} from a mention string.<br/>
	 * The function will check if the mention is valid and then try to get the role from the {@link Guild}.<br/>
	 * If the mention is invalid or the role doesn't exist in the guild, the optional will be empty.
	 * 
	 * @param mention The string containing the role's mention
	 * @param guild The guild where the role is located
	 * @return An {@link Optional} containing the {@link Mono} of the {@link Role} or an empty {@link Optional} if an error occurs
	 */
	public static final Optional<Mono<Role>> getRoleFromMention(String mention, Guild guild) {
		final Optional<Snowflake> snowflake = getSnowflakeFromMention(DiscordMentionType.ROLE, mention);
		return snowflake.map(guild::getRoleById);
	}
	
	/**
	 * Get an {@link Optional} containing the {@link Mono} of a {@link GuildChannel} from a mention string.<br/>
	 * The function will check if the mention is valid and then try to get the guild channel from the {@link Guild}.<br/>
	 * If the mention is invalid or the guild channel doesn't exist in the guild, the optional will be empty.
	 * 
	 * @param mention The string containing the guild channel's mention
	 * @param guild The guild where the guild channel is located
	 * @return An {@link Optional} containing the {@link Mono} of the {@link GuildChannel} or an empty {@link Optional} if an error occurs
	 */
	public static final Optional<Mono<GuildChannel>> getEntityFromMention(String mention, Guild guild) {
		final Optional<Snowflake> snowflake = getSnowflakeFromMention(DiscordMentionType.CHANNEL, mention);
		return snowflake.map(guild::getChannelById);
	}
	
	/**
	 * Used to know if an specified message contains a valid discord ID
	 * @param message The message send by the user
	 * @return if the message contains a valid discord ID
	 * @see discord4j.core.object.util.Snowflake
	 * @since 1.0
	 * @deprecated since 6.0.0
	 * @see #isValidDiscordMention(DiscordMentionType, String)
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
	 * @deprecated since 6.0.0
	 * @see #isValidDiscordMention(DiscordMentionType, String)
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
	 * @deprecated since 6.0.0
	 * @see #isValidDiscordMention(DiscordMentionType, String)
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
	 * @deprecated since 6.0.0
	 * @see #getSnowflakeFromMention(DiscordMentionType, String)
	 * @see #getSnowflakeFromMention(String)
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
	 * @deprecated since 6.0.0
	 * @see #getRoleFromMention(String, Guild)
	 */
	public static Mono<Role> getRoleFromRawString(String text, Guild guild) {
		if(isValidRoleDiscordID(text)) {
			return guild.getRoleById(Snowflake.of(getFormattedDiscordID(text)));
		}
		
		return null;
	}
	
	/**
	 * The function will return a parsed discord ID
	 * @param discordID The discord id get from a message
	 * @param formatID Set it to true if the function need to format the ID before parsing it
	 * @return a converted discord id
	 * @since 1.0
	 * @deprecated since 6.0.0
	 * @see #getSnowflakeFromMention(String)
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
	 * Create a random string ID with a defined pattern.
	 * <p>
	 * Examples:
	 * <blockquote><pre>
	 * StringHelper.getRandomIDString(3, 4, false)
	 * returns "abc12-34d"
	 * 
	 * StringHelper.getRandomIDString(4, 3, true) 
	 * returns "ABCD1-23DE"
	 * 
	 * StringHelper.getRandomIDString(0, 7, false) 
	 * returns "123-4567"
	 * 
	 * StringHelper.getRandomIDString(5, 0, false) 
	 * returns "abcde-fg"
	 * </pre></blockquote>
	 *
	 * @implNote This method creates its own {@link Random} genrator and 
	 * uses the current time in milliseconds as a seed for the random
	 * @param random The random generator to be used to generate the ID
	 * @param charQuantity The number of character to be present in the first part of the ID
	 * @param numberQuantity The maximum of number to be present in the first part of the ID
	 * @param uppercase Are the letter going to be uppercases or lowercases
	 * @return the newly created ID
	 * @since 2.0
	 * @see #getRandomIDString(Random, int, int, boolean)
	 */
	public static String getRandomIDString(int charQuantity, int numberQuantity, boolean uppercase) {
		return getRandomIDString(new Random(System.currentTimeMillis()), charQuantity, numberQuantity, uppercase);
	}
	
	/**
	 * Create a random string ID with a defined pattern.
	 * <p>
	 * Examples:
	 * <blockquote><pre>
	 * StringHelper.getRandomIDString(new Random(), 3, 4, false)
	 * returns "abc12-34d"
	 * 
	 * StringHelper.getRandomIDString(new Random(), 4, 3, true) 
	 * returns "ABCD1-23DE"
	 * 
	 * StringHelper.getRandomIDString(new Random(), 0, 7, false) 
	 * returns "123-4567"
	 * 
	 * StringHelper.getRandomIDString(new Random(), 5, 0, false) 
	 * returns "abcde-fg"
	 * </pre></blockquote>
	 *
	 * @param random The random generator to be used to generate the ID
	 * @param charQuantity The number of character to be present in the first part of the ID
	 * @param numberQuantity The maximum of number to be present in the first part of the ID
	 * @param uppercase Are the letter going to be uppercases or lowercases
	 * @return the newly created ID
	 * @since 2.0
	 */
	public static String getRandomIDString(Random random, int charQuantity, int numberQuantity, boolean uppercase) {
		final StringBuilder sb = new StringBuilder();
		for(int i = 0; i < charQuantity; i++) {
			final char c = ALPHABET[random.nextInt(ALPHABET.length)];
			sb.append(uppercase ? Character.toUpperCase(c) : c);
		}
		
		for(int i = 0; i <= numberQuantity; i++) {
			sb.append(i == numberQuantity/2 ? "-" : random.nextInt(9));
		}
		if(numberQuantity == 0) sb.append("-");
		
		for(int i = 0; i < charQuantity/2; i++) {
			final char c = ALPHABET[random.nextInt(ALPHABET.length)];
			sb.append(uppercase ? Character.toUpperCase(c) : c);
		}
				
		return sb.toString();
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
			str1 : Normalizer.normalize(str1, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");

		final String normalizedStr2 = Normalizer.isNormalized(str2, Normalizer.Form.NFKD) ?
			str2: Normalizer.normalize(str2, Normalizer.Form.NFKD).replaceAll("\\p{M}", "");
		
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
		if(text == null || text.isBlank()) return "";
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
	
	/**
	 * Used to convert a UUID to a base64 string without padding <br/>
	 * The UUID is converted to a byte array and then encoded to base64
	 * 
	 * @param uuid The UUID to be converted
	 * @return The base64 string
	 * @see #fastUUIDToBase64(UUID)
	 */
	public static String shortenUUIDToBase64(UUID uuid) {
		final ByteBuffer buf = ByteBuffer.wrap(new byte[16]);
		buf.putLong(uuid.getMostSignificantBits());
		buf.putLong(uuid.getLeastSignificantBits());
		
		return Base64.getEncoder().encodeToString(buf.array()).replace("=", "");
	}
	
	/**
	 * Used to convert a base64 string to a UUID <br/>
	 * The string is decoded from base64 and then converted to a byte buffer to get the UUID <br/>
	 * If the string is not padded, it will be padded to avoid illegal base64 character exception
	 * 
	 * @param encoded The base64 string to be converted
	 * @return The UUID converted from the string
	 * @see #fastBase64ToUUID(String)
	 */
	public static UUID base64ToUUID(String encoded) {
		if(!encoded.endsWith("=")) {
			encoded = encoded + "=="; //	To avoid illegal base64 character exception, add padding to the string			
		}
		
		final ByteBuffer buf = ByteBuffer.wrap(Base64.getDecoder().decode(encoded));
		return new UUID(buf.getLong(), buf.getLong());
	}
	
	/**
	 * Convert a string to a UUID <br/>
	 * If the string is not a valid UUID, an empty optional is returned rather than throwing an exception
	 * 
	 * @param uuidStr The string to be converted
	 * @return An {@link Optional} containing the UUID if the string is a valid UUID
	 */
	public static Optional<UUID> optionalUUIDFromString(String uuidStr) {
		if(uuidStr == null || uuidStr.isEmpty() || uuidStr.isBlank()) return Optional.empty();
		try {
			return Optional.of(UUID.fromString(uuidStr));
		} catch (IllegalArgumentException e) {
			return Optional.empty();
		}
	}
	
	/**
	 * Convert an {@link UUID} to its base64 string representation without padding <br/>
	 * This method works like {@link #shortenUUIDToBase64(UUID)} but is faster by not using a {@link ByteBuffer} <br/>
	 * The UUID is converted to a byte array and then encoded to base64 removing the padding at the end of the string
	 * 
	 * @param uuid The UUID to be shortened
	 * @return The shortened base64 string
	 */
	public static String fastUUIDToBase64(UUID uuid) {
		final byte[] buffer = new byte[16];
	
		final long most = uuid.getMostSignificantBits();
		buffer[0] = (byte)(most >>> 56); buffer[1] = (byte)(most >>> 48);
		buffer[2] = (byte)(most >>> 40); buffer[3] = (byte)(most >>> 32);
		buffer[4] = (byte)(most >>> 24); buffer[5] = (byte)(most >>> 16);
		buffer[6] = (byte)(most >>> 8); buffer[7] = (byte)(most >>> 0);
		
		final long least = uuid.getLeastSignificantBits();
		buffer[8] = (byte)(least >>> 56); buffer[9] = (byte)(least >>> 48);
		buffer[10] = (byte)(least >>> 40); buffer[11] = (byte)(least >>> 32);
		buffer[12] = (byte)(least >>> 24); buffer[13] = (byte)(least >>> 16);
		buffer[14] = (byte)(least >>> 8); buffer[15] = (byte)(least >>> 0);

		final byte[] encoded = Base64.getEncoder().encode(buffer);
		return new String(encoded, 0, encoded.length-2); //Remove the padding at the end of the string
	}
	
	/**
	 * Convert a base64 string to a {@link UUID} <br/>
	 * This method works like {@link #base64ToUUID(String)} but is faster by not using a {@link ByteBuffer} <br/>
	 * The string is decoded from base64 and then converted to a {@link UUID} by reading the byte array
	 * 
	 * @param encoded The base64 string to be converted
	 * @return The {@link UUID} converted from the string
	 */
	public static UUID fastBase64ToUUID(String encoded) {
		final byte[] buffer = Base64.getDecoder().decode(encoded); //The decode can do its work without padding
				
		final long most = ((long)buffer[0] << 56) + ((long)(buffer[1] & 255) << 48)
				+ ((long)(buffer[2] & 255) << 40) + ((long)(buffer[3] & 255) << 32)
				+ ((long)(buffer[4] & 255) << 24) + ((long)(buffer[5] & 255) << 16)
				+ ((long)(buffer[6] & 255) << 8) + (long)(buffer[7] & 255);
				
		final long least = ((long)buffer[8] << 56) + ((long)(buffer[9] & 255) << 48)
				+ ((long)(buffer[10] & 255) << 40) + ((long)(buffer[11] & 255) << 32)
				+ ((long)(buffer[12] & 255) << 24) + ((long)(buffer[13] & 255) << 16)
				+ ((long)(buffer[14] & 255) << 8) + (long)(buffer[15] & 255);
		
		return new UUID(most, least);
	}
	
	/**
	 * Check if a string is a valid UUID v1, v4, v5 or v7
	 * 
	 * @param uuid The string to be checked
	 * @param version The version of the UUID
	 * @return If the string is a valid UUID
	 */
	public static boolean isValidUUID(String uuid, int version) {
		switch (version) {
		case 1:
			return UUID_V1_PATTERN.matcher(uuid).matches();
		case 4:
			return UUID_V4_PATTERN.matcher(uuid).matches();
		case 5:
			return UUID_V5_PATTERN.matcher(uuid).matches();
		case 7:
			return UUID_V7_PATTERN.matcher(uuid).matches();
		default:
			return false;
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
	
	/**
	 * Count the number of occurrence of a substring inside another string <br/>
	 * The method is case sensitive and will return 0 if the base or the substring is null or empty
	 * 
	 * @param base The string to be checked
	 * @param sub The substring to be counted
	 * @return The number of occurrence of the substring in the base string
	 */
	public static int countOccurence(String base, String sub) {
		if(base == null || base.isBlank()) return 0;
		if(sub == null || sub.isBlank()) return 0;
		
		int count = 0;
        int idx = 0;
        while((idx = base.indexOf(sub, idx)) != -1) {
            count++;
            idx+=sub.length();
        }
        
        return count;
	}
	
	/**
	 * Count the number of occurrence of a character inside a string <br/>
	 * The method is case sensitive and will return 0 if the base is null or empty
	 * 
	 * @param base The string to be checked
	 * @param c The character to be counted
	 * @return The number of occurrence of the character in the base string
	 */
	public static int countOccurence(String base, char c) {
		if(base == null || base.isBlank()) return 0;
        
        int count = 0;
        for(int i = 0; i < base.length(); i++) {
            if(base.charAt(i) == c) count++;
        }
        
        return count;
	}
	
	/**
	 * Shorten a string to a given maximum length and add a suffix at the end.
	 * <p>
	 * The method will return the string as is if its length is less than the maximum length or
	 * truncate it to the maximum length <strong>plus</strong> the suffix's length.
	 * <p>
	 * Examples:
	 * <blockquote><pre>
	 * StringHelper.shortenString("Hello World", 5) returns "Hello…"
	 * StringHelper.shortenString("Hello World", 20) returns "Hello World"
	 * StringHelper.shortenString("Foo bar", 5) returns "Foo b…"
	 * </pre></blockquote>
	 * 
	 * @param string The string to be shortened
	 * @param maxLength The maximum length of the string
	 * @return The string as is or shortened
	 */
	public static String shortenString(String string, int maxLength) {
		return shortenString(string, maxLength, "…");
	}
	
	/**
	 * Shorten a string to a given maximum length and add a given suffix at the end.
	 * <p>
	 * The method will return the string as is if its length is less than the maximum length or
	 * truncate it to the maximum length <strong>plus</strong> the suffix's length.
	 * <p>
	 * Examples:
	 * <blockquote><pre>
	 * StringHelper.shortenString("Hello World", 5, "!!!") returns "Hello!!!"
	 * StringHelper.shortenString("Hello World", 20, "boo") returns "Hello World"
	 * StringHelper.shortenString("Foo bar", 5, "ar!") returns "Foo bar!"
	 * </pre></blockquote>
	 * 
	 * @param string The string to be shortened
	 * @param maxLength The maximum length of the string
	 * @param suffix The suffix to be added at the end of the string
	 * @return The string as is or shortened
	 */
	public static String shortenString(String string, int maxLength, String suffix) {
		if (string.length() <= maxLength) return string;
		return string.substring(0, maxLength-1) + suffix;
	}
}
