package apbiot.core.io;

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
	
	private static final Logger LOGGER = LogManager.getLogger(IOElement.class);
	
	private final Map<Class<? extends IOElement>, IOElement> files = new HashMap<>();
	private final DirectoriesManager dirManager;
	
	@Nullable
	private final JSONConfiguration programConfiguration;

	private boolean running;
	
	public IOManager(DirectoriesManager dirManager, JSONConfiguration configuration) {
		LOGGER.info("Starting Input Output Communications");
		this.running = true;
		
		this.programConfiguration = configuration;
		this.dirManager = dirManager;
		
		registerDirectories();
	}
	
	public IOManager(DirectoriesManager dirManager) {
		LOGGER.info("Starting Input Output Communications");
		this.running = true;
		
		this.programConfiguration = null;
		this.dirManager = dirManager;
		
		registerDirectories();
	}
	
	private void registerDirectories() {
		LOGGER.info("Register every directories");
		this.dirManager.register();
		
		this.dirManager.getDirectories().values().forEach(file -> {
			try {
				if(file.mkdirs()) LOGGER.info("Directory "+file.getAbsolutePath()+" has been successfully created !"); 
				else LOGGER.info("Directory "+file.getAbsolutePath()+" has been successfully loaded !");
			}catch(SecurityException e) {
				LOGGER.error("An error occured while creating a directory",e);
				this.running = false;
			}
		});
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
				LOGGER.error("Unexpected error while loading "+element.getSimpleName()+".Access denied",e);
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
	
	/**
	 * Add a new file to the manager
	 * @param <E> - a file instance who extends IOElement
	 * @param fileDirectory - the directory where the file will be same. The directory must be already loaded
	 * @param fileName - the file name (It name and its extension)
	 * @param element - the file class
	 * @param arguments - optional argument needed by the file
	 * @return
	 */
	public <E extends IOElement> IOManager add(String fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+"...");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			files.putIfAbsent(element, object);
			LOGGER.info("Successfully loaded and stored "+element.getSimpleName()+" !");
		}
		
		return this;
	}
	
	public <E extends IOElement> IOManager addAnyways(String fileDirectory, String fileName, Class<E> element, Object... arguments) {
		LOGGER.info("Loading "+element.getSimpleName()+"...");
		
		E object = createFileObject(fileDirectory, fileName, element, arguments);
		
		if(object != null) {
			files.put(element, object);
			LOGGER.info("Successfully loaded and stored "+element.getSimpleName()+" !");
		}
		
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <E extends IOElement> E get(Class<E> elementClass) {
		if(files.containsKey(elementClass)) {
			return (E) files.get(elementClass);
		}
		
		return null;
	}
	
	public boolean isIOManagerUp() {
		return this.running;
	}
}
