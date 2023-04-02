package apbiot.core.helper;

import java.util.ArrayList;
import java.util.List;

import apbiot.core.command.AbstractCommandInstance;
import apbiot.core.command.UserCommandCooldown;
import apbiot.core.time.StaticTime.TimeUnit;
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
							+convertTimeUnit(cmdU.getCooldown().getRemainingTime(), TimeUnit.NANOSECOND, TimeUnit.SECOND)+" seconde(s)** !").block();
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
	
	/**
	 * Convert a time in a new unit (seconds to hours, weeks to days)
	 * @param toConvert - the variable which contains the "time"
	 * @param oldUnit - the unit of the toConvert variable
	 * @param newUnit - the unit of the output
	 * @return a converted time in the an unit we want
	 * @see apbiot.core.time.StaticTime.TimeUnit
	 */
	public static Long convertTimeUnit(Long toConvert, TimeUnit oldUnit, TimeUnit newUnit) {
		long temporary = toConvert;
		if(oldUnit.getIndex() == -1 || oldUnit.getIndex() == -1) return 0L;
		
		int i = oldUnit.getIndex();
		while(i != newUnit.getIndex()) {
			if(isGreaterThanCurrentUnit(oldUnit, newUnit)) {
				if(i+1 < TimeUnit.values().length)
					temporary/=TimeUnit.values()[i+1].getOperation();
				i+=1;
			}else {
				temporary*=TimeUnit.values()[i].getOperation();
				
				i-=1;
			}
		}
		return temporary;
	}
	
	/**
	 * Check if a unit is "greater" than another (seconds is greater than milliseconds, days are smaller than weeks)
	 * @param old - the unit we want to compare
	 * @param compared - the comparator
	 * @return boolean if the comparator is greater than the compare
	 * @see apbiot.core.time.StaticTime.TimeUnit
	 */
	private static boolean isGreaterThanCurrentUnit(TimeUnit old, TimeUnit compared) {
		return compared.getIndex() > old.getIndex();
	}
	
}
