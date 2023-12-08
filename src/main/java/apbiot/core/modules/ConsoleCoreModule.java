package apbiot.core.modules;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.command.SystemCommand;
import apbiot.core.helper.ArgumentHelper;
import apbiot.core.modules.exceptions.CoreModuleLaunchingException;
import apbiot.core.modules.exceptions.CoreModuleLoadingException;
import apbiot.core.modules.exceptions.CoreModuleShutdownException;
import apbiot.core.pems.LoggableProgramEvent;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.events.CommandsListParsedEvent;

public class ConsoleCoreModule extends CoreModule {

	protected static Logger LOGGER;
	
	private BufferedReader inputReader;
	
	private Map<Set<String>, SystemCommand> commandMap = new HashMap<>();
	
	public ConsoleCoreModule() {
		super(UUID.randomUUID());
	}
	
	@Override
	public void executeAssertion() {
		try {
			Class.forName("org.apache.logging.log4j.LogManager");
		}catch(ClassNotFoundException e) {
			System.err.println("Cannot find reference for Log4J class LogManager. Aborting launch...");
			System.exit(-1);
		}
	}
	
	@Override
	public void init() throws CoreModuleLoadingException {
		LOGGER = LogManager.getLogger(ConsoleCoreModule.class);
		this.coreHealthy.set(true);
		
		this.inputReader = new BufferedReader(new InputStreamReader(System.in));
	}

	
	@Override
	public void launch() throws CoreModuleLaunchingException {
		this.coreThread = new Thread(new Runnable() {

			@Override
			public void run() {
				coreRunning.set(true);
				while(coreRunning.get()) {
					try {
						final String input = inputReader.readLine();
						
						for(var entry : commandMap.entrySet()) {
							if(entry.getKey().contains(input.split(" +")[0])) {
								entry.getValue().execute(ArgumentHelper.formatCommandArguments(false, input));
							}
						}
					
					}catch(IOException e) {
						LOGGER.warn("Unexpected error while parsing console input ",e);
					}
				}
			}
			
		}, getType().getName()+" Thread");
		
		this.coreThread.setDaemon(true);
		this.coreThread.start();
	}
	
	@Override
	public void shutdown() throws CoreModuleShutdownException {
		this.coreRunning.set(false);
		
		try {
			this.inputReader.close();
			
			if(this.coreThread.isInterrupted()) this.coreThread.interrupt();
		}catch(IOException | SecurityException e ) {
			throw new CoreModuleShutdownException("An exception happened while shutting down "+getType().getName()+" core.", e);
		}
	}
	
	@Override
	public void onEventReceived(ProgramEvent e, EventPriority priority) {
		if(e instanceof CommandsListParsedEvent) {
			final Optional<Map<Set<String>, SystemCommand>> map = ((CommandsListParsedEvent)e).getConsoleCoreCommands();
			if(map.isPresent()) this.commandMap = map.get();
			
		}else if(e instanceof LoggableProgramEvent) {
			LOGGER.log(((LoggableProgramEvent)e).getLogPriority().getLevel(), ((LoggableProgramEvent)e).getLoggerMessage());
		}
	}
	
	@Override
	public CoreModuleType getType() {
		return CoreModuleType.CONSOLE_LOGGING;
	}
}
