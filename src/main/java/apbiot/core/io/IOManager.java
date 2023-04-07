package apbiot.core.io;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.io.json.JSONConfiguration;
import apbiot.core.io.objects.Directory;
import apbiot.core.io.objects.IOArguments;
import apbiot.core.io.objects.IOElement;

public class IOManager {
	
	private static final String CONFIGURATION_FILE_NAME = "config.json";
	
	private static final Logger LOGGER = LogManager.getLogger(IOManager.class);
	
	private static IOManager instance;
	
	private final Map<Class<? extends IOElement>, IOElement> files = new HashMap<>();
	private Set<Directory> directories = new HashSet<>();
	
	@Nullable
	private final Class<? extends JSONConfiguration> programConfigurationClass;
	@Nullable
	private JSONConfiguration programConfiguration;
	@Nullable
	private Directory programConfigurationDirectory;
	
	private IOManager(Set<Directory> directories, Directory configurationDirectory, Class<? extends JSONConfiguration> configuration) {
		LOGGER.info("Starting Input Output Communications...");
		
		this.programConfigurationClass = configuration;
		this.directories = directories;
		this.programConfigurationDirectory = configurationDirectory;
		
		if(configuration != null) generateConfiguration();
	}
	
	/**
	 * Create a new instance of {@link IOManager} as a singleton<br>
	 * If the instance already exist return the existing instance
	 * @param directories A set of directories
	 * @param configurationDirectory the directory of the configuration file
	 * @param configuration the class representing the configuration
	 * @return the instance of IOManager
	 * @see DirectoriesManager
	 * @see JSONConfiguration
	 */
	public static IOManager createInstance(Set<Directory> directories, Directory configurationDirectory, Class<? extends JSONConfiguration> configuration) {
		if(instance == null) {
			synchronized (IOManager.class) {
				if(instance == null) instance = new IOManager(directories, configurationDirectory, configuration);
			}
		}
		return instance;
	}
	
	/**
	 * Create a new instance of {@link IOManager} as a singleton<br>
	 * If the instance already exist return the existing instance
	 * @param directories A set of directories
	 * @return the instance of IOManager
	 * @see DirectoriesManager
	 */
	public static IOManager createInstance(Set<Directory> directories) {
		if(instance == null) {
			synchronized (IOManager.class) {
				if(instance == null) instance = new IOManager(directories, null, null);
			}
		}
		
		return instance;
	}
	
	/**
	 * Get the instance of IOManager as a singleton
	 * @return the instance of IOManager
	 */
	public static IOManager getInstance() {
		return instance;
	}
	
	/**
	 * Generate the program configuration file to be saved in this instance
	 */
	private void generateConfiguration() {
		LOGGER.info("Loading program's configuration...");
		
		final JSONConfiguration temp = createFileObject(this.programConfigurationDirectory.getPath().toAbsolutePath().toString(), CONFIGURATION_FILE_NAME, this.programConfigurationClass, null);
		
		if(temp != null) {
			this.programConfiguration = temp;
			LOGGER.info("Successfully loaded and stored the program's configuration!");
		}
	}
	
	/**
	 * Check if the directory wanted is loaded and ready to use
	 * @param directory The directory wanted 
	 * @return true if it's present
	 */
	private boolean isDirectoryAlreadyCreated(Directory directory) {
		return this.directories.contains(directory);
	}
	
	/**
	 * Create a new file object stored in the IOManager
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file will be same. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return the file instance
	 */
	private <E extends IOElement> E createFileObject(Directory fileDirectory, String fileName, Class<E> element, Object[] arguments) {
		if(isDirectoryAlreadyCreated(fileDirectory)) {
			E object = null;
			try {
				Constructor<E> constructor = element.getConstructor(IOArguments.class);
				
				object = constructor.newInstance(new IOArguments(fileDirectory, fileName, arguments));
			} catch (NoSuchMethodException | IllegalArgumentException e) {
				LOGGER.error("Unexpected error while loading "+element.getSimpleName()+". Cannot find the right constructor",e);
			} catch (SecurityException | IllegalAccessException e) {
				LOGGER.error("Unexpected error while loading "+element.getSimpleName()+". Access denied",e);
			} catch (InstantiationException e) {
				LOGGER.error("Unexpected error while loading "+element.getSimpleName()+". The class cannot be instancied",e);
			} catch (InvocationTargetException e) {
				LOGGER.error("An error occured in "+element.getSimpleName()+"'s contructor",e);
			}
			
			return object;
		}else {
			LOGGER.error("Directory "+fileDirectory.getName()+" hasn't been loaded before. Please load a directory before using it");
			
			return null;
		}
	}
	
