package apbiot.core.handler;

public class HandlerRegisteringException extends Exception {

	private static final long serialVersionUID = -5904459346153892967L;

	public HandlerRegisteringException() {
		super();
	}
	
	public HandlerRegisteringException(String msg) {
		super(msg);
	}
	
	public HandlerRegisteringException(String msg, Exception cause) {
		super(msg, cause);
	}

}
