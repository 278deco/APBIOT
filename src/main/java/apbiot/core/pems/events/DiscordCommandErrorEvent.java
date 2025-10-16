package apbiot.core.pems.events;

import java.util.Optional;

import apbiot.core.commandator.CommandatorEntry;
import apbiot.core.modules.DiscordCoreModule;
import apbiot.core.pems.LoggableEvent;
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
public record DiscordCommandErrorEvent(String user, String command, Optional<CommandatorEntry> commandatorResponse, Type channelType) implements LoggableEvent {
	
	public DiscordCommandErrorEvent(String user, String command, CommandatorEntry commandatorResponse, Type channelType) {
		this(user, command, Optional.ofNullable(commandatorResponse), channelType);
	}
	
	@Override
	public String getLoggerMessage() {
		return "User "+user+" issued inexistent bot command : "+command+
				(commandatorResponse.isPresent() ? " and got commandator response: "+commandatorResponse.get()+")" : " and got no commandator response")+
				" (Channel Type: "+channelType+")";
	}

	@Override
	public LogPriority getLogPriority() {
		return LogPriority.INFO;
	}
	
	@Override
	public int getArgumentCount() {
		return 4;
	}	
	
}
