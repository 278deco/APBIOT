package apbiot.core.pems;

public interface ProgramEventEnumerator {
	Class<? extends ProgramEvent> getEventClass();
}
