package apbiot.core.helper;

import java.io.IOException;
import java.util.Objects;

import apbiot.core.img.ConstructedImage;
import apbiot.core.img.ConstructedImage.ImageStatus;
import apbiot.core.io.ResourceManager;
import apbiot.core.io.objects.Directory;
import apbiot.core.io.resources.Resource;
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
	public static Resource getValidDiscordImage(Directory directory, ConstructedImage img) throws IOException {
		if(!ResourceManager.doesInstanceExist()) throw new IllegalStateException("Cannot call this method if the resourceManager doesn't exist!");
		final Resource resource = new Resource(Objects.requireNonNull(directory), img.getName(), img.getFormat(), img.getRawImage());

		ResourceManager.getInstance().saveResource(resource);

		return resource;
	}
	
}
