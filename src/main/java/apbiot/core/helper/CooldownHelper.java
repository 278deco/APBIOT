package apbiot.core.helper;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.command.UserCommandCooldown;
import apbiot.core.utils.Emojis;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class CooldownHelper {

	/**
	 * Check if the cooldown of an user is over or not
	 * @param cooldown an {@link UserCommandCooldown} instance 
	 * @return boolean if the cooldown is over or not
	 * @see UserCommandCooldown#isCooldownOver()
	 * @since 3.0
	 */
	public static boolean isCooldownOver(UserCommandCooldown cooldown) {
		return cooldown.isCooldownOver();
	}
	
	/**
	 * Check if an user can execute a command (check if the player is the cooldown list)
	 * @param list The list which contains the userlist cooldown
	 * @param user The {@link User} wanting to execute the command
	 * @param chan The {@link MessageChannel} where the user send the message
	 * @return boolean if the user can execute the command
	 * @since 3.0
	 */
	public static boolean canExecuteCommand(List<UserCommandCooldown> list, User u, MessageChannel chan) {
		for(UserCommandCooldown cmdU : list) {
			if(cmdU.getUser().getId().compareTo(u.getId()) == 0) {
				if(isCooldownOver(cmdU)) {
					return true;
				}else {
					chan.createMessage(Emojis.ALARM_CLOCK+" Vous ne pourrez éxécuter cette commande que dans **"
							+cmdU.getCooldown().getTimeUnit().toSeconds(cmdU.getCooldown().getRemainingTime())+" seconde(s)** !").block();
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Clears all cooldown for which the timer has ended
	 * @param list The list which contains the user list cooldown
	 * @return the list without null instance
	 * @since 3.0
	 */
	public synchronized static List<UserCommandCooldown> wipeNullInstance(List<UserCommandCooldown> list) {
		final List<UserCommandCooldown> memory = new ArrayList<>(list);
		
		for(UserCommandCooldown cmdU : list) {
			if(cmdU.getUser() == null) {
				memory.remove(cmdU);
			}
		}
		
		return memory;
	}
	
	/**
	 * Used to create an new command cooldown
	 * @param cmd The command executed by the user
	 * @param user The targeted user
	 * @param guild The guild where the command was been executed
	 * @return an instance of UserCommandCooldown or null
	 * @see apbiot.core.command.UserCommandCooldown
	 * @since 3.0
	 */
	public static UserCommandCooldown createNewCooldown(AbstractCommandInstance cmd, User user, Guild guild) {
		if(cmd.getCooldown().isARestrictingCooldown()) {
			return PermissionHelper.userHaveTheRole(cmd.getCooldown().getRoles(), guild.getMemberById(user.getId()).block()) ? 
					new UserCommandCooldown(cmd, user, cmd.getCooldown().getTimer()) : null;
		}else {
			return PermissionHelper.userHaveTheRole(cmd.getCooldown().getRoles(), guild.getMemberById(user.getId()).block()) ? 
					null : new UserCommandCooldown(cmd, user, cmd.getCooldown().getTimer());
		}
	}
	
}
