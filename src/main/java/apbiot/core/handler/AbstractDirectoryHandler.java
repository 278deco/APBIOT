package apbiot.core.handler;

import java.util.HashSet;
import java.util.Set;

import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEventManager;
import discord4j.core.GatewayDiscordClient;
import marshmalliow.core.objects.Directory;

public abstract class AbstractDirectoryHandler extends Handler {

	private final Set<Directory> directories = new HashSet<>();

	protected abstract void registerDirectories();
	
	@Override
	protected void register(GatewayDiscordClient client) throws HandlerPreProcessingException {
		registerDirectories();
		
		ProgramEventManager.get().dispatchEvent(BaseProgramEventEnum.DIRECTORIES_LOADED_EVENT, new Object[] {directories});
	}

	@Override
	public final HandlerType getType() {
		return HandlerType.DEFAULT;
	}

}
