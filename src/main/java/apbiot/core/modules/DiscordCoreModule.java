package apbiot.core.modules;

import java.util.Optional;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.builder.ClientBuilder;
import apbiot.core.builder.UnbuiltBotException;
import apbiot.core.pems.Subscribe;
import apbiot.core.pems.SubscribeType;
import apbiot.core.pems.commands.LogIntoDiscordAction;
import apbiot.core.pems.commands.RebuildDiscordCommandsAction;
import apbiot.core.pems.events.ConfigurationLoadedEvent;
import apbiot.core.pems.events.CoreModulesReadyEvent;
import apbiot.core.pems.events.DiscordCommandParsedEvent;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.http.client.ClientException;

public class DiscordCoreModule extends CoreModule {
	
	private static final Logger LOGGER = LogManager.getLogger(ConsoleCoreModule.class);
	
	private static final String DEFAULT_PREFIX = ";";
	private static final IntentSet DEFAULT_INTENTS = IntentSet.none();
	
	private Optional<String> tokenSecret = Optional.empty();
	
	private Optional<String> prefix = Optional.empty();
	private Optional<IntentSet> intents = Optional.empty();
	private Optional<ClientPresence> defaultPresence = Optional.empty();

	//Builder
	private ClientBuilder clientBuilder;
	
	public DiscordCoreModule() {
		super(UUID.randomUUID());
	}

	@Override
	public void executeAssertion() {
	}

	@Override
	public void init() throws CoreModuleLoadingException {
		this.clientBuilder = new ClientBuilder();
		this.clientBuilder.createNewInstance();
		
		this.coreHealthy.set(true);
	}

	@Override
	public void launch() throws CoreModuleLaunchingException {
		
		this.coreThread = new Thread(new Runnable() {

			@Override
			public void run() {
				coreRunning.set(true);
				try {
					clientBuilder.launch(tokenSecret.orElseThrow(() -> new UnbuiltBotException("Undefined token secret")), intents.orElse(DEFAULT_INTENTS), prefix.orElse(DEFAULT_PREFIX), defaultPresence);
				} catch (UnbuiltBotException | ClientException e) {
					LOGGER.error("Unexpected error while launching client", e);
					coreHealthy.set(false);
					coreRunning.set(false);
					coreThread.interrupt();
				}
			}
			
		}, getType().getName()+" Thread");
		
		try {
			this.coreThread.start();
		}catch(IllegalThreadStateException e) {
			this.coreHealthy.set(false);
			this.coreRunning.set(false);
			throw new CoreModuleLaunchingException("Unexpected error while launching core thread", e);
		}
	}

	@Override
	public void shutdown() throws CoreModuleShutdownException {
		if(this.coreRunning.get()) {
			try {
				this.coreRunning.set(false);
				clientBuilder.shutdownInstance();
			} catch (UnbuiltBotException e) {
				this.coreHealthy.set(false);
				throw new CoreModuleShutdownException("Couldn't shutdown client correctly", e);
			}
		}
	}
	
	@Subscribe(type = SubscribeType.EVENT)
	public void onCommandParsed(DiscordCommandParsedEvent event) {
		clientBuilder.updateNativeCommandMapping(event.optionalDiscordNativeCommands());
		clientBuilder.updateSlashCommandMapping(event.optionalDiscordSlashCommands());
		clientBuilder.updateApplicationCommandMapping(event.optionalDiscordApplicationCommands());
		clientBuilder.updateComponentCommandMapping(event.optionalDiscordComponentCommands());
		clientBuilder.buildCommandator();
	}
	
	@Subscribe(type = SubscribeType.EVENT)
	public void onConfigurationLoaded(ConfigurationLoadedEvent event) {
		this.prefix = Optional.ofNullable(event.instancePrefix());
		this.intents = Optional.ofNullable(event.intentSet());
		this.defaultPresence = Optional.ofNullable(event.clientPresence());
	}
	
	@Subscribe(type = SubscribeType.EVENT)
	public void onCoreModulesReady(CoreModulesReadyEvent event) {
		clientBuilder.setReady(true);
	}
	
	@Subscribe(type = SubscribeType.ACTION)
	public void loginToDiscordAct(LogIntoDiscordAction action) {
		this.tokenSecret = Optional.ofNullable(action.clientToken());
	}
	
	@Subscribe(type = SubscribeType.ACTION)
	public void rebuildCommandMapping(RebuildDiscordCommandsAction action) {
		clientBuilder.rebuildCommandMapping(action.scope());
	}
	
	@Override
	public CoreModuleType getType() {
		return BaseCoreModuleType.DISCORD_GATEWAY;
	}

}
