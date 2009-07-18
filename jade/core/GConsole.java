package jade.core;

import jade.fov.Camera;
import jade.util.ColoredChar;
import jade.util.Coord;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.TreeMap;
import javax.imageio.ImageIO;

/**
 * GConsole takes a regular Jade Console and adds the capability to use
 * graphical tiles instead of ColoredChar. This is done by using the camera
 * methods and by registering substitute tiles for ColoredChars. Otherwise, this
 * console behaves exactly the same way as a basic Jade Console.
 */
public class GConsole extends Console
{
	private Map<Coord, CharPair> imageBuffer;
	private Map<Coord, CharPair> imageSaved;
	private Map<ColoredChar, Image> images;

	/**
	 * Constructs a default GConsole. As opposed to the normal Jade Console, the
	 * tileWidth matches the tileHeight. The default tile size is 32x32 pixels
	 * with a width and height of 24 tiles.
	 */
	public GConsole()
	{
		this(32, 24, 24);
	}

	/**
	 * Constructs a GConsole with a specified size.
	 * @param tileSize the number of pixels wide and high in pixels of each
	 * individual tile.
	 * @param width the number of tiles wide the GConsole starts with
	 * @param height the number of tiles high the GConsole starts with
	 */
	public GConsole(int tileSize, int width, int height)
	{
		super(tileSize, tileSize, width, height);
		imageBuffer = new TreeMap<Coord, CharPair>();
		imageSaved = new TreeMap<Coord, CharPair>();
		images = new TreeMap<ColoredChar, Image>();
	}

	/**
	 * Returns a new default GConsole which has been placed inside a JFrame.
	 * @param frameTitle the title of the frame
	 * @return a new default GConsole which has been placed inside a JFrame
	 */
	public static GConsole getFramedConsole(String frameTitle)
	{
		GConsole console = new GConsole();
		frameConsole(console, frameTitle);
		return console;
	}

	/**
	 * Returns a new GConsole of the specified size which has been placed inside a
	 * JFrame.
	 * @param frameTitle the title of the JFrame
	 * @param tileSize the size in pixels of each tile
	 * @param width the width of the GConsole
	 * @param height the height of the GConsole
	 * @return a new GConsole placed inside a JFrame
	 */
	public static GConsole getFramedConsole(String frameTitle, int tileSize,
			int width, int height)
	{
		GConsole console = new GConsole(tileSize, width, height);
		frameConsole(console, frameTitle);
		return console;
	}

	@Override
	public void buffChar(Coord coord, ColoredChar ch)
	{
		super.buffChar(coord, ch);
		imageBuffer.remove(coord);
	}

	/**
	 * Performs the same as in a normal JadeConsole, only that any registered
	 * images will be used in place of the ascii representation of each tile.
	 */
	@Override
	public void buffCamera(Camera camera, int x, int y, Coord coord, char ch,
			Color color)
	{
		int offX = x - camera.x();
		int offY = y - camera.y();
		CharPair pair = new CharPair(camera.world().tile(coord).look(),
				new ColoredChar(ch, color));
		imageBuffer.put(coord.getTranslated(offX, offY), pair);
	}

	/**
	 * Performs the same as in a normal JadeConsole, only that any registered
	 * images will be used in place of the ascii representation of each tile.
	 */
	public void buffCamera(Camera camera, int x, int y)
	{
		int offX = x - camera.x();
		int offY = y - camera.y();
		World world = camera.world();
		for(Coord coord : camera.getFoV())
		{
			Coord off = coord.getTranslated(offX, offY);
			CharPair pair = new CharPair(world.tile(coord).look(), world.look(coord));
			imageBuffer.put(off, pair);
		}
	}

	@Override
	public void saveBuffer()
	{
		super.saveBuffer();
		imageSaved.clear();
		imageSaved.putAll(imageBuffer);
	}

	@Override
	public void recallBuffer()
	{
		super.recallBuffer();
		imageBuffer.clear();
		imageBuffer.putAll(imageSaved);
	}

	@Override
	public void clearBuffer()
	{
		super.clearBuffer();
		imageBuffer.clear();
	}

	/**
	 * Registers an image to replace a specific ascii representation of a tile. It
	 * works by finding the tile in the file tileSet at (x,y) of the tileSize of
	 * this GConsole. Any time the camera is then used, this tile will replace it.
	 * @param tileSet the file containing the tile set.
	 * @param x the x-coordinate of the specific tile wanted
	 * @param y the y-coordinate of the specific tile wanted
	 * @param ch the char of the ColoredChar to replace
	 * @param color the color of the ColoredChar to replace
	 * @return true if the substitution was successful
	 */
	public boolean registerImage(String tileSet, int x, int y, char ch,
			Color color)
	{
		try
		{
			BufferedImage tiles = ImageIO.read(new File(tileSet));
			BufferedImage tile = tiles.getSubimage(x * tileHeight, y * tileHeight,
					tileHeight, tileHeight);
			images.put(new ColoredChar(ch, color), tile);
			return true;
		}
		catch(IOException e)
		{
			return false;
		}
	}
	
	@Override
	public void paintComponent(Graphics page)
	{
		super.paintComponent(page);
		try
		{
			for(Coord coord : imageBuffer.keySet())
			{
				CharPair pair = imageBuffer.get(coord);
				if(images.containsKey(pair.bgChar))
					page.drawImage(images.get(pair.bgChar), coord.x() * tileWidth, coord
							.y()
							* tileHeight, tileWidth, tileHeight, null);
				if(images.containsKey(pair.fgChar))
					page.drawImage(images.get(pair.fgChar), coord.x() * tileWidth, coord
							.y()
							* tileHeight, tileWidth, tileHeight, null);
				else
				{
					page.setColor(pair.fgChar.color());
					page.drawString(pair.fgChar.toString(), coord.x() * tileWidth, (coord
							.y() + 1)
							* tileHeight);
				}
			}
		}
		catch(ConcurrentModificationException dontWorry)
		{
		}
		catch(NullPointerException dontWorry)
		{
		}
	}

	private class CharPair
	{
		private ColoredChar bgChar;
		private ColoredChar fgChar;

		public CharPair(ColoredChar bgChar, ColoredChar fgChar)
		{
			this.bgChar = bgChar;
			this.fgChar = fgChar;
		}
	}
}