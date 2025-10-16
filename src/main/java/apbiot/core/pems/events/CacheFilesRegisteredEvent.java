package apbiot.core.pems.events;

import apbiot.core.pems.Event;

public record CacheFilesRegisteredEvent() implements Event {

	@Override
	public int getArgumentCount() {
		return 0;
	}
}
