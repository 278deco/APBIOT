package apbiot.core;

import java.util.List;
import java.util.Map;

import apbiot.core.builder.ClientBuilder;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.exceptions.UnbuiltBotException;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

public class ClientInstance {
	
	//Builder
	private ClientBuilder clientBuilder;
	
	protected static boolean running;
	
	/**
	 * Create a new instance of ClientInstance
	 */
	public ClientInstance() {
		clientBuilder = new ClientBuilder();
	}
	
	/**
	 * Build the client before launching it
	 * @param botPrefix
	 * @param commands
	 * @return
	 */
	public ClientInstance build(String botPrefix) {
		clientBuilder.createNewInstance(botPrefix);
		
		return this;
	}	
	
	/**
	 * Used to launch the client
	 * @param args - the program's arguments
	 * @param defaultPresence - the default presence used by the bot
	 * @param intent - the intent used and required by the bot
	 * @throws IllegalAccessException 
	 */
	public void launch(String[] args, ClientPresence defaultPresence, IntentSet intent) {
		running = true;
		
		try {
			clientBuilder.build(args[0], defaultPresence, intent);
			
		} catch (UnbuiltBotException e) {
			MainInitializer.LOGGER.error("Error thrown will launching the client",e);
			running = false;
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
		running = true;
		
		try {
			clientBuilder.build(token, defaultPresence, intent);
			
		} catch (UnbuiltBotException e) {
			MainInitializer.LOGGER.error("Error thrown will launching the client",e);
			running = false;
		}
	}

	/**
	 * Finish the build of the bot and build commandator
	 * @param commandsMap - the command map
	 * @throws IllegalAccessException
	 */
	public void finishBuild(Map<List<String>, NativeCommandInstance> nativeCommandsMap, Map<List<String>, SlashCommandInstance> slashCommandsMap) throws IllegalAccessException {
		if(running == false) throw new IllegalAccessException("You cannot finish the build without launching the bot."); 
		clientBuilder.initAfterBotLaunch(nativeCommandsMap, slashCommandsMap);
		clientBuilder.buildCommandator();
	}
	
	/**
	 * Get if the client is still running
	 * @return if the client is running
	 */
	public boolean isInstanceAlive() {
		return running;
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
