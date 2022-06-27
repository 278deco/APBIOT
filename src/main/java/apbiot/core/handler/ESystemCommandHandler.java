package apbiot.core.handler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apbiot.core.command.SystemCommand;
import apbiot.core.objects.interfaces.IHandler;

/**
 * SystemCommandHandler class
 * This class handle all the system commands created by the bot
 * @author 278deco
 * @see apbiot.core.objects.interfaces.IHandler
 */
public abstract class ESystemCommandHandler implements IHandler {
	public final Map<List<String>, SystemCommand> COMMANDS = new HashMap<>();
	
	protected void addNewCommand(SystemCommand cmd) {
		COMMANDS.put(cmd.getNames(), cmd);
	}

}
