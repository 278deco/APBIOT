package apbiot.core.pems.events;

import apbiot.core.pems.LoggableProgramEvent;
import discord4j.core.GatewayDiscordClient;

public class InstanceConnectedEvent extends LoggableProgramEvent {

	public InstanceConnectedEvent(Object[] arguments) {
		super(arguments);
	}
	
	public GatewayDiscordClient getGateway() {
		return getEventArgument(GatewayDiscordClient.class, 0);
	}

	@Override
	public String getLoggerMessage() {
		return "Client connected to gateway with success.";
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
