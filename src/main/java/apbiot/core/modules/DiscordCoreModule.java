package apbiot.core.modules;

import java.util.Optional;
import java.util.UUID;

import apbiot.core.builder.ClientBuilder;
import apbiot.core.exceptions.UnbuiltBotException;
import apbiot.core.modules.exceptions.CoreModuleLaunchingException;
import apbiot.core.modules.exceptions.CoreModuleLoadingException;
import apbiot.core.modules.exceptions.CoreModuleShutdownException;
import apbiot.core.pems.ProgramEvent;
import apbiot.core.pems.ProgramEvent.EventPriority;
import apbiot.core.pems.events.CommandsListParsedEvent;
import apbiot.core.pems.events.ConfigurationFileLoadedEvent;
import apbiot.core.pems.events.InstanceTokenAcquieredEvent;
import discord4j.core.object.presence.ClientPresence;
import discord4j.gateway.intent.IntentSet;

public class DiscordCoreModule extends CoreModule {
	
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
				} catch (UnbuiltBotException e) {
					coreHealthy.set(false);
					coreRunning.set(false);
					coreThread.interrupt();
				}
			}
			
		}, getType().getName()+" Thread");
		
		try {
			this.coreThread.start();
		}catch(IllegalThreadStateException e) {
			throw new CoreModuleLaunchingException("Unexpected error while launching core thread", e);
		}
	}

	@Override
	public void shutdown() throws CoreModuleShutdownException {
		if(this.coreRunning.get()) {
			try {
				clientBuilder.shutdownInstance();
				this.coreRunning.set(false);
			} catch (UnbuiltBotException e) {
				throw new CoreModuleShutdownException("Couldn't shutdown client correctly", e);
			}
		}
	}
	
	@Override
	public void onEventReceived(ProgramEvent e, EventPriority priority) {
		if(priority == EventPriority.HIGH) {
			if(e instanceof CommandsListParsedEvent) {
				final CommandsListParsedEvent parsed = (CommandsListParsedEvent)e;
				clientBuilder.updateNativeCommandMapping(parsed.getDiscordCoreNativeCommands());
				clientBuilder.updateSlashCommandMapping(parsed.getDiscordCoreSlashCommands());
				clientBuilder.updateApplicationCommandMapping(parsed.getDiscordCoreApplicationCommands());
			}else if(e instanceof ConfigurationFileLoadedEvent) {
				final ConfigurationFileLoadedEvent parsed = (ConfigurationFileLoadedEvent)e;
				this.prefix = parsed.getInstancePrefix();
				this.intents = parsed.getInstanceIntentSet();
				this.defaultPresence = parsed.getInstanceClientPresence(); 
			}else if(e instanceof InstanceTokenAcquieredEvent) {
				this.tokenSecret = ((InstanceTokenAcquieredEvent)e).getClientToken();
			}
		}
	}

	@Override
	public CoreModuleType getType() {
		return CoreModuleType.DISCORD_GATEWAY;
	}

}
