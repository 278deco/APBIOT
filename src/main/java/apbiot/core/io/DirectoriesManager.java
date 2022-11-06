package apbiot.core.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DirectoriesManager {
	
	private final Map<String, File> directories = new HashMap<>();
	
	/**
	 * Register new directories used in the program
	 */
	public abstract void register();
	
	/**
	 * Add a new directory if it isn't already set
	 * @param directoryPath - the path of the directory
	 * @return if the directory has been added
	 */
	protected boolean addNewDirectory(String directoryPath) {
		return directories.putIfAbsent(directoryPath, new File(directoryPath)) == null;
	}
	
	/**
	 * Add a new directory even if it has already been set 
	 * @param directoryPath - the path of the directory
	 */
	protected void addNewDirectoryAnyways(String directoryPath) {
		 directories.put(directoryPath, new File(directoryPath));
	}
	
	/**
	 * Get a unmodifiable view of the stored directories. Changes in this object's map will appear in the returned map 
	 * @return an unmodifiable view of the directories
	 */
	public Map<String, File> getDirectories() {
		return Collections.unmodifiableMap(this.directories);
	}
}
