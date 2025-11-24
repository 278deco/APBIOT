package apbiot.core.permissions;

import java.util.Optional;

import discord4j.rest.util.Permission;

public class Permissions {
	
	private Optional<String> roleName = Optional.empty();
	private Optional<Permission> perm = Optional.empty();
	
	public Permissions(String roleName) {
		this.roleName = Optional.of(roleName);
	}
	
	public Permissions(Permission perm) {
		this.perm = Optional.of(perm);
	}
	
	public boolean isRolePermission() {
		return roleName.isPresent();
	}
	
	public boolean isDiscordPermission() {
		return perm.isPresent();
	}
	
	public String getRole() {
		return isRolePermission() ? roleName.get() : null;
	}
	
	public Permission getPermission() {
		return isDiscordPermission() ? perm.get() : null;
	}
	
}
