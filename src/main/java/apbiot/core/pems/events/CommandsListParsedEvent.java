package apbiot.core.pems.events;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import apbiot.core.command.ApplicationCommandInstance;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.command.SystemCommand;
import apbiot.core.pems.ProgramEvent;

public class CommandsListParsedEvent extends ProgramEvent {

	public CommandsListParsedEvent(Object[] arguments) {
		super(arguments);
	}

	@SuppressWarnings("unchecked")
	public Optional<Map<Set<String>, SystemCommand>> getConsoleCoreCommands() {
		try {
			return Optional.ofNullable((Map<Set<String>, SystemCommand>) getEventArgument(0));
		}catch(ClassCastException | NullPointerException e) {
			return Optional.empty();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Optional<Map<Set<String>, NativeCommandInstance>> getDiscordCoreNativeCommands() {
		try {
			return Optional.ofNullable((Map<Set<String>, NativeCommandInstance>) getEventArgument(1));
		}catch(ClassCastException | NullPointerException e) {
			return Optional.empty();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Optional<Map<Set<String>, SlashCommandInstance>> getDiscordCoreSlashCommands() {
		try {
			return Optional.ofNullable((Map<Set<String>, SlashCommandInstance>) getEventArgument(2));
		}catch(ClassCastException | NullPointerException e) {
			return Optional.empty();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Optional<Map<String, ApplicationCommandInstance>> getDiscordCoreApplicationCommands() {
		try {
			return Optional.ofNullable((Map<String, ApplicationCommandInstance>)getEventArgument(3));
		}catch(ClassCastException | NullPointerException e) {
			return Optional.empty();
		}
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}

}
