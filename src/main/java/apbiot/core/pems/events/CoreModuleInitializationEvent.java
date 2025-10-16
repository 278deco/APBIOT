package apbiot.core.pems.events;

import apbiot.core.pems.Event;

public record CoreModuleInitializationEvent() implements Event {

	@Override
	public int getArgumentCount() {
		return 0;
	}

}
