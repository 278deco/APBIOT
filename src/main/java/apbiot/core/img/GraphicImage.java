package apbiot.core.img;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.io.IOException;
import java.nio.file.Path;

import io.netty.util.concurrent.BlockingOperationException;

/**
 * A class used to apply modification on all types of pictures
 * @see apbiot.core.img.ConstructedImage
 * @author 278deco
 * @deprecated 4.0
 */
public class GraphicImage {

	private ConstructedImage constructedImg;
	private Graphics2D graph;
	private boolean isDisposed;
	
	/**
	 * @param path - the path of the image
	 * @param name - the name of the image (and his extension)
	 * @throws IOException
	 */
	public GraphicImage(ConstructedImage image) throws IOException {
		this.constructedImg = image;
		this.graph = this.constructedImg.getImage().createGraphics();
		this.isDisposed = true;
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
		isDisposed = false;
		
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
		graph.drawImage(constructedImg.getImage(), x, y, null);
		isDisposed = false;
		
		return this;
	}
	
	/**
	 * Used to convert image's color into only black and white
	 * @see java.awt.image.ColorConvertOp
	 * @return an instance of GraphicImage
	 */
	public GraphicImage blackAndWhite() {
		ColorConvertOp gray = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY),null);
		gray.filter(this.constructedImg.getImage(), this.constructedImg.getImage());
		isDisposed = false;
		
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
		bright.filter(this.constructedImg.getImage(), this.constructedImg.getImage());
		isDisposed = false;
		
		return this;
	}
	
	/**
	 * Used to release the graphic class using the image
	 * Can't call the save method before dispose the picture
	 */
	public void dispose() {
		graph.dispose();
		isDisposed = true;
	}
	
	public void saveImage() throws IOException {
		if(!this.isDisposed) throw new BlockingOperationException("Can't save an image without call disposal method");
//		constructedImg.saveImage();
	}
	
	public void saveImage(Path path) throws IOException {
		if(!this.isDisposed) throw new BlockingOperationException("Can't save an image without call disposal method");
//		constructedImg.saveImage(path);
	}

}
