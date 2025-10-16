package apbiot.core.pems.exceptions;

public class ActionDispatchException extends RuntimeException {

	private static final long serialVersionUID = -4886967712981911149L;

	public ActionDispatchException() {
		super();
	}
	
	public ActionDispatchException(String msg) {
		super(msg);
	}
	
	public ActionDispatchException(String msg, Throwable cause) {
		super(msg, cause);
	}
	
}
