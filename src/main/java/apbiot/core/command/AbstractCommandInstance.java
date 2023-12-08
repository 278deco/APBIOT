package apbiot.core.command;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import apbiot.core.command.informations.GatewayApplicationCommandPacket;
import apbiot.core.command.informations.GatewayComponentCommandPacket;
import apbiot.core.command.informations.GatewayNativeCommandPacket;
import apbiot.core.objects.interfaces.ICommandCategory;
import apbiot.core.permissions.CommandPermission;
import apbiot.core.time.CommandCooldown;

public abstract class AbstractCommandInstance {
	
	protected static final Logger LOGGER = LogManager.getLogger(AbstractCommandInstance.class);
	
	private String commandDisplayName;
	private HashSet<String> commandNames = new HashSet<>(); //Contains the aliases and the display name
	
	private String description;
	private ICommandCategory category;
	private CommandPermission permissions;
	
	private final UUID commandId;
	
	protected boolean built;
	
	/**
	 * Create a new CommandInstance
	 * @param displayName The main name of the command. Is displayed to represent the command to the user
	 * @param aliases A {@link Set} of aliases for the command.
	 * @param description The command's description
	 * @param category The category of the command
	 */
	public AbstractCommandInstance(String displayName, Set<String> aliases, String description, ICommandCategory category) {
		this.commandDisplayName = displayName;
		this.commandNames.add(displayName);
		if(aliases != null) this.commandNames.addAll(aliases);
		
		this.description = (description == null || description.isBlank() || description.isEmpty() ? "No description available" : description);
		this.category = category;
		this.commandId = UUID.randomUUID();
		
		this.permissions = setPermissions();
	}
	
	/**
	 * Create a new CommandInstance
	 * @param displayName The main name of the command. Is displayed to represent the command to the user
	 * @param aliases A {@link Set} of aliases for the command.
	 * @param description The command's description
	 * @param category The category of the command
	 * @param staticID Define an UUID for the command. With this defined, the id used will remains the same for all instances
	 */
	public AbstractCommandInstance(String displayName, Set<String> aliases, String description, ICommandCategory category, String staticID) {
		this.commandDisplayName = displayName;
		this.commandNames.add(displayName);
		if(aliases != null) this.commandNames.addAll(aliases);
		
		this.description = (description == null || description.isBlank() || description.isEmpty() ? "No description available" : description);
		this.category = category;
		this.commandId = UUID.fromString(staticID);
		
		this.permissions = setPermissions();
	}
	
	/**
	 * Init values and variables contained in the command
	 * This method is only call one time at the start of the bot
	 * Useful when a command use a Embed
	 */
	public void buildCommand() { }
	
	/**
	 * Define if a command has been successfully initialized
	 * @see #initCommand()
	 * @return if the command has been initialized
	 */
	public boolean isBuilt() { return built; }
	
	/**
	 * Execute the code contained in the command instance
	 * @param info The informations given by the bot
	 */
	public abstract void execute(GatewayNativeCommandPacket infos);
	public abstract void execute(GatewayApplicationCommandPacket infos);
	
	/**
	 * Execute the code if the command is handling discord components
	 * @param infos The informations given by the bot
	 */
	public abstract void executeComponent(GatewayComponentCommandPacket infos);
	
	/**
	 * Define if a command should be or not in the help list
	 * @return if the command is in the help list
	 */
	public boolean isInHelpListed() {
		if(this.permissions != null) {
			return this.permissions.isDeveloperCommand() ? false : true;
		}else {
			return true;
		}
	}
	
	/**
	 * Define if the command can be call only on a server or in every channel (DM, GROUPS and GUILD)
	 * If the command work with permission, the command needs to be server only
	 * @return if the command is only callable on servers
	 */
	public abstract boolean isServerOnly();
	
	/**
	 * Get the command's description
	 * @return the command's description
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Get the display name of the command and its aliases. The set can only contains the display name if no aliases are found.
	 * @return A set containing the command's display name and aliases
	 */
	public Set<String> getNames() {
		return Collections.unmodifiableSet(this.commandNames);
	}

	/**
	 * Get the main name of the command
	 * @return the main name
	 * @deprecated since 5.0
	 * @see #getDisplayName()
	 */
	public String getMainName() {
		return "null";
	}
	
	/**
	 * Get the command's display name
	 * @return the main name
	 */
	public String getDisplayName() {
		return this.commandDisplayName;
	}
	
	/**
	 * Get the category of a command
	 * @return the command's category
	 */
	public ICommandCategory getCommandCategory() {
		return this.category;
	}
	
	/**
	 * Check if the given category correspond to the category of the command
	 * @param cat The given category
	 * @return if the two categories matches
	 */
	public boolean isSameCommandCategory(ICommandCategory cat) {
		return this.category.equals(cat);
	}
	
	/**
	 * Get the unique command id
	 * @return the command's id
	 */
	public UUID getID() {
		return this.commandId;
	}
	
	/**
	 * Get the permission of the command
	 * @see apbiot.core.command.CommandInstance.CommandPermission
	 * @return command's permission
	 */
	public CommandPermission getPermissions() {
		return permissions;
	}
	
	/**
	 * Set the permission for the command
	 * @see apbiot.core.command.CommandInstance.CommandPermission
	 * @return an instance of CommandPermission
	 */
	protected abstract CommandPermission setPermissions();
	
	/**
	 * Get the cooldown defined for the command
	 * @return the cooldown
	 */
	public CommandCooldown getCooldown() {
		return new CommandCooldown().setWithoutCooldown();
	}
	
}
