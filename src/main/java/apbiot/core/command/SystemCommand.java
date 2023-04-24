package apbiot.core.command;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SystemCommand {
	
	protected static final Logger LOGGER = LogManager.getLogger(SystemCommand.class);
	
	private List<String> cmdNames;
	
	/**
	 * Create a new SystemCommand
	 * @param cmdName - the command name and it alias
	 */
	public SystemCommand(List<String> cmdName) {
		this.cmdNames = cmdName;
	}
	
	/**
	 * Execute the code contained in the command instance
	 * @param arguments - the optional arguments for the command
	 */
	public abstract void execute(List<String> arguments);
	
	/**
	 * Get the main name of the command and it alias
	 * @return the command's name
	 */
	public List<String> getNames() {
		return cmdNames;
	}

	/**
	 * Get the main name of the command
	 * @return the main name
	 */
	public String getMainName() {
		return cmdNames.size() > 0 ? cmdNames.get(0) : "null";
	}
	
}
