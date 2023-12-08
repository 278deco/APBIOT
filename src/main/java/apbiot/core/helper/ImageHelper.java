package apbiot.core.helper;

import java.io.IOException;
import java.util.Objects;

import apbiot.core.img.ConstructedImage;
import apbiot.core.img.ConstructedImage.ImageStatus;
import discord4j.rest.util.Image;
import marshmalliow.core.objects.Directory;

public class ImageHelper {
	
	/**
	 * Convert a discord image into a constructed image
	 * @param directory - the directory of the image
	 * @param discImage - the discord image
	 * @param imageName - the name of the future image
	 * @see discord4j.rest.util.Image
	 * @see apbiot.core.img.ConstructedImage
	 * @return an instance of ConstructedImage
	 * @throws IOException 
	 */
	public static ConstructedImage convertDiscordImage(Image discordImage, String name) throws IOException {
		return new ConstructedImage(Objects.requireNonNull(discordImage).getData(), name, ImageStatus.TEMPORARY);
	}
	
	/**
	 * Save an image in the temporary folder and format it ready to be sent
	 * @param directory - the directory of the image
	 * @param img - the ConstructedImage instance
	 * @return a tuple containing the image name and the path to the image
	 * @throws IOException 
	 */
	public static void getValidDiscordImage(Directory directory, ConstructedImage img) throws IOException {
		//TODO
		return;
	}
	
}
