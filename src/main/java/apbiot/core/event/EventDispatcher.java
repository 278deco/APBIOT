package apbiot.core.event;

import java.util.HashSet;
import java.util.Set;

import apbiot.core.objects.interfaces.IEvent;

public class EventDispatcher {
	
	private Set<EventListener> listeners;
	
	public EventDispatcher() {
		this.listeners = new HashSet<EventListener>();
	}
	
	public boolean addListener(EventListener listener) {
		return this.listeners.add(listener);
	}
	
	public boolean removeListener(EventListener listener) {
		return this.listeners.remove(listener);
	}
	
	public void dispatchEvent(IEvent event) {
		this.listeners.forEach(listener -> listener.newEventReceived(event));
	}

}
