package apbiot.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apbiot.core.objects.Argument;
import apbiot.core.objects.enums.Ternary;
import apbiot.core.utils.Emojis;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Role;
import discord4j.rest.util.Permission;
import reactor.core.publisher.Mono;

public class ArgumentHelper {
	
	/**
	 * @deprecated since 4.0
	 * @see apbiot.core.helper.ArgumentHelper#getStringHelpSyntaxe(List, String, String)
	 */
	public static String getStringHelpSyntaxeArgument(int maxArg, String cmdName, String prefix, boolean isFirstObligatory, boolean isAllOptional) {
		StringBuilder sb = new StringBuilder();
		sb.append(Emojis.WARNING+" ERREUR Syntaxe : "+prefix+""+cmdName+" ");
		
		for(int i = 1; i <= maxArg; i++) {
			
			if(i == 1 && isFirstObligatory) { sb.append("**<argument "+i+">** "); }
			else {
			
				if(isAllOptional) { sb.append("*<argument "+i+">* "); }
				else { sb.append("<argument "+i+"> "); }
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * Format a string containing the command and its arguments for help purpose
	 * @param args The command's arguments
	 * @param commandName The name of the command
	 * @param prefix The prefix used by the client
	 * @return the formatted string
	 * @see apbiot.core.objects.enums.ArgumentLevel
	 * @since 2.0
	 */
	public static String getStringHelpSyntaxe(List<Argument> args, String commandName, String prefix ) {
		final StringBuilder sb = new StringBuilder(Emojis.WARNING+" ERREUR Syntaxe : "+prefix+""+commandName+" ");
				
		for(Argument arg : args) {
			final String argName = arg.haveMultipleName() ? StringHelper.listToString(arg.getNames(), "/") : arg.getPrincipalName();
			
			switch(arg.getLevel()) {
				case REQUIRED -> sb.append("**<"+argName+">** ");
				case OPTIONNAL -> sb.append("*<"+argName+">* ");
				default -> sb.append("<"+argName+"> ");
			}
		}
		
		sb.setLength(sb.length() - 1);
		return sb.toString();
	}
	
	/**
	 * Format a list of {@link Object} and make all the item appear in italic using Markdown syntax
	 * @param list The list of objects to be formatted
	 * @return The list's content formatted
	 * @since 5.0
	 */
	public static String formatListContentItalic(List<Object> list) {
		final StringBuilder sb = new StringBuilder();
		list.forEach(obj -> sb.append("*"+obj.toString()+"*").append(", "));

		if(!list.isEmpty()) sb.setLength(sb.length() - 2);
		return sb.toString();
	}
	
	/**
	 * Format a list and make all the item appear in italic using Markdown syntax
	 * @param list The list to be formatted
	 * @return the formatted list
	 * @since 2.0
	 * @deprecated since 5.0
	 * @see #formatListContentItalic(List)
	 */
	public static String getFormattedStringList(List<String> list) {
		final StringBuilder sb = new StringBuilder();
		list.forEach(str -> sb.append("*"+str+"*").append(", "));

		if(!list.isEmpty()) sb.setLength(sb.length() - 2);
		return sb.toString();
	}
	
	/**
	 * Format a list of permission and make all the item appear in italic using Markdown syntax
	 * @param list The list containing the permission
	 * @return the formatted list
	 * @since 2.0
	 * @deprecated since 5.0
	 * @see #formatListContentItalic(List)
	 */
	public static String getFormattedPermissionList(List<Permission> list) {
		final StringBuilder sb = new StringBuilder();
		list.forEach(perm -> sb.append("*"+perm.toString()+"*").append(", "));

		if(!list.isEmpty()) sb.setLength(sb.length() - 2);
		return sb.toString();
	}
	
	/**
	 * Used when getting a 'yes' or 'no' answer for an user.<br/>
	 * Convert the response to a {@link Ternary}. The ternary will be {@code true} if the user answered yes, {@code false} if the user answered no.
	 * If the response cannot be parsed properly, the ternary will be set to {@code undefined}.
	 * @param response The user's answer to a question
	 * @return a Ternary operator
	 * @since 3.0
	 */
	public static Ternary getBooleanArgumentResponse(String response) {
		response = StringHelper.getRawCharacterString(response);
		
		switch(response.toLowerCase()) {
			case "oui":
			case "yes":
			case "ja":
			case "si":
			case "sí":
				return Ternary.TRUE;
			case "non":
			case "no":
			case "nein":
				return Ternary.FALSE;
			default:
				return Ternary.UNDEFINED;	
		}
	}
	
	/**
	 * @deprecated since 4.0
	 * @see apbiot.core.helper.StringHelper#getRoleFromRawString(String, Guild)
	 */
	public static Mono<Role> getRoleFromRawArgument(String argument, Guild guild) {
		if(StringHelper.isValidRoleDiscordID(argument)) {
			return guild.getRoleById(Snowflake.of(StringHelper.getFormattedDiscordID(argument)));
		}
		
		return null;
	}
	
	/**
	 * Format the arguments provided in an user command. Separate the command from the argument.
	 * @param isPrefixSplitted tell the function if its needs to handle a prefix separated from the command
	 * @param command the whole command containing the command name and the arguments
	 * @return the list of arguments as {@link String}
	 * @since 2.0
	 */
	public static List<String> formatCommandArguments(boolean isPrefixSplitted, String command) {
		if(command == "") return Arrays.asList("");
		
		final List<String> result = new ArrayList<>(Arrays.asList(command.split(" +")));
		if(isPrefixSplitted) result.remove(1); //Remove the separated prefix
		result.remove(0); //Remove the command name
		
		return result;
	}
}
