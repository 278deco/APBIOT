package apbiot.core.objects;

public class Tuple<A, B> {

	private A valueA;
	private B valueB;
	
	public Tuple(A valueA, B valueB) {
		this.valueA = valueA;
		this.valueB = valueB;
	}
	
	public A getValueA() {
		return valueA;
	}
	
	public B getValueB() {
		return valueB;
	}

	public String toString() {
		return "( "+ valueA + " : " + valueB +" )";
	}
	
}
