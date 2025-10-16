package apbiot.core.exceptions;

@Deprecated(since="7.0.0", forRemoval=true)
public class LoadingErrorException extends Exception {

	private static final long serialVersionUID = 6569932711955855075L;

	public LoadingErrorException(String s) {
		super(s);
	}
}
