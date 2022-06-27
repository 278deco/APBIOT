package apbiot.core.time;

import java.util.ArrayList;
import java.util.List;

public class CommandCooldown {
	
	/*
	 * If the bypass are set, you cannot have restricted role and vice versa
	 */
	
	protected List<String> roleList = new ArrayList<>(); 
	protected boolean restrictedRoles;
	protected StaticTime timer;
	protected boolean isWithoutTimer;
	
	/**
	 * Set the timer of the cooldown
	 * @param time - the time of the cooldown
	 * @return an instance of CommandCooldown
	 */
	public CommandCooldown setTimer(StaticTime time) {
		this.timer = time;
		return this;
	}
	
	/**
	 * Define if the cooldown is a restricting cooldown
	 * @return if it's a restricting cooldown
	 */
	public boolean isARestrictingCooldown() {
		return restrictedRoles;
	}
	
	/**
	 * Get the timer used by the cooldown
	 * @see apbiot.core.time.StaticTime
	 * @return the time
	 */
	public StaticTime getTimer() {
		return this.timer;
	}
	
	/**
	 * Get the role
	 * @return the role
	 */
	public List<String> getRoles() {
		return this.roleList;
	}
	
	/**
	 * add a new role to the role list
	 * @param roleName - the role to be added
	 * @return an instance of CommandCooldown
	 */
	public CommandCooldown addRole(String... roleName) {
		if(isWithoutTimer) return this;
		
		for(String name : roleName) this.roleList.add(name);
		return this;
	}
	
	/**
	 * Set if the command will be without cooldown
	 * @return an instance of CommandCooldown
	 */
	public CommandCooldown setWithoutCooldown() {
		this.isWithoutTimer = true;
		return this;
	}
	
	/**
	 * Get if the command is without cooldown
	 * @return if is without cooldown
	 */
	public boolean isWithoutCooldown() {
		return isWithoutTimer;
	}
	
}
