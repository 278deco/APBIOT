package apbiot.core.io;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.io.objects.Directory;

public class DirectoriesManager {
	
	private static final Logger LOGGER = LogManager.getLogger(DirectoriesManager.class);
	
	private Set<Directory> directories = new HashSet<>();
	private Directory configurationDirectory = null;
	
	private DirectoriesManager(Builder builder) {
		this.directories = builder.getDirectories();
		this.configurationDirectory = builder.getConfigurationDirectory();
	}
	
	public void registerDirectories() {		
		if(configurationDirectory != null) registerConfigurationDirectory();
		
		LOGGER.info("Registering "+this.getDirectoriesNumber()+" directories...");
		
		this.directories.forEach(dir -> {
			try {
				if(Files.createDirectories(dir.getPath()) != null) LOGGER.info("Directory "+dir.getName()+" has been successfully created !"); 
				else LOGGER.info("Directory "+dir.getName()+" has been successfully loaded !");
			}catch(SecurityException | IOException e) {
				LOGGER.error("An error occured while creating directory",dir.getName(),". Skipping 1 directory.",e.getMessage());
			}
		});
		
		LOGGER.info("Every directory has been sucessfully registered");
	}
	
	private void registerConfigurationDirectory() {
		LOGGER.info("Registering configuration directory...");
		
		try {
			if(Files.createDirectories(this.configurationDirectory.getPath()) != null) LOGGER.info("Directory "+this.configurationDirectory.getName()+" has been successfully created !"); 
			else LOGGER.info("Directory "+this.configurationDirectory.getName()+" has been successfully loaded !");
		} catch (IOException e) {
			LOGGER.error("An error occured while creating directory",this.configurationDirectory.getName(),". Skipping 1 directory.",e.getMessage());
		}
	}
	
	public Directory getLoadedDirectory(String name) {
		return this.directories.stream().filter(dir -> dir.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}
	
	/**
	 * Get a unmodifiable view of the stored directories. Changes in this object's map will appear in the returned map 
	 * @return an unmodifiable view of the directories
	 */
	public Set<Directory> getLoadedDirectories() {
		return Collections.unmodifiableSet(this.directories);
	}
	
	/**
	 * Get an instance of File pointing to the configuration directory
	 * @return an File's instance of the configuration directory or null if the directory hasn't been set
	 */
	public Directory getConfigurationDirectory() {
		return isConfigurationDirectorySet() ? this.configurationDirectory : null;
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
	
	public static DirectoriesManager.Builder builder() {
		return new DirectoriesManager.Builder();
	}
	
	public static class Builder {
		
		private final Set<Directory> directories = new HashSet<>();
		private Directory configurationDirectory = null;
		
		private Builder() { }
		
		/**
		 * Define the directory used by the configuration
		 * @param directoryPath - the path of the directory
		 * @return if the directory has already been set by the past
		 */
		public Builder configurationDirectory(Directory dir) {
			this.configurationDirectory = dir;
			return this;
		}
		
		/**
		 * Add a new directory if it isn't already set
		 * @param directoryPath - the path of the directory
		 * @return if the directory has been added
		 */
		public Builder newDirectory(Directory dir) {
			directories.add(dir);
			return this;
		}
		
		public DirectoriesManager build() {
			return new DirectoriesManager(this);
		}
		
		private Directory getConfigurationDirectory() {
			return configurationDirectory;
		}
		
		private Set<Directory> getDirectories() {
			return directories;
		}
	}
}
