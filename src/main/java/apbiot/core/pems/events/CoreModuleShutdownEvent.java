package apbiot.core.pems.events;

import apbiot.core.pems.ProgramEvent;

public class CoreModuleShutdownEvent extends ProgramEvent {

	public CoreModuleShutdownEvent(Object[] arguments) {
		super(arguments);
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.INTERMEDIATE;
	}
}
