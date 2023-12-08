package apbiot.core.handler;

public class HandlerPreProcessingException extends Exception {

	private static final long serialVersionUID = 8065945910694378856L;

	public HandlerPreProcessingException() {
		super();
	}
	
	public HandlerPreProcessingException(String msg) {
		super(msg);
	}
	
	public HandlerPreProcessingException(String msg, Exception cause) {
		super(msg, cause);
	}
	
}
