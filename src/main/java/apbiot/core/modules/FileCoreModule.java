package apbiot.core.modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

import javax.management.InstanceNotFoundException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.exceptions.CoreModuleLaunchingException;
import apbiot.core.exceptions.CoreModuleLoadingException;
import apbiot.core.exceptions.CoreModuleShutdownException;
import apbiot.core.i18n.LanguageManager;
import apbiot.core.io.json.JSONClientConfiguration;
import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.ProgramEventManager;
import apbiot.core.pems.events.DirectoriesLoadedEvent;
import apbiot.core.utils.References;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;
import marshmalliow.core.builder.DirectoryManager;
import marshmalliow.core.builder.IOCacheManager;
import marshmalliow.core.builder.IOFactory;
import marshmalliow.core.builder.JSONFactory;
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
		JSONFactory.get();

		try {
			IOFactory.bindDirectoryManager(this.directoryManager);
			JSONFactory.withDirectoryManager(this.directoryManager);
		} catch (InstanceNotFoundException e) {
			throw new CoreModuleLoadingException("", e);
		}finally {
			this.coreRunning.set(false);
		}
	}

	@Override
	public void preLaunch() throws CoreModuleLaunchingException {
		this.coreRunning.set(true);
		try {
			final Path configPath = Path.of("config");
			final String configFileName = References.PROD_ENVIRONMENT ? "config" : "config_sdev";

			if(Files.exists(configPath.resolve(configFileName+".json"))) {
				this.directoryManager.registerNewDirectory(new Directory("main:configuration", configPath)); //Register the configuration directory

				JSONClientConfiguration configurationFile = null;
				try {
					
					configurationFile = JSONFactory.get().createJSONFileFromBase(JSONClientConfiguration.class, "main:configuration", configFileName, null);
					configurationFile.readFile();
				} catch (IOException e) {
					throw new CoreModuleLaunchingException("Couldn't correctly read config.json file",e);
				}

				IOCacheManager.get().add("client_configuration", configurationFile); //Add the config to the cache

				//Dipatch the event
				final IntentSet intentset = configurationFile.getIntentSet();
				final ClientPresence presence = configurationFile.getClientPresence();

				if(configurationFile.getPrefix() == null || intentset == null || presence == null) {
					throw new CoreModuleLaunchingException("Missing mandatory values in configuration file.");
				}

				ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.CONFIGURATION_LOADED_EVENT, new Object[] {configurationFile.getPrefix(), intentset, presence, configurationFile.getVersion()});
			}else {
				LOGGER.warn("No configuration file was found. Some client's properties might not be initialized correctly.");
			}
			
			//Load the LanguageManager (Localization)
			final Path languagePath = Path.of("config/lang");
			if(Files.exists(languagePath)) {
				LanguageManager.get().loadLanguagesFolder(languagePath);
			}else {
				LOGGER.warn("No language folder was found. Localized string might be appear broken.");
			}
			
		}finally {
			this.coreRunning.set(false);
		}
	}

	@Override
	public void launch() throws CoreModuleLaunchingException {
		ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.FILES_REGISTRATION_EVENT);
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
		return BaseCoreModuleType.IO_FACTORY;
	}

}
