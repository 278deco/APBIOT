package apbiot.core.img;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.io.IOException;

/**
 * A class used to apply modification on all types of pictures
 * @see apbiot.core.img.ConstructedImage
 * @author 278deco
 */
public class GraphicImage extends ConstructedImage {

	private Graphics2D graph;
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @throws IOException
	 */
	public GraphicImage(String path, String name) throws IOException {
		super(path, name);
		this.graph = this.img.createGraphics(); 
	}
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param status - the status of the image
	 * @see apbiot.core.img.ConstructedImage.ImageStatus
	 * @throws IOException
	 */
	public GraphicImage(String path, String name, ImageStatus status) throws IOException {
		super(path, name, status);
		this.graph = this.img.createGraphics();
	}
	
	/**
	 * 
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param img - a BufferedImage to store
	 * @throws IOException
	 */
	public GraphicImage(String path, String name, BufferedImage img) throws IOException {
		super(path, name, img);
		this.graph = this.img.createGraphics();
	}
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param img - a BufferedImage to store
	 * @param status - the status of the image
	 * @see apbiot.core.img.ConstructedImage.ImageStatus
	 * @throws IOException
	 */
	public GraphicImage(String path, String name, BufferedImage img, ImageStatus status) throws IOException {
		super(path,name,img,status);
		this.graph = this.img.createGraphics();
	}
	
	/**
	 * 
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param imgData - The data contained in an image
	 * @throws IOException
	 */
	public GraphicImage(String path, String name, byte[] imgData) throws IOException {
		super(path, name, imgData);
		this.graph = this.img.createGraphics();
	}
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @param imgData - The data contained in an image
	 * @param status - the status of the image
	 * @see apbiot.core.img.ConstructedImage.ImageStatus
	 * @throws IOException
	 */
	public GraphicImage(String path, String name, byte[] imgData, ImageStatus status) throws IOException {
		super(path,name,imgData,status);
		this.graph = this.img.createGraphics();
	}
	
	/**
	 * Used to merge two image together
	 * @param img - an other BufferedImage to paste on instance's image
	 * @param x - the X value where the image need to be paste
	 * @param y - the Y value where the image need to be paste
	 * @return an instance of GraphicImage
	 */
	public GraphicImage mergeImages(BufferedImage img, int x, int y) {
		graph.drawImage(img, x, y, null);
		isModified = true;
		
		return this;
	}
	
	
	
	/**
	 * Used to merge two image together
	 * @param constructedImg - an other ConstructedImage to paste on instance's image
	 * @param x - the X value where the image need to be paste
	 * @param y - the Y value where the image need to be paste
	 * @return an instance of GraphicImage
	 */
	public GraphicImage mergeImages(ConstructedImage constructedImg, int x, int y) {
		graph.drawImage(constructedImg.img, x, y, null);
		isModified = true;
		
		return this;
	}
	
	/**
	 * Used to convert image's color into only black and white
	 * @see java.awt.image.ColorConvertOp
	 * @return an instance of GraphicImage
	 */
	public GraphicImage blackAndWhite() {
		ColorConvertOp gray = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null);
		gray.filter(this.img, this.img);
		isModified = true;
		
		return this;
	}
	
	/**
	 * Used to change the brightness and increase the hue of an image
	 * @param brightness - the new brightness of the image
	 * @param hue - the new hue of the image
	 * @see java.awt.image.RescaleOp
	 * @return an instance of GraphicImage
	 */
	public GraphicImage changeBrightness(float brightness, int hue) {
		RescaleOp bright = new RescaleOp(brightness, hue, null);
		bright.filter(this.img, this.img);
		isModified = true;
		
		return this;
	}
	
	/**
	 * Used to release the graphic class using the image
	 * Can't call the save method before dispose the picture
	 */
	public void dispose() {
		graph.dispose();
		isModified = false;
	}
	
	/**
	 * Same method as dispose but return a new instance
	 * @return a new instance of a ConstructedImage with the change of the GraphicImage instance
	 * @throws IOException
	 */
	public ConstructedImage disposeAndCreateNew() throws IOException {
		graph.dispose();
		isModified = false;
		return new ConstructedImage(path, imgName, img, status);
	}

}
