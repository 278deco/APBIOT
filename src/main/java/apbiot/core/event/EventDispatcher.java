package apbiot.core.event;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.objects.interfaces.IEvent;

public class EventDispatcher {
	
	private List<EventListener> listenerLst;
	
	public EventDispatcher() {
		this.listenerLst = new ArrayList<EventListener>();
	}
	
	public void addListener(EventListener listener) {
		this.listenerLst.add(listener);
	}
	
	public void dispatchEvent(IEvent event) {
		for(EventListener listener : this.listenerLst) {
			listener.newEventReceived(event);
		}
	}

}
