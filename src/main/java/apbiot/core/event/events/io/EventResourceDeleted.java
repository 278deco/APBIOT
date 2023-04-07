package apbiot.core.event.events.io;

import apbiot.core.objects.interfaces.IEvent;

public class EventResourceDeleted implements IEvent {
	
	private String resourceId;
	
	public EventResourceDeleted(String resourceID) {
		this.resourceId = resourceID;
	}
	
	public String getResourceId() {
		return resourceId;
	}
	
}
