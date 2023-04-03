package apbiot.core.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import apbiot.core.io.objects.Directory;
import apbiot.core.io.objects.Resource;

public class ResourceManager {

	private static ResourceManager instance;
	private Set<Directory> directories = new HashSet<>();
	
	private static final byte BUFFER_SIZE = 32;
	private List<Resource> resourceBuffer = new ArrayList<>(BUFFER_SIZE);
	
	private ResourceManager() { }
	
	public static ResourceManager createInstance(Set<Directory> directories) {
		if(instance == null) {
			synchronized (ResourceManager.class) {
				if(instance == null) instance = new ResourceManager();
			}
		}
		
		return instance;
	}
	
	public static ResourceManager getInstance() {
		return instance;
	}
	
	public Resource getResource(Path resourcePath, String resource, boolean isErasable) throws IOException {
		if(!isDirectoryExisting(resourcePath)) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start!");
		final String[] splitName = resource.split(".");
		if(splitName.length < 2) throw new IllegalArgumentException("Invalid resource name. Must be composed of a name and a extension!");
		
		String resourceId = splitName[0];
		
		Resource opt = getResource(resourceId);
		if(opt != null) { 
			
			return opt;
		}else {
			
			byte[] fileResult = null;
			FileInputStream inputStream = null;
			BufferedInputStream buffer = null;
			
			try {
				inputStream = new FileInputStream(resourcePath.resolve(resource).toFile());
				//buffer = new BufferedInputStream(inputStream);
				
				fileResult = inputStream.readAllBytes();
				
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				if(buffer != null) buffer.close();
				if(inputStream != null) inputStream.close();
			}
			
			return new Resource(resourcePath, splitName[1], fileResult, resourceId, isErasable);
		}
	}
	
	public Resource getResource(Path resourcePath, String resource) throws IOException {
		return getResource(resourcePath, resource, true);
	}
	
	public Resource getResource(String id) {
		final Optional<Resource> opt = resourceBuffer.stream().filter(rsc -> rsc.getName().equals(id)).findFirst(); 
			
		return opt.isPresent() ? opt.get() : null;
	}
	
	public Resource getResource(int index) {
		if(index >= resourceBuffer.size()) throw new IllegalArgumentException("Cannot have an index greater than the buffer size!");
		return resourceBuffer.get(index);
	}
	
	public boolean deleteResource(Path resourcePath, String resource) throws IOException {
		if(!isDirectoryExisting(resourcePath)) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start !");
		
		return Files.deleteIfExists(resourcePath.resolve(resourcePath));
	}
	
	public boolean deleteResource(String id) throws IOException {
		final Optional<Resource> opt = resourceBuffer.stream().filter(rsc -> rsc.getName().equals(id)).findFirst(); 
		
		if(opt.isPresent()) {
			resourceBuffer.remove(opt.get());
			return Files.deleteIfExists(opt.get().getPath().resolve(opt.get().getFileName()));
		}
			
		return false;
	}
	
	public boolean deleteResource(int index) throws IOException {
		if(index >= resourceBuffer.size()) throw new IllegalArgumentException("Cannot have an index greater than the buffer size!");
		Resource rsc = resourceBuffer.remove(index);
		return Files.deleteIfExists(rsc.getPath().resolve(rsc.getFileName()));
	}
	
	public synchronized Resource addResourceToBuffer(Resource rsc) {
		if(resourceBuffer.size() >= BUFFER_SIZE) {
			byte index = 0;
			while(resourceBuffer.size() >= BUFFER_SIZE && index < resourceBuffer.size()) {
				if(resourceBuffer.get(index).isErasable()) resourceBuffer.remove(index);
				index+=1;
			}
			if(resourceBuffer.size() >= BUFFER_SIZE) throw new BufferOverflowException();
		}
		
		if(!resourceBuffer.contains(rsc))
			resourceBuffer.add(rsc);
		
		return rsc;
	}
	
	public synchronized Resource addResourceToBuffer(Path resourcePath, String resource) throws IOException {	
		return addResourceToBuffer(getResource(resourcePath, resource));
	}
	
	public synchronized Resource addResourceToBuffer(Path resourcePath, String resource, boolean isErasable) throws IOException {	
		return addResourceToBuffer(getResource(resourcePath, resource, isErasable));
	}
	
	public synchronized void removeResourceFromBuffer(int index) {
		if(index >= resourceBuffer.size()) throw new IllegalArgumentException("Cannot have an index greater than the buffer size!");
		resourceBuffer.remove(index);
	}
	
	public synchronized void removeResourceFromBuffer(String id) {
		resourceBuffer.removeIf(resource -> resource.getName().equals(id));
	}
	
	public synchronized void clearBuffer() {
		resourceBuffer.clear();
	}
	
	private boolean isDirectoryExisting(Path path) {
		return directories.stream().anyMatch(dir -> dir.isPathSimilar(path));
	}
}
