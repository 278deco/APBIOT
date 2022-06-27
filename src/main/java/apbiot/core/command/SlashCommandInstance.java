package apbiot.core.command;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.command.informations.CommandGatewayNativeInformations;
import apbiot.core.objects.interfaces.ICommandCategory;
import discord4j.discordjson.json.ApplicationCommandOptionData;
import discord4j.discordjson.json.ApplicationCommandRequest;
import discord4j.discordjson.json.ImmutableApplicationCommandRequest.Builder;

public abstract class SlashCommandInstance extends AbstractCommandInstance {
	
	public SlashCommandInstance(List<String> cmdName, String description, ICommandCategory category) {
		super(cmdName, description, category);
		
		initialize = false;
		
	}
	
	public SlashCommandInstance(List<String> cmdName, String description, ICommandCategory category, String staticID) {
		super(cmdName, description, category, staticID);
		
		initialize = false;
		
	}
	
	public ApplicationCommandRequest createApplicationCommand(List<ApplicationCommandOptionData> arguments) {
		initialize = true;
		
		Builder request = ApplicationCommandRequest.builder().name(getMainName()).description(getDescription());
		
		return arguments.isEmpty() ? request.build() : request.addAllOptions(arguments).build();
				
	}
	
	@Override
	public void execute(CommandGatewayNativeInformations infos) { }
	
	/**
	 * Define the arguments of the command
	 * @param args - the arguments
	 * @see discord4j.core.object.command.ApplicationCommandOptionData
	 * @return a List of ApplicationCommandOption
	 */
	public abstract List<ApplicationCommandOptionData> getCommandArguments(ArrayList<ApplicationCommandOptionData> args);

	
}
