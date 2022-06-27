package apbiot.core.command.informations;

import java.util.List;

import apbiot.core.helper.StringHelper;
import apbiot.core.objects.interfaces.IGatewayInformations;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class CommandGatewayNativeInformations implements IGatewayInformations {
	
	private final MessageCreateEvent event;
	
	private final User user;
	private final String content;
	private final List<String> arguments;
	private final String cmdName;
	private final String prefix;
	private final MessageChannel channel;
	
	/**
	 * Create a new instance of CommandGatewayInformations
	 * @param commandEvent - the MessageCreateEvent fired by discord
	 * @param executor - the executor of the command
	 * @param channel - the channel where the command has been executed
	 * @param arguments - the command's arguments
	 * @param commandName - the commandName
	 * @param usedPrefix - the prefix used by the bot
	 */
	public CommandGatewayNativeInformations(MessageCreateEvent commandEvent, User executor, MessageChannel channel, List<String> arguments, String commandName, String usedPrefix) {
		this.event = commandEvent;
		this.user = executor;
		this.channel = channel;
		this.content = StringHelper.listToString(arguments, " ");
		this.arguments = arguments;
		this.cmdName = commandName;
		this.prefix = usedPrefix;
	}
	
	public MessageCreateEvent getEvent() {
		return this.event;
	}
	
	@Override
	public User getExecutor() {
		return this.user;
	}
	
	@Override
	public MessageChannel getChannel() {
		return this.channel;
	}
	
	public String getMessageContent() {
		return this.content;
	}
	
	public List<String> getArguments() {
		return this.arguments;
	}
	
	public String getUsedCommandName() {
		return this.cmdName;
	}
	
	public String getUsedPrefix() {
		return this.prefix;
	}

	@Override
	public Guild getGuild() {
		return getEvent().getGuild().block();
	}
	
	@Override
	public Message getMessage() {
		return getEvent().getMessage();
	}
	
	
}
