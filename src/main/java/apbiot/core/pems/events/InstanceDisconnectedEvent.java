package apbiot.core.pems.events;

import apbiot.core.pems.LoggableEvent;

public record InstanceDisconnectedEvent() implements LoggableEvent {

	@Override
	public String getLoggerMessage() {
		return "Client disconnected from gateway with success.";
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
