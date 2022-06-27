package apbiot.core.event;

import apbiot.core.objects.enums.CommandType;
import apbiot.core.objects.interfaces.ILoggerEvent;
import discord4j.core.object.entity.channel.Channel.Type;

public class EventCommandReceived implements ILoggerEvent {
	
	private String user;
	private String command;
	private Type chanType;
	private CommandType cmdType;
	
	public EventCommandReceived(String user, String command, Type chanType, CommandType commandType) {
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
	
	public CommandType getCommandType() {
		return this.cmdType;
	}

	@Override
	public String getLoggerMessage() {
		return "User "+getUser()+" issued "+(getCommandType() == CommandType.NATIVE ? "native" : "slash")+" bot command : "+getCommand()+" (Channel Type: "+getChannelType()+")";
	}
	
	@Override
	public EventPriority getEventPriority() {
		return EventPriority.INFO;
	}
}
