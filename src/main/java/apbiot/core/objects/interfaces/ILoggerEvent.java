package apbiot.core.objects.interfaces;

public interface ILoggerEvent extends IEvent {
	
	String getLoggerMessage();
	EventPriority getEventPriority();
	
	public enum EventPriority {
		INFO,
		WARNING,
		ERROR,
		FATAL;
	}
}
