package apbiot.core.helper;

import java.util.List;
import java.util.Optional;

import apbiot.core.permissions.CommandPermission;
import apbiot.core.permissions.Permissions;
import apbiot.core.utils.Emojis;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.rest.util.Permission;

public class PermissionHelper {

	/**
	 * Used to tell if the user have the specified permissions
	 * 
	 * @param user - the user who ran the command
	 * @param cmdPerm - the permission(s) required by the command
	 * @return if the user have the permission
	 */
	public static boolean comparePermission(Member user, List<Permission> permissions) {
		if (permissions == null) return true;

		for (Permission perm : permissions) {
			if (!user.getBasePermissions().block().contains(perm))
				return false;
		}
		return true;
	}
	
	/**
	 * Get the highest role for an user
	 * @param user - the specified user
	 * @param guild - the guild where the user is present
	 * @return the highest role for the user
	 */
	public static Role getHighestRoleForUser(User user, Guild guild) {
		Optional<Member> m = guild.getMemberById(user.getId()).blockOptional();
		if(m.isPresent()) {
			return m.get().getRoles().blockLast();
		}else {
			return null;
		}
	}

	/**
	 * Used to tell if the user can execute a command or not
	 * @param user - the specified user
	 * @param cmd - The command instance
	 * @see apbiot.core.permissions.Permissions
	 * @param ownerID - the id of the bot's owner
	 * @return if the user have all the required permissions or not
	 */
	public static boolean compareCommandPermissions(Member user, CommandPermission cmdPerm, Snowflake ownerID) throws NullPointerException {
		if(cmdPerm == null) throw new NullPointerException("Command permission variable cannot be null");
		
		if(cmdPerm.areNoPermissionsRequired()) {	
			return true;
		}else if(cmdPerm.isDeveloperCommand()) {
			return user.getId().compareTo(ownerID) == 0;
		}else {
			for(Permissions p : cmdPerm.getPermissions()) {
				if ((p.isDiscordPermission() && user.getBasePermissions().block().contains(p.getPermission())) ||
					(p.isRolePermission() && user.getRoles().any(role -> role.getName().equals(p.getRole())).block()))
					return cmdPerm.areRestrictivePermissions() ? false : true;
			}
				
			return cmdPerm.areRestrictivePermissions() ? true : false;
		}
	}
	
	/**
	 * Check if an user have the specified role
	 * @param names - the roles' name
	 * @param user - the targetted user
	 * @return boolean if user have the role
	 */
	public static boolean userHaveTheRole(List<String> names, Member user) {
		for (String roleName : names) {
			if (user.getRoles().any(role -> role.getName().equals(roleName)).block()) return true;
		}
		
		return false;
	}
	
	/**
	 * Basic error message when user doesn't have the role / permission
	 * @return error message
	 */
	public static String getStringErrorPermission() {
		return Emojis.X_CROSS+" Vous n'avez pas les permissions / rôles requis pour exécuter cette commande !";
	}
	
	/**
	 * Check if the channel is private or public (DM or Guild)
	 * @param chanType - the channel type
	 * @return if the chan is on a server
	 */
	public static boolean isServerEnvironnment(Type chanType) {
		return chanType != Type.GROUP_DM && chanType != Type.DM && chanType != Type.UNKNOWN;
	}
}
