package apbiot.core.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import apbiot.core.objects.Argument;
import apbiot.core.objects.enums.ArgumentLevel;
import apbiot.core.objects.enums.Ternary;
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
		sb.append("⚠ ERREUR Syntaxe : "+prefix+""+cmdName+" ");
		
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
	 * A advanced function for help with argument command
	 * @param map contains the command's arguments
	 * @param command name the name of the command
	 * @param prefix the prefix of the bot
	 * @return a constructed string
	 * @see apbiot.core.objects.enums.ArgumentLevel
	 */
	
	public static String getStringHelpSyntaxe(List<Argument> args, String commandName, String prefix ) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("⚠ ERREUR Syntaxe : "+prefix+""+commandName+" ");
		
		for(Argument arg : args) {
			
			String argName = arg.haveMultipleName() ? StringHelper.listToString(arg.getNames(), "/") : arg.getPrincipalName();
			
			if(arg.getLevel() == ArgumentLevel.REQUIRED) {
				sb.append("**<"+argName+">** ");
			}else if(arg.getLevel()  == ArgumentLevel.OPTIONNAL) {
				sb.append("*<"+argName+">* ");
			}else {
				sb.append("<"+argName+"> ");
			}
		}
		
		sb.setLength(sb.length() - 1);
		
		return sb.toString();
	}
	
	/**
	 * Format a list and make all the item appear in italic
	 * @param list - the list to format
	 * @return the formatted list
	 */
	public static String getFormattedStringList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for(String str : list) {
			sb.append("*"+str+"*").append(", ");
		}
		if(!list.isEmpty()) sb.setLength(sb.length() - 2);
		return sb.toString();
	}
	
	/**
	 * Format a list of permission and make all the item appear in italic
	 * @param list - the list containing the permission
	 * @return the formatted list
	 */
	public static String getFormattedPermissionList(List<Permission> list) {
		StringBuilder sb = new StringBuilder();
		for(Permission perm : list) {
			sb.append("*"+perm.toString()+"*").append(", ");
		}
		if(!list.isEmpty()) sb.setLength(sb.length() - 2);
		return sb.toString();
	}
	
	/**
	 * Handle all boolean command's argument
	 * Return if the boolean is true, false or in an undefined state
	 * @param message - the argument
	 * @return an Ternary operator (true or false like a boolean and UNDEFINED a problem occur) 
	 */
	public static Ternary getBooleanArgumentResponse(String message) {
		message = StringHelper.getRawCharacterString(message);
		
		if(message.equalsIgnoreCase("oui") || message.equalsIgnoreCase("yes")) {
			return Ternary.TRUE;
		}else if(message.equalsIgnoreCase("non") || message.equalsIgnoreCase("no")) {
			return Ternary.FALSE;
		}else {
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
	 * Used to return the arguments contains in user's message
	 * @param userCommand - a tuple containing the information about user's command
	 * @return the list of arguments
	 */
	public static List<String> formatCommandArguments(boolean isPrefixSplitted, String command) {
		if(command == "") return Arrays.asList("");
		
		List<String> result = new ArrayList<>(Arrays.asList(command.split(" ")));
		if(isPrefixSplitted) {
			result.remove(1);
		}
		result.remove(0);
		
		return result;
	}
}
