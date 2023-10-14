package apbiot.core.objects.enums;

import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;

public enum ApplicationCommandType {
	/**
	 * Native aren't a discord related type of ApplicationCommand but a representation of the old way
	 * of doing commands on discord (reading all messages and responding to them when they had a prefix)
	 */
	NATIVE(null),
	
	/**
	 * Chat Input also known as "slash command" </br>
	 * See <a href="https://discord.com/developers/docs/interactions/application-commands#slash-commands">Discord Documentation</a> for more informations
	 */
	CHAT_INPUT(ChatInputInteractionEvent.class),
	
	/**
	 * User
	 * See <a href="https://discord.com/developers/docs/interactions/application-commands#user-commands">Discord Documentation</a> for more informations 
	 */
	USER(UserInteractionEvent.class),
	
	/**
	 * Message
	 * See <a href="https://discord.com/developers/docs/interactions/application-commands#message-commands">Discord Documentation</a> for more informations  
	 */
	MESSAGE(MessageInteractionEvent.class),
	
	NULL(null);
	
	private Class<? extends ApplicationCommandInteractionEvent> eventClass;
	private ApplicationCommandType(Class<? extends ApplicationCommandInteractionEvent> eventClass) {
		this.eventClass = eventClass;
	}
	
	public Class<? extends ApplicationCommandInteractionEvent> getEventClass() {
		return eventClass;
	}
}
