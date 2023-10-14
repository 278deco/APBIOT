package apbiot.core.command.informations;

import java.util.Optional;

import apbiot.core.objects.enums.ApplicationCommandType;
import apbiot.core.objects.interfaces.IGatewayInformations;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteraction;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class GatewayApplicationCommandPacket implements IGatewayInformations {
	
	private final ApplicationCommandInteractionEvent event;
	private final ApplicationCommandType type;
	
	private final MessageChannel channel;
	private final User executor;
	
	/**
	 * Create a new instance of CommandGatewayApplication
	 * @param commandEvent - the ComponentInteractEvent fired by discord
	 * @param componentId - the id of the component
	 * @param channel - the channel where the command has been executed
	 */
	public GatewayApplicationCommandPacket(ApplicationCommandInteractionEvent interactionEvent, ApplicationCommandType type, User executor, MessageChannel channel) {
		this.event = interactionEvent;
		this.type = type;
		this.channel = channel;
		this.executor = executor;
	}
	
	public ApplicationCommandInteractionEvent getEvent() {
		return this.event;
	}
	
	public <E extends ApplicationCommandInteractionEvent> E getEventAs(Class<E> casterEvent) {
		if(casterEvent.equals(this.type.getEventClass())) return casterEvent.cast(this.event);
		else throw new IllegalArgumentException(casterEvent.getName()+" isn't a valid event caster class");
	}
	
	public ApplicationCommandType getType() {
		return this.type;
	}
	
	public boolean isCommandResultPresent() {
		return getEvent().getInteraction().getCommandInteraction().isPresent();
	}
	
	public Optional<ApplicationCommandInteraction> getCommandResult() {
		return getEvent().getInteraction().getCommandInteraction();
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
	public Optional<Message> getMessage() {
		return getEvent().getInteraction().getMessage();
	}
	
}
