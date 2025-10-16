package apbiot.core.pems.commands;

import apbiot.core.pems.Action;

public record RebuildDiscordCommandsAction(CommandRebuildScope scope) implements Action<Boolean> {

	public static enum CommandRebuildScope {
		
		ONLY_NATIVE,
		ONLY_SLASH,
		BOTH_NATIVE_SLASH,
		ONLY_APPLICATION,
		BOTH_SLASH_APPLICATION,
		ONLY_COMPONENT,
		ALL;
	}
}