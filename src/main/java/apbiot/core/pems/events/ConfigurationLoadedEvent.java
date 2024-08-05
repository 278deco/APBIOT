package apbiot.core.pems.events;

import java.util.Optional;

import apbiot.core.pems.ProgramEvent;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

public class ConfigurationLoadedEvent extends ProgramEvent {

	public ConfigurationLoadedEvent(Object[] arguments) {
		super(arguments);
	}

	public Optional<String> getInstancePrefix() {
		return Optional.ofNullable(getEventArgument(String.class, 0));
	}
	
	public Optional<IntentSet> getInstanceIntentSet() {
		return Optional.ofNullable(getEventArgument(IntentSet.class, 1));
	}
	
	public Optional<ClientPresence> getInstanceClientPresence() {
		return Optional.ofNullable(getEventArgument(ClientPresence.class, 2));
	}
	
	public Optional<String> getInstanceVersion() {
		return Optional.ofNullable(getEventArgument(String.class, 3));
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}

}
