package apbiot.core.pems.events;

import apbiot.core.pems.ProgramEvent;

public class CoreModuleLaunchEvent extends ProgramEvent {

	public CoreModuleLaunchEvent(Object[] arguments) {
		super(arguments);
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.INTERMEDIATE;
	}
}