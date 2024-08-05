package apbiot.core.io.resources;

import marshmalliow.core.objects.Directory;

public class Resource {
	
	private byte[] data;
	private String nameID;
	private Directory directory;
	private String extension;
	
	private boolean isErasable;
	
	public Resource(Directory directory, String id, String extension, byte[] data, boolean isErasable) {
		this.directory = directory;
		this.extension = extension;
		this.data = data;
		this.nameID = id;
	}
	
	public Resource(Directory directory, String id, String extension, byte[] data) {
		this(directory, id, extension, data, true);
	}
	
	public Resource(byte[] data, String name) {
		this(null, name, null, data, true);
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getID() {
		return nameID;
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
		return this.nameID+"."+this.extension;
	}
	
	public boolean isErasable() {
		return isErasable;
	}
	
	@Override
	public String toString() {
		return "Resource[Name:"+nameID+", isErasable:"+isErasable+"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Resource && ((Resource)obj).getID().equals(this.nameID);
	}
}
