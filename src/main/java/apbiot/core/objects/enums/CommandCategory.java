package apbiot.core.objects.enums;

import apbiot.core.objects.interfaces.ICommandCategory;

public enum CommandCategory implements ICommandCategory {

	FUN("fun"),
	ADMIN("admin"),
	MUSIC("music"),
	UTILITY("utility"),
	GAME("game");
	
	private String name;
	CommandCategory(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
}
