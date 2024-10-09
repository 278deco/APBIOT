package apbiot.core.modules;

public interface CoreModuleType {

	public String getName();
	
	public boolean isMandatory();
	
	public int getOrderPriority();
	
}
