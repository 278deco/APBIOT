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

public class ConsoleLoggerBuilder {
	
	private boolean running;
	private BufferedReader reader;
	private InputStreamReader insReader;
	
	//Compilated Command Map
	private Map<List<String>, SystemCommand> COMMANDS = new HashMap<>();
	
	/**
	 * Build a new ConsoleLogger
	 * @param cmds - the command map
	 * @see apbiot.core.handler.ESystemCommandHandler
	 * @return an instance of ConsoleLoggerBuilder
	 */
	public ConsoleLoggerBuilder build(Map<List<String>, SystemCommand> cmds) {
		this.COMMANDS = cmds;
		
		return this;
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
			
		},"Console Thread");
		
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
	
}
