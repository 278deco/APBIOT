package apbiot.core.handler;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
	
	private static final Logger LOGGER = LogManager.getLogger(AbstractImageHandler.class);
	
	@Override
	public void register() {
		temporaryImageDir = FileHelper.generateDirectoryWithLogging("img/temporary", LOGGER);
		permanentImageDir = FileHelper.generateDirectoryWithLogging("img/permanent", LOGGER); 

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
