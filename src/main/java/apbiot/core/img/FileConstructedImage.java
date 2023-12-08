package apbiot.core.img;

import java.io.IOException;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

import apbiot.core.io.resources.Resource;
import marshmalliow.core.objects.Directory;

public class FileConstructedImage extends ConstructedImage {

	private Directory directory;
	
	/**
	 * Create a new image object
	 * @param resource the resource storing the image
	 * @param status the status of the image
	 * @see ImageStatus
	 * @throws IOException
	 */
	public FileConstructedImage(Resource resource, ImageStatus status) throws IOException {
		super(resource, status);
		this.directory = resource.getDirectory();
	}
	
	/**
	 * Create a new image object
	 * @param resource the resource storing the image
	 * @throws IOException
	 */
	public FileConstructedImage(Resource resource) throws IOException {
		super(resource, ImageStatus.TEMPORARY);
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
	public FileConstructedImage(Directory directory, byte[] imgData, String name, ImageStatus status) throws IOException {
		super(imgData, name, status);
		this.directory = directory;
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
	public FileConstructedImage(Path path, byte[] imgData, String name, ImageStatus status) throws IOException {
		super(imgData, name, status);
		this.directory = new Directory(path);
	}
	
	/**
	 * Create a new image object
	 * @param directory The directory of the image
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @throws IOException
	 */
	public FileConstructedImage(Directory directory, byte[] imgData, String name) throws IOException {
		this(directory, imgData, name, ImageStatus.TEMPORARY);
	}
	
	/**
	 * Create a new image object
	 * @param path The path of the image
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @throws IOException
	 */
	public FileConstructedImage(Path path, byte[] imgData, String name) throws IOException {
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
	public FileConstructedImage(byte[] imgData, String name, ImageStatus status) throws IOException {
		super(imgData, name, status);
	}
	
	/**
	 * Create a new image object
	 * @param imgData A byte array containing the image data
	 * @param name The name of the image
	 * @throws IOException
	 */
	public FileConstructedImage(byte[] imgData, String name) throws IOException {
		this(imgData, name, ImageStatus.TEMPORARY);
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
			destination = new FileImageOutputStream(this.directory.getPath().resolve(this.nameID+".png").toFile());
			writer.setOutput(destination);
			writer.write(this.image);
			
		}finally {
			if(destination != null) {
				destination.flush();
				destination.close();
			}
		}
	}

}
