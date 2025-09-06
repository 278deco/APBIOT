package apbiot.core.pems.events;

import apbiot.core.pems.ProgramEvent;
import marshmalliow.core.builder.DirectoryManager;

public class DirectoryManagerReadyEvent extends ProgramEvent {

	public DirectoryManagerReadyEvent(Object[] arguments) {
		super(arguments);
	}

	public DirectoryManager getCurrentDirectoryManager() {		
		return getEventArgument(DirectoryManager.class, 0);
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.INTERMEDIATE;
	}
}
