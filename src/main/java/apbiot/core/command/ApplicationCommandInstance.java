package apbiot.core.command;

import apbiot.core.command.informations.GatewayComponentCommandPacket;
import apbiot.core.command.informations.GatewayNativeCommandPacket;
import apbiot.core.objects.interfaces.ICommandCategory;
import discord4j.core.object.command.ApplicationCommand.Type;
import discord4j.discordjson.json.ApplicationCommandRequest;

public abstract class ApplicationCommandInstance extends AbstractCommandInstance {

	protected Type applicationCommandType;
	
	public ApplicationCommandInstance(String displayName, Type applicationCommandType, ICommandCategory category) {
		super(displayName, null, "", category);
		
		this.applicationCommandType = applicationCommandType;
		built = false;
		
	}
	
	public ApplicationCommandInstance(String displayName, Type applicationCommandType, ICommandCategory category, String staticID) {
		super(displayName, null, "", category, staticID);
		
		this.applicationCommandType = applicationCommandType;
		built = false;
		
	}
	
	public ApplicationCommandRequest createApplicationCommand() {
		built = true;
		
		return ApplicationCommandRequest.builder().type(applicationCommandType.getValue()).name(getDisplayName()).build();	
	}

	@Override
	public void execute(GatewayNativeCommandPacket infos) { }
	
	@Override
	public void executeComponent(GatewayComponentCommandPacket infos) { }
}
