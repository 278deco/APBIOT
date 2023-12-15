package apbiot.core.handler;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import apbiot.core.pems.BaseProgramEventEnum;
import apbiot.core.pems.ProgramEventManager;
import discord4j.core.GatewayDiscordClient;
import marshmalliow.core.objects.Directory;

public abstract class AbstractDirectoryHandler extends Handler {

	private final Set<Directory> directories = new HashSet<>();

	protected final void addNewDirectory(Directory dir) {
		this.directories.add(dir);
	}
	
	protected final void addNewDirectory(String name, Path dir) {
		this.directories.add(new Directory(name, dir));
	}
	
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
