package apbiot.core.pems;

public class GlobalEventBus {

	private static final EventBus INSTANCE = new EventBus();
	
	public static EventBus get() {
		synchronized (INSTANCE) {
			return INSTANCE;
		}
	}
	
	private GlobalEventBus() {}
	
}
