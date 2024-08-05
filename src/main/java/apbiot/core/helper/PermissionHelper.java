package apbiot.core.helper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import apbiot.core.permissions.CommandPermission;
import apbiot.core.permissions.Permissions;
import apbiot.core.utils.Emojis;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.Channel.Type;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

public class PermissionHelper {

	/**
	 * Used to tell if the user have the specified permissions
	 * @param user The user who ran the command
	 * @param cmdPerm The permission(s) required by the command
	 * @return if the user have the permission
	 * @since 2.0
	 */
	public static boolean comparePermission(Member user, List<Permission> permissions) {
		if (permissions == null || permissions.isEmpty()) return true;

		final PermissionSet userPermissions = user.getBasePermissions().block();
		for (Permission perm : permissions) {
			if (!userPermissions.contains(perm))
				return false;
		}
		return true;
	}
	
	/**
	 * Get the highest role for an user
	 * @param user The specified user
	 * @param guild The guild where the user is present
	 * @return the highest role for the user
	 * @since 2.0
	 */
	public static Optional<Role> getHighestRoleForUser(User user, Guild guild) {
		final Optional<Member> m = guild.getMemberById(user.getId()).blockOptional();
		if(m.isPresent()) {
			return Optional.ofNullable(m.get().getRoles().blockLast());
		}else {
			return Optional.empty();
		}
	}

	/**
	 * Used to tell if the user can execute a command or not
	 * @param user The specified user
	 * @param cmd The command instance
	 * @see apbiot.core.permissions.Permissions
	 * @param ownerID The id of the bot's owner
	 * @return if the user have all the required permissions or not
	 * @since 2.0
	 */
	public static boolean doesUserHavePermissions(Member user, CommandPermission cmdPerm, Snowflake ownerID) throws NullPointerException {
		Objects.requireNonNull(user);
		Objects.requireNonNull(cmdPerm);
		
		if(cmdPerm.areNoPermissionsRequired()) {	
			return true;
		}else if(cmdPerm.isDeveloperCommand()) {
			return user.getId().compareTo(ownerID) == 0;
		}else {
			final PermissionSet userPermissions = user.getBasePermissions().block();
			for(Permissions p : cmdPerm.getPermissions()) {
				if ((p.isDiscordPermission() && userPermissions.contains(p.getPermission())) ||
					(p.isRolePermission() && user.getRoles().any(role -> role.getName().equals(p.getRole())).block()))
					return cmdPerm.areRestrictivePermissions() ? false : true;
			}
				
			return cmdPerm.areRestrictivePermissions() ? true : false;
		}
	}
	
	/**
	 * Check if an user have the specified role
	 * @param names The roles' name
	 * @param user The targeted user
	 * @return boolean if user have the role
	 * @since 2.0
	 */
	public static boolean userHaveTheRole(List<String> names, Member user) {
		for (String roleName : names) {
			if (user.getRoles().any(role -> role.getName().equals(roleName)).block()) return true;
		}
		
		return false;
	}
	
	/**
	 * Check if an user have the specified role
	 * @param names The roles' name
	 * @param user The optional targeted user
	 * @return boolean if user have the role
	 * @since 5.0
	 */
	public static boolean userHaveTheRole(List<String> names, Optional<Member> user) {
		if(user.isEmpty()) return false;
		
		for (String roleName : names) {
			if (user.get().getRoles().any(role -> role.getName().equals(roleName)).block()) return true;
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
	 * Check if the {@link Channel} is private or public (DM or Guild)
	 * @param chanType The channel type
	 * @return if the channel is on a server
	 */
	public static boolean isServerEnvironnment(Type chanType) {
		return chanType != Type.GROUP_DM && chanType != Type.DM && chanType != Type.UNKNOWN;
	}
}
