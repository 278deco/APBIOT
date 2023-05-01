package apbiot.core.builder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.MainInitializer;
import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.command.NativeCommandInstance;
import apbiot.core.command.SlashCommandInstance;
import apbiot.core.command.UserCommandCooldown;
import apbiot.core.command.informations.CommandGatewayComponentInformations;
import apbiot.core.command.informations.CommandGatewayNativeInformations;
import apbiot.core.command.informations.CommandGatewaySlashInformations;
import apbiot.core.commandator.Commandator;
import apbiot.core.event.events.discord.EventCommandError;
import apbiot.core.event.events.discord.EventCommandReceived;
import apbiot.core.event.events.discord.EventInstanceConnected;
import apbiot.core.exceptions.UnbuiltBotException;
import apbiot.core.handler.AbstractCommandHandler;
import apbiot.core.helper.ArgumentHelper;
import apbiot.core.helper.CommandHelper;
import apbiot.core.helper.CooldownHelper;
import apbiot.core.helper.PermissionHelper;
import apbiot.core.helper.StringHelper;
import apbiot.core.objects.Tuple;
import apbiot.core.objects.enums.CommandType;
import apbiot.core.objects.interfaces.IGatewayInformations;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.interaction.ApplicationCommandInteractionEvent;
import discord4j.core.event.domain.interaction.ComponentInteractionEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.object.presence.ClientPresence;
import discord4j.core.shard.GatewayBootstrap;
import discord4j.gateway.intent.IntentSet;

/**
 * Class that handle the bot instance.
 * Be careful to build the instance before register an EventHandler
 * @version 2.0
 * @author 278deco
 * @see discord4j.core.DiscordClient
 * @see discord4j.core.DiscordClientBuilder#DiscordClientBuilder(String)
 */
public class ClientBuilder {

	private static final Logger LOGGER = LogManager.getLogger(ClientBuilder.class);
	
	private static GatewayDiscordClient gateway;
	private Thread clientThread;
	
	private String botPrefix;
	private Snowflake ownerID;

	//Compilated Command Map and Slash Command Map
	private Map<List<String>, NativeCommandInstance> NATIVE_COMMANDS;
	private Map<List<String>, SlashCommandInstance> SLASH_COMMANDS;
	
	private List<UserCommandCooldown> commandCooldown;
	private Commandator commandator;
	
	/**
	 * Create a discord client with DiscordClientBuilder
	 * @return an instance of ClientBuilder
	 */
	public ClientBuilder createNewInstance() {
		this.commandCooldown = new ArrayList<>();
		
		this.NATIVE_COMMANDS = new HashMap<>();
		this.SLASH_COMMANDS = new HashMap<>();
		
		return this;
	}
	
	/**
	 * Initialize the commandMaps and make the bot ready to interact
	 * @param nativeCommandsMap - the native command map
	 * @param slashCommandsMap - the slash command map
	 * @see apbiot.core.handler.ECommandHandler
	 */
	public void updatedCommandReferences(AbstractCommandHandler cmdHandler) {
		this.NATIVE_COMMANDS = cmdHandler.NATIVE_COMMANDS;
		this.SLASH_COMMANDS = cmdHandler.SLASH_COMMANDS;
	}
	
	/**
	 * Get the registered native command map loaded by the client and available to be used
	 * @return an unmodifiable map representing the commands
	 */
	public Map<List<String>, NativeCommandInstance> getNativeCommandMap() {
		return Collections.unmodifiableMap(NATIVE_COMMANDS);
	}
	
	/**
	 * Get the registered slash command map loaded by the client and available to be used
	 * @return an unmodifiable map representing the commands
	 */
	public Map<List<String>, SlashCommandInstance> getSlashCommandMap() {
		return Collections.unmodifiableMap(SLASH_COMMANDS);
	}
	
	private void createComponentListener() {
		gateway.on(ComponentInteractionEvent.class).subscribe(event -> {
			String interactionId = event.getCustomId();
			
			AbstractCommandInstance cmd = searchCommandId(interactionId);
			if(cmd != null) {
				String discordCompoId = CommandHelper.getComponentID(interactionId);
				
				if(discordCompoId != null) {
					new Thread(new Runnable() {

						@Override
						public void run() {
							cmd.executeComponent(generateNewComponentInformations(event, discordCompoId));
						}
						
					}).start();
					
				}else {
					event.reply("⛔ An error has occured, please contact the administrator.").block();
				}
			}else {
				event.reply("⛔ An error has occured, please contact the administrator.").block();
			}
		});
	}
	
