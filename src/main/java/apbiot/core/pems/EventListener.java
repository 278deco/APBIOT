package apbiot.core.pems;

import apbiot.core.pems.ProgramEvent.EventPriority;

public interface EventListener {
	
	void onEventReceived(ProgramEvent e, EventPriority priority);
	
}
