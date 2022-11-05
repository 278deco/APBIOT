package apbiot.core.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apbiot.core.command.AbstractCommandInstance;
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
	
	/* Example :
	 * addNewCommand(new CommandExample());
	 */
	
	protected void addNewCommand(AbstractCommandInstance cmd) {
		if(cmd instanceof NativeCommandInstance) NATIVE_COMMANDS.put(cmd.getNames(), (NativeCommandInstance)cmd);
		if(cmd instanceof SlashCommandInstance) SLASH_COMMANDS.put(cmd.getNames(), (SlashCommandInstance)cmd);
	}
	
	/**
	 * Register and push slash commands to discord API
	 * @param gateway - the discord gateway
	 * @param guildID - if present register the slash commands to a specific server, else register them as global
	 */
	protected void registerSlashCommand(GatewayDiscordClient gateway, long guildID) {
		
		boolean toGlobal = guildID == -1L;
		if(!toGlobal) {
			try {
				gateway.getGuildById(Snowflake.of(guildID)).block();
			}catch(Exception e) {
				toGlobal = true;
			}
		}
		
		List<ApplicationCommandRequest> commands = new ArrayList<>();
		
		for(Map.Entry<List<String>, SlashCommandInstance> entry : SLASH_COMMANDS.entrySet()) {
			commands.add(entry.getValue().createApplicationCommand(entry.getValue().getCommandArguments(new ArrayList<>())));	
		}
		
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