	/**
	 * Manage to get all the messages from servers and handle they
	 */
	private void createNativeCommandListener() {
		gateway.on(MessageCreateEvent.class).subscribe(event -> {
			String content = event.getMessage().getContent();
			
			if(!content.startsWith(botPrefix)) return;
			if((event.getMessage().getAuthor().get().getId().compareTo(gateway.getApplicationInfo().block().getId()) == 0) || content.equals("")) return;
			
			if(NATIVE_COMMANDS.isEmpty()) {
				event.getMessage().getChannel().block().createMessage("⛔ Le bot est encore en chargement... Veuillez réessayer ultérieurement.").block();
				return;
			}
			
			Tuple<String, Boolean> userCommand = getCommandFromUserInput(content.split(" "));
			
			AbstractCommandInstance cmd = searchCommandResult(userCommand.getValueA(), CommandType.NATIVE);
			
			MessageChannel channel = event.getMessage().getChannel().block();
			
			if(cmd != null) {
				MainInitializer.getEventDispatcher().dispatchEvent(new EventCommandReceived(StringHelper.getRawCharacterString(
								event.getMessage().getAuthor().get().getUsername()), 
								userCommand.getValueA(),
								channel.getType(), CommandType.NATIVE));
				
				if(CooldownHelper.canExecuteCommand(commandCooldown, event.getMessage().getAuthor().get(), channel)) {
					this.commandCooldown = CooldownHelper.wipeNullInstance(commandCooldown);
					
					if(!PermissionHelper.isServerEnvironnment(channel.getType())) {
						if(cmd.getPermissions() == null && !cmd.isServerOnly()) {
							handleNewCommandWithoutPermission(cmd, generateNewCommandInformations(event, userCommand), CommandType.NATIVE);
							
						}else {
							new TimedMessage(channel.createMessage("⛔ Vous ne pouvez pas éxécuter cette commande ici !").block())
							.setDelayedDelete(Duration.ofSeconds(5), true);
						}
					}else {
						handleNewCommand(cmd, generateNewCommandInformations(event, userCommand), CommandType.NATIVE);
					}
					
				}
				
				
			}else {
				handleUnknownCommand(userCommand.getValueA(), event.getMember().get(), channel);
			}

		});
	}
	
	/**
	 * Manage to get all the slash commands received from servers and handle they
	 */
	public void createSlashCommandListener() {
		gateway.on(ApplicationCommandInteractionEvent.class).subscribe(event -> {
			
			if(SLASH_COMMANDS.isEmpty()) {
				event.reply("⛔ Le bot est encore en chargement... Veuillez réessayer ultérieurement.").block();
				return;
			}
			
			AbstractCommandInstance cmd = searchCommandResult(event.getCommandName(), CommandType.SLASH);
			MessageChannel channel = event.getInteraction().getChannel().block();
			
			if(cmd != null) {
				MainInitializer.getEventDispatcher().dispatchEvent(new EventCommandReceived(StringHelper.getRawCharacterString(
						event.getInteraction().getUser().getUsername()), 
						cmd.getMainName(),
						channel.getType(), CommandType.SLASH));
				
				if(CooldownHelper.canExecuteCommand(commandCooldown, event.getInteraction().getUser(), channel)) {
					this.commandCooldown = CooldownHelper.wipeNullInstance(commandCooldown);
					
					if(!PermissionHelper.isServerEnvironnment(channel.getType())) {
						if(cmd.getPermissions() == null && !cmd.isServerOnly()) {
							handleNewCommandWithoutPermission(cmd, generateNewCommandInformations(event), CommandType.SLASH);
							
						}else {
							event.reply("⛔ Vous ne pouvez pas éxécuter cette commande ici !").withEphemeral(true).block();
						}
					}else {
						handleNewCommand(cmd, generateNewCommandInformations(event), CommandType.SLASH);
					}
				
				}
				
			}else {
				handleUnknownCommand(event.getCommandName(), event.getInteraction().getUser(), channel);
			}
			
		});
	}
	
	/**
	 * Search a command id if it exist
	 * @param providedCmdName - the name of the command
	 * @return the command instance if it exist
	 */
	private AbstractCommandInstance searchCommandId(String providedCmdId) {
		for(NativeCommandInstance command : NATIVE_COMMANDS.values()) {
			if(providedCmdId.startsWith(command.getID())) return command;
		}
		
		for(SlashCommandInstance command : SLASH_COMMANDS.values()) {
			if(providedCmdId.startsWith(command.getID())) return command;
		}
		
		return null;
	}
	
