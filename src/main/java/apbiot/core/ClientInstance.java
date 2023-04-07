package apbiot.core;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.builder.ClientBuilder;
import apbiot.core.exceptions.UnbuiltBotException;
import apbiot.core.handler.AbstractCommandHandler;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

public class ClientInstance {
	
	private static ClientInstance instance;
	
	private static final Logger LOGGER = LogManager.getLogger(ClientInstance.class);
	
	//Builder
	private ClientBuilder clientBuilder;
	
	private AtomicBoolean running;
	
	public static ClientInstance createInstance() {
		if(instance == null) {
			synchronized (ClientInstance.class) {
				if(instance == null) instance = new ClientInstance();
			}
		}
		return instance;
	}
	
	public static ClientInstance getInstance() {
		return instance;
	}
	
	public static boolean doesInstanceExist() {
		return instance != null;
	}
	
	/**
	 * Create a new instance of ClientInstance & build the client
	 */
	private ClientInstance() {
		clientBuilder = new ClientBuilder();
		
		clientBuilder.createNewInstance();
	}
	
	/**
	 * Used to launch the client
	 * @param args The program's arguments
	 * @param defaultPresence The default presence used by the bot
	 * @param intent The intent used and required by the bot
	 * @throws IllegalAccessException 
	 */
	public synchronized void launch(String[] args, ClientPresence defaultPresence, IntentSet intent) {
		this.running.set(true);
		
		try {
			clientBuilder.build(args[0], defaultPresence, intent);
			
		}catch (UnbuiltBotException e) {
			LOGGER.fatal("Error thrown will launching the client",e);
			this.running.set(false);
		}catch(ArrayIndexOutOfBoundsException e1) {
			LOGGER.fatal("No token was found during the launch. Shutting down...");
			this.running.set(false);
		}
	}
	
	/**
	 * Used to launch the client
	 * @param token The token of the bot
	 * @param defaultPresence The default presence used by the bot
	 * @param intent The intent used and required by the bot
	 * @throws IllegalAccessException 
	 */
	public synchronized void launch(String token, ClientPresence defaultPresence, IntentSet intent) {
		this.launch(new String[] {token}, defaultPresence, intent);
	}
	
	/**
	 * Finish the build of the bot
	 * @param clientPrefix The prefix used by the client
	 * @throws IllegalAccessException
	 */
	public synchronized void finishBuild(String clientPrefix) throws IllegalAccessException {
		if(isInstanceAlive()) throw new IllegalAccessException("You cannot update the command references if the bot isn't built."); 
		clientBuilder.finishBuild(clientPrefix);
	}
	
	/**
	 * Updated the command mapping of the bot
	 * @throws IllegalAccessException
	 */
	public synchronized void updatedCommandReferences() throws IllegalAccessException {
		if(isInstanceAlive()) throw new IllegalAccessException("You cannot update the command references if the bot isn't built."); 
		clientBuilder.updatedCommandReferences(MainInitializer.getHandlers().getHandler(AbstractCommandHandler.class));
		
		clientBuilder.buildCommandator();
	}
	
	/**
	 * Get if the client is still running
	 * @return if the client is running
	 */
	public boolean isInstanceAlive() {
		return this.running.get();
	}
	
	/**
	 * Get the stored ClientBuilder
	 * @return an instance of ClientBuilder
	 */
	public ClientBuilder getClientBuilder() {
		return clientBuilder;
	}
	
	/**
	 * Used to shut down the client
	 * @throws UnbuiltBotException
	 */
	public synchronized void shutdown() throws UnbuiltBotException {
		if(isInstanceAlive()) clientBuilder.shutdownInstance();
	}
	
	public synchronized void destroy() throws UnbuiltBotException {
		if(clientBuilder != null) {
			if(isInstanceAlive()) clientBuilder.shutdownInstance();
			clientBuilder = null;
				
		}else {
			throw new UnbuiltBotException("Couldn't destroy an non-existing instance of the bot.");
		}
	}
	
}
