package apbiot.core.pems.events;

import apbiot.core.pems.ProgramEvent;

public class DirectoriesLoadedEvent extends ProgramEvent {

	public DirectoriesLoadedEvent(Object[] arguments) {
		super(arguments);
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.INTERMEDIATE;
	}

}
