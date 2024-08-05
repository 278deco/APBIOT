package apbiot.core.pems.events;

import apbiot.core.modules.DiscordCoreModule;
import apbiot.core.objects.enums.ApplicationCommandType;
import apbiot.core.pems.LoggableProgramEvent;
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
public class CommandReceivedEvent extends LoggableProgramEvent {
	
	public CommandReceivedEvent(Object[] arguments) {
		super(arguments);
	}
	
	public String getUser() {
		return (String)this.arguments[0];
	}

	public String getCommand() {
		return (String)this.arguments[1];
	}
		
	public Type getChannelType() {
		return (Type)this.arguments[2];
	}
	
	public ApplicationCommandType getCommandType() {
		return (ApplicationCommandType)this.arguments[3];
	}

	@Override
	public String getLoggerMessage() {
		return "User "+getUser()+" issued "+getCommandType().toString()+" bot command : "+getCommand()+" (Channel Type: "+getChannelType()+")";
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
