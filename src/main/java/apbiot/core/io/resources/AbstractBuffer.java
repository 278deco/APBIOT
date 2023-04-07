package apbiot.core.io.resources;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.naming.spi.DirectoryManager;

import apbiot.core.event.EventListener;
import apbiot.core.event.events.io.EventResourceDeleted;
import apbiot.core.io.objects.Directory;
import apbiot.core.objects.interfaces.IEvent;

public abstract class AbstractBuffer implements EventListener {
	
	private final List<Resource> resourceBuffer = new ArrayList<>(getBufferSize());
	private final Set<Directory> directories;
	
	public AbstractBuffer(Set<Directory> directories) {
		this.directories = directories;
	}
	
	public abstract void registerResources();

	/**
	 * Get a resource from the disk and use it as a {@link Resource} in the program<br>
	 * The resource can only be obtained from directory that have been loaded by {@link DirectoryManager}<br>
	 * <i>The function check if the requested resource isn't already saved in the buffer</i>
	 * @param resourcePath The path where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @param isErasable Tells the program that the resource could be deleted from memory if the buffer runs out of space
	 * @return The newly created resource class
	 * @throws IOException
	 */
	public Resource getResource(Path resourcePath, String resource, boolean isErasable) throws IOException {
		final Directory dir = new Directory(resourcePath);
		final String[] splitName = resource.split(".");
		
		if(!isDirectoryExisting(dir)) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start!");
		if(splitName.length < 2) throw new IllegalArgumentException("Invalid resource name. Must be composed of a name and a extension!");
		
		String resourceId = splitName[0];
		
		Resource opt = getResource(resourceId);
		if(opt != null) { 
			
			return opt;
		}else {
			byte[] fileResult = null;
			FileInputStream inputStream = null;

			try {
				inputStream = new FileInputStream(resourcePath.resolve(resource).toFile());
				fileResult = inputStream.readAllBytes();
				
			}catch(Exception e) {
				throw new IOException(e.getMessage());
			}finally {
				if(inputStream != null) inputStream.close();
			}
			
			return new Resource(dir, resourceId, splitName[1], fileResult, isErasable);
		}
	}
	
	/**
	 * Get a resource from the buffer with its ID<br>
	 * @param id The resource's ID
	 * @see AbstractBuffer#getResource(Path, String, boolean)
	 */
	public Resource getResource(String id) {
		final Optional<Resource> opt = resourceBuffer.stream().filter(rsc -> rsc.getName().equals(id)).findFirst(); 
			
		return opt.isPresent() ? opt.get() : null;
	}
	
	/**
	 * Get a resource from the buffer with its position<br>
	 * This method can be a bit dangerous if the position of the resource isn't correctly known
	 * @param index The resource's index
	 * @see AbstractBuffer#getResource(Path, String, boolean)
	 */
	public Resource getResource(int index) {
		if(index >= resourceBuffer.size()) throw new IllegalArgumentException("Cannot have an index greater than the buffer size!");
		return resourceBuffer.get(index);
	}
	
	/**
	 * Remove a resource from the disk with its ID<br>
	 * The method remove the resource from the buffer at the same time
	 * @param id The resource's id
	 */
	public boolean deleteResource(String id) throws IOException {
		final Optional<Resource> opt = resourceBuffer.stream().filter(rsc -> rsc.getName().equals(id)).findFirst(); 
		
		if(opt.isPresent()) {
			resourceBuffer.remove(opt.get());
			return Files.deleteIfExists(opt.get().getDirectory().getPath().resolve(opt.get().getFileName()));
		}
		return false;
	}
	
	/**
	 * Delete a resource from the disk with its position in the buffer<br>
	 * The method remove the resource from the buffer at the same time
	 * This method can be dangerous if the position of the resource isn't correctly known
	 * @param index The resource's index
	 */
	public boolean deleteResource(int index) throws IOException {
		if(index >= resourceBuffer.size()) throw new IllegalArgumentException("Cannot have an index greater than the buffer size!");
		Resource rsc = resourceBuffer.remove(index);
		return Files.deleteIfExists(rsc.getDirectory().getPath().resolve(rsc.getFileName()));
	}
	
	/**
	 * Add a new resource into the buffer
	 * @param rsc The resource to be added
	 * @return The added resource
	 * @throws IOException
	 */
	public synchronized Resource add(Resource rsc) throws IOException {
		if(resourceBuffer.size() >= getBufferSize()) {
			byte index = 0;
			while(resourceBuffer.size() >= getBufferSize() && index < resourceBuffer.size()) {
				if(resourceBuffer.get(index).isErasable()) resourceBuffer.remove(index);
				index+=1;
			}
			if(resourceBuffer.size() >= getBufferSize()) throw new BufferOverflowException();
		}
		
		if(!resourceBuffer.contains(rsc))
			resourceBuffer.add(rsc);
		
		return rsc;
	}
	
	/**
	 * Add a new resource into the buffer<br>
	 * The resource can only be saved in directory that have been loaded by {@link DirectoryManager}<br>
	 * By default this method will set the flag <i>isErasable</i> to true
	 * @param resourcePath The path where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @return the newly added resource
	 * @throws IOException
	 */
	public synchronized Resource add(Path resourcePath, String resource) throws IOException {	
		return add(getResource(resourcePath, resource, true));
	}
	
	/**
	 * Add a new resource into the buffer<br>
	 * The resource can only be saved in directory that have been loaded by {@link DirectoryManager}<br>
	 * @param resourcePath The path where the resource is stored
	 * @param resource The name of the resource (the file name)
	 * @param isErasable Tells the program that the resource could be deleted from memory if the buffer runs out of it
	 * @return the newly added resource
	 * @throws IOException
	 */
	public synchronized Resource add(Path resourcePath, String resource, boolean isErasable) throws IOException {	
		return add(getResource(resourcePath, resource, isErasable));
	}
	
	/**
	 * Remove a resource from the buffer with its instance<br>
	 * @param index The resource's instance
	 */
	public synchronized void remove(Resource rsc) {
		resourceBuffer.remove(Objects.requireNonNull(rsc));
	}

	/**
	 * Remove a resource from the buffer with its position in the buffer<br>
	 * This method can be dangerous if the position of the resource isn't correctly known
	 * @param index The resource's index
	 */
	public synchronized void remove(int index) {
		if(index >= resourceBuffer.size()) throw new IllegalArgumentException("Cannot have an index greater than the buffer size!");
		resourceBuffer.remove(index);
	}
	
	/**
	 * Remove a resource from the buffer with its ID
	 * @param id The resource's id
	 */
	public synchronized void remove(String id) {
		resourceBuffer.removeIf(resource -> resource.getName().equals(id));
	}
	
	/**
	 * Clear the buffer entirely, remove all saved resources
	 */
	public synchronized void clear() {
		resourceBuffer.clear();
	}
	
	/**
	 * Return the maximum number of elements that can be saved in the buffer
	 * @return the buffer maximum size
	 */
	public byte getBufferSize() {
		return 32;
	}
	
	/**
	 * Check if the directory exist in the directory set
	 * @param directory The directory to be checked
	 * @return if it exits
	 */
	private boolean isDirectoryExisting(Directory directory) {
		return directories.stream().anyMatch(dir -> dir.isPathSimilar(directory.getPath()));
	}
	
	@Override
	public void newEventReceived(IEvent e) {
		if(e instanceof EventResourceDeleted) {
			this.remove(((EventResourceDeleted)e).getResourceId());
		}
	}
}
