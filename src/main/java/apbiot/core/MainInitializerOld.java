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
import apbiot.core.exceptions.UnbuiltBotException;
import apbiot.core.handler.AbstractCommandHandler;
import apbiot.core.handler.AbstractSystemCommandHandler;
import apbiot.core.handler.Handler;
import apbiot.core.io.DirectoriesManager;
import apbiot.core.io.IOManager;
import apbiot.core.io.ResourceManager;
import apbiot.core.io.json.JSONProperties;
import apbiot.core.pems.EventListener;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

/**
 * @deprecated
 * @see ClientProgramInstance
 */
public abstract class MainInitializerOld {

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
	protected static final Logger LOGGER = LogManager.getLogger(MainInitializerOld.class);
	
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
	public void init(DirectoriesManager dirManager, @Nullable Class<? extends JSONProperties> configuration, 
			AbstractCommandHandler commandHandler, AbstractSystemCommandHandler sysCmdHandler) {
		this.initWithoutConsole = false;
		
		initIOFilesAndRessources(dirManager, configuration);
		LOGGER.info("Starting program...");
		
		handlerBuilder = HandlerManager.builder();
//		handlerBuilder.addHandler(commandHandler)
//			.addHandler(sysCmdHandler);
		
		LOGGER.info("Initializing handlers (phase 1) completed.");
		
		ClientInstance.createInstance(commandHandler.getClass());
		ConsoleLogger.createInstance(sysCmdHandler.getClass());
		
		eventDispatcher = new EventDispatcher();
		eventDispatcher.addListener(ConsoleLogger.getInstance().new ConsoleLoggerListener());
		eventDispatcher.addListener(new ClientConnectionListener());
		eventDispatcher.addListener(IOManager.getInstance().getEventListener());

		LOGGER.info("Initializing sequence finished. Ready to build the program and launch the client.");
	}
	
	/**
	 * Init all the system required by a bot to start without initializing the console
	 * @param cmdHandler - the CommandHandler
	 * @param prefix - the prefix used by the bot
	 * @see apbiot.core.handler.ECommandHandler
	 * @see apbiot.core.handler.EmojiRessources
	 */
	public void init(DirectoriesManager dirManager, @Nullable Class<? extends JSONProperties> configuration, AbstractCommandHandler commandHandler) {
		this.initWithoutConsole = true;
		
		initIOFilesAndRessources(dirManager, configuration);
		
		handlerBuilder = HandlerManager.builder();
		handlerBuilder.addHandler(commandHandler);
		
		LOGGER.info("Initializing handlers (phase 1) completed.");
		
		ClientInstance.createInstance(commandHandler.getClass());
		
		eventDispatcher = new EventDispatcher();
		eventDispatcher.addListener(new ClientConnectionListener());
		
		LOGGER.info("Initializing sequence finished. Ready to build the program and launch the client.");
	}
	
	private void initIOFilesAndRessources(DirectoriesManager dirManager, @Nullable Class<? extends JSONProperties> configurationClass) {
		directoriesManager = dirManager;
		dirManager.registerDirectories();
		
		IOManager.createInstance(dirManager.getLoadedDirectories(), dirManager.getConfigurationDirectory(), configurationClass);
		LOGGER.info("Input Output management is now lauched.");
		ResourceManager.createInstance(dirManager.getLoadedDirectories());
		LOGGER.info("Resource management is now lauched.");
	}
	
	/**
	 * Build the program, launch the bot and the console listener
	 * @param args - the program argument
	 * @param defaultPresence - the default presence shown by the bot
	 * @param intent - the intent used and required by the bot
	 */
	public void buildAndLaunchClient(String[] args, ClientPresence defaultPresence, IntentSet intent, @Nullable String prefix) {
		handlerManager = this.handlerBuilder.build();
		this.handlerBuilder = null;
		
		handlerManager.preRegisterHandlers();
		
		final Optional<String> optPrefix = Optional.ofNullable(prefix);
		try {			
			ClientInstance.getInstance().launch(args, defaultPresence, intent, optPrefix.isPresent() ? optPrefix.get() : DISCORD_SLASH_PREFIX);
		}catch(Exception e) {
			LOGGER.fatal("Unexpected error during client launch with message {}. Shutting down...",e.getMessage());
			System.exit(-1);
		}
		
		if(!this.initWithoutConsole) {
			try {
				ConsoleLogger.getInstance().startListening();
			}catch(Exception e) {
				LOGGER.error("Unexpected error during console logger launch with message {}",e.getMessage());
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
	public void buildAndLaunchClient(String token, ClientPresence defaultPresence, IntentSet intent, @Nullable String prefix) {
		this.buildAndLaunchClient(new String[] {token}, defaultPresence, intent, prefix);
	}
	
	/**
	 * Used to add another handler
	 * @param handler - the handler to add
	 * @throws IllegalAccessError
	 */
	public void addRequiredHandler(Handler handler) {
		if(handlerBuilder == null) {
			throw new IllegalAccessError("Cannot add an handler neither before the initialization of the program nor after its launching!");
		}else {
			handlerBuilder.addHandler(handler);
		}
	}
	
	//eventDispatcher.dispatchEvent(new EventProgramStopping(fileClosingNumber));
	protected abstract void onShutdown();
	
	protected abstract void onFullyLoaded();
	 
	/**
	 * Used to shutdown the program
	 */
	public class ShutdownProgram implements Runnable {

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
	
	private class ClientConnectionListener implements EventListener {

		@Override
		public void onEventReceived(ProgramEvent e, EventPriority priority) {
//			if(e instanceof InstanceConnectedEvent) {
//				handlerManager.registerHandlers(ClientInstance.getInstance().getClientBuilder().getGateway());
//				LOGGER.info("Registering handlers (phase 2) completed.");
//				
//				handlerManager.postRegisterHandlers();
//				LOGGER.info("Building handlers (phase 3) completed.");
//				LOGGER.info("All handlers have been completly registered ("+handlerManager.getRequiredHandlerNumber()+")");
//				
//				try {
//					ClientInstance.getInstance().updatedCommandReferences();
//					ConsoleLogger.getInstance().updatedCommandReferences();
//				} catch (IllegalAccessException | NoSuchElementException err) {
//					LOGGER.fatal("Unexpected error during client build with message {}. Shutting down...",err.getMessage());
//					new ShutdownProgram();
//				}
//				
//				eventDispatcher.dispatchEvent(new EventProgramFullyLoaded(ClientInstance.getInstance().getClientBuilder().isUsingCommandator(), ClientInstance.getInstance().getClientBuilder().getBotPrefix()));
//				onFullyLoaded(); //Call this method when all elements of the client have been fully loaded
//			}
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
