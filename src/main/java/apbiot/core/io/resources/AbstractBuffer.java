package apbiot.core.io.resources;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import marshmalliow.core.directory.LocalDirectory;

public abstract class AbstractBuffer {
	
	private final List<Resource> resourceBuffer = new ArrayList<>(getBufferSize());
	
	public AbstractBuffer() { }
	
	public abstract void registerResources();

	/**
	 * Get a resource from the buffer with its name (the file name)<br>
	 * @param resource The name of the resource (the file name)
	 * @return The newly created resource class
	 * @throws IOException
	 */
	public Resource getResourceByName(String resource) throws IOException {
		final String[] splitName = resource.split("\\.");
		if(splitName.length < 2) throw new IllegalArgumentException("Invalid resource name. Must be composed of a name and a extension!");
		
		return getResourceByID(splitName[0]);
	}
	
	/**
	 * Get a resource from the buffer with its ID<br>
	 * @param id The resource's ID
	 * @see AbstractBuffer#getResource(Path, String, boolean)
	 */
	public Resource getResourceByID(String id) {
		final Optional<Resource> opt = resourceBuffer.stream().filter(rsc -> rsc.getID().equals(id)).findFirst(); 
			
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
		final Optional<Resource> opt = resourceBuffer.stream().filter(rsc -> rsc.getID().equals(id)).findFirst(); 
		
		if(opt.isPresent() && opt.get().getDirectory() instanceof LocalDirectory) {
			resourceBuffer.remove(opt.get());
			return Files.deleteIfExists(Path.of(opt.get().getDirectory().resolve(opt.get().getFileName())));
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
		final Resource rsc = resourceBuffer.remove(index);
		
		final boolean result = !(rsc.getDirectory() instanceof LocalDirectory) || Files.deleteIfExists(Path.of(rsc.getDirectory().resolve(rsc.getFileName())));
		return result;
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
		resourceBuffer.removeIf(resource -> resource.getID().equals(id));
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
	
}
