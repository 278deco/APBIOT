package apbiot.core.io.objects;

import apbiot.core.objects.enums.FileType;

public abstract class IOElement {
	
	protected IOArguments classArguments;
	
	protected final Directory directory;
	protected final String fileName;
	
	public IOElement(IOArguments args) {
		this.classArguments = args;
		this.directory = this.classArguments.getDirectory();
		this.fileName = this.classArguments.getName();
	}
	
	public abstract boolean saveFile();
	public abstract boolean reloadFile();
	protected abstract boolean readFile();
	
	public abstract FileType getFileType();
	
	/**
	 * Get the absolute file path 
	 * @return the file path
	 */
	public Directory getDirectory() {
		return this.directory;
	}
	
	/**
	 * Get the file name if present, else optional while be empty
	 * @return the file name
	 */
	public String getFileName() {
		return this.fileName;
	}
}
