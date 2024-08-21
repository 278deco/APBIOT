package apbiot.core.command;

import java.util.Map;

import apbiot.core.command.informations.GatewayComponentCommandPacket;
import apbiot.core.command.informations.GatewayNativeCommandPacket;
import apbiot.core.i18n.LanguageManager;
import apbiot.core.objects.interfaces.ICommandCategory;
import discord4j.core.object.command.ApplicationCommand.Type;
import discord4j.discordjson.json.ApplicationCommandRequest;

public abstract class ApplicationCommandInstance extends AbstractCommandInstance {

	protected Type applicationCommandType;
	
	public ApplicationCommandInstance(String internalName, Type applicationCommandType, ICommandCategory category) {
		super(internalName, category);
		
		this.applicationCommandType = applicationCommandType;
		built = false;
		
	}
	
	public ApplicationCommandInstance(String internalName, Type applicationCommandType, ICommandCategory category, String staticID) {
		super(internalName, category, staticID);
		
		this.applicationCommandType = applicationCommandType;
		built = false;
		
	}
	
	public ApplicationCommandRequest createApplicationCommand() {
		built = true;
		
		final Map<String, String> localizationNames = LanguageManager.get()
				.getCommandLocalizationMapping(getInternalName(), "name");

		return ApplicationCommandRequest.builder().type(applicationCommandType.getValue())
				.name(getInternalName())
				.description("") // Default description in English
				.nameLocalizationsOrNull(localizationNames)
				.build();	
	}

	@Override
	public final void execute(GatewayNativeCommandPacket infos) { }
	
	@Override
	public final void executeComponent(GatewayComponentCommandPacket infos) { }
}
