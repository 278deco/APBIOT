package apbiot.core.modules;

import java.util.UUID;

import apbiot.core.modules.exceptions.CoreModuleLaunchingException;
import apbiot.core.modules.exceptions.CoreModuleLoadingException;
import apbiot.core.modules.exceptions.CoreModuleShutdownException;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;

public class FileCoreModule extends CoreModule {

	protected FileCoreModule() {
		super(UUID.randomUUID());
	}

	@Override
	public void executeAssertion() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init() throws CoreModuleLoadingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void launch() throws CoreModuleLaunchingException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() throws CoreModuleShutdownException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onEventReceived(ProgramEvent e, EventPriority priority) {
	
	}


	@Override
	public CoreModuleType getType() {
		return CoreModuleType.IO_FACTORY;
	}

}
