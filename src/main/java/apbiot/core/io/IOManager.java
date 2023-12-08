package apbiot.core.io;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.exceptions.NonExistingFileInstanceException;
import apbiot.core.helper.DirectoryHelper;
import apbiot.core.io.json.types.JSONProperties;
import apbiot.core.io.objects.IOArguments;
import apbiot.core.io.objects.IOElement;
import apbiot.core.pems.EventListener;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import marshmalliow.core.objects.Directory;

/**
 * Handled all Input/Output exchanges
 * @author 278deco
 * @deprecated since 5.0.0
 */
public class IOManager {
	
	@SuppressWarnings("unused")
	private static final String CONFIGURATION_FILE_NAME = "config.json";
	
	private static final Logger LOGGER = LogManager.getLogger(IOManager.class);
	
	private static IOManager instance;
	private IOManagerEventListener instanceEventListener;
	
	private final Map<Class<? extends IOElement>, IOElement> uniqueFiles = new HashMap<>();
	private final Map<String, IOElement> files = new HashMap<>();
	private Set<Directory> directories = new HashSet<>();
	
	@Nullable
	private final Class<? extends JSONProperties> programConfigurationClass;
	@Nullable
	private JSONProperties programConfiguration;
	@Nullable
	private Directory programConfigurationDirectory;
	
