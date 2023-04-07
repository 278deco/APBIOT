package apbiot.core.exceptions;

public class NonExistingFileInstanceException extends RuntimeException {

	private static final long serialVersionUID = 5071022227261473382L;

	public NonExistingFileInstanceException() {

	}
	
	public NonExistingFileInstanceException(String msg) {
		super(msg);
	}
	
}
