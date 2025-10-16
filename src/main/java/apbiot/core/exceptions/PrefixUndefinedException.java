package apbiot.core.exceptions;

@Deprecated(since="7.0.0", forRemoval=true)
public class PrefixUndefinedException extends Exception {
	
	private static final long serialVersionUID = 2310958702937927420L;

	public PrefixUndefinedException(String s) {
		super(s);
	}

}
