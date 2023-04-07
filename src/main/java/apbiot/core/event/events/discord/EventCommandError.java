package apbiot.core.event.events.discord;

import apbiot.core.objects.interfaces.ILoggerEvent;
import discord4j.core.object.entity.channel.Channel.Type;

public class EventCommandError implements ILoggerEvent {
	
	private String user;
	private String command, commandatorResponse;
	private Type chanType;
	
	public EventCommandError(String user, String command, String commandatorResponse, Type chanType) {
		this.user = user;
		this.command = command;
		this.commandatorResponse = commandatorResponse;
		this.chanType = chanType;
	}
	
	public String getUser() {
		return this.user;
	}

	public String getCommand() {
		return this.command;
	}
	
	public String getCommandatorResponse() {
		return this.commandatorResponse == "" ? "none" : this.commandatorResponse;
	}
	
	public Type getChannelType() {
		return this.chanType;
	}

	@Override
	public String getLoggerMessage() {
		return "User "+getUser()+" issued inexistent bot command : "+getCommand()+
				" and got commandator response : "+getCommandatorResponse()+" (Channel Type: "+getChannelType()+")";
	}

	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}
}
