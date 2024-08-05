package apbiot.core.builder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.command.ApplicationCommandInstance;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.command.UserCommandCooldown;
import apbiot.core.command.informations.GatewayApplicationCommandPacket;
import apbiot.core.command.informations.GatewayComponentCommandPacket;
import apbiot.core.command.informations.GatewayNativeCommandPacket;
import apbiot.core.commandator.Commandator;
import apbiot.core.commandator.CommandatorEntry;
import apbiot.core.exceptions.UnbuiltBotException;
import apbiot.core.handler.AbstractCommandHandler;
import apbiot.core.helper.ArgumentHelper;
import apbiot.core.helper.CommandHelper;
import apbiot.core.helper.CooldownHelper;
import apbiot.core.helper.PermissionHelper;
import apbiot.core.helper.StringHelper;
import apbiot.core.objects.Tuple;
import apbiot.core.objects.enums.ApplicationCommandType;
import apbiot.core.objects.interfaces.IGatewayInformations;
import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEventManager;
import apbiot.core.pems.actions.CommandRebuildAction.CommandRebuildScope;
import apbiot.core.utils.Emojis;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.interaction.MessageInteractionEvent;
import discord4j.core.event.domain.interaction.UserInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.gateway.intent.IntentSet;
import discord4j.rest.http.client.ClientException;

/**
 * Class that handle the bot instance.
 * Be careful to build the instance before register an EventHandler
 * @version 5.0
 * @author 278deco
 * @see discord4j.core.DiscordClient
 * @see discord4j.core.DiscordClientBuilder#DiscordClientBuilder(String)
 */
public class ClientBuilder {

	private static final Logger LOGGER = LogManager.getLogger(ClientBuilder.class);
	
	private GatewayDiscordClient gateway;
	private ReentrantLock lock = new ReentrantLock();
	private boolean isReady = false;
	
	private String botPrefix;
	private Snowflake ownerID;

	//Compilated Command Map and Slash Command Map
	private Map<Set<String>, NativeCommandInstance> NATIVE_COMMANDS;
	private Map<Set<String>, SlashCommandInstance> SLASH_COMMANDS;
	//Compilated User and Message Commands
	private Map<String, ApplicationCommandInstance> APPLICATION_COMMANDS;
	
	private List<UserCommandCooldown> commandCooldown;
	private Commandator commandator;
	
	/**
	 * Create a discord client with DiscordClientBuilder
	 */
	public void createNewInstance() {
		try {
			this.lock.lock();
			this.commandCooldown = new ArrayList<>();
			
			this.NATIVE_COMMANDS = new HashMap<>();
			this.SLASH_COMMANDS = new HashMap<>();
			this.APPLICATION_COMMANDS = new HashMap<>();
		}finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * Initialize the commandMaps and make the bot ready to interact
	 * @param nativeCommandsMap The native command map
	 * @param slashCommandsMap The slash command map
	 * @see apbiot.core.handler.ECommandHandler
	 * @deprecated since 5.0
	 */
	public void updatedCommandReferences(AbstractCommandHandler cmdHandler) {
//		this.NATIVE_COMMANDS = cmdHandler.NATIVE_COMMANDS;
//		this.SLASH_COMMANDS = cmdHandler.SLASH_COMMANDS;
//		this.APPLICATION_COMMANDS = cmdHandler.APPLICATION_COMMANDS;
	}
	
	public void updateNativeCommandMapping(Optional<Map<Set<String>, NativeCommandInstance>> mapping) {
		if(mapping.isPresent()) this.NATIVE_COMMANDS = mapping.get();
	}
	
	public void updateSlashCommandMapping(Optional<Map<Set<String>, SlashCommandInstance>> mapping) {
		if(mapping.isPresent()) this.SLASH_COMMANDS = mapping.get();
	}
	
	public void updateApplicationCommandMapping(Optional<Map<String, ApplicationCommandInstance>> mapping) {
		if(mapping.isPresent()) this.APPLICATION_COMMANDS = mapping.get();
	}
	
	public void rebuildCommandMapping(CommandRebuildScope scope) {
		final List<String> errors = new ArrayList<String>();
		
		if(scope == CommandRebuildScope.ONLY_NATIVE || scope == CommandRebuildScope.BOTH_NATIVE_SLASH || scope == CommandRebuildScope.ALL) {
			this.NATIVE_COMMANDS.values().forEach(cmd -> {
				try {
					cmd.buildCommand();
				}catch(Exception e) {
					errors.add(cmd.getDisplayName());
				}
			});
		}
		
		if(scope == CommandRebuildScope.ONLY_SLASH || scope == CommandRebuildScope.BOTH_NATIVE_SLASH || 
				scope == CommandRebuildScope.BOTH_SLASH_APPLICATION || scope == CommandRebuildScope.ALL) {
			this.SLASH_COMMANDS.values().forEach(cmd -> {
				try {
					cmd.buildCommand();
				}catch(Exception e) {
					errors.add(cmd.getDisplayName());
				}
			});
		}
		
		if(scope == CommandRebuildScope.ONLY_APPLICATION || scope == CommandRebuildScope.BOTH_SLASH_APPLICATION || scope == CommandRebuildScope.ALL) {
			this.NATIVE_COMMANDS.values().forEach(cmd -> {
				try {
					cmd.buildCommand();
				}catch(Exception e) {
					errors.add(cmd.getDisplayName());
				}
			});
		}

		ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.COMMAND_LIST_BUILD, new Object[] {errors.toArray(), scope});
	}
	
