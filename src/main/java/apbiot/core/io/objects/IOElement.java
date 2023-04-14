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
	
	public abstract void saveFile() throws Exception;
	public abstract void reloadFile() throws Exception;
	protected abstract void readFile() throws Exception;
	
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
