package apbiot.core.pems.events;

import java.util.Optional;

import apbiot.core.pems.ProgramEvent;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

public class ConfigurationFileLoadedEvent extends ProgramEvent {

	public ConfigurationFileLoadedEvent(Object[] arguments) {
		super(arguments);
	}

	public Optional<String> getInstancePrefix() {
		return Optional.ofNullable((String)getEventArgument(0));
	}
	
	public Optional<IntentSet> getInstanceIntentSet() {
		return Optional.ofNullable((IntentSet)getEventArgument(1));
	}
	
	public Optional<ClientPresence> getInstanceClientPresence() {
		return Optional.ofNullable((ClientPresence)getEventArgument(2));
	}
	
	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}

}
