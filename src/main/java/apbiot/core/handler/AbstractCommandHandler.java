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
import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEventManager;
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
	
	private final Snowflake destination;
	
	public AbstractCommandHandler(Snowflake guild) {
		this.destination = guild;
	}
	
	public AbstractCommandHandler() {
		this(null);
	}
	
	/* Example :
	 * addNewCommand(new CommandExample());
	 */
	
	protected abstract void registerCommands(GatewayDiscordClient client);
	
	protected void addNewCommand(AbstractCommandInstance cmd) {
		if(cmd instanceof NativeCommandInstance) NATIVE_COMMANDS.put(cmd.getNames(), (NativeCommandInstance)cmd);
		if(cmd instanceof SlashCommandInstance) SLASH_COMMANDS.put(cmd.getNames(), (SlashCommandInstance)cmd);
		if(cmd instanceof ApplicationCommandInstance) APPLICATION_COMMANDS.put(cmd.getDisplayName(), (ApplicationCommandInstance)cmd);
	}
	
	@Override
	protected final void register(GatewayDiscordClient client) throws HandlerPreProcessingException {
		registerCommands(client); //Read and register all the commands provided to the client
		
		loadApplicationCommand(client); //Link commands to discord
		
		//Build the commands (configure the variables)
		for(var entry : NATIVE_COMMANDS.entrySet()) {
			entry.getValue().buildCommand();
		}
		
		for(var entry : SLASH_COMMANDS.entrySet()) {
			entry.getValue().buildCommand();
		}
		
		ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.COMMAND_LIST_PARSED, new Object[] {null, NATIVE_COMMANDS, SLASH_COMMANDS, APPLICATION_COMMANDS});
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
	protected void loadApplicationCommand(GatewayDiscordClient gateway) {
		boolean toGlobal = this.destination == null;
		if(!toGlobal) {
			try {
				gateway.getGuildById(this.destination).block();
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
						this.destination.asLong(), 
						commands
				).subscribe();	
			}
		}
	}
}
