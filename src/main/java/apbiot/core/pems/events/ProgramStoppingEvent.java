package apbiot.core.pems.events;

import apbiot.core.pems.LoggableProgramEvent;

public class ProgramStoppingEvent extends LoggableProgramEvent {
	
	public ProgramStoppingEvent(Object[] arguments) {
		super(arguments);
	}

	@Override
	public String getLoggerMessage() {
		return null;
	}	
	
	@Override
	public LogPriority getLogPriority() {
		return LogPriority.INFO;
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}
}
