package apbiot.core.pems.events;

import apbiot.core.pems.LoggableEvent;
import discord4j.core.GatewayDiscordClient;

public record InstanceConnectedEvent(GatewayDiscordClient client) implements LoggableEvent {

	@Override
	public String getLoggerMessage() {
		return "Client connected to gateway with success.";
	}
	
	@Override
	public LogPriority getLogPriority() {
		return LogPriority.INFO;
	}

	@Override
	public int getArgumentCount() {
		return 1;
	}	
}
