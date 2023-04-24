package apbiot.core.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.spi.DirectoryManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.event.EventDispatcher;
import apbiot.core.event.events.io.EventResourceDeleted;
import apbiot.core.io.objects.Directory;
import apbiot.core.io.resources.AbstractBuffer;
import apbiot.core.io.resources.Resource;

public class ResourceManager {

	private static final Logger LOGGER = LogManager.getLogger(ResourceManager.class);
	
	private static ResourceManager instance;
	
	private final Set<Directory> directories;
	private final Map<Class<? extends AbstractBuffer>, AbstractBuffer> buffers = new HashMap<>();
	private final EventDispatcher ioEventDispatcher = new EventDispatcher();
	
	private ResourceManager(Set<Directory> directories) { 
		this.directories = directories;
	}
	
	/**
	 * Create a new instance of {@link ResourceManager} as a singleton<br>
	 * If the instance already exist return the existing instance
	 * @param directories A set of directories
	 * @return the instance of ResourceManager
	 * @see DirectoriesManager
	 */
	public static ResourceManager createInstance(Set<Directory> directories) {
		if(instance == null) {
			synchronized (ResourceManager.class) {
				if(instance == null) instance = new ResourceManager(directories);
			}
		}
		
		return instance;
	}
	
	/**
	 * Check if the instance of ResourceManager has already been created
	 * @return if the instance exist or not
	 */
	public static boolean doesInstanceExist() {
		return instance != null;
	}
	
	/**
	 * Get the instance of {@link ResourceManager} as a singleton
	 * @return the instance of ResourceManager
	 */
	public static ResourceManager getInstance() {
		return instance;
	}
	
	/**
	 * Get a resource from the disk and use it as a {@link Resource} in the program<br>
	 * The resource can only be obtained from directory that have been loaded by {@link DirectoryManager}
	 * @param resourceDir The directory where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @param isErasable Tells the program that the resource could be deleted from memory if the it runs out of space
	 * @return The newly created resource class
	 * @throws IOException
	 */
	public Resource getResource(Directory resourceDir, String resource, boolean isErasable) throws IOException {
		final String[] splitName = resource.split("\\.");
		
		if(!isDirectoryExisting(resourceDir)) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start!");
		if(splitName.length < 2) throw new IllegalArgumentException("Invalid resource name. Must be composed of a name and a extension!");
	
		byte[] fileResult = null;
		FileInputStream inputStream = null;
		BufferedInputStream buffer = null;
		
		try {
			inputStream = new FileInputStream(resourceDir.getPath().resolve(resource).toFile());
			//buffer = new BufferedInputStream(inputStream);
			
			fileResult = inputStream.readAllBytes();
			
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			if(buffer != null) buffer.close();
			if(inputStream != null) inputStream.close();
		}
		
		return new Resource(resourceDir, splitName[0], splitName[1], fileResult, isErasable);
		
	}
	
	/**
	 * Get a resource from the disk and use it as a {@link Resource} in the program<br>
	 * The resource can only be obtained from directory that have been loaded by {@link DirectoryManager}
	 * @param resourcePath The path where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @param isErasable Tells the program that the resource could be deleted from memory if the it runs out of space
	 * @return The newly created resource class
	 * @throws IOException
	 */
	public Resource getResource(Path resourcePath, String resource, boolean isErasable) throws IOException {
		return this.getResource(new Directory(resourcePath), resource, isErasable);
	}
	
	/**
	 * Get a resource from the disk and use it as a {@link Resource} in the program<br>
	 * The resource can only be obtained from directory that have been loaded by {@link DirectoryManager}<br>
	 * By default this method will set the flag <i>isErasable</i> to true
	 * @param resourcePath The path where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @return The newly created resource class
	 * @throws IOException
	 * @see {@link ResourceManager#getResource(Path, String, isErasable)}
	 */
	public Resource getResource(Path resourcePath, String resource) throws IOException {
		return getResource(resourcePath, resource, true);
	}
	
	/**
	 * Get a resource from the disk and use it as a {@link Resource} in the program<br>
	 * The resource can only be obtained from directory that have been loaded by {@link DirectoryManager}<br>
	 * By default this method will set the flag <i>isErasable</i> to true
	 * @param resourceDir The directory where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @return The newly created resource class
	 * @throws IOException
	 * @see {@link ResourceManager#getResource(Directory, String, isErasable)}
	 */
	public Resource getResource(Directory resourceDir, String resource) throws IOException {
		return getResource(resourceDir, resource, true);
	}
	
	/**
	 * Save a resource to the disk<br>
	 * @param resource The resource to be saved
	 * @throws IOException
	 */
	public void saveResource(Resource resource) throws IOException {
		if(!isDirectoryExisting(resource.getDirectory())) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start !");
		final Path filePath = resource.getDirectory().getPath().resolve(resource.getFileName());
		
		if(!Files.exists(filePath)) Files.createFile(filePath);
		
		try (FileOutputStream stream = new FileOutputStream(filePath.toFile())) {
			stream.write(resource.getData());
		}
	}
	
