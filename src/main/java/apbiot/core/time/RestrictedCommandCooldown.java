package apbiot.core.time;

public class RestrictedCommandCooldown extends CommandCooldown {

	public RestrictedCommandCooldown addRole(String... roleName) {
		for(String name : roleName) this.roleList.add(name);
		this.restrictedRoles = true;
		return this;
	}
	
}