	private IOManager(Set<Directory> directories, Directory configurationDirectory, Class<? extends JSONProperties> configuration) {
		LOGGER.info("Starting Input Output management...");
		
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
	 * @see JSONProperty
	 */
	public static IOManager createInstance(Set<Directory> directories, Directory configurationDirectory, Class<? extends JSONProperties> configuration) {
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
	
	public IOManagerEventListener getEventListener() {
		if(instanceEventListener == null) {
			synchronized (IOManagerEventListener.class) {
				if(instanceEventListener == null) instanceEventListener = new IOManagerEventListener();
			}
		}
		
		return instanceEventListener;
	}
	
	/**
	 * Generate the program configuration file to be saved in this instance
	 */
	@SuppressWarnings("unused")
	private void generateConfiguration() {
		LOGGER.info("Loading program's configuration...");
		
		final JSONProperties temp = null;/*createFileObject(this.programConfigurationDirectory, CONFIGURATION_FILE_NAME, this.programConfigurationClass, null);*/
		
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
		return this.directories.contains(directory) || programConfigurationDirectory.equals(directory);
	}
	
	/**
	 * Create a new file object stored in the IOManager
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file is saved or will be saved. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return the file instance
	 */
	private <E extends IOElement> E createFileObject(Directory fileDirectory, String fileName, Class<E> element, Object[] arguments) {
		if(fileDirectory == null) { 
			LOGGER.error("Unexpected error while loading "+element.getSimpleName()+". Provided directory is null!");
			return null;
		}else if(isDirectoryAlreadyCreated(fileDirectory)) {
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
	 * Reload all files from the disk saved in this IOManager instance
	 * @throws Exception
	 */
	public void reloadFiles() throws Exception {
		LOGGER.info("Reloading "+files.size()+" files...");
		
		for(IOElement element : files.values()) {
			element.reloadFile();
		}
		
		LOGGER.info("Successfully reloaded "+files.size()+" files!");
	}
	
	/**
	 * Reload all unique files from the disk saved in this IOManager instance
	 * @throws Exception
	 */
	public void reloadUniqueFiles() throws Exception {
		LOGGER.info("Reloading "+uniqueFiles.size()+" files...");
		
		for(IOElement element : uniqueFiles.values()) {
			element.reloadFile();
		}
		
		LOGGER.info("Successfully reloaded "+uniqueFiles.size()+" files!");
	}
	
	/**
	 * Save all files in this IOManager instance
	 * @throws Exception
	 */
	public void saveFiles() throws Exception {
		LOGGER.info("Saving "+files.size()+" files...");
		int error = 0;
		
		for(IOElement element : files.values()) {
			try {
				element.saveFile(false);
			}catch(IOException e) {
				error+=1;
			}
		}
		
		LOGGER.info("Successfully saved "+files.size()+" files! [errors occured:{}]", error);
	}
	
	/**
	 * Save all files in this IOManager instance
	 * @param forceSave Force the file to save itself even if no modification were made to its content
	 * @throws Exception
	 */
	public void saveFiles(boolean forceSave) throws Exception {
		LOGGER.info("Saving "+files.size()+" files...");
		int error = 0;
		
		for(IOElement element : files.values()) {
			try {
				element.saveFile(forceSave);
			}catch(IOException e) {
				error+=1;
			}
		}
		
		LOGGER.info("Successfully saved "+files.size()+" files! [errors occured:{}]", error);
	}
	
	/**
	 * Save all unique files in this IOManager instance
	 * @throws Exception
	 */
	public void saveUniqueFiles() throws Exception {
		LOGGER.info("Saving "+uniqueFiles.size()+" files...");
		int error = 0;
		
		for(IOElement element : uniqueFiles.values()) {
			try {
				element.saveFile(false);
			}catch(IOException e) {
				error+=1;
			}
		}
		
		LOGGER.info("Successfully saved "+uniqueFiles.size()+" files! [errors occured:{}]", error);
	}
	
	/**
	 * Save all unique files in this IOManager instance
	 * @param forceSave Force the file to save itself even if no modification were made to its content
	 * @throws Exception
	 */
	public void saveUniqueFiles(boolean forceSave) throws Exception {
		LOGGER.info("Saving "+uniqueFiles.size()+" files...");
		int error = 0;
		
		for(IOElement element : uniqueFiles.values()) {
			try {
				element.saveFile(forceSave);
			}catch(IOException e) {
				error+=1;
			}
		}
		
		LOGGER.info("Successfully saved "+files.size()+" files! [errors occured:{}]", error);
	}
	
	/**
	 * Reload the program configuration from the disk if it exists else throw an IllegalAccessError
	 * @throws Exception
	 */
	public void reloadConfiguration() throws Exception {
		if(programConfiguration == null) throw new IllegalAccessError("No program's configuration was found");
		LOGGER.info("Reloading program's configuration...");
		
		/*this.programConfiguration.reloadFile();*/
		
		LOGGER.info("Successfully reloaded program's configuration");
	}
	
	/**
	 * Save the program configuration if it exists else throw an IllegalAccessError
	 * @throws Exception
	 */
	public void saveConfiguration() throws Exception {
		if(programConfiguration == null) throw new IllegalAccessError("No program's configuration was found");
		LOGGER.info("Saving program's configuration...");
		
		this.programConfiguration.saveFile(false);
		
		LOGGER.info("Successfully saved program's configuration");
	}
	
	/**
	 * Save the program configuration if it exists else throw an IllegalAccessError
	 * @param forceSave Force the file to save itself even if no modification were made to its content
	 * @throws Exception
	 */
	public void saveConfiguration(boolean forceSave) throws Exception {
		if(programConfiguration == null) throw new IllegalAccessError("No program's configuration was found");
		LOGGER.info("Saving program's configuration...");
		
		this.programConfiguration.saveFile(forceSave);
		
		LOGGER.info("Successfully saved program's configuration");
	}
	
	/**
	 * Add a new file to the manager<br>
	 * The file must be represented by its own class in the program
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectoryID the name id of the directory where the is saved or will be saved. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager addUniqueFile(String fileDirectoryID, String fileName, Class<E> element, Object... arguments) {
		return this.addUniqueFile(DirectoryHelper.getDirectoryByName(directories, fileDirectoryID), fileName, element, arguments);
	}
	
	/**
	 * Add a new file to the manager<br>
	 * The file must be represented by its own class in the program
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file is saved or will be saved. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 * @see Directory
	 */
	public synchronized <E extends IOElement> IOManager addUniqueFile(Directory fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+"...");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			if(uniqueFiles.putIfAbsent(element, object) != null)
				LOGGER.info("Couldn't load and stored "+element.getSimpleName()+" because a mapping already exist for the file !");
			else
				LOGGER.info("Successfully loaded and stored "+element.getSimpleName()+" (Type: "+object.getFileType()+") !");
		}
		
		return this;
	}
	
	/**
	 * Add a new file to the manager<br>
	 * The file will be registered in the manager with its id (AKA the name of the file on the disk without extension)<br>
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectoryID the name id of the directory where the file is saved or will be saved. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager addFile(String fileDirectoryID, String fileName, Class<E> element, Object... arguments) {
		return this.addFile(DirectoryHelper.getDirectoryByName(directories, fileDirectoryID), fileName, element, arguments);
	}
	
	/**
	 * Add a new file to the manager<br>
	 * The file will be registered in the manager with its id (AKA the name of the file on the disk without extension)<br>
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file is saved or will be saved. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 * @see Directory
	 */
	public synchronized <E extends IOElement> IOManager addFile(Directory fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+" [file: "+fileName+"]...");

		final String[] splitName = fileName.split("\\.");
		if(splitName.length < 2) throw new IllegalArgumentException("Invalid resource name. Must be composed of a name and a extension!");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			if(files.putIfAbsent(splitName[0], object) != null)
				LOGGER.info("Couldn't load and stored "+element.getSimpleName()+" because a mapping already exist for the file !");
			else
				LOGGER.info("Successfully loaded and stored "+splitName[0]+" (Type: "+object.getFileType()+") !");
		}
		
		return this;
	}
	
