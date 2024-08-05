package apbiot.core.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import apbiot.core.command.SystemCommand;
import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEventManager;
import discord4j.core.GatewayDiscordClient;

/**
 * SystemCommandHandler class
 * This class register all created commands handled by the console.
 * @author 278deco
 * @version 2.0.0
 * @see apbiot.core.handler.Handler
 */
public abstract class AbstractSystemCommandHandler extends Handler {
	public final Map<Set<String>, SystemCommand> COMMANDS = new HashMap<>();
	
	protected abstract void registerCommands(GatewayDiscordClient client);
	
	protected void addNewCommand(SystemCommand cmd) {
		COMMANDS.put(cmd.getNames(), cmd);
	}
	
	@Override
	protected final void register(GatewayDiscordClient client) throws HandlerRegisteringException {
		registerCommands(client);
				
		ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.COMMAND_LIST_PARSED, new Object[] {COMMANDS, null, null, null});
	}

	@Override
	public final HandlerType getType() {
		return HandlerType.GATEWAY;
	}
	
}
