package apbiot.core;

import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.builder.ConsoleLoggerBuilder;
import apbiot.core.builder.HandlerBuilder;
import apbiot.core.event.EventDispatcher;
import apbiot.core.event.EventInstanceConnected;
import apbiot.core.event.EventListener;
import apbiot.core.exceptions.UnbuiltBotException;
import apbiot.core.handler.ECommandHandler;
import apbiot.core.handler.ESystemCommandHandler;
import apbiot.core.handler.EmojiRessources;
import apbiot.core.objects.interfaces.IEvent;
import apbiot.core.objects.interfaces.IHandler;
import apbiot.core.objects.interfaces.IOptionalHandler;
import apbiot.core.objects.interfaces.IRunnableMethod;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

public abstract class MainInitializer {

	//Handlers
	protected static HandlerBuilder handlerBuilder;
	protected static EmojiRessources emojiHandler;
	protected static ECommandHandler cmdHandler;
	protected static ESystemCommandHandler consoleCmdHandler;
	
	//Bot core
	protected static ClientInstance clientInstance;
	
	//Core events
	protected static EventDispatcher eventDispatcher;
	
	//Console
	protected static ConsoleLoggerBuilder consoleLogger;
	
	/**
	 * Program's logger
	 * @see org.apache.logging.log4j.Logger
	 */
	public static final Logger LOGGER = LogManager.getLogger(MainInitializer.class);
	
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
	public void init(ECommandHandler commandHandler, ESystemCommandHandler sysCmdHandler, String prefix) {
		consoleLogger = new ConsoleLoggerBuilder();
		
		cmdHandler = commandHandler;
		consoleCmdHandler = sysCmdHandler;
		handlerBuilder = new HandlerBuilder();
		emojiHandler = new EmojiRessources();
		
		handlerBuilder.addHandler(cmdHandler);
		handlerBuilder.addHandler(consoleCmdHandler);
		handlerBuilder.addOptionalHandler(emojiHandler);
		
		LOGGER.info("Building handlers phase 1 completed. Ready to client launch.");
		
		eventDispatcher = new EventDispatcher();
		eventDispatcher.addListener(consoleLogger.new ConsoleLoggerListener());
		eventDispatcher.addListener(new ClientInitListener());
		
		consoleLogger.build(consoleCmdHandler.COMMANDS);
		clientInstance = new ClientInstance().build(prefix);
		
		LOGGER.info("Building sequence finished.");
	}
	
	/**
	 * Used to add another optionnal handler
	 * @param handler - the handler to add
	 * @throws IllegalAccessError
	 */
	public void addOptionnalHandler(IOptionalHandler handler) {
		if(handlerBuilder == null) {
			throw new IllegalAccessError("Cannot add an handler before initializing the programm !");
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
			throw new IllegalAccessError("Cannot add an handler before initializing the programm !");
		}else {
			handlerBuilder.addHandler(handler);
		}
	}
	
	/**
	 * Launch the bot and the console listener
	 * @param args - the program argument
	 * @param defaultPresence - the default presence shown by the bot
	 * @param intent - the intent used and required by the bot
	 */
	public void launch(String[] args, ClientPresence defaultPresence, IntentSet intent) {
		try {
			clientInstance.launch(args, defaultPresence, intent);
		}catch(Exception e) {
			LOGGER.error("Unexpected error during client launch",e);
		}
		
		try {
			consoleLogger.startListening();
		}catch(Exception e) {
			LOGGER.error("Unexpected error during console logger launch",e);
		}
		
		LOGGER.info("Console has been successfully launched.");
		
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
				clientInstance.shutdown();
			} catch (UnbuiltBotException e) {
				LOGGER.error("Unexpected error while shutting down the client",e);
			}
			
			try {
				consoleLogger.stopListening();
			} catch (IOException e) {
				LOGGER.error("Unexpected error when closing the console logger instance",e);
			}
		}
	}
	
	private class ClientInitListener implements EventListener {

		@Override
		public void newEventReceived(IEvent e) {
			if(e instanceof EventInstanceConnected) {
				handlerBuilder.computeHandlers(clientInstance.getClientBuilder().getGateway());
				handlerBuilder.computeOptionalHandlers();
				LOGGER.info("Building handlers phase 2 completed.");
				LOGGER.info("All handlers have been registered ("+handlerBuilder.getRequiredHandlerNumber()+" required, "+handlerBuilder.getOptionalHandlerNumber()+" optional(s))");
				
				try {
					clientInstance.finishBuild(cmdHandler.NATIVE_COMMANDS, cmdHandler.SLASH_COMMANDS);
				} catch (IllegalAccessException err) {
					LOGGER.error("Unexpected error during client build",err);
				}
			}
		}
		
	}
	
	public static EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}
	
	public static ECommandHandler getCommandHandler() {
		return cmdHandler;
	}
	
}
