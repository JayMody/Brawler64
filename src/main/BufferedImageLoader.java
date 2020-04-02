package main;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BufferedImageLoader 
{
	private BufferedImage image;
	
	public BufferedImage loadImage(String path)
	{
		try 
		{
			image = ImageIO.read(getClass().getResource(path));
		}
		catch (IOException e) 
		{
			System.out.println("IO Exception, image path not found: " + path);
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) 
		{
			System.out.println("IllegalArgumentException, image path not found: " + path);
			e.printStackTrace();
		}
		
		return image;
	}
}
