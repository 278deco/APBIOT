package apbiot.core.pems.events;

import apbiot.core.pems.Event;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

public record ConfigurationLoadedEvent(String instancePrefix, IntentSet intentSet, ClientPresence clientPresence, String instanceVersion) implements Event {

	@Override
	public int getArgumentCount() {
		return 4;
	}

}
