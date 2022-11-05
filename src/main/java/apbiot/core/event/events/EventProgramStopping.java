package apbiot.core.event.events;

import apbiot.core.objects.interfaces.ILoggerEvent;

public class EventProgramStopping implements ILoggerEvent {
	
	private int fileClosingNumber;
	
	public EventProgramStopping(int fileClosingNumber) {
		this.fileClosingNumber = fileClosingNumber;
	}
	
	public int getClosingFileNumber() {
		return this.fileClosingNumber;
	}

	@Override
	public String getLoggerMessage() {
		return "Shutting down program. Saving "+getClosingFileNumber()+" file(s).";
	}
	
	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}
}
