package apbiot.core.pems.events;

import apbiot.core.pems.Event;

public record DirectoriesRegisteredEvent() implements Event {

	@Override
	public int getArgumentCount() {
		return 0;
	}

}
