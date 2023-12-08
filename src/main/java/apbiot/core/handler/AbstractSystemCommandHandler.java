package apbiot.core.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import apbiot.core.command.SystemCommand;

/**
 * SystemCommandHandler class
 * This class register all created commands handled by the console.
 * @author 278deco
 * @version 2.0.0
 * @see apbiot.core.handler.Handler
 */
public abstract class AbstractSystemCommandHandler extends Handler {
	public final Map<Set<String>, SystemCommand> COMMANDS = new HashMap<>();
	
	protected void addNewCommand(SystemCommand cmd) {
		COMMANDS.put(cmd.getNames(), cmd);
	}

}
