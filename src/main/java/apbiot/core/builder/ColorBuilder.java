package apbiot.core.builder;

import java.awt.Color;
import java.util.Random;

public class ColorBuilder {
	private Color color;
	
	/**
	 * Used to define a new color
	 * @param r - the red value between 0 and 255
	 * @param g - the green value between 0 and 255
	 * @param b - the blue value between 0 and 255
	 * @return and instance of ColorBuilder
	 */
	public ColorBuilder newColor(float r, float g, float b) {
		color = new Color(r, g, b);
		return this;
	}
	
	/**
	 * Used to create a new random color
	 * @see java.util.Random
	 * @return an instance of ColorBuilder
	 */
	public ColorBuilder newRandomColor() {
		Random random = new Random();
		
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();
		
		color = new Color(r, g, b);
		return this;
	}
	
	/**
	 * Used to get the color saved
	 * @return the color defined
	 */
	public Color get() {
		return color;
	}
	
	/**
	 * Used to get the color saved in the discord4j format
	 * @see discord4j.rest.util.Color
	 * @return the color defined
	 */
	public discord4j.rest.util.Color getDiscordColor() {
		return discord4j.rest.util.Color.of(color.getRGB());
	}
}
