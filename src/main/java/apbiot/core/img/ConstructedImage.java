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
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import apbiot.core.io.objects.Directory;
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
	
	private BufferedImage image;
	private Directory directory;
	private String imgName;
	private ImageStatus status;
	
	private Graphics2D graph;
	private boolean isDisposed;
	
	/**
	 * Create a new image object
	 * @param resource the resource storing the image
	 * @param status the status of the image
	 * @see ImageStatus
	 * @throws IOException
	 */
	public ConstructedImage(Resource resource, ImageStatus status) throws IOException {
		this.image = createImageFromBytes(resource.getData());
		this.directory = resource.getDirectory();
		this.imgName = resource.getName();
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
	public ConstructedImage(Directory directory, byte[] imgData, String name, ImageStatus status) throws IOException {
		this.image = createImageFromBytes(imgData);
		this.directory = directory;
		this.imgName = name;
		this.status = status;
	}
	
	/**
	 * Create a new image object
	 * @param path The path of the image
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @param status the status of the image
	 * @see ImageStatus
	 * @throws IOException
	 */
	public ConstructedImage(Path path, byte[] imgData, String name, ImageStatus status) throws IOException {
		this.image = createImageFromBytes(imgData);
		this.directory = new Directory(path);
		this.imgName = name;
		this.status = status;
	}
	
	/**
	 * Create a new image object
	 * @param directory The directory of the image
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @throws IOException
	 */
	public ConstructedImage(Directory directory, byte[] imgData, String name) throws IOException {
		this(directory, imgData, name, ImageStatus.TEMPORARY);
	}
	
	/**
	 * Create a new image object
	 * @param path The path of the image
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @throws IOException
	 */
	public ConstructedImage(Path path, byte[] imgData, String name) throws IOException {
		this(path, imgData, name, ImageStatus.TEMPORARY);
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
		this.imgName = name;
		this.status = status;
	}
	
	/**
	 * Create a new image object
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @throws IOException
	 */
	public ConstructedImage(byte[] imgData, String name) throws IOException {
		this(imgData, name, ImageStatus.TEMPORARY);
	}
	
	protected BufferedImage createImageFromBytes(byte[] imageData) throws IOException {
	    final ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	    return ImageIO.read(bais); 
	}
	
	/**
	 * Save the image stored in this instance at the new path given
	 * @param savePath the new path
	 * @throws IOException
	 */
	public void saveImage(Path savePath) throws IOException {
		if(!isDisposed) throw new IllegalAccessError("Cannot access to the image's properties if it hasn't been disposed!");
		ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next();
		FileImageOutputStream destination = null;
		
		try {
			destination = new FileImageOutputStream(savePath.toFile());
			writer.setOutput(destination);
			writer.write(this.image);
			
		}finally {
			if(destination != null) {
				destination.flush();
				destination.close();
			}
		}
		
	}
	
	/**
	 * Save the image stored in this instance at its original path
	 * @throws IOException
	 */
	public void saveImage() throws IOException {
		if(!isDisposed) throw new IllegalAccessError("Cannot access to the image's properties if it hasn't been disposed!");
		ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next();
		FileImageOutputStream destination = null;
		
		try {
			destination = new FileImageOutputStream(this.directory.getPath().resolve(this.imgName+".png").toFile());
			writer.setOutput(destination);
			writer.write(this.image);
			
		}finally {
			if(destination != null) {
				destination.flush();
				destination.close();
			}
		}
	}
	
	/**
	 * Release the graphic class using the image
	 * Can't call the save method before dispose the picture
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
		this.imgName = newName;
	}
	
	public byte[] getRawImage() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(this.getImage(), this.getFormat(), baos);
		
		return baos.toByteArray();
	}
	
	/**
	 * Get the stored image's width
	 * @return an integer containing the image's width
	 */
	public int getWidth() {
		if(!isDisposed) throw new IllegalAccessError("Cannot access to the image's properties if it hasn't been disposed!");
		return image.getWidth();
	}
	
	/**
	 * Get the stored image's height
	 * @return an integer containing the image's height 
	 */
	public int getHeight() {
		if(!isDisposed) throw new IllegalAccessError("Cannot access to the image's properties if it hasn't been disposed!");
		return image.getHeight();
	}
	
	/**
	 * Get the stored image
	 * @return a BufferedImage, the stored image
	 */
	public BufferedImage getImage() {
		if(!isDisposed) throw new IllegalAccessError("Cannot access to the image if it hasn't been disposed!");
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
		return this.imgName;
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