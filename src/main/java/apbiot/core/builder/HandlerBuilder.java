package apbiot.core.builder;

import java.util.ArrayList;

import apbiot.core.objects.interfaces.IHandler;
import apbiot.core.objects.interfaces.IOptionalHandler;
import discord4j.core.GatewayDiscordClient;

public class HandlerBuilder {
	
	private ArrayList<IHandler> requiredHandlers = new ArrayList<>();
	private ArrayList<IOptionalHandler> optionalHandlers = new ArrayList<>();
	
	/**
	 * Add a new handler
	 * @param handler - the handler to be added
	 */
	public HandlerBuilder addHandler(IHandler... handler) {
		for(IHandler h : handler) {
			requiredHandlers.add(h);
		}
		
		return this;
	}
	
	/**
	 * Add a new optional handler
	 * @param handler - the optional handler to be added
	 */
	public HandlerBuilder addOptionalHandler(IOptionalHandler... handler) {
		for(IOptionalHandler h : handler) {
			optionalHandlers.add(h);
		}
		
		return this;
	}
	
	/**
	 * Register and init all the handlers
	 * @param gateway - the discord client gateway
	 * @return an instance of HandlerBuilder
	 */
	public void computeHandlers(GatewayDiscordClient gateway) {
		for(IHandler h : requiredHandlers) {
			h.register(gateway);
			h.init();
		}
	}
	
	/**
	 * Register and init all the optional handlers
	 * @return an instance of HandlerBuilder
	 */
	public void computeOptionalHandlers() {
		for(IOptionalHandler h : optionalHandlers) {
			h.register();
			h.init();
		}
	}
	
	/**
	 * Check if at least one optional handler is present
	 * @return if an optional handler is present
	 */
	public boolean isAnOptionalHandlerPresent() {
		return optionalHandlers.size() > 0;
	}
	
	/**
	 * Get the number of handlers listed in this instance
	 * @return number of handlers
	 */
	public int getRequiredHandlerNumber() {
		return this.requiredHandlers.size();
	}
	
	/**
	 * Get the number of optional handlers listed in this instance
	 * @return number of optional handlers
	 */
	public int getOptionalHandlerNumber() {
		return this.optionalHandlers.size();
	}
}
