package apbiot.core.pems.events;

import apbiot.core.modules.DiscordCoreModule;
import apbiot.core.objects.enums.ApplicationCommandType;
import apbiot.core.pems.LoggableEvent;
import discord4j.core.object.entity.channel.Channel.Type;

/**
 * CommandReceivedEvent is dispatched in {@link DiscordCoreModule}<br/>
 * Arguments : <ul>
 * <li>User (string)</li>
 * <li>Command (string)</li>
 * <li>Channel Type ({@link Type})</li>
 * <li>Application command Type (({@link ApplicationCommandType})</li>
 * </ul>
 */
public record DiscordCommandReceivedEvent(String user, String command, Type channelType, ApplicationCommandType commandType) implements LoggableEvent {
	
	@Override
	public String getLoggerMessage() {
		return "User "+user+" issued "+commandType.toString()+" bot command : "+command+" (Channel Type: "+channelType+")";
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