	/**
	 * Add a new file to the manager. This method will add a file even if a mapping already existed for the file<br>
	 * The file must be represented by its own class in the program
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectoryID the name id of directory where the file is saved or will be saved. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager addUniqueFileAnyways(String fileDirectoryID, String fileName, Class<E> element, Object... arguments) {
		return this.addUniqueFileAnyways(DirectoryHelper.getDirectoryByName(directories, fileDirectoryID), fileName, element, arguments);
	}
	
	/**
	 * Add a new file to the manager. This method will add a file even if a mapping already existed for the file<br>
	 * The file must be represented by its own class in the program
	 * @param <E> a file instance who extends IOElement
	 * @param fileDirectory the directory where the file is saved or will be saved. The directory must be already loaded
	 * @param fileName the file name (It name and its extension)
	 * @param element the file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager addUniqueFileAnyways(Directory fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+" [file: "+fileName+"]...");
		
		final String[] splitName = fileName.split("\\.");
		if(splitName.length < 2) throw new IllegalArgumentException("Invalid resource name. Must be composed of a name and a extension!");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			files.put(splitName[0], object);
			LOGGER.info("Successfully loaded and stored "+splitName[0]+" (Type: "+object.getFileType()+") !");
		}
		
		return this;
	}
	
	/**
	 * Add a new file to the manager. This method will add a file even if a mapping already existed for the file<br>
	 * The file will be registered in the manager with its id (AKA the name of the file on the disk without extension)<br>
	 * @param <E> A file instance who extends IOElement
	 * @param fileDirectoryID The id of the directory where the file is saved or will be saved. The directory must be already loaded
	 * @param fileName The file name (It name and its extension)
	 * @param element The file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager addFileAnyways(String fileDirectoryID, String fileName, Class<E> element, Object... arguments) {
		return this.addUniqueFileAnyways(DirectoryHelper.getDirectoryByName(directories, fileDirectoryID), fileName, element, arguments);
	}
	
	/**
	 * Add a new file to the manager. This method will add a file even if a mapping already existed for the file<br>
	 * The file will be registered in the manager with its id (AKA the name of the file on the disk without extension)<br>
	 * @param <E> A file instance who extends IOElement
	 * @param fileDirectory The directory where the file is saved or will be saved. The directory must be already loaded
	 * @param fileName The file name (It name and its extension)
	 * @param element The file's class
	 * @param arguments optional argument needed by the file
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager addFileAnyways(Directory fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+"...");
		
		final String[] splitName = fileName.split("\\.");
		if(splitName.length < 2) throw new IllegalArgumentException("Invalid resource name. Must be composed of a name and a extension!");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			files.put(splitName[0], object);
			LOGGER.info("Successfully loaded and stored "+splitName[0]+" (Type: "+object.getFileType()+") !");
		}
		
		return this;
	}
	
	/**
	 * Remove an element from the list of accessible files. <br>
	 * Safe remove means that any change made to the original file that are not saved will be saved
	 * before removing it from the list<br>
	 * The file to be removed must be a file represented by its own class in the program
	 * @param <E> a file instance who extends IOElement
	 * @param element the file's class
	 * @return this instance of IOManager
	 * @throws Exception
	 */
	public synchronized <E extends IOElement> IOManager safeRemoveUniqueFile(Class<E> element) throws Exception {
		uniqueFiles.get(element).saveFile(true);
		
		uniqueFiles.remove(element);
		return this;
	}
	
