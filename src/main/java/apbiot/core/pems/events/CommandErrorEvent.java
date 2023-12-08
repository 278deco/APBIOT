package apbiot.core.pems.events;

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
		return (String)arguments[0];
	}

	public String getCommand() {
		return (String)arguments[1];
	}
	
	public String getCommandatorResponse() {
		return this.arguments[2] == null ? "none" : (String)this.arguments[2];
	}
	
	public Type getChannelType() {
		return (Type)this.arguments[3];
	}
	
	@Override
	public String getLoggerMessage() {
		return "User "+getUser()+" issued inexistent bot command : "+getCommand()+
				" and got commandator response : "+getCommandatorResponse()+" (Channel Type: "+getChannelType()+")";
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
