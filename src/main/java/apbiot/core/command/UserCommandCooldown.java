package apbiot.core.command;

import apbiot.core.time.StaticTime;
import apbiot.core.time.Time;
import discord4j.core.object.entity.User;

public class UserCommandCooldown {

	private AbstractCommandInstance command;
	private User user;
	private Time cooldown;
	
	/**
	 * 
	 * @param cmd - the executed command
	 * @param user - the targetted user
	 * @param time - a new static timer
	 * @see apbiot.core.time.StaticTime
	 */
	public UserCommandCooldown(AbstractCommandInstance cmd, User user, StaticTime time) {
		this.command = cmd;
		this.user = user;
		this.cooldown = new Time().create(time.getInitialDurationTime(), time.getTimeUnit());
	}
	
	/**
	 * @return the stocked cooldown
	 */
	public Time getCooldown() {
		return cooldown;
	}
	
	/**
	 * Check if the cooldown is over
	 * @see apbiot.core.time.Time#isFinish()
	 * @return boolean if is over or not
	 */
	public boolean isCooldownOver() {
		if(cooldown.isFinish()) {
			destroy();
			return true;
		}
		return false;
	}
	
	/**
	 * @return the stocked user
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * @return the stocked targetted command
	 */
	public AbstractCommandInstance getTargettedCommand() {
		return command;
	}
	
	/**
	 * User to destroy this instance (make all stocked variable null)
	 */
	public void destroy() {
		this.command = null;
		this.user = null;
		this.cooldown = null;
	}
	
}