	/**
	 * Remove an element from the list of accessible files. <br>
	 * Hard remove means that any change made to the original file that are not saved will be erased<br>
	 * The file to be removed must be a file represented by its own class in the program
	 * @param <E> a file instance who extends IOElement
	 * @param element the file's class
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager hardRemoveUniqueFile(Class<E> element) {
		uniqueFiles.remove(element);
		return this;
	}
	
	/**
	 * Remove an element from the list of accessible files. <br>
	 * Safe remove means that any change made to the original file that are not saved will be saved
	 * before removing it from the list<br>
	 * The file to be removed must be registered with its id<br>
	 * <strong>ID :</strong> name of the file on the disk
	 * @param <E> a file instance who extends IOElement
	 * @param element the file's class
	 * @return this instance of IOManager
	 * @throws Exception
	 */
	public synchronized <E extends IOElement> IOManager safeRemoveFile(String fileID) throws Exception {
		files.get(fileID).saveFile(true);
		
		files.remove(fileID);
		return this;
	}
	
	/**
	 * Remove an element from the list of accessible files. <br>
	 * Hard remove means that any change made to the original file that are not saved will be erased<br>
	 * The file to be removed must be registered with its id<br>
	 * <strong>ID :</strong> name of the file on the disk
	 * @param <E> a file instance who extends IOElement
	 * @param element the file's class
	 * @return this instance of IOManager
	 */
	public synchronized <E extends IOElement> IOManager hardRemoveFile(String fileID) {
		files.remove(fileID);
		return this;
	}
	
	/**
	 * Get the instance of a file
	 * @param <E> a file instance who extends IOElement
	 * @param elementClass the file's class
	 * @return the instance of the file saved in the list
	 */
	public <E extends IOElement> E get(Class<E> element) {
		if(uniqueFiles.containsKey(element)) {
			return element.cast(uniqueFiles.get(element));
		}else if(this.programConfiguration != null && element.equals(this.programConfigurationClass)) {
			return element.cast(this.programConfiguration);
		}else {
			throw new NonExistingFileInstanceException("Couldn't find file in IOManager instance [class: "+element.getSimpleName()+"]");
		}
	}
	
	/**
	 * Get the instance of a file
	 * @param <E> a file instance who extends IOElement
	 * @param cls The class object used to store the file in the program
	 * @param fileID The id of the stored file
	 * @return the instance of the file saved in the list
	 */
	public <E extends IOElement> E get(Class<E> cls, String fileID) {
		if(files.containsKey(fileID)) {
			return cls.cast(files.get(fileID));
		}else {
			throw new NonExistingFileInstanceException("Couldn't find file in IOManager instance [id: "+fileID+", type: "+cls.getSimpleName()+"]");
		}
	}
	
	/**
	 * Return true if a configuration file is present and loaded in the IOManager class
	 * @return a boolean telling if it present or not
	 * @see JSONProperty
	 */
	public boolean isConfigurationPresent() {
		return this.programConfiguration != null;
	}
	
	/**
	 * Return the total of files saved in this instance of {@link IOManager}
	 * @return the number of files
	 */
	public int getFilesNumber() {
		return this.files.size();
	}
	
	/**
	 * Return the total of unique files saved in this instance of {@link IOManager}
	 * @return the number of unique files
	 */
	public int getUniqueFilesNumber() {
		return this.uniqueFiles.size();
	}
	
	/**
	 * Return the total of every files saved in this instance of {@link IOManager}
	 * @return the number of unique files
	 * @see IOManager#getFilesNumber()
	 * @see IOManager#getUniqueFilesNumber()
	 */
	public int getTotalFilesNumber() {
		return getFilesNumber()+getUniqueFilesNumber();
	}
	
	
	/**
	 * Subclass of {@link IOManager} dedicated to listen about the different event of the program
	 * @author 278deco
	 * @deprecated since 5.0
	 */
	public class IOManagerEventListener implements EventListener {

		@Override
		public void onEventReceived(ProgramEvent e, EventPriority priority) {
			
		}
	}
}


