package apbiot.core.builder;

import java.util.ArrayList;

import apbiot.core.objects.interfaces.IHandler;
import apbiot.core.objects.interfaces.IOptionalHandler;
import discord4j.core.GatewayDiscordClient;

public class HandlerBuilder {
	
	private ArrayList<IHandler> requiredHandlers = new ArrayList<>();
	private ArrayList<IOptionalHandler> optionalHandlers = new ArrayList<>();
	
	private HandlerBuilder(HandlerBuilder.Builder builder) {
		this.requiredHandlers = builder.getRequiredHandlers();
		this.optionalHandlers = builder.getOptionalHandlers();
	}
	
	/**
	 * Register all the handlers
	 * @param gateway - the discord client gateway
	 */
	public void registerHandlers(GatewayDiscordClient gateway) {
		for(IHandler h : requiredHandlers) {
			h.register(gateway);
		}
	}
	
	/**
	 * Register all the optional handlers
	 */
	public void registerOptionnalHandlers() {
		for(IOptionalHandler h : optionalHandlers) {
			h.register();
		}
	}
	
	/**
	 * Initialize all the handlers
	 */
	public void initHandlers() {
		for(IHandler h : requiredHandlers) {
			h.init();
		}
	}
	
	/**
	 * Initialize all the optional handlers
	 */
	public void initOptionalHandlers() {
		for(IOptionalHandler h : optionalHandlers) {
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
	
	public static class Builder {
		
		private ArrayList<IHandler> requiredHandlers = new ArrayList<>();
		private ArrayList<IOptionalHandler> optionalHandlers = new ArrayList<>();
		
		private Builder() { }
		
		/**
		 * Add a new handler
		 * @param handler - the handler to be added
		 */
		public Builder addHandler(IHandler... handler) {
			for(IHandler h : handler) {
				requiredHandlers.add(h);
			}
			
			return this;
		}
		
		/**
		 * Add a new optional handler
		 * @param handler - the optional handler to be added
		 */
		public Builder addOptionalHandler(IOptionalHandler... handler) {
			for(IOptionalHandler h : handler) {
				optionalHandlers.add(h);
			}
			
			return this;
		}
		
		public HandlerBuilder build() {
			return new HandlerBuilder(this);
		}
		
		private ArrayList<IOptionalHandler> getOptionalHandlers() {
			return optionalHandlers;
		}
		
		private ArrayList<IHandler> getRequiredHandlers() {
			return requiredHandlers;
		}
		
	}
}
