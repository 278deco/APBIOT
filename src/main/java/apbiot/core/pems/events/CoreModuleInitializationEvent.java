package apbiot.core.pems.events;

import apbiot.core.pems.ProgramEvent;

public class CoreModuleInitializationEvent extends ProgramEvent {

	public CoreModuleInitializationEvent(Object[] arguments) {
		super(arguments);
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.INTERMEDIATE;
	}
}
