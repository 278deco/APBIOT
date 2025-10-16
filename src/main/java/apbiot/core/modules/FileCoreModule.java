package apbiot.core.modules;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.i18n.LanguageManager;
import apbiot.core.io.json.JSONClientConfiguration;
import apbiot.core.io.json.JSONClientConfigurationBuilder;
import apbiot.core.pems.GlobalActionBus;
import apbiot.core.pems.GlobalEventBus;
import apbiot.core.pems.commands.RegisterAdditionalFilesAction;
import apbiot.core.pems.commands.RegisterConfigurationFilesAction;
import apbiot.core.pems.commands.RegisterDirectoriesAction;
import apbiot.core.pems.events.ConfigurationLoadedEvent;
import apbiot.core.utils.References;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;
import marshmalliow.core.builder.IOCacheManager;
import marshmalliow.core.builder.JSONFactory;
import marshmalliow.core.directory.Directory;
import marshmalliow.core.directory.GlobalDirectoryRegistry;
import marshmalliow.core.directory.LocalDirectory;
import marshmalliow.core.file.ReadMode;
import marshmalliow.core.file.SaveMode;
import marshmalliow.core.json.objects.JSONObject;

public class FileCoreModule extends CoreModule {

	private static final Logger LOGGER = LogManager.getLogger(FileCoreModule.class);

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

		GlobalDirectoryRegistry.get(); // Initialize the global directory registry
		IOCacheManager.get();
		JSONFactory.get();
	}

	@Override
	public void preLaunch() throws CoreModuleLaunchingException {
		this.coreRunning.set(true);
		try {
			final Path configPath = Path.of("config");
			final String configFileName = References.PROD_ENVIRONMENT ? "config" : "config_sdev";

			if(Files.exists(configPath.resolve(configFileName+".json"))) {
				GlobalDirectoryRegistry.get().register("main:configuration", configPath.toUri()); //Register the configuration directory

				JSONClientConfiguration configurationFile = null;
				try {
					configurationFile =  JSONClientConfigurationBuilder.builder(GlobalDirectoryRegistry.get())
							.directoryId("main:configuration")
							.name(configFileName)
							.base(new JSONObject())
							.build(); 
					configurationFile.readFile(ReadMode.NORMAL);
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
				
				GlobalActionBus.get().dispatchAction(new RegisterDirectoriesAction());
				GlobalActionBus.get().dispatchAction(new RegisterConfigurationFilesAction());
				GlobalEventBus.get().dispatchEvent(new ConfigurationLoadedEvent(configurationFile.getPrefix(), intentset, presence, configurationFile.getVersion()));
			}else {
				LOGGER.warn("No configuration file was found. Some client's properties might not be initialized correctly.");
			}
			
			//Load the LanguageManager (Localization)
			final Directory dir = new LocalDirectory("config:lang", Path.of("config", "lang"));
			try {
				if(dir.exists("")) {
					LanguageManager.get().loadLanguagesFolder(dir);
				}else {
					LOGGER.warn("No language folder was found. Localized string might be appear broken.");
				}
			} catch (IOException e) {
				LOGGER.warn("No language folder was found. Localized string might be appear broken.");
			}
			
		}finally {
			this.coreRunning.set(false);
		}
	}

	@Override
	public void launch() throws CoreModuleLaunchingException {
		GlobalActionBus.get().dispatchAction(new RegisterAdditionalFilesAction());
	}

	@Override
	public void shutdown() throws CoreModuleShutdownException {
		this.coreRunning.set(true);
		try {
			IOCacheManager.get().saveAll(SaveMode.NORMAL);
		} catch (IOException e) {
			throw new CoreModuleShutdownException("Couldn't shutdown saved files...", e);
		}finally {
			this.coreRunning.set(false);
		}
	}

	@Override
	public CoreModuleType getType() {
		return BaseCoreModuleType.IO_FACTORY;
	}

}
