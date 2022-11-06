package apbiot.core.objects;

public class IOArguments {
	
	private String path;
	private String name;
	
	private Object[] otherArguments;
	
	public IOArguments(String filePath, String fileName, Object... arguments) {
		this.path = filePath;
		this.name = fileName;
		this.otherArguments = arguments;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPath() {
		return path;
	}
	
	public Object[] getOtherArguments() {
		return otherArguments;
	}
	
}