	/**
	 * Create a new file object stored in the IOManager
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file will be same. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return the file instance
	 */
	private <E extends IOElement> E createFileObject(String fileDirectory, String fileName, Class<E> element, Object[] arguments) {
		return this.createFileObject(new Directory(fileDirectory), fileName, element, arguments);
	}
	
	/**
	 * Reload all files from the disk saved in this IOManager instance
	 * @throws Exception
	 */
	public void reloadAllFiles() throws Exception {
		LOGGER.info("Reloading "+files.size()+" files...");
		
		for(IOElement element : files.values()) {
			element.reloadFile();
		}
		
		LOGGER.info("Successfully reloaded "+files.size()+" files");
	}
	
	/**
	 * Save all saved files in this IOManager instance
	 * @throws Exception
	 */
	public void saveAllFiles() throws Exception {
		LOGGER.info("Saving "+files.size()+" files...");
		
		for(IOElement element : files.values()) {
			element.saveFile();
		}
		
		LOGGER.info("Successfully saved "+files.size()+" files");
	}
	
	/**
	 * Reload the program configuration from the disk if it exists else throw an IllegalAccessError
	 * @throws Exception
	 */
	public void reloadConfiguration() throws Exception {
		if(programConfiguration == null) throw new IllegalAccessError("No program's configuration was found");
		LOGGER.info("Reloading program's configuration...");
		
		this.programConfiguration.reloadFile();
		
		LOGGER.info("Successfully reloaded program's configuration");
	}
	
	/**
	 * Save the program configuration if it exists else throw an IllegalAccessError
	 * @throws Exception
	 */
	public void saveConfiguration() throws Exception {
		if(programConfiguration == null) throw new IllegalAccessError("No program's configuration was found");
		LOGGER.info("Saving program's configuration...");
		
		this.programConfiguration.saveFile();
		
		LOGGER.info("Successfully saved program's configuration");
	}
	
	/**
	 * Add a new file to the manager
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file will be same. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager add(String fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+"...");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			if(files.putIfAbsent(element, object) != null)
				LOGGER.info("Couldn't load and stored "+element.getSimpleName()+" because a mapping already exist for the file !");
			else
				LOGGER.info("Successfully loaded and stored "+element.getSimpleName()+" (Type: "+object.getFileType()+") !");
		}
		
		return this;
	}
	
	/**
	 * Add a new file to the manager
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file will be same. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 * @see Directory
	 */
	public synchronized <E extends IOElement> IOManager add(Directory fileDirectory, String fileName, Class<E> element, Object... arguments) {
		return this.add(fileDirectory.getPath().toAbsolutePath().toString(), fileName, element, arguments);
	}
	
	/**
	 * Add a new file to the manager. This method will add a file even if a mapping already existed for the file
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file will be same. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager addAnyways(String fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+"...");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			files.put(element, object);
			LOGGER.info("Successfully loaded and stored "+element.getSimpleName()+" (Type: "+object.getFileType()+") !");
		}
		
		return this;
	}
	
	public synchronized <E extends IOElement> IOManager addAnyways(Directory fileDirectory, String fileName, Class<E> element, Object... arguments) {
		return this.addAnyways(fileDirectory.getPath().toAbsolutePath().toString(), fileName, element, arguments);
	}
	/**
	 * Remove an element from the list of accessible files. <br>
	 * Safe remove means that any change made to the original file that are not saved will be saved
	 * before removing it from the list
	 * @param <E> a file instance who extends IOElement
	 * @param element the file's class
	 * @return this instance of IOManager
	 * @throws Exception
	 */
	public synchronized <E extends IOElement> IOManager safeRemove(Class<E> element) throws Exception {
		files.get(element).saveFile();
		
		files.remove(element);
		return this;
	}
	
	/**
	 * Remove an element from the list of accessible files. <br>
	 * Hard remove means that any change made to the original file that are not saved will be erased
	 * @param <E> a file instance who extends IOElement
	 * @param element the file's class
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager hardRemove(Class<E> element) {
		files.remove(element);
		return this;
	}
	
	/**
	 * Get the instance of a file
	 * @param <E> a file instance who extends IOElement
	 * @param elementClass the file's class
	 * @return the instance of the file saved in the list
	 */
	public <E extends IOElement> E get(Class<E> element) {
		if(files.containsKey(element)) {
			return element.cast(files.get(element));
		}else if(this.programConfiguration != null && element.equals(this.programConfigurationClass)) {
			return element.cast(this.programConfiguration);
		}else {
			return null;
		}
	}
	
	/**
	 * Return true if a configuration file is present and loaded in the IOManager class
	 * @return a boolean telling if it present or not
	 * @see JSONConfiguration
	 */
	public boolean isConfigurationPresent() {
		return this.programConfiguration != null;
	}
}
