package apbiot.core.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.MainInitializer;
import apbiot.core.command.SystemCommand;
import apbiot.core.event.EventListener;
import apbiot.core.handler.AbstractSystemCommandHandler;
import apbiot.core.helper.ArgumentHelper;
import apbiot.core.objects.interfaces.IEvent;
import apbiot.core.objects.interfaces.ILoggerEvent;

public class ConsoleLogger {
	
	private static final Logger LOGGER = LogManager.getLogger(ConsoleLogger.class);
	
	private static ConsoleLogger instance;
	private Map<List<String>, SystemCommand> COMMANDS = new HashMap<>();
	
	private boolean running;
	private BufferedReader reader;
	private InputStreamReader insReader;
	
	private ConsoleLogger() {}
	
	public static ConsoleLogger getInstance() {
		if(instance == null) {
			synchronized (ConsoleLogger.class) {
				if(instance == null) instance = new ConsoleLogger();
			}
		}
		return instance;
	}
	
	public static boolean doesInstanceExists() {
		return instance != null;
	}
	
	/**
	 * Updated the command mapping of the console logger
	 */
	public void updatedCommandReferences() {
		this.COMMANDS = MainInitializer.getHandlers().getHandler(AbstractSystemCommandHandler.class).COMMANDS;
	}
	
	/**
	 * Start listening to every command launched into the console
	 */
	public void startListening() {
		this.running = true;
		
		insReader = new InputStreamReader(System.in);
		reader = new BufferedReader(insReader);
		Thread cThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(running) {
					String line = "";
					
					try {
						line = reader.readLine();
					} catch (IOException e) {
						LOGGER.warn("Unexpected error while reading console",e);
					}
						
					for(Map.Entry<List<String>, SystemCommand> entry : COMMANDS.entrySet()) {
						for(String commandName : entry.getKey()) {
							if(line.startsWith(commandName)) {
								entry.getValue().execute(ArgumentHelper.formatCommandArguments(false, line));
							}
						}
					}
				}
			}
			
		},"Console Logger Thread");
		
		cThread.setDaemon(true);
		cThread.start();
		
	}
	
	/**
	 * Stop the console listener
	 * @throws IOException
	 */
	public void stopListening() throws IOException {
		this.running = false;
		
		insReader.close();
		reader.close();

	}
	
	public class ConsoleLoggerListener implements EventListener {
		
		@Override
		public void newEventReceived(IEvent e) {
			if(e instanceof ILoggerEvent) {
				
				switch(((ILoggerEvent)e).getEventPriority()) {
					case INFO:
						LOGGER.info(((ILoggerEvent)e).getLoggerMessage());
						break;
					case WARNING:
						LOGGER.warn(((ILoggerEvent)e).getLoggerMessage());
						break;
					case ERROR:
						LOGGER.error(((ILoggerEvent)e).getLoggerMessage());
						break;
					case FATAL:
						LOGGER.fatal(((ILoggerEvent)e).getLoggerMessage());
						break;
				}
			} 
		}	
	}
}
