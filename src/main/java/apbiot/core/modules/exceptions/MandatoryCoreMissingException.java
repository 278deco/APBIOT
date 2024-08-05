package apbiot.core.modules.exceptions;

public class MandatoryCoreMissingException extends Exception {

	private static final long serialVersionUID = -9065437210123714565L;

	public MandatoryCoreMissingException() {
		super();
	}
	
	public MandatoryCoreMissingException(String msg) {
		super(msg);
	}
}
