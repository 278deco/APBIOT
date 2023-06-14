package apbiot.core.objects;

import java.util.Objects;

public class Tuple<A, B> {

	private static final Tuple<?,?> EMPTY = new Tuple<>(null, null);
	
	private final A valueA;
	private final B valueB;
	
	private Tuple(A valueA, B valueB) {
		this.valueA = valueA;
		this.valueB = valueB;
	}
	
	/**
	 * Create a new empty tuple meaning value A and B will be equals to null
	 * @param <A> The type for the value A
	 * @param <B> The type for the value B
	 * @return An empty tuple
	 */
	@SuppressWarnings("unchecked")
	public static<A,B> Tuple<A,B> empty() {
		return (Tuple<A,B>) EMPTY;
	}
	
	/**
	 * Create a tuple containing the two elements. The two values must be different from {@code null}
	 * @param <A> The type for the value A
	 * @param <B> The type for the value B
	 * @param elementA The value for the A field
	 * @param elementB The value for the B field
	 * @return a Tuple containing the two elements
	 * @throws NullPointerException if one of the element is {@code null}
	 * @see #ofNullable(Object, Object)
	 */
	public static<A,B> Tuple<A,B> of(A elementA, B elementB) {
		return new Tuple<A, B>(Objects.requireNonNull(elementA), Objects.requireNonNull(elementB));
	}
	
	/**
	 * Create a tuple containing the two elements. If the two elements are null create an empty {@link Tuple}.
	 * @param <A> The type for the value A
	 * @param <B> The type for the value B
	 * @param elementA The value for the A field
	 * @param elementB The value for the B field
	 * @return a Tuple containing the two elements or an empty {@link Tuple}
	 * @see #of(Object, Object)
	 */
	@SuppressWarnings("unchecked")
	public static<A,B> Tuple<A,B> ofNullable(A elementA, B elementB) {
		return (elementA == null && elementB == null) ? (Tuple<A,B>) EMPTY : new Tuple<A, B>(elementA, elementB);
	}
	
	/**
	 * Get the value A of the {@link Tuple}
	 * @return the value A
	 */
	public A getValueA() {
		return valueA;
	}
	
	public boolean isValueAPresent() {
		return valueA != null;
	}
	
	public boolean isValueAEmpty() {
		return valueA == null;
	}
	
	/**
	 * Get the value B of the {@link Tuple}
	 * @return the value B
	 */
	public B getValueB() {
		return valueB;
	}
	
	public boolean isValueBPresent() {
		return valueB != null;
	}
	
	public boolean isValueBEmpty() {
		return valueB == null;
	}
	
	public boolean isTupleEmpty() {
		return isValueAEmpty() && isValueBEmpty();
	}
	
	public boolean isTuplePresent() {
		return isValueAPresent() && isValueBPresent();
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Tuple<?, ?> && areEqual((Tuple<?,?>)obj);
	}
	
	private boolean areEqual(Tuple<?,?> tuple) {
		return Objects.equals(this.getValueA(), tuple.getValueA()) && Objects.equals(this.getValueB(), tuple.getValueB());
	}

	public String toString() {
		return "( "+ valueA + ", " + valueB +" )";
	}
	
	
}