	/**
	 * Search if a command exist in the map
	 * @param providedCmdName - the name of the command
	 * @param onlySlashCommand - if the method has been called by the slash command event
	 * @return the command instance if it exist
	 */
	private AbstractCommandInstance searchCommandResult(String providedCmdName, CommandType type) {
		if(type == CommandType.NATIVE) {
			for(Map.Entry<List<String>, NativeCommandInstance> entry : NATIVE_COMMANDS.entrySet()) {
				for(String commandName : entry.getKey()) {
					
					if(commandName.equalsIgnoreCase(providedCmdName)) {
						return entry.getValue();
					}
				}
			}
		}else {
			for(Map.Entry<List<String>, SlashCommandInstance> entry : SLASH_COMMANDS.entrySet()) {
				if(entry.getKey().get(0).equalsIgnoreCase(providedCmdName)) {
					return entry.getValue();
				}
			}
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
	private CommandGatewayComponentInformations generateNewComponentInformations(ComponentInteractionEvent event, String componentId) {
		return new CommandGatewayComponentInformations(
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
	private CommandGatewaySlashInformations generateNewCommandInformations(ApplicationCommandInteractionEvent event) {
		return new CommandGatewaySlashInformations(
				event, 
				event.getInteraction().getUser(),
				event.getInteraction().getChannel().block());
	}
	
	/**
	 * Generate a new CommandGatewayInformations for a specified command
	 * @param event - the MessageCreateEvent from discord
	 * @param userCommand - a tuple containing informations about the layout of the user's command
	 * @see apbiot.core.command.CommandGatewayNativeInformations
	 * @return new CommandGatewayInformations
	 */
	private CommandGatewayNativeInformations generateNewCommandInformations(MessageCreateEvent event, Tuple<String, Boolean> userCommand) {
		return new CommandGatewayNativeInformations(
				event, 
				event.getMessage().getAuthor().get(), 
				event.getMessage().getChannel().block(), 
				ArgumentHelper.formatCommandArguments(userCommand.getValueB(),event.getMessage().getContent()), 
				userCommand.getValueA(), 
				botPrefix);
	}
	
	/**
	 * Get the command send by the user and the argument(s)
	 * @param userMessage - the message sent by the user
	 * @return a tuple containing the command and if the command was separate from the prefix
	 */
	private Tuple<String, Boolean> getCommandFromUserInput(String[] userMessage) {
		if(userMessage.length > 1 && userMessage[0].equals(botPrefix)) {
			return Tuple.of(userMessage[1], true);
		}else if(userMessage[0].contains(botPrefix) && userMessage.length >= 1) {
			return Tuple.of(userMessage[0].substring(1), false);
		}
		return Tuple.of("",false);
	}
	
	/**
	 * Handle every command computed by the bot and create a new thread for it to work
	 * work only for the command with permissions (guild command)
	 * @param cmd - the command itself
	 * @param info - the CommandGatewayInformations generated for the command
	 */
	private void handleNewCommand(AbstractCommandInstance cmd, IGatewayInformations info, CommandType type) {
		if(userHaveThePermission(cmd, info.getExecutor(),info.getGuild())) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					if(type == CommandType.SLASH) {
						cmd.execute((CommandGatewaySlashInformations)info);
					}else if(type == CommandType.NATIVE) {
						cmd.execute((CommandGatewayNativeInformations)info);
					}
				}
			}).start();
			
			if(!cmd.getCooldown().isWithoutCooldown()) {
				UserCommandCooldown cmdCooldown = CooldownHelper.createNewCooldown(cmd, info.getExecutor(), info.getGuild());
				if(cmdCooldown != null) commandCooldown.add(cmdCooldown);
			}
		}else {
			handlePermissionError(cmd, info.getEvent(), type);
		}
	}
	
