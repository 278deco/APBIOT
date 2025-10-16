package apbiot.core.pems;

public class GlobalActionBus {
	
	private static ActionBus INSTANCE = new ActionBus();
	
	public static ActionBus get() {
		synchronized (GlobalActionBus.class) {
			return INSTANCE;
		}
	}
	
	private GlobalActionBus() {}

}
