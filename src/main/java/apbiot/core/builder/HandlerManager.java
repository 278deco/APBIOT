package apbiot.core.builder;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import apbiot.core.objects.interfaces.IHandler;
import apbiot.core.objects.interfaces.IOptionalHandler;
import discord4j.core.GatewayDiscordClient;

/**
 * Store every handlers and manage them across all the lifetime of the program <br><br>
 * The life of an handler can be resumed like this : <ul>
 * <li>The handler class is added to the HandlerManager</li>
 * <li>The first method which needs to be call is <strong>init</strong></li>
 * <li>Then we can call <strong>register</strong></li>
 * </ul>
 * @author 278deco
 *
 */
public class HandlerManager {
	
	private Set<IHandler> requiredHandlers = new HashSet<>();
	private Set<IOptionalHandler> optionalHandlers = new HashSet<>();
	
	private HandlerManager(HandlerManager.Builder builder) {
		this.requiredHandlers = builder.getRequiredHandlers();
		this.optionalHandlers = builder.getOptionalHandlers();
	}
	
	/**
	 * Register all the handlers
	 * @param gateway - the discord client gateway
	 */
	public void registerHandlers(GatewayDiscordClient gateway) {
		requiredHandlers.forEach(h -> h.register(gateway));
	}
	
	/**
	 * Register all the optional handlers
	 */
	public void registerOptionnalHandlers() {
		optionalHandlers.forEach(h -> h.register());
	}
	
	/**
	 * Initialize all the handlers
	 */
	public void initHandlers() {
		requiredHandlers.forEach(h -> h.init());
	}
	
	/**
	 * Initialize all the optional handlers
	 */
	public void initOptionalHandlers() {
		optionalHandlers.forEach(h -> h.init());
	}

	/**
	 * Get an handler registered and initialized by the HandlerBuilder
	 * @param <E> The handler needed
	 * @param cls The class of the handler needed
	 * @return the handler instance
	 * @throws NoSuchElementException
	 */
	public <E extends IHandler> E getHandler(Class<E> cls) {
		return cls.cast(this.requiredHandlers.stream().filter(let -> let.getClass().equals(cls)).findFirst().orElseThrow());
	}
	
	/**
	 * Get an optional handler registered and initialized by the HandlerBuilder
	 * @param <E> The optional handler needed
	 * @param cls The class of the optional handler needed
	 * @return the optional handler instance
	 * @throws NoSuchElementException
	 */
	public <E extends IOptionalHandler> IOptionalHandler getOptionalHandler(Class<E> cls) {
		return this.optionalHandlers.stream().filter(let -> let.getClass().equals(cls)).findFirst().orElseThrow();
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
	
	public static HandlerManager.Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Set<IHandler> requiredHandlers = new HashSet<>();
		private Set<IOptionalHandler> optionalHandlers = new HashSet<>();
		
		private Builder() { }
		
		/**
		 * Add a new handler
		 * @param handler - the handler to be added
		 */
		public Builder addHandler(IHandler... handler) {
			for(IHandler h : handler) 
				requiredHandlers.add(h);
			
			return this;
		}
		
		/**
		 * Add a new optional handler
		 * @param handler - the optional handler to be added
		 */
		public Builder addOptionalHandler(IOptionalHandler... handler) {
			for(IOptionalHandler h : handler) 
				optionalHandlers.add(h);
			
			return this;
		}
		
		public HandlerManager build() {
			return new HandlerManager(this);
		}
		
		private Set<IOptionalHandler> getOptionalHandlers() {
			return optionalHandlers;
		}
		
		private Set<IHandler> getRequiredHandlers() {
			return requiredHandlers;
		}
		
	}
}
