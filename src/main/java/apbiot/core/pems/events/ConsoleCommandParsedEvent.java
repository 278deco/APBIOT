package apbiot.core.pems.events;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import apbiot.core.command.SystemCommand;
import apbiot.core.pems.Event;

public record ConsoleCommandParsedEvent(Map<Set<String>, SystemCommand> consoleCommands) implements Event {
	
	public Optional<Map<Set<String>, SystemCommand>> optionalConsoleCommands() {
		return Optional.ofNullable(consoleCommands);
	}

	@Override
	public int getArgumentCount() {
		return 1;
	}

}
