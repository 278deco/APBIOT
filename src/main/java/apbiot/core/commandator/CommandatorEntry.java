package apbiot.core.commandator;

import apbiot.core.objects.enums.ApplicationCommandType;

public class CommandatorEntry {

	private String commandName;
	private ApplicationCommandType commandType;
	
	public CommandatorEntry(String displayCommandName, ApplicationCommandType commandType) {
		this.commandName = displayCommandName;
		this.commandType = commandType;
	}

	public String getCommandName() {
		return commandName;
	}
	
	public ApplicationCommandType getCommandType() {
		return commandType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CommandatorEntry) {
			return areEqual((CommandatorEntry) obj);
		} else {
			return false;
		}
	}
	
	private boolean areEqual(CommandatorEntry entry) {
		return this.commandName.equals(entry.commandName) && this.commandType.equals(entry.commandType);
	}
	
}