	private void createComponentListener() {
		gateway.on(ComponentInteractionEvent.class).subscribe(event -> {
			final AbstractCommandInstance cmd = searchCommandId(event.getCustomId());
			if(cmd == null) {
				event.reply(Emojis.EXCLAMATION+" An error has occured, please contact the administrator.").block();
				return;
			}
				
			final String discordCompoId = CommandHelper.getComponentID(event.getCustomId());
			if(discordCompoId == null) {
				event.reply(Emojis.EXCLAMATION+" An error has occured, please contact the administrator.").block();
				return;
			}
				
			new Thread(new Runnable() {

				@Override
				public void run() {
					cmd.executeComponent(generateNewComponentInformations(event, discordCompoId));
				}
				
			}).start();
		});
	}
	
	/**
	 * Manage to get all the messages from servers and handle they
	 */
	private void createNativeCommandListener() {
		gateway.on(MessageCreateEvent.class).subscribe(event -> {
			final String content = event.getMessage().getContent();
			
			if(content.isBlank() || content.isEmpty() || !content.startsWith(botPrefix)) return;
			if((event.getMessage().getAuthor().get().getId().compareTo(gateway.getApplicationInfo().block().getId()) == 0)) return;
			
			try {
				this.lock.lock();
				
				if(NATIVE_COMMANDS.isEmpty() || !isReady) {
					event.getMessage().getChannel().block().createMessage(Emojis.TOOLS+" Le bot est encore en chargement... Veuillez réessayer ultérieurement.").block();
					return;
				}
				
				final MessageChannel channel = event.getMessage().getChannel().block();		
				final Tuple<String, Boolean> userCommand = CommandHelper.getCommandFromUserInput(content.split(" "), this.botPrefix);
				final AbstractCommandInstance cmd = searchCommandResult(userCommand.getValueA(), ApplicationCommandType.NATIVE);
	
				if(userCommand.isTupleEmpty() || cmd == null) {
					handleUnknownCommand(userCommand.getValueA(), event.getMember().get(), channel);
					return;
				}
	
				ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.COMMAND_RECEIVED, new Object[] {
						StringHelper.getRawCharacterString(event.getMessage().getAuthor().get().getUsername()), 
						userCommand.getValueA(),
						channel.getType(), 
						ApplicationCommandType.NATIVE});
				
				if(CooldownHelper.canExecuteCommand(commandCooldown, event.getMessage().getAuthor().get(), channel)) {
					this.commandCooldown = CooldownHelper.wipeNullInstance(commandCooldown);
					
					if(!PermissionHelper.isServerEnvironnment(channel.getType())) {
						if(cmd.getPermissions() == null && !cmd.isServerOnly()) {
							handleNewCommandWithoutPermission(cmd, generateNewNativeCommandPacket(event, userCommand), ApplicationCommandType.NATIVE);
							
						}else {
							new TimedMessage(channel.createMessage(Emojis.NO_ENTRY+" Vous ne pouvez pas éxécuter cette commande ici !").block())
							.setDelayedDelete(Duration.ofSeconds(5), true);
						}
					}else {
						handleNewCommand(cmd, generateNewNativeCommandPacket(event, userCommand), ApplicationCommandType.NATIVE);
					}	
				}
			}finally {
				this.lock.unlock();
			}
		});
	}
	
	/**
	 * Manage to get all the SLASH, USER and MESSAGE commands received from servers and handle them
	 * The two types of command are handled separately
	 */
	private void createApplicationCommandListener() {
		gateway.on(ChatInputInteractionEvent.class).subscribe(event -> {
			setupApplicationCommandListener(event, ApplicationCommandType.CHAT_INPUT);
		});
	
		gateway.on(UserInteractionEvent.class).subscribe(event -> {
			setupApplicationCommandListener(event, ApplicationCommandType.USER);
		});
		
		gateway.on(MessageInteractionEvent.class).subscribe(event -> {
			setupApplicationCommandListener(event, ApplicationCommandType.MESSAGE);
		});
		
	}
	
	private void setupApplicationCommandListener(ApplicationCommandInteractionEvent event, ApplicationCommandType type) {
		if((APPLICATION_COMMANDS.isEmpty() && type != ApplicationCommandType.CHAT_INPUT) ||
				(SLASH_COMMANDS.isEmpty() && type == ApplicationCommandType.CHAT_INPUT) ||
				!isReady) {
			event.reply(Emojis.TOOLS+" Le bot est encore en chargement... Veuillez réessayer ultérieurement.").block();
			return;
		}
		
		final AbstractCommandInstance cmd = searchCommandResult(event.getCommandName(), type);
		final MessageChannel channel = event.getInteraction().getChannel().block();
		
		if(cmd == null) {
			handleUnknownCommand(event.getCommandName(), event.getInteraction().getUser(), channel);
			return;
		}
		
		ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.COMMAND_RECEIVED, new Object[] {
				StringHelper.getRawCharacterString(event.getInteraction().getUser().getUsername()), 
				cmd.getDisplayName(),
				channel.getType(), 
				type});
		
		if(CooldownHelper.canExecuteCommand(commandCooldown, event.getInteraction().getUser(), channel)) {
			this.commandCooldown = CooldownHelper.wipeNullInstance(commandCooldown);
			
			if(!PermissionHelper.isServerEnvironnment(channel.getType())) {
				if(cmd.getPermissions() == null && !cmd.isServerOnly()) {
					handleNewCommandWithoutPermission(cmd, generateNewApplicationCommandPacket(event, type), type);
					
				}else {
					event.reply(Emojis.NO_ENTRY+" Vous ne pouvez pas éxécuter cette commande ici !").withEphemeral(true).block();
				}
			}else {
				handleNewCommand(cmd, generateNewApplicationCommandPacket(event, type), type);
			}
		}

	}
	
	/**
	 * Search a command id if it exist
	 * @param providedCmdName The name of the command
	 * @return the command instance if it exist
	 */
	private AbstractCommandInstance searchCommandId(String providedCmdId) {
		for(NativeCommandInstance command : NATIVE_COMMANDS.values()) {
			if(providedCmdId.startsWith(command.getShortenID())) return command;
		}
		
		for(SlashCommandInstance command : SLASH_COMMANDS.values()) {
			if(providedCmdId.startsWith(command.getShortenID())) return command;
		}
		
		return null;
	}
	
	/**
	 * Search if a command exist in the map
	 * @param providedCmdName - the name of the command
	 * @param onlySlashCommand - if the method has been called by the slash command event
	 * @return the command instance if it exist
	 */
	private AbstractCommandInstance searchCommandResult(String providedCmdName, ApplicationCommandType type) {
		if(providedCmdName == null) return null;
		
		switch(type) {
			case NATIVE -> {
				for(var entry : NATIVE_COMMANDS.entrySet()) {
					if(entry.getKey().contains(providedCmdName.toLowerCase())) {
						return entry.getValue();
					}
				}
			}
			case CHAT_INPUT -> {
				for(var entry : SLASH_COMMANDS.entrySet()) {
					if(entry.getValue().getDisplayName().equalsIgnoreCase(providedCmdName)) {
						return entry.getValue();
					}
				}
			}
			case MESSAGE, USER -> {
				for(var entry : APPLICATION_COMMANDS.entrySet()) {
					if(entry.getKey().equalsIgnoreCase(providedCmdName)) {
						return entry.getValue();
					}
				}
			}
			default -> throw new IllegalArgumentException("Unexpected value: " + type);
		}
		
		return null;
	}
	
	/**
	 * Generate a new CommandGatewayComponentInteraction for a specified command
	 * @param event - the MessageCreateEvent from discord
	 * @param userCommand - a tuple containing informations about the layout of the user's command
	 * @see apbiot.core.command.informations.informations.CommandGatewayComponentInteraction
	 * @return new CommandGatewayComponentInteraction
	 */
	private GatewayComponentCommandPacket generateNewComponentInformations(ComponentInteractionEvent event, String componentId) {
		return new GatewayComponentCommandPacket(
				event, 
				componentId,
				event.getInteraction().getUser(),
				event.getInteraction().getChannel().block());
	}
	
	/**
	 * Generate a new CommandGatewaySlashInformations for a specified command
	 * @param event - the ApplicationCommandInteractionEvent from discord
	 * @see apbiot.core.command.informations.informations.CommandGatewaySlashInformations
	 * @return new CommandGatewaySlashInformations
	 */
	private GatewayApplicationCommandPacket generateNewApplicationCommandPacket(ApplicationCommandInteractionEvent event, ApplicationCommandType type) {
		return new GatewayApplicationCommandPacket(
				event, 
				type,
				event.getInteraction().getUser(),
				event.getInteraction().getChannel().block());
	}
	
	/**
	 * Generate a new CommandGatewayInformations for a specified command
	 * @param event - the MessageCreateEvent from discord
	 * @param userCommand - a tuple containing informations about the layout of the user's command
	 * @see apbiot.core.command.GatewayNativeCommandPacket
	 * @return new CommandGatewayInformations
	 */
	private GatewayNativeCommandPacket generateNewNativeCommandPacket(MessageCreateEvent event, Tuple<String, Boolean> userCommand) {
		return new GatewayNativeCommandPacket(
				event, 
				event.getMessage().getAuthor().get(), 
				event.getMessage().getChannel().block(), 
				ArgumentHelper.formatCommandArguments(userCommand.getValueB(),event.getMessage().getContent()), 
				userCommand.getValueA(), 
				botPrefix);
	}
	
	/**
	 * Handle every command computed by the bot and create a new thread for it to work
	 * work only for the command with permissions (guild command)
	 * @param cmd - the command itself
	 * @param info - the CommandGatewayInformations generated for the command
	 */
	private void handleNewCommand(AbstractCommandInstance cmd, IGatewayInformations info, ApplicationCommandType cmdType) throws NullPointerException {
		if(PermissionHelper.doesUserHavePermissions(info.getGuild().getMemberById(info.getExecutor().getId()).onErrorComplete().block(), cmd.getPermissions(), ownerID)) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					switch(cmdType) {
						case CHAT_INPUT, USER, MESSAGE -> {
							cmd.execute((GatewayApplicationCommandPacket)info);
						}
						case NATIVE -> {
							cmd.execute((GatewayNativeCommandPacket)info);
						}
						default -> throw new IllegalArgumentException("Unexpected value: " + cmdType);
					}
				}
			}).start();
			
			if(!cmd.getCooldown().isWithoutCooldown()) {
				final UserCommandCooldown cmdCooldown = CooldownHelper.createNewCooldown(cmd, info.getExecutor(), info.getGuild());
				if(cmdCooldown != null) commandCooldown.add(cmdCooldown);
			}
		}else {
			handlePermissionError(cmd, info.getEvent(), cmdType);
		}
	}
	
	/**
	 * Handle every command computed by the bot et create a new thread for it to work
	 * work only for the command without permissions (dm & group dm command)
	 * @param cmd - the command itself
	 * @param info - the CommandGatewayInformations generated for the command
	 */
	private void handleNewCommandWithoutPermission(AbstractCommandInstance cmd, IGatewayInformations info, ApplicationCommandType cmdType) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				switch(cmdType) {
					case CHAT_INPUT, USER, MESSAGE -> {
						cmd.execute((GatewayApplicationCommandPacket)info);
					}
					case NATIVE -> {
						cmd.execute((GatewayNativeCommandPacket)info);
					}
					default -> throw new IllegalArgumentException("Unexpected value: " + cmdType);
				}
			}
		}).start();
		
		if(!cmd.getCooldown().isWithoutCooldown()) {
			UserCommandCooldown cmdCooldown = CooldownHelper.createNewCooldown(cmd, info.getExecutor(), info.getGuild());
			if(cmdCooldown != null) commandCooldown.add(cmdCooldown);
		}
	}
	
	/**
	 * Handle and display permission error when thrown
	 * @param cmd The command instance
	 * @param chan The channel where the command has been executed
	 */
	private void handlePermissionError(AbstractCommandInstance cmd, Event event, ApplicationCommandType type) {
		final String msg = cmd.getPermissions().getPermissionErrorMessage().isPresent() ? 
				Emojis.X_CROSS+" **ERREUR :** "+cmd.getPermissions().getPermissionErrorMessage().get() : 
				Emojis.X_CROSS+" **ERREUR :** Vous n'avez pas la permission d'éxecuter cette commande.";
		
		if(type == ApplicationCommandType.CHAT_INPUT) {
			((ApplicationCommandInteractionEvent)event).reply(msg).withEphemeral(true).block();
		}
		else {	
			new TimedMessage(((MessageCreateEvent)event).getMessage().getChannel().block()
					.createMessage(msg).block())
			.setDelayedDelete(Duration.ofSeconds(7), true);
		}
	}
	
	/**
	 * Handle and display commandator help when an unknown command is fired
	 * @param commandName The command written by the user
	 * @param user The user
	 * @param channel The channel which received the command
	 */
	private void handleUnknownCommand(String commandName, User user, MessageChannel channel) {
		Optional<CommandatorEntry> helpMessage = Optional.empty();
		try {
			helpMessage = commandator.newRequest(commandName);
		} catch (InterruptedException e) {
			LOGGER.error("Unexpected error while catching a command",e);
			return;
		}
		
		ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.COMMMAND_ERROR, new Object[] {
				StringHelper.getRawCharacterString(user.getUsername()), 
				commandName, 
				helpMessage.orElse(null), 
				channel.getType()});
				
		if(helpMessage.isPresent()) {
			final String cmdPrefix = helpMessage.get().getCommandType() == ApplicationCommandType.NATIVE ? botPrefix : "/";
			
			new TimedMessage(channel.createMessage(
					"ℹ️ "+user.getMention()+", Vous vouliez surement dire **"+cmdPrefix+""+helpMessage.get().getCommandName()+"**.").block())
			.setDelayedDelete(Duration.ofSeconds(5), true);
		}else {
			new TimedMessage(channel.createMessage(
					"ℹ️ "+user.getMention()+", Aucune commande connue ne porte ce nom.").block())
			.setDelayedDelete(Duration.ofSeconds(5), true);
		}
	}
	
	
	/**
	 * Change the presence of the bot
	 * @param presence The presence to apply
	 * @return an instance of ClientBuilder
	 */
	public ClientBuilder setPresenceText(ClientPresence status) {
		if (gateway == null) {
			LOGGER.error(new NullPointerException("CLIENT - Client instance isn't defined"));
			return this;
		}
		gateway.updatePresence(status).block();
		return this;
	}
	
	/**
	 * Edit the bot's prefix
	 * @param prefix The prefix used by the bot
	 * @return an instance of ClientBuilder
	 * @deprecated since 5.0
     */
	@Deprecated
	public ClientBuilder editBotPrefix(String prefix) {
		this.botPrefix = prefix;
		return this;
	}
	
	/**
	 * Set a new prefix used by the bot
	 * @param prefix The new prefix which will be used by the bot
	 * @return an instance of ClientBuilder
	 */
	public ClientBuilder setBotPrefix(String prefix) {
		this.botPrefix = prefix;
		return this;
	}
	
	public void setReady(boolean value) {
		try {
			this.lock.lock();
			
			this.isReady = value;
		}finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * Used to build the instance of the bot and connect it to Discord
	 * @param token The bot's token
	 * @param defaultStatus The default status used by the bot
	 * @param intent The intent used and required by the bot
	 * @see discord4j.gateway.intent.IntentSet
	 * @see DiscordClient#login()
	 * @throws UnbuiltBotException
	 */
	public void launch(String token, IntentSet intent, String prefix, Optional<ClientPresence> defaultStatus) throws UnbuiltBotException, ClientException {
		try {
			this.lock.lock();
			if(NATIVE_COMMANDS == null || SLASH_COMMANDS == null || APPLICATION_COMMANDS == null ) throw new UnbuiltBotException("You cannot launch a bot without building it.");
			
			this.botPrefix = prefix;
			
			final DiscordClient client = DiscordClient.create(token);
			
			this.gateway = GatewayBootstrap.create(client)
				.setEnabledIntents(intent)
				.setDisabledIntents(IntentSet.none())
				.login().block();
			
			if(defaultStatus.isPresent()) setPresenceText(defaultStatus.get());
			this.ownerID = this.gateway.getApplicationInfo().block().getOwnerId();
			
			createNativeCommandListener();
			createApplicationCommandListener();
			
			createComponentListener();
					
			ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.CLIENT_INSTANCE_CONNECTED, new Object[] {gateway});	
		}finally {
			this.lock.unlock();
		}
		
		this.gateway.onDisconnect().block();
	}
	
	/**
	 * Used to create the instance of Commandator
	 * @see apbiot.core.commandator.Commandator
	 */
	public void buildCommandator() {
		
		commandator = new Commandator(NATIVE_COMMANDS.keySet(), SLASH_COMMANDS.keySet());
	}
	
	/**
	 * Used to disconnect the bot from discord
	 * @throws UnbuiltBotException
	 */
	public void shutdownInstance() throws UnbuiltBotException {
		try {
			this.lock.lock();
			
			if(this.gateway == null) throw new UnbuiltBotException("You cannot destroy a nonexistent bot.");
			this.gateway.logout().block();
		}finally {
			this.lock.unlock();
		}
	}
	
	/**
	 * Client instance of the bot created by Discord4J
	 * @return the client bot instance
	 */
	public GatewayDiscordClient getGateway() {
		return this.gateway;
	}
	
	/**
	 * This is bot's prefix when it is set at the starting
	 * @return the prefix of the bot
	 */
	public String getBotPrefix() {
		if (botPrefix == null) {
			LOGGER.error(new NullPointerException("CLIENT - Bot's prefix isn't defined"));
			return "";
		}
		return botPrefix;
	}
	
	/**
	 * This is Commandator, an home made program to help users with bot's command
	 * @return a Commandator instance
	 */
	public Commandator getCommandator() {
		if (commandator == null) {
			LOGGER.error(new NullPointerException("CLIENT - Commandator program has not been launched"));
			return null;
		}
		return commandator;
	}
	
	/**
	 * Tells if the program is using {@link Commandator} subprogram
	 * @return if the program is using Commandator
	 */
	public boolean isUsingCommandator() {
		return commandator != null;
	}
	
	/**
	 * Check if an instance of the bot is running
	 * @return if an instance is alive
	 */
	public boolean isInstancePresent() {
		return gateway != null;
	}
	
}
