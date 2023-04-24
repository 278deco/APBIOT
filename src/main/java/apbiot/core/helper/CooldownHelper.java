package apbiot.core.helper;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.command.UserCommandCooldown;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;

public class CooldownHelper {

	/**
	 * Check if the cooldown of an user is over or not
	 * @param cooldown - an UserCommandCooldown instance 
	 * @return boolean if the cooldown is over or not
	 * @see apbiot.core.command.UserCommandCooldown#isCooldownOver()
	 */
	public static boolean isCooldownOver(UserCommandCooldown cooldown) {
		return cooldown.isCooldownOver();
	}
	
	/**
	 * Check if an user can execute a command (check if the player is the cooldown list)
	 * @param list - The list which contains the userlist cooldown
	 * @param user - The user targetted
	 * @param chan - The messagechannel where the user send the message
	 * @return boolean if the user can execute the command
	 */
	public static boolean canExecuteCommand(List<UserCommandCooldown> list, User u, MessageChannel chan) {
		for(UserCommandCooldown cmdU : list) {
			if(cmdU.getUser().getId().compareTo(u.getId()) == 0) {
				if(isCooldownOver(cmdU)) {
					return true;
				}else {
					chan.createMessage("⛔ Vous ne pourrez éxécuter cette commande que dans **"
							+cmdU.getCooldown().getTimeUnit().toSeconds(cmdU.getCooldown().getRemainingTime())+" seconde(s)** !").block();
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Clears all cooldowns for which the timer has ended
	 * @param list - The list which contains the userlist cooldown
	 * @return the list without null instance
	 */
	public synchronized static List<UserCommandCooldown> wipeNullInstance(List<UserCommandCooldown> list) {
		List<UserCommandCooldown> memory = new ArrayList<>(list);
		
		for(UserCommandCooldown cmdU : list) {
			if(cmdU.getUser() == null) {
				memory.remove(cmdU);
			}
		}
		
		return memory;
	}
	
	/**
	 * Used to create an new command cooldown
	 * @param cmd - the command executed by the user
	 * @param user - the targetted user
	 * @param guild - the guild where the command was been executed
	 * @return an instance of UserCommandCooldown or null
	 * @see apbiot.core.command.UserCommandCooldown
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
