package apbiot.core.command.informations;

import apbiot.core.objects.interfaces.IGatewayInformations;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class CommandGatewaySlashInformations implements IGatewayInformations {
	
	private final ApplicationCommandInteractionEvent event;
	
	private final MessageChannel channel;
	private final User executor;
	
	/**
	 * Create a new instance of CommandGatewayComponentInteraction
	 * @param commandEvent - the ComponentInteractEvent fired by discord
	 * @param componentId - the id of the component
	 * @param channel - the channel where the command has been executed
	 */
	public CommandGatewaySlashInformations(ApplicationCommandInteractionEvent interactionEvent, User executor, MessageChannel channel) {
		this.event = interactionEvent;
		this.channel = channel;
		this.executor = executor;
	}
	
	public ApplicationCommandInteractionEvent getEvent() {
		return this.event;
	}
	
	public boolean isCommandResultPresent() {
		return getEvent().getInteraction().getCommandInteraction().isPresent();
	}
	
	public ApplicationCommandInteraction getCommandResult() {
		return isCommandResultPresent() ? getEvent().getInteraction().getCommandInteraction().get() : null;
	}
	
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
	public Message getMessage() {
		return getEvent().getInteraction().getMessage().isPresent() ? getEvent().getInteraction().getMessage().get() : null;
	}
	
}
