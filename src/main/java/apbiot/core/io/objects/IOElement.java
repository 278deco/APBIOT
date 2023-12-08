package apbiot.core.io.objects;

import java.io.IOException;

import apbiot.core.objects.enums.FileType;
import marshmalliow.core.objects.IOClass;
import marshmalliow.core.objects.Directory;

/**
 * 
 * @author 278deco
 * @deprecated 5.0
 * @see IOClass
 */
public abstract class IOElement {
	
	protected IOArguments classArguments;
	
	protected final Directory directory;
	protected final String fileName;
	
	public IOElement(IOArguments args) {
		this.classArguments = args;
		this.directory = this.classArguments.getDirectory();
		this.fileName = this.classArguments.getName();
	}
	
	public abstract boolean saveFile(boolean forceSave) throws IOException;
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
