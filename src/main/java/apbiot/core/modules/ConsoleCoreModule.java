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
import apbiot.core.exceptions.CoreModuleLaunchingException;
import apbiot.core.exceptions.CoreModuleLoadingException;
import apbiot.core.exceptions.CoreModuleShutdownException;
import apbiot.core.helper.ArgumentHelper;
import apbiot.core.pems.LoggableProgramEvent;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.events.CommandsListParsedEvent;

public class ConsoleCoreModule extends CoreModule {

	private static final String COMMAND_NOT_FOUND_MSG = "Cannot recognize command input. Type 'help' to get the whole list of commands.";
	
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
				String input = null;
				do {
					try {
						input = inputReader.readLine();
						
						if(input == null) continue;
						
						if(input.isBlank() || input.isEmpty() || input == "") {
							LOGGER.info(COMMAND_NOT_FOUND_MSG);
							continue;
						}
						
						boolean found = false;
						for(var entry : commandMap.entrySet()) {
							if(entry.getKey().contains(input.split(" +")[0])) {
								found = true;
								entry.getValue().execute(ArgumentHelper.formatCommandArguments(false, input));
							}
						}
						
						if(!found) LOGGER.info(COMMAND_NOT_FOUND_MSG);
						
					}catch(IOException e) {
						LOGGER.warn("Unexpected error while parsing console input ",e);
					}
				}while(coreRunning.get() && input != null);
			}
			
		}, getType().getName()+" Thread");
		
		try {
			this.coreThread.setDaemon(true);
			this.coreThread.start();
		}catch(IllegalThreadStateException e) {
			this.coreHealthy.set(false);
			this.coreRunning.set(false);
			throw new CoreModuleLaunchingException("", e);
		}
	}
	
	@Override
	public void shutdown() throws CoreModuleShutdownException {
		try {
			this.coreRunning.set(false);			
			if(!this.coreThread.isInterrupted()) this.coreThread.interrupt();
			
		}catch(SecurityException e) {
			this.coreHealthy.set(false);
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
		return BaseCoreModuleType.CONSOLE_LOGGING;
	}
}
