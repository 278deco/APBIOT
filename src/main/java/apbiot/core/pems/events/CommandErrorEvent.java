package apbiot.core.pems.events;

import java.util.Optional;

import apbiot.core.commandator.CommandatorEntry;
import apbiot.core.modules.DiscordCoreModule;
import apbiot.core.pems.LoggableProgramEvent;
import discord4j.core.object.entity.channel.Channel.Type;

/**
 * CommandErrorEvent dispatched in {@link DiscordCoreModule}<br/>
 * Arguments : <ul>
 * <li>User (string)</li>
 * <li>Command (string)</li>
 * <li>Commandator Response (string|nullable)</li>
 * <li>Channel Type ({@link Type})</li>
 * </ul>
 */
public class CommandErrorEvent extends LoggableProgramEvent {

	public CommandErrorEvent(Object[] arguments) {
		super(arguments);
	}

	public String getUser() {
		return getEventArgument(String.class, 0);
	}

	public String getCommand() {
		return getEventArgument(String.class, 1);
	}
	
	public Optional<CommandatorEntry> getCommandatorResponse() {
		return this.arguments[2] == null ? Optional.empty() : getEventArgumentAsOptional(CommandatorEntry.class, 2);
	}
	
	public Type getChannelType() {
		return getEventArgument(Type.class, 3);
	}
	
	@Override
	public String getLoggerMessage() {
		return "User "+getUser()+" issued inexistent bot command : "+getCommand()+
				(getCommandatorResponse().isPresent() ? " and got commandator response: "+getCommandatorResponse().get()+")" : " and got no commandator response")+
				" (Channel Type: "+getChannelType()+")";
	}

	@Override
	public LogPriority getLogPriority() {
		return LogPriority.INFO;
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.LOW;
	}	

}
