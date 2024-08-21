package apbiot.core.objects.enums;

import apbiot.core.objects.interfaces.ICommandCategory;

public enum CommandCategory implements ICommandCategory {

	FUN("fun", true),
	ADMIN("admin", true),
	MUSIC("music", true),
	UTILITY("utility", true),
	GAME("game", true),
	NO_CATEGORY("no_category", false);
	
	private String name;
	private boolean isIndexed;
	CommandCategory(String name, boolean isIndexed) {
		this.name = name;
		this.isIndexed = isIndexed;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean isIndexed() {
		return this.isIndexed;
	}
	
}
