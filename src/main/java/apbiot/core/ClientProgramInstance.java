package apbiot.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.handler.Handler;
import apbiot.core.handler.HandlerPreProcessingException;
import apbiot.core.modules.CoreModule;
import apbiot.core.modules.CoreModuleType;
import apbiot.core.modules.exceptions.CoreModuleLaunchingException;
import apbiot.core.modules.exceptions.CoreModuleLoadingException;
import apbiot.core.modules.exceptions.CoreModuleShutdownException;
import apbiot.core.modules.exceptions.MandatoryCoreMissingException;
import apbiot.core.objects.interfaces.IRunnableMethod;
import apbiot.core.pems.ProgramEventManager;

public class ClientProgramInstance {
	
	private static Logger LOGGER;
	
	//Instance UUID (for this instance only)
	public static final String INSTANCE_UUID = UUID.randomUUID().toString();
	
	private final Map<CoreModuleType, CoreModule> activeModules = new LinkedHashMap<>();
	private final Set<Handler> activeHandlers;
	
	private ClientProgramInstance(ClientProgramInstance.Builder builder) {
		ProgramEventManager.get(); //Init PEMS
		
		for(CoreModule m : builder.activeModules) {
			activeModules.put(m.getType(), m);
			ProgramEventManager.get().addNewListener(m);
		}
		
		this.activeHandlers = builder.activeHandlers;
		this.activeHandlers.forEach(handler -> ProgramEventManager.get().addNewListener(handler));
	}
	
	public void launch() throws MandatoryCoreMissingException, CoreModuleLoadingException, CoreModuleLaunchingException {
		
		//Assertion phase
		for(CoreModule cm : activeModules.values()) {
			cm.executeAssertion();
		}
		
		LOGGER = LogManager.getLogger(ClientProgramInstance.class);
		
		//Check if mandatory cores are present
		if(!isModuleActive(CoreModuleType.CREDENTIALS_HOLDER)) {
			throw new MandatoryCoreMissingException("Missing mandatory CoreModule "+CoreModuleType.CREDENTIALS_HOLDER.getName());
		}
		if(!isModuleActive(CoreModuleType.CONSOLE_LOGGING)) {
			throw new MandatoryCoreMissingException("Missing mandatory CoreModule "+CoreModuleType.CONSOLE_LOGGING.getName());
		}
		if(!isModuleActive(CoreModuleType.DISCORD_GATEWAY)) {
			throw new MandatoryCoreMissingException("Missing mandatory CoreModule "+CoreModuleType.DISCORD_GATEWAY.getName());
		}
		
		//Pre-process handlers
		for(Handler handler : activeHandlers) {
			LOGGER.info("PHASE 0 - Pre-processing {} handler from class {}...", handler.getType().name(), handler.getClass().getName());
			try {
				handler.preProcessing();
			} catch (HandlerPreProcessingException e) {
				LOGGER.error("Handler [Class:{}, Type:{}] encoutered fatal error during pre-processing phase!", handler.getClass().getName(), handler.getType().name());
			}
		}
		
		//Initialization phase
		for(CoreModule cm : activeModules.values()) {
			LOGGER.info("PHASE 1 - Itinializing Core Module {}...",cm.getType().getName());
			try {
				cm.init();
			} catch (CoreModuleLoadingException e) {
				final String err = "Mandatory CoreModule [ID:"+cm.getUUID().toString()+", Name:"+cm.getType().getName()+"] encoutered fatal error during initialization phase!";
				if(cm.getType().isMandatory()) {
					throw new CoreModuleLoadingException(err);
				}else {
					LOGGER.error(err);
				}
			}
		}
		
		//Pre-Launching phase
		for(CoreModule cm : activeModules.values()) {
			LOGGER.info("PHASE 2 - Pre-launching Core Module {}...",cm.getType().getName());
			try {
				cm.preLaunch();
			} catch (CoreModuleLaunchingException e) {
				e.printStackTrace();
				LOGGER.error("CoreModule [ID:{}, Name:{}, IsMandatoy:{}] encoutered fatal error during pre-launching phase!", cm.getUUID().toString(), cm.getType().getName(), cm.getType().isMandatory());
			}
		}
		
		//Launching phase
		for(CoreModule cm : activeModules.values()) {
			LOGGER.info("PHASE 3 - Launching Core Module {}...",cm.getType().getName());
			try {
				cm.launch();
			} catch (CoreModuleLaunchingException e) {
				final String err = "CoreModule [ID:"+cm.getUUID().toString()+", Name:"+cm.getType().getName()+"] encoutered fatal error during launching phase!";
				if(cm.getType().isMandatory()) {
					throw new CoreModuleLaunchingException("Mandatory "+err);
				}else {
					LOGGER.error(err);
				}
			}
		}
		
		//Launching phase
		for(CoreModule cm : activeModules.values()) {
			LOGGER.info("PHASE 4 - Post-launching Core Module {}...",cm.getType().getName());
			try {
				cm.postLaunch();
			} catch (CoreModuleLaunchingException e) {
				LOGGER.error("CoreModule [ID:{}, Name:{}, IsMandatoy:{}] encoutered fatal error during post-launching phase!", cm.getUUID().toString(), cm.getType().getName(), cm.getType().isMandatory());
			}
		}
		
		LOGGER.info("All Core Modules have been launched. Get system status with 'core status'.");
	}
	
	public boolean isModuleActive(CoreModuleType moduleKey) {
		return activeModules.containsKey(moduleKey);
	}
	
	public Collection<CoreModule> getActiveModules() {
		return Collections.unmodifiableCollection(activeModules.values());
	}
	
	public class ShutdownProgram implements IRunnableMethod {

		@Override
		public void run() {
			
			for(CoreModule cm : activeModules.values()) {
				try {
					LOGGER.info("Shutting down Core Module {}...",cm.getType().getName());
					cm.shutdown();
				} catch (CoreModuleShutdownException e) {
					LOGGER.error("Mandatory CoreModule [ID:{}, Name:{}] encoutered fatal error during launching phase!",cm.getUUID().toString(), cm.getType().getName());		
				}
			}
		}
	}
	
	public static ClientProgramInstance.Builder builder() {
		return new ClientProgramInstance.Builder();
	}
	
	public static final class Builder {
		
		private final Set<CoreModule> activeModules = new TreeSet<>();
		private final Set<Handler> activeHandlers = new HashSet<>();
		
		private Builder() { }
		
		public Builder withModule(CoreModule module) {
			activeModules.add(module);
			return this;
		}
		
		public Builder withModules(CoreModule... modules) {
			for(CoreModule m : modules) activeModules.add(m);
			return this;
		}
		
		public Builder withHandler(Handler handler) {
			activeHandlers.add(handler);
			return this;
		}
		
		public Builder withHandlers(Handler... handlers) {
			for(Handler h : handlers) activeHandlers.add(h);
			return this;
		}
		
		public ClientProgramInstance build() {
			return new ClientProgramInstance(this);
		}
		
	}
}
