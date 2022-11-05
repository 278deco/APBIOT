package apbiot.core.img;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import io.netty.util.concurrent.BlockingOperationException;

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
	
	protected BufferedImage img;
	protected File file;
	protected String path, imgName;
	protected ImageStatus status;
	protected boolean isModified = false;
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param status - the status of the image
	 * @see apbiot.core.img
	 * @throws IOException
	 */
	public ConstructedImage(String path, String name, ImageStatus status) throws IOException {
		this.path = path;
		this.file = new File(path+File.separator+name);
		this.imgName = name;
		this.status = status;
		this.img = openImage();
	}
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @throws IOException
	 */
	public ConstructedImage(String path, String name) throws IOException {
		this.path = path;
		this.file = new File(path+File.separator+name);
		this.imgName = name;
		this.status = ImageStatus.TEMPORARY;
		this.img = openImage();
	}
	
	/**
	 * 
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param img - a BufferedImage to store
	 * @throws IOException
	 */
	public ConstructedImage(String path, String name, BufferedImage img) throws IOException {
		this.path = path;
		this.file = new File(path+File.separator+name);
		this.imgName = name;
		this.status = ImageStatus.TEMPORARY;
		this.img = img;
	}
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param img - a BufferedImage to store
	 * @param status - the status of the image
	 * @see apbiot.core.img
	 * @throws IOException
	 */
	public ConstructedImage(String path, String name, BufferedImage img, ImageStatus status) throws IOException {
		this.path = path;
		this.file = new File(path+File.separator+name);
		this.imgName = name;
		this.status = status;
		this.img = img;
	}
	
	/**
	 * 
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param imgData - The data contained in an image
	 * @throws IOException
	 */
	public ConstructedImage(String path, String name, byte[] imgData) throws IOException {
		this.path = path;
		this.file = new File(path+File.separator+name);
		this.imgName = name;
		this.status = ImageStatus.TEMPORARY;
		this.img = createImageFromBytes(imgData);
	}
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param imgData - The data contained in an image
	 * @param status - the status of the image
	 * @see apbiot.core.img
	 * @throws IOException
	 */
	public ConstructedImage(String path, String name, byte[] imgData, ImageStatus status) throws IOException {
		this.path = path;
		this.file = new File(path+File.separator+name);
		this.imgName = name;
		this.status = status;
		this.img = createImageFromBytes(imgData);
	}
	
	protected BufferedImage createImageFromBytes(byte[] imageData) throws IOException {
	    ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
	    
	    return ImageIO.read(bais);
	}
	
	/**
	 * Used to read a file and store the image in a BufferedImage
	 * @return the image contained in the file
	 * @throws IOException
	 */
	private BufferedImage openImage() throws IOException {
		return ImageIO.read(this.file);
	}
	
	/**
	 * Used to save the image stored in this instance at the original image's path
	 * @throws IOException
	 */
	public void saveImage() throws IOException {
		if(isModified) throw new BlockingOperationException("Can't save an image without call disposal method");
		else {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next();
			
			FileImageOutputStream destination = new FileImageOutputStream(this.file);
			writer.setOutput(destination);
			writer.write(this.img);
			
			destination.close();
		}
	}
	
	/**
	 * Used to save the image stored in this instance in a new position
	 * @param file - where the image need to be save
	 * @throws IOException
	 */
	public void saveImage(File file) throws IOException {
		if(isModified) throw new BlockingOperationException("Can't save an image without call disposal method");
		else {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next();
			
			FileImageOutputStream destination = new FileImageOutputStream(file);
			writer.setOutput(destination);
			writer.write(this.img);
			
			destination.close();
		}
	}
	
	/**
	 * Used to save the image stored in this instance with a new name but with the original image's path
	 * @param name - the new name of the image
	 * @throws IOException
	 */
	public void saveImage(String name) throws IOException {
		if(isModified) throw new BlockingOperationException("Can't save an image without call disposal method");
		else {
			ImageWriter writer = ImageIO.getImageWritersByFormatName("PNG").next();
			
			FileImageOutputStream destination = new FileImageOutputStream(new File(this.path+name));
			writer.setOutput(destination);
			writer.write(this.img);
			
			destination.close();
		}
	}
	
	/**
	 * Used to resize the stored image
	 * @param newW - the new width of the image
	 * @param newH - the new height of the image
	 * @param conserveTransparence - if the image have transparency, setting this to true will cause the image to retain its transparency 
	 */
	public void resizeImage(int newW, int newH, boolean conserveTransparence) {
		Image result = this.img.getScaledInstance(newW, newH, Image.SCALE_DEFAULT);
		int imgType = conserveTransparence ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
		
		BufferedImage returningImg = new BufferedImage(newW, newH, imgType);
		returningImg.getGraphics().drawImage(result, 0, 0, null);
		
		this.img = returningImg;
	}
	
	public boolean deleteImage(boolean deleteIfTemporary) {
		if(deleteIfTemporary) {
			if(this.status == ImageStatus.TEMPORARY) {
				return this.file.delete();
			}else {
				return false;
			}
		}else {
			return this.file.delete();
		}
	}
	
	public GraphicImage duplicateAndEditImage() throws IOException {
		return new GraphicImage(this.path, this.imgName, this.img, this.status);
	}
	
	/**	
	 * Change the name of the image contained in the instance
	 * @param newName - the new name of the image
	 */
	public void changeImageName(String newName) {
		this.imgName = newName;
	}
	
	/**
	 * Get the stored image's width
	 * @return an integer containing the image's width
	 */
	public int getWidth() {
		return img.getWidth();
	}
	
	/**
	 * Get the stored image's height
	 * @return an integer containing the image's height 
	 */
	public int getHeight() {
		return img.getHeight();
	}
	
	/**
	 * Get the stored image
	 * @return a BufferedImage, the stored image
	 */
	public BufferedImage getImage() {
		return img;
	}
	
	/**
	 * Get the file where the image is stored
	 * @return a File where the image is
	 */
	public File getFile() {
		return file;
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

}