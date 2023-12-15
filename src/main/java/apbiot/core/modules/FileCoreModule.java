package apbiot.core.modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.management.InstanceNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.modules.exceptions.CoreModuleLaunchingException;
import apbiot.core.modules.exceptions.CoreModuleLoadingException;
import apbiot.core.modules.exceptions.CoreModuleShutdownException;
import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.ProgramEventManager;
import apbiot.core.pems.events.DirectoriesLoadedEvent;
import discord4j.core.object.presence.Activity.Type;
import discord4j.core.object.presence.ClientActivity;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.object.presence.Status;
import discord4j.gateway.intent.IntentSet;
import marshmalliow.core.builder.DirectoryManager;
import marshmalliow.core.builder.IOCacheManager;
import marshmalliow.core.builder.IOFactory;
import marshmalliow.core.json.JSONFile;
import marshmalliow.core.json.objects.JSONObject;
import marshmalliow.core.objects.Directory;

public class FileCoreModule extends CoreModule {

	private static final Logger LOGGER = LogManager.getLogger(FileCoreModule.class);
	
	private DirectoryManager directoryManager;
	
	public FileCoreModule() {
		super(UUID.randomUUID());
	}

	@Override
	public void executeAssertion() {
		try {
			Class.forName("marshmalliow.core.builder.DirectoryManager");
			Class.forName("marshmalliow.core.builder.IOCacheManager");
		}catch(ClassNotFoundException e) {
			System.err.println("Cannot find reference for MarshmallIOw class DirectoryManager or IOCacheManager. Aborting launch...");
			System.exit(-1);
		}
	}

	@Override
	public void init() throws CoreModuleLoadingException {
		this.coreHealthy.set(true);
		this.coreRunning.set(true);
		
		this.directoryManager = new DirectoryManager();
		IOCacheManager.get();
		IOFactory.get();
		
		try {
			IOFactory.bindDirectoryManager(this.directoryManager);
		} catch (InstanceNotFoundException e) {
			throw new CoreModuleLoadingException("", e);
		}finally {
			this.coreRunning.set(false);
		}
	}

	@Override
	public void postLaunch() throws CoreModuleLaunchingException {
		this.coreRunning.set(true);
		try {
			final Path configPath = Path.of("config");
			if(Files.exists(configPath.resolve("config.json"))) {
				this.directoryManager.registerNewDirectory(new Directory("main:configuration", configPath)); //Register the configuration directory
				
				final JSONFile configurationFile = IOFactory.get().createNewJSONFile("main:configuration", "config", JSONObject.class, Optional.empty());
				try {
					configurationFile.readFile();
				} catch (IOException e) {
					throw new CoreModuleLaunchingException("Couldn't correctly read config.json file",e);
				}
				
				IOCacheManager.get().add(configurationFile); //Add the config to the cache
				
				//Dipatch the event
				final String prefix = configurationFile.getContentAsObject().get("prefix", String.class);
				final String intentStrValue = configurationFile.getContentAsObject().get("intents", String.class); //Accepted values : ALL, NONE, NON_PRIVILEGED
				final JSONObject clientObjPresence = configurationFile.getContentAsObject().get("discord_status", JSONObject.class); //Formatted as {"status": "", "activity":"", "text":""} or {"status": "", "activity":"", "text":"", "url":""}
					
				System.out.println(prefix);
				System.out.println(intentStrValue);
				System.out.println(clientObjPresence);
				
				if(prefix != null && intentStrValue != null && clientObjPresence != null) {
					try {
						final ClientActivity activity = ClientActivity.of(Type.valueOf(clientObjPresence.get("activity", String.class)), clientObjPresence.get("text", String.class), clientObjPresence.get("url", String.class));
						final ClientPresence presence = ClientPresence.of(Status.valueOf(clientObjPresence.get("status", String.class)), activity);
						
						ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.CONFIGURATION_LOADED_EVENT, new Object[] {prefix, parseIntentSet(intentStrValue), presence});
					}catch(IllegalArgumentException | NullPointerException e) {
						throw new CoreModuleLaunchingException("Malformed mandatory value in configuration file.", e);
					}
				}else {
					throw new CoreModuleLaunchingException("Missing mandatory values in configuration file.");
				}
			}else {
				LOGGER.warn("No configuration file was found. Certain client's properties might not be initialized correctly.");
			}
		}finally {
			this.coreRunning.set(false);
		}
	}
	
	private final IntentSet parseIntentSet(String intentStr) {
		switch (intentStr.toUpperCase()) {
		case "ALL":
			return IntentSet.all();
		case "NON_PRIVILEGED":
			return IntentSet.nonPrivileged();
		default:
			return IntentSet.none();
		}
	}
	
	@Override
	public void launch() throws CoreModuleLaunchingException {
	}

	@Override
	public void shutdown() throws CoreModuleShutdownException {
		this.coreRunning.set(true);
		try {
			IOCacheManager.get().saveAll();
		} catch (IOException e) {
			throw new CoreModuleShutdownException("Couldn't shutdown saved files...", e);
		}finally {
			this.coreRunning.set(false);
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
