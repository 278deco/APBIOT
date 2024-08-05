package apbiot.core.pems.events;

import apbiot.core.pems.ProgramEvent;

public class FileRegistrationEvent extends ProgramEvent {

	public FileRegistrationEvent(Object[] arguments) {
		super(arguments);
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.INTERMEDIATE;
	}

}
