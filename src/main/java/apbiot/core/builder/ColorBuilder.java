package apbiot.core.builder;

import java.util.Random;

import discord4j.rest.util.Color;

public class ColorBuilder {
	private Color color;
	
	private ColorBuilder(float r, float g, float b) { 
		this.color = Color.of(r,g,b);
	}
	
	private ColorBuilder(int hexValue) { 
		this.color = Color.of(hexValue);
	}
	
	private ColorBuilder(Color clr) { 
		this.color = clr;
	}
	
	
	/**
	 * Used to define a new color
	 * @param r The red value between 0 and 255
	 * @param g The green value between 0 and 255
	 * @param b The blue value between 0 and 255
	 * @return and instance of ColorBuilder
	 */
	public static ColorBuilder of(float r, float g, float b) {
		return new ColorBuilder(r,g,b);
	}
	
	/**
	 * Used to define a new color
	 * @param hexValue The hexadecimal value of the color
	 * @return and instance of ColorBuilder
	 */
	public static ColorBuilder of(int hexValue) {
		return new ColorBuilder(hexValue);
	}
	
	/**
	 * Used to create a new random color
	 * @see java.util.Random
	 * @return an instance of ColorBuilder
	 */
	public static ColorBuilder randomColor() {
		final Random random = new Random();
		
		float r = random.nextFloat();
		float g = random.nextFloat();
		float b = random.nextFloat();
		
		return new ColorBuilder(r,g,b);
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
	
	public ColorBuilder copy() {
		return new ColorBuilder(this.color);
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Color && this.color.equals((Color)obj);
	}
}
