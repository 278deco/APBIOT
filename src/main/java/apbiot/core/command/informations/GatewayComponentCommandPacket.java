package apbiot.core.command.informations;

import java.util.Optional;

import apbiot.core.objects.interfaces.IGatewayInformations;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class GatewayComponentCommandPacket implements IGatewayInformations {

	private final ComponentInteractionEvent event;
	
	private final String componentId;
	private final MessageChannel channel;
	private final User executor;
	
	/**
	 * Create a new instance of CommandGatewayComponentInteraction
	 * @param commandEvent - the ComponentInteractEvent fired by discord
	 * @param componentId - the id of the component
	 * @param channel - the channel where the command has been executed
	 */
	public GatewayComponentCommandPacket(ComponentInteractionEvent interactionEvent, String componentId, User executor, MessageChannel channel) {
		this.event = interactionEvent;
		this.componentId = componentId;
		this.channel = channel;
		this.executor = executor;
	}
	
	public ComponentInteractionEvent getEvent() {
		return this.event;
	}
	
	public String getComponentId() {
		return this.componentId;
	}
	
	@Override
	public User getExecutor() {
		return this.executor;
	}
	
	@Override
	public MessageChannel getChannel() {
		return this.channel;
	}

	@Override
	public Guild getGuild() {
		return getEvent().getInteraction().getGuild().block();
	}

	@Override
	public Optional<Message> getMessage() {
		return getEvent().getMessage();
	}
		
}
