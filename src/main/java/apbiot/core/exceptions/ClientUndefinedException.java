package apbiot.core.exceptions;

@Deprecated(since="7.0.0", forRemoval=true)
public class ClientUndefinedException extends Exception {

	private static final long serialVersionUID = -2926797593247187094L;

	public ClientUndefinedException(String s) {
		super(s);
	}
}
