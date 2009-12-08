package jade.util;

import java.awt.Color;
import java.io.Serializable;

/**
 * Represents a char with an associated color. ColoredChars are immutable once
 * created.
 */
public class ColoredChar implements Serializable, Comparable<ColoredChar> {
	private final char ch;
	private final Color color;

	public ColoredChar(char ch, Color color) {
		this.ch = ch;
		this.color = color;
	}

	public int compareTo(ColoredChar other) {
		if (color.hashCode() == other.color.hashCode())
			return ch - other.ch;
		else
			return color.hashCode() - other.color.hashCode();
	}

	/**
	 * Returns the character represented by this ColoredChar.
	 * 
	 * @return the character represented by this ColoredChar.
	 */
	public char getCh() {
		return ch;
	}

	/**
	 * Returns the color represented by this ColoredChar.
	 * 
	 * @return the color represented by this ColoredChar.
	 */
	public Color getColor() {
		return color;
	}

	@Override
	public String toString() {
		return Character.toString(ch);
	}
}
