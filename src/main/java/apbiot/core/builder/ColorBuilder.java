package apbiot.core.builder;

import java.util.Random;

import discord4j.rest.util.Color;

public class ColorBuilder {
	private Color color;
	
	/**
	 * Used to define a new color
	 * @param r - the red value between 0 and 255
	 * @param g - the green value between 0 and 255
	 * @param b - the blue value between 0 and 255
	 * @return and instance of ColorBuilder
	 */
	public ColorBuilder of(float r, float g, float b) {
		this.color = Color.of(r, g, b);
		return this;
	}
	
	/**
	 * Used to define a new color
	 * @param hexValue - the hexadecimal value of the color
	 * @return and instance of ColorBuilder
	 */
	public ColorBuilder of(int hexValue) {
		this.color = Color.of(hexValue);
		return this;
	}
	
	/**
	 * Used to create a new random color
	 * @see java.util.Random
	 * @return an instance of ColorBuilder
	 */
	public ColorBuilder randomColor() {
		Random random = new Random();
		
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();
		
		this.color = Color.of(r, g, b);
		return this;
	}
	
	/**
	 * Used to get the color saved
	 * @return the color defined
	 */
	public Color get() {
		return this.color;
	}
	
	public java.awt.Color getAsJavaColor() {
		return new java.awt.Color(this.color.getRGB());
	}
	
	public Color copy() {
		return Color.of(this.color.getRGB());
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.color.equals(obj);
	}
}
