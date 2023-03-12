package apbiot.core;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

import apbiot.core.builder.ConsoleLogger;
import apbiot.core.builder.HandlerManager;
import apbiot.core.event.EventDispatcher;
import apbiot.core.event.EventListener;
import apbiot.core.event.events.EventInstanceConnected;
import apbiot.core.exceptions.UnbuiltBotException;
import apbiot.core.handler.AbstractCommandHandler;
import apbiot.core.handler.AbstractSystemCommandHandler;
import apbiot.core.io.DirectoriesManager;
import apbiot.core.io.IOManager;
import apbiot.core.io.json.JSONConfiguration;
import apbiot.core.objects.interfaces.IEvent;
import apbiot.core.objects.interfaces.IHandler;
import apbiot.core.objects.interfaces.IOptionalHandler;
import apbiot.core.objects.interfaces.IRunnableMethod;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

public abstract class MainInitializer {

	private static String DISCORD_SLASH_PREFIX = "/";
	
	//Handlers
	private HandlerManager.Builder handlerBuilder;
	protected static HandlerManager handlerManager;
	
	protected static DirectoriesManager directoriesManager;
	
	//Core events
	protected static EventDispatcher eventDispatcher;
	
	//Console status
	private boolean initWithoutConsole;
	
	/**
	 * Program's logger
	 * @see org.apache.logging.log4j.Logger
	 */
	private static final Logger LOGGER = LogManager.getLogger(MainInitializer.class);
	
	/**
	 * Program's json factory and Object Mapper
	 * <p> Used to handle json files <br>
	 * Not used by JSONConfiguration and JSONFile <br>
	 * THREAD FRIENDLY</p>
	 * @see com.fasterxml.jackson.core.JsonFactory
	 * @see com.fasterxml.jackson.databind.ObjectMapper
	 */
	public static final JsonFactory JSON_FACTORY = new JsonFactory();
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	//Instance UUID (for this instance only)
	public static final String INSTANCE_UUID = UUID.randomUUID().toString();
	
	/**
	 * Init all the system required by a bot to start
	 * @param cmdHandler - the CommandHandler
	 * @param sysCmdHandler - the SystemCommandHandler
	 * @param prefix - the prefix used by the bot
	 * @see apbiot.core.handler.ECommandHandler
	 * @see apbiot.core.handler.EmojiRessources
	 */
	public void init(DirectoriesManager dirManager, @Nullable Class<? extends JSONConfiguration> configuration, 
			AbstractCommandHandler commandHandler, AbstractSystemCommandHandler sysCmdHandler, @Nullable String prefix) {
		this.initWithoutConsole = false;
		
		initIOFilesAndRessources(dirManager, configuration);
		LOGGER.info("Starting program...");
		
		handlerBuilder = HandlerManager.builder();
		handlerBuilder.addHandler(commandHandler)
			.addHandler(sysCmdHandler);
		
		LOGGER.info("Initializing handlers phase 1 completed.");
		
		eventDispatcher = new EventDispatcher();
		eventDispatcher.addListener(ConsoleLogger.getInstance().new ConsoleLoggerListener());
		eventDispatcher.addListener(new ClientBuiltListener());
		
		final Optional<String> optPrefix = Optional.ofNullable(prefix);
		ClientInstance.createInstance(optPrefix.isPresent() ? optPrefix.get() : DISCORD_SLASH_PREFIX);
		
		LOGGER.info("Initializing sequence finished. Ready to build the program and launch the client.");
	}
	
	/**
	 * Init all the system required by a bot to start without initializing the console
	 * @param cmdHandler - the CommandHandler
	 * @param prefix - the prefix used by the bot
	 * @see apbiot.core.handler.ECommandHandler
	 * @see apbiot.core.handler.EmojiRessources
	 */
	public void init(DirectoriesManager dirManager, @Nullable Class<? extends JSONConfiguration> configuration, AbstractCommandHandler commandHandler, @Nullable String prefix) {
		this.initWithoutConsole = true;
		
		initIOFilesAndRessources(dirManager, configuration);
		
		handlerBuilder = HandlerManager.builder();
		handlerBuilder.addHandler(commandHandler);
		
		LOGGER.info("Initializing handlers phase completed.");
		eventDispatcher = new EventDispatcher();
		eventDispatcher.addListener(new ClientBuiltListener());
		
		final Optional<String> optPrefix = Optional.ofNullable(prefix);
		ClientInstance.createInstance(optPrefix.isPresent() ? optPrefix.get() : DISCORD_SLASH_PREFIX);
		
		LOGGER.info("Initializing sequence finished. Ready to build the program and launch the client.");
	}
	
