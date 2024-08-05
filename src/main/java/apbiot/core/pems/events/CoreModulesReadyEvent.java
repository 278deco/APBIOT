package apbiot.core.pems.events;

import apbiot.core.pems.ProgramEvent;

public class CoreModulesReadyEvent extends ProgramEvent {

	public CoreModulesReadyEvent(Object[] arguments) {
		super(arguments);
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.INTERMEDIATE;
	}

}
