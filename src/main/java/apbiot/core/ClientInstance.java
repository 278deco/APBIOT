package apbiot.core;

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
	
	private boolean running;
	
	public static ClientInstance createInstance(String clientPrefix) {
		if(instance == null) {
			synchronized (ClientInstance.class) {
				if(instance == null) instance = new ClientInstance(clientPrefix);
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
	 * @param clientPrefix - the prefix used by the client
	 */
	private ClientInstance(String clientPrefix) {
		clientBuilder = new ClientBuilder();
		
		clientBuilder.createNewInstance(clientPrefix);
	}
	
	/**
	 * Used to launch the client
	 * @param args - the program's arguments
	 * @param defaultPresence - the default presence used by the bot
	 * @param intent - the intent used and required by the bot
	 * @throws IllegalAccessException 
	 */
	public void launch(String[] args, ClientPresence defaultPresence, IntentSet intent) {
		this.running = true;
		
		try {
			clientBuilder.build(args[0], defaultPresence, intent);
			
		}catch (UnbuiltBotException e) {
			LOGGER.fatal("Error thrown will launching the client",e);
			this.running = false;
		}catch(ArrayIndexOutOfBoundsException e1) {
			LOGGER.fatal("No token was found during the launch. Shutting down...");
			this.running = false;
		}
	}
	
	/**
	 * Used to launch the client
	 * @param token - the token of the bot
	 * @param defaultPresence - the default presence used by the bot
	 * @param intent - the intent used and required by the bot
	 * @throws IllegalAccessException 
	 */
	public void launch(String token, ClientPresence defaultPresence, IntentSet intent) {
		this.launch(new String[] {token}, defaultPresence, intent);
	}
	
	/**
	 * Finish the build of the bot
	 * @throws IllegalAccessException
	 */
	public void finishBuild() throws IllegalAccessException {
		if(running == false) throw new IllegalAccessException("You cannot update the command references if the bot isn't built."); 
		clientBuilder.finishBuild();
	}
	
	/**
	 * Updated the command mapping of the bot
	 * @throws IllegalAccessException
	 */
	public void updatedCommandReferences() throws IllegalAccessException {
		if(running == false) throw new IllegalAccessException("You cannot update the command references if the bot isn't built."); 
		clientBuilder.updatedCommandReferences(MainInitializer.getHandlers().getHandler(AbstractCommandHandler.class));
		
		clientBuilder.buildCommandator();
	}
	
	/**
	 * Get if the client is still running
	 * @return if the client is running
	 */
	public boolean isInstanceAlive() {
		return this.running;
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
	public void shutdown() throws UnbuiltBotException {
		if(isInstanceAlive()) {
			clientBuilder.shutdownInstance();
		}
	}
	
}
