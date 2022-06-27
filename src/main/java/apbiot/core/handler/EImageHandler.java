package apbiot.core.handler;

import java.io.File;

import apbiot.core.MainInitializer;
import apbiot.core.objects.interfaces.IOptionalHandler;

/**
 * ImageHandler class
 * This class handle all the image created by the bot
 * @author 278deco
 * @see apbiot.core.objects.interfaces.IHandler
 */
public abstract class EImageHandler implements IOptionalHandler {
	public static File temporaryImageDir, permanentImageDir;
	
	
	@Override
	public void register() {
		temporaryImageDir = new File("img/temporary");
		permanentImageDir = new File("img/permanent");
		if(temporaryImageDir.mkdirs()) {
			MainInitializer.LOGGER.info("Directory "+temporaryImageDir.getName()+" has been successfully created !");
		}else {
			MainInitializer.LOGGER.info("Directory "+temporaryImageDir.getName()+" has been successfully loaded !");
		}
		if(permanentImageDir.mkdirs()) {
			MainInitializer.LOGGER.info("Directory "+permanentImageDir.getName()+" has been successfully created !");
		}else {
			MainInitializer.LOGGER.info("Directory "+permanentImageDir.getName()+" has been successfully loaded !");
		}
		
		directoriesRegister();
	}
	
	public abstract void directoriesRegister();

	@Override
	public abstract void init();

}
