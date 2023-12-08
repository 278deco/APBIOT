package apbiot.core.command;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class SystemCommand {
	
	protected static final Logger LOGGER = LogManager.getLogger(SystemCommand.class);
	
	private String commandDisplayName;
	private HashSet<String> commandNames = new HashSet<>(); //Contains the aliases and the display name
	
	/**
	 * Create a new SystemCommand
	 * @param displayName The main name of the command. Is displayed to represent the command to the user
	 * @param aliases A {@link Set} of aliases for the command.
	 */
	public SystemCommand(String displayName, Set<String> aliases) {
		this.commandDisplayName = displayName;
		this.commandNames.add(displayName);
		if(aliases != null) this.commandNames.addAll(aliases);
		
	}
	
	/**
	 * Create a new SystemCommand
	 * @param displayName The main name of the command. Is displayed to represent the command to the user
	 */
	public SystemCommand(String displayName) {
		this.commandDisplayName = displayName;
		this.commandNames.add(displayName);
	}
	
	/**
	 * Execute the code contained in the command instance
	 * @param arguments - the optional arguments for the command
	 */
	public abstract void execute(List<String> arguments);
	
	/**
	 * Get the display name of the command and its aliases. The set can only contains the display name if no aliases are found.
	 * @return A set containing the command's display name and aliases
	 */
	public Set<String> getNames() {
		return Collections.unmodifiableSet(this.commandNames);
	}

	/**
	 * Get the main name of the command
	 * @return the main name
	 * @deprecated since 5.0
	 * @see #getDisplayName()
	 */
	public String getMainName() {
		return "null";
	}
	
	/**
	 * Get the command's display name
	 * @return the main name
	 */
	public String getDisplayName() {
		return this.commandDisplayName;
	}
	
}
