package apbiot.core.builder;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

import apbiot.core.objects.interfaces.IHandler;
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
	
	private HandlerManager(HandlerManager.Builder builder) {
		this.requiredHandlers = builder.getRequiredHandlers();
	}
	
	/**
	 * Pre-register all the handlers<br>
	 */
	public synchronized void preRegisterHandlers() {
		requiredHandlers.forEach(h -> h.preRegister());
	}
	
	/**
	 * Register all the handlers
	 * @param gateway - the discord client gateway
	 */
	public synchronized void registerHandlers(GatewayDiscordClient gateway) {
		requiredHandlers.forEach(h -> h.register(gateway));
	}
	
	/**
	 * Pre-register all the handlers<br>
	 */
	public synchronized void postRegisterHandlers() {
		requiredHandlers.forEach(h -> h.postRegister());
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
	 * Get the number of handlers listed in this instance
	 * @return number of handlers
	 */
	public int getRequiredHandlerNumber() {
		return this.requiredHandlers.size();
	}
	
	public static HandlerManager.Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Set<IHandler> requiredHandlers = new HashSet<>();
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
		
		public HandlerManager build() {
			return new HandlerManager(this);
		}
		
		private Set<IHandler> getRequiredHandlers() {
			return requiredHandlers;
		}
		
	}
}
