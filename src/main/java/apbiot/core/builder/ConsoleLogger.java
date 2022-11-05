package apbiot.core.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import apbiot.core.MainInitializer;
import apbiot.core.command.SystemCommand;
import apbiot.core.event.EventListener;
import apbiot.core.helper.ArgumentHelper;
import apbiot.core.objects.interfaces.IEvent;
import apbiot.core.objects.interfaces.ILoggerEvent;

public class ConsoleLogger {
	
	private boolean running;
	private BufferedReader reader;
	private InputStreamReader insReader;
	
	//Compilated Command Map
	private final Map<List<String>, SystemCommand> COMMANDS;
	
	private ConsoleLogger(ConsoleLogger.Builder builder) {
		this.COMMANDS = builder.getCommands();
	}
	
	public static ConsoleLogger.Builder builder() {
		return new ConsoleLogger.Builder();
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
						MainInitializer.LOGGER.warn("Unexpected error while reading console",e);
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
						MainInitializer.LOGGER.info(((ILoggerEvent)e).getLoggerMessage());
						break;
					case ERROR:
						MainInitializer.LOGGER.error(((ILoggerEvent)e).getLoggerMessage());
						break;
					case WARNING:
						MainInitializer.LOGGER.warn(((ILoggerEvent)e).getLoggerMessage());
						break;
				}
			} 
		}	
	}
	
	/**
	 * Build a new ConsoleLogger
	 * @author 278deco
	 * @see apbiot.core.builder.ConsoleLogger
	 */
	public static final class Builder {

		private Map<List<String>, SystemCommand> commands;
		
		private Builder() { }
		
		/**
		 * Add the command map
		 * @param cmd - the command map
		 * @return this instance
		 */
		public Builder withCommands(Map<List<String>, SystemCommand> cmd) {
			this.commands = cmd;
			
			return this;
		}
		
		/**
		 * Add a command to the map
		 * @param cmdName - the list of alliases
		 * @param cmdInstance - the command instance
		 * @return this instance
		 */
		public Builder withCommands(List<String> cmdName, SystemCommand cmdInstance) {
			if(this.commands == null) this.commands = new HashMap<>();
			this.commands.put(cmdName, cmdInstance);
			
			return this;
		}
		
		/**
		 * Build a new ConsoleLogger instance
		 * @return a new ConsoleLogger
		 */
		public ConsoleLogger build() {
			return new ConsoleLogger(this);
		}
		
		private Map<List<String>, SystemCommand> getCommands() {
			return commands;
		}
		
	}
	
}
