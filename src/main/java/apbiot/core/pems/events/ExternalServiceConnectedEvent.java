package apbiot.core.pems.events;

import apbiot.core.pems.Event;

public record ExternalServiceConnectedEvent(String serviceName) implements Event {
	
	@Override
	public int getArgumentCount() {
		return 1;
	}

}
