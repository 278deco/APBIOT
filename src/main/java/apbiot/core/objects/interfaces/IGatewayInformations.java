package apbiot.core.objects.interfaces;

import java.util.Optional;

import discord4j.core.event.domain.Event;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public interface IGatewayInformations {
	
	Event getEvent();
	User getExecutor();
	Guild getGuild();
	MessageChannel getChannel();
	Optional<Message> getMessage();
}
