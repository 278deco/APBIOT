package apbiot.core.io;

import apbiot.core.objects.IOArguments;
import apbiot.core.objects.enums.FileType;

public abstract class IOElement {
	
	protected IOArguments classArguments;
	
	protected final String filePath;
	protected final String fileName;
	
	public IOElement(IOArguments args) {
		this.classArguments = args;
		this.filePath = this.classArguments.getPath();
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
	public String getFilePath() {
		return this.filePath;
	}
	
	/**
	 * Get the file name if present, else optional while be empty
	 * @return the file name
	 */
	public String getFileName() {
		return this.fileName;
	}
}
