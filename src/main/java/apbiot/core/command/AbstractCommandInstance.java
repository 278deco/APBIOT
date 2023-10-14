package apbiot.core.command;

import java.util.ArrayList;
import java.util.List;
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
	
	private List<String> commandNames = new ArrayList<>();
	private String description;
	private ICommandCategory category;
	private CommandPermission permissions;
	
	private final UUID commandId;
	
	protected boolean built;
	
	/**
	 * Create a new CommandInstance
	 * @param cmdName - the command's name and it alias
	 * @param description - the command's description
	 * @param category - the category of the command
	 */
	public AbstractCommandInstance(List<String> cmdName, String description, ICommandCategory category) {
		this.commandNames = cmdName;
		this.description = (description == null || description == "" ? "No description available" : description);
		this.category = category;
		this.commandId = UUID.randomUUID();
		
		this.permissions = setPermissions();
		
	}
	
	/**
	 * Create a new CommandInstance
	 * @param cmdName - the command's name and it alias
	 * @param description - the command's description
	 * @param category - the category of the command
	 * @param staticID - define an ID for the command. With this defined, the id used will remains the same for all instances
	 */
	public AbstractCommandInstance(List<String> cmdName, String description, ICommandCategory category, String staticID) {
		this.commandNames = cmdName;
		this.description = (description == null || description == "" ? "No description available" : description);
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
	 * @param info - the informations given by the bot
	 */
	public abstract void execute(GatewayNativeCommandPacket infos);
	public abstract void execute(GatewayApplicationCommandPacket infos);
	
	/**
	 * Execute the code if the command is handling discord components
	 * @param infos - the informations given by the bot
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
	 * Get the main name of the command and it alias
	 * @return the command's name
	 */
	public List<String> getNames() {
		return this.commandNames;
	}
	
	/**
	 * Get the main name of the command
	 * @return the main name
	 */
	public String getMainName() {
		return commandNames.size() > 0 ? commandNames.get(0) : "null";
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