	/**
	 * Handle every command computed by the bot et create a new thread for it to work
	 * work only for the command without permissions (dm & group dm command)
	 * @param cmd - the command itself
	 * @param info - the CommandGatewayInformations generated for the command
	 */
	private void handleNewCommandWithoutPermission(AbstractCommandInstance cmd, IGatewayInformations info, CommandType type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				if(type == CommandType.SLASH) {
					cmd.execute((CommandGatewaySlashInformations)info);
				}else if(type == CommandType.NATIVE) {
					cmd.execute((CommandGatewayNativeInformations)info);
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
	 * @param cmd - the command instance
	 * @param chan - the channel where the command has been executed
	 */
	private void handlePermissionError(AbstractCommandInstance cmd, Event event, CommandType type) {
		String msg = cmd.getPermissions().getSpecifiedPermissionError() != null ? 
				"⛔ ERREUR : "+cmd.getPermissions().getSpecifiedPermissionError() : 
				"⛔ ERREUR : Vous n'avez pas la permission d'éxecuter cette commande.";
		
		if(type == CommandType.SLASH) {
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
	 * @param commandName - the command written by the user
	 * @param user - the user
	 * @param channel - the channel which received the command
	 */
	private void handleUnknownCommand(String commandName, User user, MessageChannel channel) {
		String helpMessage = "";
		try {
			helpMessage = commandator.newRequest(commandName);
		} catch (InterruptedException e) {
			LOGGER.error("Unexpected error while catching a command",e);
		}
		
		MainInitializer.getEventDispatcher().dispatchEvent(new EventCommandError(StringHelper.getRawCharacterString(
				user.getUsername()), commandName, helpMessage, channel.getType()));
		
		if(helpMessage != "") {
			
			new TimedMessage(channel.createMessage(
					"ℹ️ "+user.getMention()+", Vous vouliez surement dire **"+botPrefix+""+helpMessage+"**.").block())
			.setDelayedDelete(Duration.ofSeconds(5), true);
		}else {
			new TimedMessage(channel.createMessage(
					"ℹ️ "+user.getMention()+", Aucune commande connue ne porte ce nom.").block())
			.setDelayedDelete(Duration.ofSeconds(5), true);
		}
	}
	
	/**
	 * Check if the a member can execute a command
	 * @param cmdPerm - the permission(s) required for the command
	 * @param m - the member who have execute the command
	 * @return if he have the permission
	 */
	private boolean userHaveThePermission(AbstractCommandInstance cmd, User m, Guild g) {
		return PermissionHelper.compareCommandPermissions(g.getMemberById(m.getId()).block(), cmd, ownerID);
	}
	
	/**
	 * Change the presence of the bot
	 * @param presence - the presence to apply
	 * @return an instance of ClientBuilder
	 */
	public ClientBuilder setGameText(ClientPresence status) {
		if (gateway == null) {
			LOGGER.error(new NullPointerException("CLIENT - Client instance isn't defined"));
			return this;
		}
		gateway.updatePresence(status).block();
		return this;
	}
	
	/**
	 * Used to build the instance of the bot and connect it to Discord
	 * @param token - the bot's token
	 * @param defaultStatus - the default status used by the bot
	 * @param intent -  the intent used and required by the bot
	 * @see discord4j.gateway.intent.IntentSet
	 * @see apbiot.core.builder.ClientBuilder#createCommandListener()
	 * @see discord4j.core.DiscordClient#login()
	 * @throws UnbuiltBotException
	 */
	public synchronized void build(String token, ClientPresence defaultStatus, IntentSet intent, String prefix) throws UnbuiltBotException {
		if(NATIVE_COMMANDS == null) throw new UnbuiltBotException("You cannot launch a bot without building it.");
		
		final DiscordClient client = DiscordClientBuilder.create(token).build();
		this.botPrefix = prefix;
		
		clientThread = new Thread(new Runnable() {

			@Override
			public void run() {
				
				GatewayDiscordClient g = GatewayBootstrap.create(client).setEnabledIntents(intent).setDisabledIntents(IntentSet.none()).login().block();
				gateway = g;
				setGameText(defaultStatus);
				
				createNativeCommandListener();
				createSlashCommandListener();
				
				createComponentListener();
				
				ownerID = gateway.getApplicationInfo().block().getOwnerId();
				MainInitializer.getEventDispatcher().dispatchEvent(new EventInstanceConnected(true, botPrefix));
				
				g.onDisconnect().block();
			}
			
		});
		clientThread.start();
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
	public synchronized void shutdownInstance() throws UnbuiltBotException {
		if(gateway == null) throw new UnbuiltBotException("You cannot destroy a nonexistent bot.");
		gateway.logout().block();
	}
	
	/**
	 * Edit the bot's prefix
	 * @param prefix - the prefix used by the bot
	 * @return an instance of ClientBuilder
	 */
	public ClientBuilder editBotPrefix(String prefix) {
		this.botPrefix = prefix;
		return this;
	}
	
	/**
	 * @deprecated
	 * @since 2.0
	 * @see apbiot.core.builder#createNewInstance
	 * @return the client bot instance
	 */
	@Deprecated
	public DiscordClient getRawClient() {
		return null;
	}
	
	/**
	 * Client instance of the bot created by Discord4J
	 * @return the client bot instance
	 */
	public GatewayDiscordClient getGateway() {
		return gateway;
	}
	
	/**
	 * User instance of the bot
	 * @return the client user instance
	 */
	public User getSelf() {
		return gateway.getSelf().block();
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
	 * Check if an instance of the bot is running
	 * @return if an instance is alive
	 */
	public boolean isInstancePresent() {
		return gateway != null;
	}
	
}
