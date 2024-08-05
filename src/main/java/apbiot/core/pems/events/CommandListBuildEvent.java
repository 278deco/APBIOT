package apbiot.core.pems.events;

import java.util.Arrays;

import apbiot.core.pems.LoggableProgramEvent;
import apbiot.core.pems.actions.CommandRebuildAction.CommandRebuildScope;

public class CommandListBuildEvent extends LoggableProgramEvent {

	public CommandListBuildEvent(Object[] arguments) {
		super(arguments);
	}

	public String[] getExceptionMessages() {
		return getEventArgument(String[].class, 0);
	}
	
	public CommandRebuildScope getRebuildScope() {
		return getEventArgument(CommandRebuildScope.class, 1);
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.LOW;
	}

	@Override
	public String getLoggerMessage() {
		final String errors = Arrays.toString(getExceptionMessages()).replaceAll("(\\[|\\])", "");
		return "Built all commands successfully ! [command with errors:"+errors+"]";
	}

	@Override
	public LogPriority getLogPriority() {
		return LogPriority.INFO;
	}

}
