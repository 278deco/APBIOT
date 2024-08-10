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
import apbiot.core.helper.StringHelper;
import apbiot.core.i18n.LanguageManager;
import apbiot.core.objects.interfaces.ICommandCategory;
import apbiot.core.permissions.CommandPermission;
import apbiot.core.time.CommandCooldown;

public abstract class AbstractCommandInstance {
	
	protected static final Logger LOGGER = LogManager.getLogger(AbstractCommandInstance.class);
	
	private String commandInternalName;
	private HashSet<String> commandNames = new HashSet<>(); //Contains the aliases and the display name
	
	private String description;
	private ICommandCategory category;
	private CommandPermission permissions;
	
	private final UUID commandId;
	private final String shortenCommandId; //The shorten version of the command id pre-processed for faster access. Use base64
	
	protected boolean built;
	
	/**
	 * Create a new CommandInstance
	 * @param displayName The main name of the command. Is displayed to represent the command to the user
	 * @param aliases A {@link Set} of aliases for the command.
	 * @param description The command's description
	 * @param category The category of the command
	 * @deprecated since 6.0.0
	 * @see #AbstractCommandInstance(String, String, ICommandCategory)
	 */
	public AbstractCommandInstance(String displayName, Set<String> aliases, String description, ICommandCategory category) {
		this.commandInternalName = displayName;
		this.commandNames.add(displayName);
		if(aliases != null) this.commandNames.addAll(aliases);
		
		this.description = (description == null || description.isBlank() || description.isEmpty() ? "No description available" : description);
		this.category = category;
		this.commandId = UUID.randomUUID();
		this.shortenCommandId = StringHelper.shortenUUIDToBase64(this.commandId);
		
		this.permissions = setPermissions();
	}
	
	/**
	 * Create a new CommandInstance
	 * @param displayName The main name of the command. Is displayed to represent the command to the user
	 * @param aliases A {@link Set} of aliases for the command.
	 * @param description The command's description
	 * @param category The category of the command
	 * @param staticID Define an UUID for the command. With this defined, the id used will remains the same for all instances
	 * @deprecated since 6.0.0
	 * @see #AbstractCommandInstance(String, String, ICommandCategory, String)
	 */
	public AbstractCommandInstance(String displayName, Set<String> aliases, String description, ICommandCategory category, String staticID) {
		this.commandInternalName = displayName;
		this.commandNames.add(displayName);
		if(aliases != null) this.commandNames.addAll(aliases);
		
		this.description = (description == null || description.isBlank() || description.isEmpty() ? "No description available" : description);
		this.category = category;
		this.commandId = UUID.fromString(staticID);
		this.shortenCommandId = StringHelper.shortenUUIDToBase64(this.commandId);
		
		this.permissions = setPermissions();
	}
	
	/**
	 * Create a new CommandInstance
	 * 
	 * @param internalName The main name of the command.
	 * 		  It is used to get the command's values in the localization like name, description
	 * @param aliases A {@link Set} of aliases for the command.
	 * @param category The category of the command
	 * @since 6.0.0
	 */
	public AbstractCommandInstance(String internalName, ICommandCategory category) {
		this.commandInternalName = internalName;

		this.category = category;
		this.commandId = UUID.randomUUID();
		this.shortenCommandId = StringHelper.shortenUUIDToBase64(this.commandId);
		
		this.permissions = setPermissions();
	}
	
	/**
	 * Create a new CommandInstance
	 * 
	 * @param internalName The main name of the command. 
	 * 		  It is used to get the command's values in the localization like name, description
	 * @param aliases A {@link Set} of aliases for the command.
	 * @param category The category of the command
	 * @param staticID Define an UUID for the command. With this defined, the id used will remains the same for all instances
	 * @since 6.0.0
	 */
	public AbstractCommandInstance(String internalName, ICommandCategory category, String staticID) {
		this.commandInternalName = internalName;
		
		this.category = category;
		this.commandId = UUID.fromString(staticID);
		this.shortenCommandId = StringHelper.shortenUUIDToBase64(this.commandId);
		
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
	 * @deprecated since 6.0.0
	 * @see #getDescription(String)
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Get the command's description in the given language <br/>
	 * If the language is not found, the default description is returned
	 * 
	 * @param languageCode The language code to get the description
	 * @return the command's description
	 */
	public final String getDescription(String languageCode) {
		final String key = "commands.discord."+getInternalName()+".description";
		return LanguageManager.get().getLanguage(languageCode).map(lang -> lang.getOrDefault(key)).orElse(key);
	}
	
	/**
	 * Get the display name of the command and its aliases. The set can only contains the display name if no aliases are found.
	 * @return A set containing the command's display name and aliases
	 * @deprecated since 6.0.0
	 * @since 5.0.0
	 */
	public Set<String> getNames() {
		return Collections.unmodifiableSet(this.commandNames);
	}
	
	/**
	 * Get the command's display name
	 * @return the main name
	 * @deprecated since 6.0.0
	 * @see #getInternalName()
	 */
	public String getDisplayName() {
		return this.commandInternalName;
	}
	
	/**
	 * Get the command's internal name <br/>
	 * It is mainly used to get the command's values in the localization like name, description, ...
	 * @return The command's internal name
	 */
	public String getInternalName() {
		return this.commandInternalName;
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
	public final UUID getID() {
		return this.commandId;
	}
	
	/**
	 * Get the unique command id shorten to base64
	 * @return the command's id
	 */
	public final String getShortenID() {
        return this.shortenCommandId;
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
