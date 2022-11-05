package apbiot.core.handler;

import java.io.File;

import apbiot.core.MainInitializer;
import apbiot.core.helper.FileHelper;
import apbiot.core.objects.interfaces.IOptionalHandler;

/**
 * ImageHandler class
 * This class handle all the image created by the bot
 * @author 278deco
 * @see apbiot.core.objects.interfaces.IHandler
 */
public abstract class AbstractImageHandler implements IOptionalHandler {
	protected static File temporaryImageDir, permanentImageDir;
	
	@Override
	public void register() {
		temporaryImageDir = FileHelper.generateDirectoryWithLogging("img/temporary", MainInitializer.LOGGER);
		permanentImageDir = FileHelper.generateDirectoryWithLogging("img/permanent", MainInitializer.LOGGER); 

		directoriesRegister();
	}
	
	public abstract void directoriesRegister();

	@Override
	public abstract void init();
	
	public static File getPermanentDirectory() {
		return permanentImageDir;
	}
	
	public static File getTemporaryDirectory() {
		return temporaryImageDir;
	}

}
