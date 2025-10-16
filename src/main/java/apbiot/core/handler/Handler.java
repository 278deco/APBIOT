package apbiot.core.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.pems.Subscribe;
import apbiot.core.pems.SubscribeType;
import apbiot.core.pems.events.InstanceConnectedEvent;
import discord4j.core.GatewayDiscordClient;

/**
 * Handler class
 * @author 278deco
 */
public abstract class Handler {
	
	private Logger LOGGER = LogManager.getLogger(Handler.class);
	
	public abstract void preProcessing() throws HandlerPreProcessingException;
	protected abstract void register(GatewayDiscordClient client) throws HandlerRegisteringException;
	
	public abstract HandlerType getType();
	
	@Subscribe(type = SubscribeType.EVENT)
	public void instanceConnected(InstanceConnectedEvent event) {
		try {
			this.register(event.client());
		} catch (HandlerRegisteringException e) {
			LOGGER.error("Handler [Class:{}, Type:{}] encoutered error during registering phase!", getClass().getName(), getType().name());
		}
	}
	
}
