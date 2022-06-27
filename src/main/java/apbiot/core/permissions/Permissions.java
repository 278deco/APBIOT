package apbiot.core.permissions;

import discord4j.rest.util.Permission;

public class Permissions {
	
	private String roleName;
	private Permission perm;
	
	public Permissions(String roleName) {
		this.roleName = roleName;
	}
	
	public Permissions(Permission perm) {
		this.perm = perm;
	}
	
	public boolean isRolePermission() {
		return roleName != null;
	}
	
	public boolean isDiscordPermission() {
		return perm != null;
	}
	
	public String getRole() {
		return roleName;
	}
	
	public Permission getPermission() {
		return perm;
	}
	
}
