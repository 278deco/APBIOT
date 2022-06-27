package apbiot.core.time;

public class BypassCommandCooldown extends CommandCooldown {

	public BypassCommandCooldown addRole(String... roleName) {
		for(String name : roleName) this.roleList.add(name);
		this.restrictedRoles = false;
		return this;
	}
	
}
