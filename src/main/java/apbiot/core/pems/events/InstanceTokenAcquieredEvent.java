package apbiot.core.pems.events;

import java.util.Optional;

import apbiot.core.pems.ProgramEvent;

public class InstanceTokenAcquieredEvent extends ProgramEvent {

	public InstanceTokenAcquieredEvent(Object[] arguments) {
		super(arguments);
	}

	public Optional<String> getClientToken() {
		return Optional.ofNullable(getEventArgument(String.class, 0));
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}

}
