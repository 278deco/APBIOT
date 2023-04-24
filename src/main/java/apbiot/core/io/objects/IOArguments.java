package apbiot.core.io.objects;

public class IOArguments {
	
	private Directory directory;
	private String name;
	
	private Object[] otherArguments;
	
	public IOArguments(Directory directory, String fileName, Object... arguments) {
		this.directory = directory;
		this.name = fileName;
		this.otherArguments = arguments;
	}
	
	public String getName() {
		return name;
	}
	
	public Directory getDirectory() {
		return directory;
	}
	
	public Object[] getAdditionalArguments() {
		return otherArguments;
	}
	
	public boolean additionalArgumentPresent() {
		return otherArguments == null || otherArguments.length == 0;
	}
	
}
