package apbiot.core.img;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import apbiot.core.io.resources.Resource;

/**
 * A class used to store an image and all the informations about it
 * @author 278deco
 */
public class ConstructedImage {
	
	/**
	 * Store all the status an image can have
	 * @author 278deco
	 */
	public static enum ImageStatus {
		PERMANENT,
		TEMPORARY,
		USER_DECISION;
	}
	
	protected String nameID;
	protected BufferedImage image;
	protected ImageStatus status;
	
	protected Graphics2D graph;
	protected boolean isDisposed;
	
	/**
	 * Create a new image object
	 * @param resource the resource storing the image
	 * @param status the status of the image
	 * @see ImageStatus
	 * @throws IOException
	 */
	public ConstructedImage(Resource resource, ImageStatus status) throws IOException {
		this.image = createImageFromBytes(resource.getData());
		this.nameID = resource.getID();
		this.status = status;
	}
	
	/**
	 * Create a new image object
	 * @param resource the resource storing the image
	 * @throws IOException
	 */
	public ConstructedImage(Resource resource) throws IOException {
		this(resource, ImageStatus.TEMPORARY);
	}
	
	/**
	 * Create a new image object
	 * @param directory The directory of the image
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @param status the status of the image
	 * @see ImageStatus
	 * @throws IOException
	 */
	public ConstructedImage(byte[] imgData, ImageStatus status) throws IOException {
		this.image = createImageFromBytes(imgData);
		this.status = status;
	}

	
	/**
	 * Create a new image object
	 * @param directory The directory of the image
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @throws IOException
	 */
	public ConstructedImage(byte[] imgData, String name) throws IOException {
		this(imgData, name, ImageStatus.TEMPORARY);
	}

	/**
	 * Create a new image object
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @param status the status of the image
	 * @see ImageStatus
	 * @throws IOException
	 */
	public ConstructedImage(byte[] imgData, String name, ImageStatus status) throws IOException {
		this.image = createImageFromBytes(imgData);
		this.nameID = name;
		this.status = status;
	}
	
	protected BufferedImage createImageFromBytes(byte[] imageData) throws IOException {
	    final ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	    return ImageIO.read(bais); 
	}
	
	/**
	 * Release the graphic class using the image<br>
	 * Cannot access any method related to the image's properties or do any action to the image (save, get, ...)
	 */
	public void dispose() {
		if(graph != null) graph.dispose();
		isDisposed = true;
	}
	
	/**
	 * Resize the stored image
	 * @param newW - the new width of the image
	 * @param newH - the new height of the image
	 * @param conserveTransparence - if the image have transparency, setting this to true will cause the image to retain its transparency 
	 * @throws IOException
	 * @return an instance of ConstructedImage
	 */
	public ConstructedImage resizeImage(int newW, int newH, boolean conserveTransparence) throws IOException {
		final Image result = this.image.getScaledInstance(newW, newH, Image.SCALE_DEFAULT);
		
		final BufferedImage newImage = new BufferedImage(newW, newH, (conserveTransparence ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB));
		newImage.getGraphics().drawImage(result, 0, 0, null);
		
		isDisposed = false;
		this.image = newImage;
		
		return this;
	}
	
	/**
	 * Used to merge two image together
	 * @param img an other BufferedImage to paste on instance's image
	 * @param x the X value where the image need to be paste
	 * @param y the Y value where the image need to be paste
	 * @return an instance of ConstructedImage
	 */
	public ConstructedImage mergeImages(BufferedImage img, int x, int y) {
		if(graph == null) graph = image.createGraphics();
		graph.drawImage(img, x, y, null);
		isDisposed = false;
		
		return this;
	}

	/**
	 * Used to merge two image together
	 * @param constructedImg an other ConstructedImage to paste on instance's image
	 * @param x the X value where the image need to be paste
	 * @param y the Y value where the image need to be paste
	 * @return an instance of ConstructedImage
	 */
	public ConstructedImage mergeImages(ConstructedImage constructedImg, int x, int y) {
		if(graph == null) graph = image.createGraphics();
		graph.drawImage(constructedImg.getImage(), x, y, null);
		isDisposed = false;
		
		return this;
	}
	
	/**
	 * Used to convert image's color into only black and white
	 * @see java.awt.image.ColorConvertOp
	 * @return an instance of ConstructedImage
	 */
	public ConstructedImage applyBlacknWhiteFilter() {	
		ColorConvertOp gray = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null);
		gray.filter(this.image, this.image);
		isDisposed = false;
		
		return this;
	}
	
	/**
	 * Used to change the brightness and increase the hue of an image
	 * @param brightness the new brightness of the image
	 * @param hue the new hue of the image
	 * @see java.awt.image.RescaleOp
	 * @return an instance of ConstructedImage
	 */
	public ConstructedImage changeBrightness(float brightness, int hue) {
		RescaleOp bright = new RescaleOp(brightness, hue, null);
		bright.filter(this.image, this.image);
		isDisposed = false;
		
		return this;
	}
	
	/**	
	 * Change the name of the image contained in the instance
	 * @param newName the new name of the image
	 */
	public void changeImageName(String newName) {
		this.nameID = newName;
	}
	
	public byte[] getRawImage() throws IOException {
		if(!isDisposed) throw new IllegalAccessError("Cannot access the image if it hasn't been disposed!");
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		ImageIO.write(this.getImage(), this.getFormat(), baos);
		
		return baos.toByteArray();
	}
	
	/**
	 * Get the stored image's width
	 * @return an integer containing the image's width
	 */
	public int getWidth() {
		if(!isDisposed) throw new IllegalAccessError("Cannot access the image's properties if it hasn't been disposed!");
		return image.getWidth();
	}
	
	/**
	 * Get the stored image's height
	 * @return an integer containing the image's height 
	 */
	public int getHeight() {
		if(!isDisposed) throw new IllegalAccessError("Cannot access the image's properties if it hasn't been disposed!");
		return image.getHeight();
	}
	
	/**
	 * Get the stored image
	 * @return a BufferedImage, the stored image
	 */
	public BufferedImage getImage() {
		if(!isDisposed) throw new IllegalAccessError("Cannot access the image if it hasn't been disposed!");
		return image;
	}
	
	/**
	 * Get the status of the image
	 * @return an ImageStatus, from the stored image
	 */
	public ImageStatus getStatus() {
		return status;
	}
	
	/**
	 * Get the name of the image
	 * @return the name of the image
	 */
	public String getName() {
		return this.nameID;
	}

	/**
	 * Default format used by the constructed images<br>
	 * Describe the extension used by the image<br><br>
	 * <i>To be reworked</i>
	 * @return the extension of the image
	 */
	public String getFormat() {
		return "png";
	}

}