package apbiot.core.pems.events;

import java.util.Map;
import java.util.Optional;

import apbiot.core.command.ApplicationCommandInstance;
import apbiot.core.command.ComponentCommandInstance;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.pems.Event;

public record DiscordCommandParsedEvent(Map<String, NativeCommandInstance> discordNativeCommands, 
		Map<String, SlashCommandInstance> discordSlashCommands, 
		Map<String, ApplicationCommandInstance> discordApplicationCommands,
		Map<String, ComponentCommandInstance> discordComponentCommands) implements Event {

	public Optional<Map<String, NativeCommandInstance>> optionalDiscordNativeCommands() {
		return Optional.ofNullable(discordNativeCommands);
	}
	
	public Optional<Map<String, SlashCommandInstance>> optionalDiscordSlashCommands() {
		return Optional.ofNullable(discordSlashCommands);
	}
	
	public Optional<Map<String, ApplicationCommandInstance>> optionalDiscordApplicationCommands() {
		return Optional.ofNullable(discordApplicationCommands);
	}
	
	public Optional<Map<String, ComponentCommandInstance>> optionalDiscordComponentCommands() {
		return Optional.ofNullable(discordComponentCommands);
	}
	
	@Override
	public int getArgumentCount() {
		return 4;
	}

}
