package apbiot.core.objects.enums;

public enum ArgumentType {
	TEXT("texte"),
	INT("nombre entier"),
	FLOAT("nombre"),
	MENTION("mention"),
	BOOLEAN("oui/non"),
	EMOJI("emoji"),
	DATE("date"),
	OTHER("autre");
	
	private String typeName;
	private ArgumentType(String name) {
		this.typeName = name;
	}
	
	public String getTypeName() {
		return typeName;
	}
}
