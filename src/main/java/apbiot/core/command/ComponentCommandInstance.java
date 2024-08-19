package apbiot.core.command;

import apbiot.core.command.informations.GatewayApplicationCommandPacket;
import apbiot.core.command.informations.GatewayNativeCommandPacket;
import apbiot.core.objects.interfaces.ICommandCategory;

public abstract class ComponentCommandInstance extends AbstractCommandInstance {

	public ComponentCommandInstance(String internalName, ICommandCategory category) {
		super(internalName, category);
		
		built = false;
		
	}
	
	public ComponentCommandInstance(String internalName, ICommandCategory category, String staticID) {
		super(internalName, category, staticID);
		
		built = false;
	}
	
	@Override
	public final void execute(GatewayApplicationCommandPacket infos) { }
	
	@Override
	public final void execute(GatewayNativeCommandPacket infos) { }
	
}
