package apbiot.core.event;

import apbiot.core.objects.interfaces.IEvent;

public interface EventListener {
	
	void newEventReceived(IEvent e);
	
}
