package apbiot.core.helper;

import java.io.File;
import java.io.IOException;

import apbiot.core.MainInitializer;
import apbiot.core.handler.EImageHandler;
import apbiot.core.img.ConstructedImage;
import apbiot.core.img.ConstructedImage.ImageStatus;
import apbiot.core.objects.Tuple;
import discord4j.rest.util.Image;

public class ImageHelper {
	
	/**
	 * Convert a discord image into a constructed image
	 * @param discImage - the discord image
	 * @param imageName - the name of the future image
	 * @see discord4j.rest.util.Image
	 * @see apbiot.core.img.ConstructedImage
	 * @return an instance of ConstructedImage
	 */
	public static ConstructedImage convertDiscordImage(Image discImage, String imageName) {
		if(EImageHandler.temporaryImageDir == null || EImageHandler.permanentImageDir == null) throw new NullPointerException("ImageHandler hasn't yet been initialized !");
		
		try {
			return new ConstructedImage(EImageHandler.temporaryImageDir.getAbsolutePath(), imageName, discImage.getData(), ImageStatus.TEMPORARY);
		} catch (IOException e) {
			MainInitializer.LOGGER.warn("Unexpected error while creating a image",e);
		}
		return null;
	}
	
	/**
	 * Save an image in the temporary folder and format it ready to be sent
	 * @param img - the ConstructedImage instance
	 * @return a tuple containing the image name and the path to the image
	 */
	public static Tuple<String, String> getValidDiscordImage(ConstructedImage img) {
		if(EImageHandler.temporaryImageDir == null || EImageHandler.permanentImageDir == null) throw new NullPointerException("ImageHandler hasn't yet been initialized !");
		
		String path = EImageHandler.temporaryImageDir.getAbsolutePath()+File.separator+img.getName();
		
		try {
			img.saveImage(new File(path));
		} catch (IOException e) {
			MainInitializer.LOGGER.warn("Unexpected error while creating a image",e);
		}
		
		return new Tuple<String, String>(img.getName(), path);
	}
	
	/**
	 * Delete an file from the temporary folder
	 * @param imagePath
	 * @return if the image has been successfully deleted
	 */
	public static boolean deleteTemporaryImage(String imageName) {
		if(EImageHandler.temporaryImageDir == null || EImageHandler.permanentImageDir == null) throw new NullPointerException("ImageHandler hasn't yet been initialized !");
		
		return new File(EImageHandler.temporaryImageDir.getAbsolutePath()+File.separator+imageName).delete();
	}
	
}
