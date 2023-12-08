package apbiot.core.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.pems.EventListener;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.events.InstanceConnectedEvent;
import discord4j.core.GatewayDiscordClient;

/**
 * Handler class
 * @author 278deco
 */
public abstract class Handler implements EventListener {
	
	private Logger LOGGER = LogManager.getLogger(Handler.class);
	
	public abstract void preProcessing() throws HandlerPreProcessingException;
	protected abstract void register(GatewayDiscordClient client) throws HandlerPreProcessingException;
	
	public abstract HandlerType getType();
	
	@Override
	public final void onEventReceived(ProgramEvent event, EventPriority priority) {
		if(priority == EventPriority.HIGH && event instanceof InstanceConnectedEvent) {
			try {
				this.register(((InstanceConnectedEvent)event).getGateway());
			}catch(HandlerPreProcessingException e) {
				LOGGER.error("Handler [Class:{}, Type:{}] encoutered error during registering phase!", getClass().getName(), getType().name());
			}
		}
	}
}
