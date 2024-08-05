package apbiot.core.modules.exceptions;

public class CoreModuleLaunchingException extends Exception {

	private static final long serialVersionUID = 2848467064019331416L;

	public CoreModuleLaunchingException() {
		super();
	}
	
	public CoreModuleLaunchingException(String msg) {
		super(msg);
	}
	
	public CoreModuleLaunchingException(String msg, Exception cause) {
		super(msg, cause);
	}
}
