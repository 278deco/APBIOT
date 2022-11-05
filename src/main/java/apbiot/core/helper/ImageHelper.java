package apbiot.core.helper;

import java.io.File;
import java.io.IOException;

import apbiot.core.MainInitializer;
import apbiot.core.img.ConstructedImage;
import apbiot.core.img.ConstructedImage.ImageStatus;
import apbiot.core.objects.Tuple;
import discord4j.rest.util.Image;

public class ImageHelper {
	
	/**
	 * Convert a discord image into a constructed image
	 * @param directory - the directory of the image
	 * @param discImage - the discord image
	 * @param imageName - the name of the future image
	 * @see discord4j.rest.util.Image
	 * @see apbiot.core.img.ConstructedImage
	 * @return an instance of ConstructedImage
	 */
	public static ConstructedImage convertDiscordImage(String directory, Image discImage, String imageName) {
		if(directory == null || directory.isEmpty() || directory.isBlank()) throw new NullPointerException("The directory isn't correctly defined");
		
		try {
			return new ConstructedImage(directory, imageName, discImage.getData(), ImageStatus.TEMPORARY);
		} catch (IOException e) {
			MainInitializer.LOGGER.warn("Unexpected error while creating a image",e);
		}
		return null;
	}
	
	/**
	 * Save an image in the temporary folder and format it ready to be sent
	 * @param directory - the directory of the image
	 * @param img - the ConstructedImage instance
	 * @return a tuple containing the image name and the path to the image
	 */
	public static Tuple<String, String> getValidDiscordImage(String directory, ConstructedImage img) {
		if(directory == null || directory.isEmpty() || directory.isBlank()) throw new NullPointerException("The directory isn't correctly defined");
		
		String path = directory+File.separator+img.getName();
		
		try {
			img.saveImage(new File(path));
		} catch (IOException e) {
			MainInitializer.LOGGER.warn("Unexpected error while creating a image",e);
		}
		
		return new Tuple<String, String>(img.getName(), path);
	}
	
	/**
	 * Delete an file from the temporary folder
	 * @param directory - the directory of the image
	 * @param imageName - the name of the image and it extension
	 * @return if the image has been successfully deleted
	 */
	public static boolean deleteTemporaryImage(String directory, String imageName) {
		if(directory == null || directory.isEmpty() || directory.isBlank()) throw new NullPointerException("The directory isn't correctly defined");
		
		return new File(directory+File.separator+imageName).delete();
	}
	
}
