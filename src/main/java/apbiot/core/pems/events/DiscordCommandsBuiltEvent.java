package apbiot.core.pems.events;

import java.util.List;

import apbiot.core.helper.StringHelper;
import apbiot.core.pems.LoggableEvent;
import apbiot.core.pems.commands.RebuildDiscordCommandsAction.CommandRebuildScope;

public record DiscordCommandsBuiltEvent(List<String> exceptionMessages, CommandRebuildScope rebuildScope) implements LoggableEvent {

	@Override
	public String getLoggerMessage() {
		final String errors = StringHelper.listToString(exceptionMessages, ", ");
		return "Built all commands successfully ! [command with errors:"+errors+"]";
	}

	@Override
	public LogPriority getLogPriority() {
		return LogPriority.INFO;
	}

	@Override
	public int getArgumentCount() {
		return 2;
	}

}
