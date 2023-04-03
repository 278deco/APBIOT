package apbiot.core.io.objects;

public class Resource {
	
	private byte[] data;
	private String name;
	private Directory directory;
	private String extension;
	
	private boolean isErasable;
	
	public Resource(Directory directory, String name, String extension, byte[] data, boolean isErasable) {
		this.directory = directory;
		this.extension = extension;
		this.data = data;
		this.name = name;
	}
	
	public Resource(Directory directory, String name, String extension, byte[] data) {
		this(directory, name, extension, data, true);
	}
	
	public Resource(byte[] data, String name) {
		this(null, name, null, data, true);
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getName() {
		return name;
	}
	
	public Directory getDirectory() {
		return directory;
	}
	
	public boolean isDirectoryPresent() {
		return directory != null;
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
