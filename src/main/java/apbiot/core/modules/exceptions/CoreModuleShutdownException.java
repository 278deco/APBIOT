package apbiot.core.modules.exceptions;

public class CoreModuleShutdownException extends Exception {

	private static final long serialVersionUID = -1608832466635308280L;

	public CoreModuleShutdownException() {
		super();
	}
	
	public CoreModuleShutdownException(String msg) {
		super(msg);
	}
	
	public CoreModuleShutdownException(String msg, Exception cause) {
		super(msg, cause);
	}
}
