package apbiot.core.handler;

import java.nio.file.Path;

import apbiot.core.MainInitializer;
import apbiot.core.objects.interfaces.IOptionalHandler;

/**
 * ImageHandler class
 * This class handle all the image created by the bot
 * @author 278deco
 * @see apbiot.core.objects.interfaces.IHandler
 */
public abstract class AbstractImageHandler implements IOptionalHandler {
	
	private Path permanentDirectory, temporaryDirectory;

	@Override
	public void build() {
		permanentDirectory = MainInitializer.getDirectoriesManager().getLoadedDirectory("image:permanent_directory").getPath();
		permanentDirectory = MainInitializer.getDirectoriesManager().getLoadedDirectory("image:temporary_directory").getPath();
		
		initDirectories();
	}
	
	public abstract void initDirectories();
	
	public Path getPermanentDirectory() {
		return this.permanentDirectory;
	}
	
	public Path getTemporaryDirectory() {
		return this.temporaryDirectory;
	}

}
