package apbiot.core.pems.actions;

import apbiot.core.pems.ProgramEvent;

public class CommandRebuildAction extends ProgramEvent {

	public CommandRebuildAction(Object[] arguments) {
		super(arguments);
	}
	
	public CommandRebuildScope getScope() {
		return getEventArgument(CommandRebuildScope.class, 0);
	}

	@Override
	public EventPriority getPriority() {
		return EventPriority.HIGH;
	}

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