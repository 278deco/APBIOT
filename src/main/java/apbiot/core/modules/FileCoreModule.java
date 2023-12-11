package apbiot.core.modules;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.management.InstanceNotFoundException;

import apbiot.core.modules.exceptions.CoreModuleLaunchingException;
import apbiot.core.modules.exceptions.CoreModuleLoadingException;
import apbiot.core.modules.exceptions.CoreModuleShutdownException;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.events.DirectoriesLoadedEvent;
import marshmalliow.core.builder.DirectoryManager;
import marshmalliow.core.builder.IOCacheManager;
import marshmalliow.core.builder.IOFactory;
import marshmalliow.core.objects.Directory;

public class FileCoreModule extends CoreModule {

	private DirectoryManager directoryManager;
	
	protected FileCoreModule() {
		super(UUID.randomUUID());
	}

	@Override
	public void executeAssertion() {
		try {
			Class.forName("marshmalliow.core.database.DirectoryManager");
			Class.forName("marshmalliow.core.database.IOCacheManager");
		}catch(ClassNotFoundException e) {
			System.err.println("Cannot find reference for MarshmallIOw class DirectoryManager or IOCacheManager. Aborting launch...");
			System.exit(-1);
		}
	}

	@Override
	public void init() throws CoreModuleLoadingException {
		this.coreHealthy.set(true);
		
		this.directoryManager = new DirectoryManager();
		IOCacheManager.get();
		IOFactory.get();
		
		try {
			IOFactory.bindDirectoryManager(this.directoryManager);
		} catch (InstanceNotFoundException e) {
			throw new CoreModuleLoadingException("", e);
		}
	}

	@Override
	public void launch() throws CoreModuleLaunchingException {
		//TODO Load configuration
	}

	@Override
	public void shutdown() throws CoreModuleShutdownException {
		try {
			IOCacheManager.get().saveAll();
		} catch (IOException e) {
			throw new CoreModuleShutdownException("Couldn't shutdown saved files...", e);
		}
	}
	
	@Override
	public void onEventReceived(ProgramEvent e, EventPriority priority) {
		if(e instanceof DirectoriesLoadedEvent) {
			final Set<Directory> directories = ((DirectoriesLoadedEvent)e).getDirectories();
			if(this.directoryManager != null && directories != null) this.directoryManager.registerNewDirectories(directories);
		}
	}


	@Override
	public CoreModuleType getType() {
		return CoreModuleType.IO_FACTORY;
	}

}
