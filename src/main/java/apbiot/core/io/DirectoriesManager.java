package apbiot.core.io;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class DirectoriesManager {
	
	private final Map<String, File> directories = new HashMap<>();
	private String configurationDirectory = null;
	
	/**
	 * Register new directories used in the program
	 */
	public abstract void register();
	
	/**
	 * Define the directory used by the configuration
	 * @param directoryPath - the path of the directory
	 * @return if the directory has already been set by the past
	 */
	protected boolean setConfigurationDirectory(String directoryPath) {
		final boolean ret = this.configurationDirectory == null;
		this.configurationDirectory = directoryPath;
		return ret;
	}
	
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
	
	/**
	 * Get an instance of File pointing to the configuration directory
	 * @return an File's instance of the configuration directory or null if the directory hasn't been set
	 */
	public File getConfigurationDirectory() {
		return isConfigurationDirectorySet() ? new File(this.configurationDirectory) : null;
	}
	
	/**
	 * Check if the configuration's directory exist
	 * @return if it exist
	 */
	public boolean isConfigurationDirectorySet() {
		return this.configurationDirectory != null;
	}
	
	/**
	 * Get the total of stored directories
	 * @return the size of the directories's list
	 */
	public int getDirectoriesNumber() {
		return this.directories.size();
	}
}
