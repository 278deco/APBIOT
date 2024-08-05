package apbiot.core.pems;

import apbiot.core.pems.actions.CommandRebuildAction;

public enum BaseProgramActionEnum implements ProgramEventEnumerator {
	
	COMMAND_REBUILD_ACTION(CommandRebuildAction.class),
	
	UNDEFINED(null);
	
	private final Class<? extends ProgramEvent> cls;
	private BaseProgramActionEnum(Class<? extends ProgramEvent> cls) {
		this.cls = cls;
	}
	
	@Override
	public Class<? extends ProgramEvent> getEventClass() {
		return cls;
	}

}
