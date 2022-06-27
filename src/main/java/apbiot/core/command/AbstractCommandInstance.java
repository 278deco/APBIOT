package apbiot.core.command;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.command.informations.CommandGatewayComponentInformations;
import apbiot.core.command.informations.CommandGatewayNativeInformations;
import apbiot.core.command.informations.CommandGatewaySlashInformations;
import apbiot.core.helper.CommandHelper;
import apbiot.core.objects.interfaces.ICommandCategory;
import apbiot.core.permissions.Permissions;
import apbiot.core.time.CommandCooldown;
import discord4j.rest.util.Permission;

public abstract class AbstractCommandInstance {
	private List<String> commandNames = new ArrayList<>();
	private String description;
	private ICommandCategory category;
	private CommandPermission permissions;
	
	private final String commandId;
	
	protected boolean initialize;
	
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
		this.commandId = CommandHelper.generateRandomID();
		
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
		this.commandId = staticID;
		
		this.permissions = setPermissions();
	}
	
	/**
	 * Init values and variables contained in the command
	 * This method is only call one time at the start of the bot
	 * Useful when a command use a Embed
	 */
	public void initCommand() { }
	
	/**
	 * Define if a command has been successfully initialized
	 * @see #initCommand()
	 * @return if the command has been initialized
	 */
	public boolean isInitialized() { return initialize; }
	
	/**
	 * Execute the code contained in the command instance
	 * @param info - the informations given by the bot
	 */
	public abstract void execute(CommandGatewayNativeInformations infos);
	public abstract void execute(CommandGatewaySlashInformations infos);
	
	/**
	 * Execute the code if the command is handling discord components
	 * @param infos - the informations given by the bot
	 */
	public abstract void executeComponent(CommandGatewayComponentInformations infos);
	
	/**
	 * Define if a command should be or not in the help list
	 * @return if the command is in the help list
	 */
	public boolean isInHelpListed() {
		if(this.permissions != null) {
			return this.permissions.isDeveloperCommand ? false : true;
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
	 * Get the unique command id
	 * @return the command's id
	 */
	public String getID() {
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
	
	public class CommandPermission {
		private final List<Permissions> PERMISSIONS = new ArrayList<>();
		private boolean reversedPermissions;
		//If true, all users with this/these permission(s) WON'T be able to use the command
		//If false, all users with this/these permission(s) WILL be able to use the command
		
		private boolean isDeveloperCommand;
		
		public List<Permissions> getPermissionsList() {
			return PERMISSIONS;
		}
		
		public void setReversedPermissions(boolean value) {
			this.reversedPermissions = value;
		}
		
		public boolean isReversedPermissions() {
			return reversedPermissions;
		}
		
		public boolean isADeveloperCommand() {
			return this.isDeveloperCommand;
		}
		public void addPermission(Permissions... perms) {
			for(Permissions p : perms) {
				PERMISSIONS.add(p);
			}
		}
		
		public void addPermission(Permission... perms) {
			for(Permission p : perms) {
				PERMISSIONS.add(new Permissions(p));
			}
		}
		
		public void setDeveloperCommand(boolean value) {
			this.isDeveloperCommand = value;
		}
		
		public String getSpecifiedPermissionError() {
			return "";
		}
	}
}
