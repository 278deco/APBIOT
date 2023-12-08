package apbiot.core.modules;

/**
 * 
 */
public enum CoreModuleType {
	
	CREDENTIALS_HOLDER("Credentials Holder", 1, true),
	CONSOLE_LOGGING("Console Logging", 2, true),
	DB_FACTORY("Database Connection", 3),
	IO_FACTORY("File Management", 4),
	DISCORD_GATEWAY("Discord Client Gateway", 5, true);
	
	private String name;
	private boolean mandatory;
	private int ordering;
	private CoreModuleType(String name, int ordering) {
		this.name = name;
		this.mandatory = false;
		this.ordering = ordering;
	}
	
	private CoreModuleType(String name, int ordering, boolean mandatory) {
		this.name = name;
		this.mandatory = mandatory;
		this.ordering = ordering;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isMandatory() {
		return mandatory;
	}
	
	public int getOrderPriority() {
		return ordering;
	}
	
}