	private void initIOFilesAndRessources(DirectoriesManager dirManager, @Nullable Class<? extends JSONConfiguration> configurationClass) {
		directoriesManager = dirManager;
		dirManager.registerDirectories();
		
		IOManager.createInstance(dirManager.getLoadedDirectories(), dirManager.getConfigurationDirectory(), configurationClass);
		//TODO Ressources
		LOGGER.info("All ressources have been successfully loaded and parsed.");
	}
	
	/**
	 * Build the program, launch the bot and the console listener
	 * @param args - the program argument
	 * @param defaultPresence - the default presence shown by the bot
	 * @param intent - the intent used and required by the bot
	 */
	public void buildAndLaunchClient(String[] args, ClientPresence defaultPresence, IntentSet intent) {
		handlerManager = this.handlerBuilder.build();
		this.handlerBuilder = null;
		
		handlerManager.buildHandlers();
		handlerManager.buildOptionalHandlers();
		
		try {
			ClientInstance.getInstance().launch(args, defaultPresence, intent);
		}catch(Exception e) {
			LOGGER.error("Unexpected error during client launch",e);
		}
		
		if(!this.initWithoutConsole) {
			try {
				ConsoleLogger.getInstance().startListening();
			}catch(Exception e) {
				LOGGER.error("Unexpected error during console logger launch",e);
			}
			
			LOGGER.info("Console logger has been successfully launched.");
		}
	}
	
	/**
	 * Build the program, launch the bot and the console listener
	 * @param token - the token of the bot
	 * @param defaultPresence - the default presence shown by the bot
	 * @param intent - the intent used and required by the bot
	 */
	public void buildAndLaunchClient(String token, ClientPresence defaultPresence, IntentSet intent) {
		this.buildAndLaunchClient(new String[] {token}, defaultPresence, intent);
	}
	
	/**
	 * Used to add another optionnal handler
	 * @param handler - the handler to add
	 * @throws IllegalAccessError
	 */
	public void addOptionnalHandler(IOptionalHandler handler) {
		if(handlerBuilder == null) {
			throw new IllegalAccessError("Cannot add an handler neither before the initialization of the program nor after its launching!");
		}else {
			handlerBuilder.addOptionalHandler(handler);
		}
	}
	
	/**
	 * Used to add another handler
	 * @param handler - the handler to add
	 * @throws IllegalAccessError
	 */
	public void addRequiredHandler(IHandler handler) {
		if(handlerBuilder == null) {
			throw new IllegalAccessError("Cannot add an handler neither before the initialization of the program nor after its launching!");
		}else {
			handlerBuilder.addHandler(handler);
		}
	}
	
	//eventDispatcher.dispatchEvent(new EventProgramStopping(fileClosingNumber));
	protected abstract void onShutdown();
	 
	/**
	 * Used to shutdown the program
	 */
	public class ShutdownProgram implements IRunnableMethod {

		@Override
		public void run() {
			onShutdown();
			
			try {
				ClientInstance.getInstance().shutdown();
			} catch (UnbuiltBotException e) {
				LOGGER.error("Unexpected error while shutting down the client",e);
			}
			
			if(!initWithoutConsole) {
				try {
					ConsoleLogger.getInstance().stopListening();
				} catch (IOException e) {
					LOGGER.error("Unexpected error when closing the console logger instance",e);
				}
			}
		}
	}
	
	private class ClientBuiltListener implements EventListener {

		@Override
		public void newEventReceived(IEvent e) {
			if(e instanceof EventInstanceConnected) {
				handlerManager.registerHandlers(ClientInstance.getInstance().getClientBuilder().getGateway());
				handlerManager.registerOptionnalHandlers();
				LOGGER.info("Building handlers phase 2 completed.");
				LOGGER.info("All handlers have been registered ("+handlerManager.getRequiredHandlerNumber()+" required, "+handlerManager.getOptionalHandlerNumber()+" optional(s))");
				
				try {
					ClientInstance.getInstance().updatedCommandReferences();
					ConsoleLogger.getInstance().updatedCommandReferences();
				} catch (IllegalAccessException err) {
					LOGGER.error("Unexpected error during client build, Shutting down...",err);
					try {
						ClientInstance.getInstance().shutdown();
					} catch (UnbuiltBotException e1) {
						LOGGER.error("Couldn't shutdown the client correctly",err);
					}
				}
			}
		}
		
	}
	
	public static EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}
	
	public static HandlerManager getHandlers() {
		return handlerManager;
	}
	
	public static DirectoriesManager getDirectoriesManager() {
		return directoriesManager;
	}
	
}
