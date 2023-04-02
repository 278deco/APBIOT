package apbiot.core.io;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.BufferOverflowException;
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
		if(!isDirectoryExisting(resourcePath)) throw new IllegalArgumentException("Cannot access a directory if it hasn't been initialized at the start !");
		String resourceId = resource.split(".")[0];
		
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
			
			return new Resource(fileResult, resourceId, isErasable);
		}
	}
	
	public Resource getResource(Path resourcePath, String resource) throws IOException {
		return getResource(resourcePath, resource, true);
	}
	
	public Resource getResource(String id) {
		final Optional<Resource> opt = resourceBuffer.stream().filter(rsc -> rsc.getId().equals(id)).findFirst(); 
			
		return opt.isPresent() ? opt.get() : null;
	}
	
	public Resource getResource(int index) {
		return resourceBuffer.get(index);
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
		resourceBuffer.remove(index);
	}
	
	public synchronized void removeResourceFromBuffer(String id) {
		resourceBuffer.removeIf(resource -> resource.getId().equals(id));
	}
	
	public synchronized void clearBuffer() {
		resourceBuffer.clear();
	}
	
	private boolean isDirectoryExisting(Path path) {
		return directories.stream().anyMatch(dir -> dir.isPathSimilar(path));
	}
}
