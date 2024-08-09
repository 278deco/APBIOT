package apbiot.core.exceptions;

public class CoreModuleLoadingException extends Exception {

	private static final long serialVersionUID = -3607725221030397128L;
	
	public CoreModuleLoadingException() {
		super();
	}
	
	public CoreModuleLoadingException(String msg) {
		super(msg);
	}
	
	public CoreModuleLoadingException(String msg, Exception cause) {
		super(msg, cause);
	}
}
