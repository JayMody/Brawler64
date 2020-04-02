package objs;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import main.BufferedImageLoader;

public class Action 
{
	protected long ticks;
	
	protected boolean active;
	
	protected String name;
	protected double value;
	protected Rectangle hitBox;
	protected BufferedImage actionImage;
	
	
	private static BufferedImageLoader loader = new BufferedImageLoader();
	
	
	public BufferedImage getActionImage() {
		return actionImage;
	}
	public void setActionImage(BufferedImage actionImage)
	{
		this.actionImage = actionImage;
	}
	public void activateActionImage(String direction)
	{
		actionImage = loader.loadImage("/images/common/" + name + direction + ".png");
	}
	public void disableActionImage()
	{
		this.actionImage = loader.loadImage("/images/common/null.png");
	}
	
		public Action(long ticks, boolean active, String name, double value)
	{
		this.ticks = ticks;
		this.active = active;
		this.name = name;
		this.value = value;
		this.hitBox = null;
		this.actionImage = loader.loadImage("/images/common/null.png");

	}
	public Action(long ticks, boolean active, String name, double value, int x, int y, int width, int height)
	{
		this.ticks = ticks;
		this.active = active;
		this.name = name;
		this.value = value;
		this.hitBox = new Rectangle(x, y, width, height);
		this.actionImage = loader.loadImage("/images/common/null.png");
	}
	
	public long getTicks() 
	{
		return ticks;
	}
	public boolean isActive() 
	{
		return active;
	}
	public String getName() 
	{
		return name;
	}
	public double getValue() 
	{
		return value;
	}
	public Rectangle getHitBox() 
	{
		return hitBox;
	}
	
	public void setTicks(long ticks) 
	{
		this.ticks = ticks;
	}
	public void setActive(boolean active) 
	{
		this.active = active;
	}
	public void setName(String name) 
	{
		this.name = name;
	}
	public void setValue(double damage) 
	{
		this.value = damage;
	}
	public void setHitBox(Rectangle hitBox) 
	{
		this.hitBox = hitBox;
	}
}
