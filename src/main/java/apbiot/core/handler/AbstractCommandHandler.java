package apbiot.core.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.command.ApplicationCommandInstance;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.SlashCommandInstance;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.discordjson.json.ApplicationCommandRequest;

/**
 * CommandHandler class
 * This class register all created commands handled by the client.
 * @author 278deco
 * @version 2.0
 * @see apbiot.core.handler.Handler
 */
public abstract class AbstractCommandHandler extends Handler {
	public final Map<Set<String>, NativeCommandInstance> NATIVE_COMMANDS = new HashMap<>();
	public final Map<Set<String>, SlashCommandInstance> SLASH_COMMANDS = new HashMap<>();
	public final Map<String, ApplicationCommandInstance> APPLICATION_COMMANDS = new HashMap<>();
	
	/* Example :
	 * addNewCommand(new CommandExample());
	 */
	
	protected void addNewCommand(AbstractCommandInstance cmd) {
		if(cmd instanceof NativeCommandInstance) NATIVE_COMMANDS.put(cmd.getNames(), (NativeCommandInstance)cmd);
		if(cmd instanceof SlashCommandInstance) SLASH_COMMANDS.put(cmd.getNames(), (SlashCommandInstance)cmd);
		if(cmd instanceof ApplicationCommandInstance) APPLICATION_COMMANDS.put(cmd.getDisplayName(), (ApplicationCommandInstance)cmd);
	}
	
	/**
	 * Register and push slash commands to discord API
	 * @param gateway The discord gateway
	 * @param guildID If present register the slash commands to a specific server, else register them as global
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