	/**
	 * Save a resource element to the disk<br>
	 * The resource can only be saved in directory that have been loaded by {@link DirectoryManager}<br>
	 * @param resourcePath The path where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @param data The data to be saved in the file
	 * @throws IOException
	 */
	public void saveResource(Path resourcePath, String resource, byte[] data) throws IOException {
		if(!isDirectoryExisting(resourcePath)) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start !");
		
		final Path filePath = resourcePath.resolve(resource);
		
		if(!Files.exists(filePath)) Files.createFile(filePath);
		
		try (FileOutputStream stream = new FileOutputStream(filePath.toFile())) {
			stream.write(data);
		}
	}
	
	/**
	 * Delete a resource present on the disk<br>
	 * @param rsc The resource to be deleted
	 * @return if the resource have been correctly deleted
	 * @throws IOException
	 */
	public boolean deleteResource(Resource rsc) throws IOException {
		if(!isDirectoryExisting(rsc.getDirectory())) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start !");
		final boolean response = Files.deleteIfExists(rsc.getDirectory().getPath().resolve(rsc.getFileName()));
		
		ioEventDispatcher.dispatchEvent(new EventResourceDeleted(rsc.getID()));
		
		return response;
	}
	
	/**
	 * Delete a resource present on the disk<br>
	 * The resource can only be deleted from directory that have been loaded by {@link DirectoryManager}<br>
	 * @param resourceDir The directory where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @return if the resource have been correctly deleted
	 * @throws IOException
	 */
	public boolean deleteResource(Directory resourceDir, String resource) throws IOException {
		final String[] splitName = resource.split("\\.");
		
		if(!isDirectoryExisting(resourceDir)) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start !");
		if(splitName.length < 2) throw new IllegalArgumentException("Invalid resource name. Must be composed of a name and a extension!");
		
		final boolean response = Files.deleteIfExists(resourceDir.getPath().resolve(resource));
		
		ioEventDispatcher.dispatchEvent(new EventResourceDeleted(splitName[0]));
		
		return response;
	}
	
	/**
	 * Delete a resource present on the disk<br>
	 * The resource can only be deleted from directory that have been loaded by {@link DirectoryManager}<br>
	 * @param resourcePath The path where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @return if the resource have been correctly deleted
	 * @throws IOException
	 */
	public boolean deleteResource(Path resourcePath, String resource) throws IOException {
		return this.deleteResource(new Directory(resourcePath), resource);
	}
	
	private <E extends AbstractBuffer> E createFileObject(Class<E> cls) {
		E object = null;
		try {
			object = cls.getConstructor().newInstance();
		
		} catch (Exception e) {
			LOGGER.warn("Unexpected error happened when creating new buffer [Err: {}]. Skipping buffer {}.",e.getMessage(), cls.getSimpleName());
		}
		
		return object;

	}
	
	/**
	 * Add a new buffer to the manager
	 * @param <E> A buffer instance which extends AbstractBuffer
	 * @param cls The class representing the buffer
	 * @see AbstractBuffer
	 */
	public synchronized <E extends AbstractBuffer> void addNewBuffer(Class<E> cls) {
		LOGGER.info("Creating new buffer "+cls.getSimpleName()+"...");
		
		E object = createFileObject(cls);
		
		if(object != null) {
			if(buffers.putIfAbsent(cls, object) != null)
				LOGGER.info("Couldn't load and stored {} because a mapping already exist for this buffer!", cls.getSimpleName());
			else {
				ioEventDispatcher.addListener(object);
				object.registerResources();
				LOGGER.info("Successfully loaded and stored buffer {}!", cls.getSimpleName());
			}
		}
	}
	
	/**
	 * Remove a saved buffer from this instance of ResourceManager
	 * @param <E> A buffer instance which extends AbstractBuffer
	 * @param cls The class representing the buffer
	 * @return if the buffer has been correctly remove
	 */
	public synchronized <E extends AbstractBuffer> boolean removeBuffer(Class<E> cls) {
		final AbstractBuffer bf = buffers.get(cls);
		
		if(bf == null) return false;
		else {
			ioEventDispatcher.removeListener(bf);
			return buffers.remove(cls) != null;
		}		
	}
	
	/**
	 * Get a buffer saved in the ResourceManager instance
	 * @param <E> a buffer instance which extends {@link AbstractBuffer}
	 * @param cls the buffer class
	 * @return the instance of the file saved in the list
	 */
	public <E extends AbstractBuffer> E get(Class<E> cls) {
		return buffers.containsKey(cls) ? cls.cast(buffers.get(cls)) : null;
	}
	
	private boolean isDirectoryExisting(Directory directory) {
		return directories.stream().anyMatch(dir -> dir.isPathSimilar(directory.getPath()));
	}
	
	private boolean isDirectoryExisting(Path path) {
		return directories.stream().anyMatch(dir -> dir.isPathSimilar(path));
	}
}
