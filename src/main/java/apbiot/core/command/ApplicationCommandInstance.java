package apbiot.core.command;

import java.util.Arrays;

import apbiot.core.command.informations.GatewayComponentCommandPacket;
import apbiot.core.command.informations.GatewayNativeCommandPacket;
import apbiot.core.objects.interfaces.ICommandCategory;
import discord4j.core.object.command.ApplicationCommand.Type;
import discord4j.discordjson.json.ApplicationCommandRequest;

public abstract class ApplicationCommandInstance extends AbstractCommandInstance {

	protected Type applicationCommandType;
	
	public ApplicationCommandInstance(String cmdName, Type applicationCommandType, ICommandCategory category) {
		super(Arrays.asList(cmdName), "", category);
		
		this.applicationCommandType = applicationCommandType;
		built = false;
		
	}
	
	public ApplicationCommandInstance(String cmdName, Type applicationCommandType, ICommandCategory category, String staticID) {
		super(Arrays.asList(cmdName), "", category, staticID);
		
		this.applicationCommandType = applicationCommandType;
		built = false;
		
	}
	
	public ApplicationCommandRequest createApplicationCommand() {
		built = true;
		
		return ApplicationCommandRequest.builder().type(applicationCommandType.getValue()).name(getMainName()).build();	
	}

	@Override
	public void execute(GatewayNativeCommandPacket infos) { }
	
	@Override
	public void executeComponent(GatewayComponentCommandPacket infos) { }
}
