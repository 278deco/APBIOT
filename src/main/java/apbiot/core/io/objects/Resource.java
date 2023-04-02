package apbiot.core.io.objects;

public class Resource {
	
	private byte[] data;
	private String id;
	private boolean isErasable;
	
	public Resource(byte[] data, String id, boolean isErasable) {
		this.data = data;
		this.id = id;
	}
	
	public Resource(byte[] data, String id) {
		this(data, id, true);
	}
	
	public byte[] getData() {
		return data;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isErasable() {
		return isErasable;
	}
	
	@Override
	public String toString() {
		return "Resource[ID:"+id+", isErasable:"+isErasable+"]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Resource && ((Resource)obj).getId().equals(this.id);
	}
}
