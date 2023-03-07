package apbiot.core.io;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.io.json.JSONConfiguration;
import apbiot.core.objects.IOArguments;

public class IOManager {
	
	private static final String CONFIGURATION_FILE_NAME = "config.json";
	
	private static final Logger LOGGER = LogManager.getLogger(IOElement.class);
	
	private final Map<Class<? extends IOElement>, IOElement> files = new HashMap<>();
	private final DirectoriesManager dirManager;
	
	@Nullable
	private final Class<? extends JSONConfiguration> programConfigurationClass;
	@Nullable
	private JSONConfiguration programConfiguration;
	
	private boolean running;
	
	public IOManager(DirectoriesManager dirManager, Class<? extends JSONConfiguration> configuration) {
		LOGGER.info("Starting Input Output Communications");
		this.running = true;
		
		this.programConfigurationClass = configuration;
		this.dirManager = dirManager;
		
		registerDirectories();
		generateConfiguration();
	}
	
	public IOManager(DirectoriesManager dirManager) {
		LOGGER.info("Starting Input Output Communications");
		this.running = true;
		
		this.programConfigurationClass = null;
		this.dirManager = dirManager;
		
		registerDirectories();
	}
	
	private void registerConfigurationDirectory() {
		LOGGER.info("Registering configuration directory...");
		
		final File dir = this.dirManager.getConfigurationDirectory();
		if(dir.mkdirs()) LOGGER.info("Directory "+dir.getAbsolutePath()+" has been successfully created !"); 
		else LOGGER.info("Directory "+dir.getAbsolutePath()+" has been successfully loaded !");
	}
	
	private void registerDirectories() {
		this.dirManager.register();
		
		if(programConfiguration != null) registerConfigurationDirectory();
		
		LOGGER.info("Registering "+this.dirManager.getDirectoriesNumber()+" directories...");
		
		this.dirManager.getDirectories().values().forEach(file -> {
			try {
				if(file.mkdirs()) LOGGER.info("Directory "+file.getAbsolutePath()+" has been successfully created !"); 
				else LOGGER.info("Directory "+file.getAbsolutePath()+" has been successfully loaded !");
			}catch(SecurityException e) {
				LOGGER.error("An error occured while creating a directory",e);
				this.running = false;
			}
		});
		
		LOGGER.info("Every directory has been sucessfully registered");
	}
	
	private void generateConfiguration() {
		LOGGER.info("Loading program's configuration...");
		
		final JSONConfiguration temp = createFileObject(this.dirManager.getConfigurationDirectory().getAbsolutePath(), CONFIGURATION_FILE_NAME, this.programConfigurationClass, null);
		
		if(temp != null) {
			this.programConfiguration = temp;
			LOGGER.info("Successfully loaded and stored the program's configuration!");
		}
	}
	
	private boolean isDirectoryAlreadyCreated(String directory) {
		return this.dirManager.getDirectories().containsKey(directory);
	}
	
	private <E extends IOElement> E createFileObject(String fileDirectory, String fileName, Class<E> element, Object[] arguments) {
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
			LOGGER.error("Directory "+fileDirectory+" hasn't been loaded before. Please load a directory before using it");
			
			return null;
		}
	}
	
	public void reloadAllFiles() throws Exception {
		LOGGER.info("Reloading "+files.size()+" files...");
		
		for(IOElement element : files.values()) {
			element.reloadFile();
		}
		
		LOGGER.info("Successfully reloaded "+files.size()+" files");
	}
	
	public void saveAllFiles() throws Exception {
		LOGGER.info("Saving "+files.size()+" files...");
		
		for(IOElement element : files.values()) {
			element.saveFile();
		}
		
		LOGGER.info("Successfully saved "+files.size()+" files");
	}
	
	public void reloadConfiguration() throws Exception {
		if(programConfiguration == null) throw new IllegalAccessError("No program's configuration was found");
		LOGGER.info("Reloading program's configuration...");
		
		this.programConfiguration.reloadFile();
		
		LOGGER.info("Successfully reloaded program's configuration");
	}
	
	public void saveConfiguration() throws Exception {
		if(programConfiguration == null) throw new IllegalAccessError("No program's configuration was found");
		LOGGER.info("Saving program's configuration...");
		
		this.programConfiguration.saveFile();
		
		LOGGER.info("Successfully saved program's configuration");
	}
	
	/**
	 * Add a new file to the manager
	 * @param <E> - a file instance who extends IOElement
	 * @param fileDirectory - the directory where the file will be same. The directory must be already loaded
	 * @param fileName - the file name (It name and its extension)
	 * @param element - the file's class
	 * @param arguments - optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public <E extends IOElement> IOManager add(String fileDirectory, String fileName, Class<E> element, Object... arguments) {
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
	 * Add a new file to the manager. This method will add a file even if a mapping already existed for the file
	 * @param <E> - a file instance who extends IOElement
	 * @param fileDirectory - the directory where the file will be same. The directory must be already loaded
	 * @param fileName - the file name (It name and its extension)
	 * @param element - the file's class
	 * @param arguments - optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public <E extends IOElement> IOManager addAnyways(String fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+"...");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			files.put(element, object);
			LOGGER.info("Successfully loaded and stored "+element.getSimpleName()+" (Type: "+object.getFileType()+") !");
		}
		
		return this;
	}

	/**
	 * Remove an element from the list of accessible files. 
	 * Safe remove means that any change made to the original file that are not saved will be saved
	 * before removing it from the list
	 * @param <E> - a file instance who extends IOElement
	 * @param element - the file's class
	 * @return this instance of IOManager
	 * @throws Exception
	 */
	public <E extends IOElement> IOManager safeRemove(Class<E> element) throws Exception {
		files.get(element).saveFile();
		
		files.remove(element);
		return this;
	}
	
	/**
	 * Remove an element from the list of accessible files. 
	 * Hard remove means that any change made to the original file that are not saved will be erased
	 * @param <E> - a file instance who extends IOElement
	 * @param element - the file's class
	 * @return this instance of IOManager
	 */
	public <E extends IOElement> IOManager hardRemove(Class<E> element) {
		files.remove(element);
		return this;
	}
	
	/**
	 * Get the instance of a file
	 * @param <E> - a file instance who extends IOElement
	 * @param elementClass - the file's class
	 * @return the instance of the file saved in the list
	 */
	@SuppressWarnings("unchecked")
	public <E extends IOElement> E get(Class<E> element) {
		if(files.containsKey(element)) {
			return (E) files.get(element);
		}else if(this.programConfiguration != null && element.equals(this.programConfigurationClass)) {
			return (E) this.programConfiguration;
		}else {
			return null;
		}
	}
	
	public boolean isConfigurationPresent() {
		return this.programConfiguration != null;
	}
	
	public boolean isIOManagerUp() {
		return this.running;
	}
}
