package apbiot.core.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.command.ApplicationCommandInstance;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.objects.interfaces.IHandler;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;

/**
 * CommandHandler class
 * This class handle all the commands created by the bot
 * @author 278deco
 * @see apbiot.core.objects.interfaces.IHandler
 */
public abstract class AbstractCommandHandler implements IHandler {
	public final Map<List<String>, NativeCommandInstance> NATIVE_COMMANDS = new HashMap<>();
	public final Map<List<String>, SlashCommandInstance> SLASH_COMMANDS = new HashMap<>();
	public final Map<String, ApplicationCommandInstance> APPLICATION_COMMANDS = new HashMap<>();
	
	/* Example :
	 * addNewCommand(new CommandExample());
	 */
	
	protected void addNewCommand(AbstractCommandInstance cmd) {
		if(cmd instanceof NativeCommandInstance) NATIVE_COMMANDS.put(cmd.getNames(), (NativeCommandInstance)cmd);
		if(cmd instanceof SlashCommandInstance) SLASH_COMMANDS.put(cmd.getNames(), (SlashCommandInstance)cmd);
		if(cmd instanceof ApplicationCommandInstance) APPLICATION_COMMANDS.put(cmd.getMainName(), (ApplicationCommandInstance)cmd);
	}
	
	/**
	 * Register and push slash commands to discord API
	 * @param gateway - the discord gateway
	 * @param guildID - if present register the slash commands to a specific server, else register them as global
	 * @deprecated see {@link #registerApplicationCommand(GatewayDiscordClient, long)}
	 */
	@Deprecated
	protected void registerSlashCommand(GatewayDiscordClient gateway, long guildID) {
		
		boolean toGlobal = guildID == -1L;
		if(!toGlobal) {
			try {
				gateway.getGuildById(Snowflake.of(guildID)).block();
			}catch(Exception e) {
				toGlobal = true;
			}
		}
		
		final List<ApplicationCommandRequest> commands = new ArrayList<>();
		
		
		
		if(commands.size() > 0) {
			if(toGlobal) {
				gateway.getRestClient().getApplicationService().bulkOverwriteGlobalApplicationCommand(
						gateway.getRestClient().getApplicationId().block(), 
						commands
				).subscribe();
			}else {
				gateway.getRestClient().getApplicationService().bulkOverwriteGuildApplicationCommand(
						gateway.getRestClient().getApplicationId().block(), 
						guildID, 
						commands
				).subscribe();	
			}
		}
	}
	
	/**
	 * Register and push application commands to discord API
	 * @param gateway The discord gateway
	 * @param guildID If present register the slash commands to a specific server, else register them as global
	 */
	protected void registerApplicationCommand(GatewayDiscordClient gateway, long guildID) {
		boolean toGlobal = guildID == -1L;
		if(!toGlobal) {
			try {
				gateway.getGuildById(Snowflake.of(guildID)).block();
			}catch(Exception e) {
				toGlobal = true;
			}
		}
		
		final List<ApplicationCommandRequest> commands = new ArrayList<>();
		
		for(Map.Entry<String, ApplicationCommandInstance> entry : APPLICATION_COMMANDS.entrySet()) {
			commands.add(entry.getValue().createApplicationCommand());
		}
		
		SLASH_COMMANDS.forEach((key, value) -> {
			commands.add(value.createApplicationCommand(value.getCommandArguments(new ArrayList<>())));
		});

		System.out.println(commands);
		
		if(commands.size() > 0) {
			if(toGlobal) {
				gateway.getRestClient().getApplicationService().bulkOverwriteGlobalApplicationCommand(
						gateway.getRestClient().getApplicationId().block(), 
						commands
				).subscribe();
			}else {
				gateway.getRestClient().getApplicationService().bulkOverwriteGuildApplicationCommand(
						gateway.getRestClient().getApplicationId().block(), 
						guildID, 
						commands
				).subscribe();	
			}
		}
	}
}
