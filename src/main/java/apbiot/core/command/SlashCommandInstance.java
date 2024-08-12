package apbiot.core.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import apbiot.core.command.informations.GatewayNativeCommandPacket;
import apbiot.core.i18n.LanguageManager;
import apbiot.core.objects.interfaces.ICommandCategory;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest.Builder;

public abstract class SlashCommandInstance extends AbstractCommandInstance {
	
	public SlashCommandInstance(String displayName, ICommandCategory category) {
		super(displayName, category);
		
		built = false;
	}
	
	public SlashCommandInstance(String displayName, ICommandCategory category, String staticID) {
		super(displayName, category, staticID);
		
		built = false;
	}
	
	public ApplicationCommandRequest createApplicationCommand(List<ApplicationCommandOptionData> arguments) {
		built = true;
		
		final Map<String, String> localizationNames = LanguageManager.get()
				.getCommandLocalizationMapping(getInternalName(), "name");
		
		final Map<String, String> localizationDescriptions = LanguageManager.get()
				.getCommandLocalizationMapping(getInternalName(), "description");
		
		final String defaultDesc = localizationDescriptions.getOrDefault("en-US", "commands.discord.no.description");
		
		final Builder request = ApplicationCommandRequest.builder()
				.name(getInternalName())
				.description(defaultDesc) // Default description in English
				.nameLocalizationsOrNull(localizationNames)
				.descriptionLocalizationsOrNull(localizationDescriptions);
		
		return arguments.isEmpty() ? request.build() : request.addAllOptions(arguments).build();
				
	}
	
	@Override
	public final void execute(GatewayNativeCommandPacket infos) { }
	
	/**
	 * Define the arguments of the command
	 * @param args The command's arguments
	 * @see discord4j.core.object.command.ApplicationCommandOptionData
	 * @return a List of ApplicationCommandOption
	 * @deprecated since 6.0.0
	 */
	public abstract List<ApplicationCommandOptionData> getCommandArguments(ArrayList<ApplicationCommandOptionData> args);
	
	/**
	 * Define the arguments of the command <br/>
	 * This method is called before the command is registered to the gateway
	 * 
	 * @param args The command options to add
	 * @return A list of {@link CommandOptions}
	 * @see ApplicationCommandOptionData
	 */
	public abstract List<CommandOptions> getCommandOptions(List<CommandOptions> args);
	
}
