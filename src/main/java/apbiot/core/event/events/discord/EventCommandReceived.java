package apbiot.core.event.events.discord;

import apbiot.core.objects.enums.ApplicationCommandType;
import apbiot.core.objects.interfaces.ILoggerEvent;
import discord4j.core.object.entity.channel.Channel.Type;

public class EventCommandReceived implements ILoggerEvent {
	
	private String user;
	private String command;
	private Type chanType;
	private ApplicationCommandType cmdType;
	
	public EventCommandReceived(String user, String command, Type chanType, ApplicationCommandType commandType) {
		this.user = user;
		this.command = command;
		this.chanType = chanType;
		this.cmdType = commandType;
	}
	
	public String getUser() {
		return this.user;
	}

	public String getCommand() {
		return this.command;
	}
	
	public Type getChannelType() {
		return this.chanType;
	}
	
	public ApplicationCommandType getCommandType() {
		return this.cmdType;
	}

	@Override
	public String getLoggerMessage() {
		return "User "+getUser()+" issued "+getCommandType().toString()+" bot command : "+getCommand()+" (Channel Type: "+getChannelType()+")";
	}
	
	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}
}
