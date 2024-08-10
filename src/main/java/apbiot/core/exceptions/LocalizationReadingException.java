package apbiot.core.exceptions;

public class LocalizationReadingException extends Exception {

	private static final long serialVersionUID = 2074718323570105848L;

	public LocalizationReadingException() {
	}
	
	public LocalizationReadingException(String msg) {
		super(msg);
	}
	
	public LocalizationReadingException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
