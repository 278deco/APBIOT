package apbiot.core.pems.events;

import apbiot.core.pems.LoggableEvent;

public record ProgramStoppingEvent() implements LoggableEvent {

	@Override
	public String getLoggerMessage() {
		return "Program is stopping..." ;
	}	
	
	@Override
	public LogPriority getLogPriority() {
		return LogPriority.INFO;
	}

	@Override
	public int getArgumentCount() {
		return 0;
	}
}
