package apbiot.core.pems.events;

import apbiot.core.pems.LoggableProgramEvent;

public class InstanceDisconnectedEvent extends LoggableProgramEvent {

	public InstanceDisconnectedEvent(Object[] arguments) {
		super(arguments);
	}

	@Override
	public String getLoggerMessage() {
		return "Client disconnected from gateway with success.";
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
