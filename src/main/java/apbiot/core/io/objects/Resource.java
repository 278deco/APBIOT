package apbiot.core.io.objects;

import java.nio.file.Path;

public class Resource {
	
	private byte[] data;
	private String name;
	private Path path;
	private String extension;
	
	private boolean isErasable;
	
	public Resource(Path path, String extension, byte[] data, String name, boolean isErasable) {
		this.path = path;
		this.extension = extension;
		this.data = data;
		this.name = name;
	}
	
	public Resource(Path path, String extension, byte[] data, String name) {
		this(path, extension, data, name, true);
	}
	
	public Resource(byte[] data, String name) {
		this(null, null, data, name, true);
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getName() {
		return name;
	}
	
	public Path getPath() {
		return path;
	}
	
	public boolean isPathPresent() {
		return path != null;
	}
	
	public String getExtension() {
		return extension;
	}
	
	public boolean isExtensionPresent() {
		return extension != null;
	}
	
	public String getFileName() {
		return this.name+"."+this.extension;
	}
	
	public boolean isErasable() {
		return isErasable;
	}
	
	@Override
	public String toString() {
		return "Resource[Name:"+name+", isErasable:"+isErasable+"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Resource && ((Resource)obj).getName().equals(this.name);
	}
}
