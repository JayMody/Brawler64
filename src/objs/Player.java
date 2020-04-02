package objs;

import main.BufferedImageLoader;

public class Player extends Entity
{
	//   Final Variables   //
	public static final double MAX_HP = 1000.0;
	public static final double PUNCH_FACTOR = 0.05;  //  Percentage of own health damage
	public static final double KICK_FACTOR = 0.025;  //  Percentage of own health damage
	public static final double BLOCK_FACTOR = 1.00;  //  Percentage of incoming damage blocked


	//   Constructors   //
	public Player()
	{

	}
	public Player(int x, int y, String skin)
	{
		name = "Player";
		
		initialHP = MAX_HP;
		hp = initialHP;

		this.x = x;
		this.y = y;
		this.newX = x;
		this.newY = y;

		velX = 0;
		velY = 0;

		width = 50;
		height = 80;
	
		jump = new Action(0, false, "jump", 0);
		punch = new Action(0, false, "punch", hp * PUNCH_FACTOR, x, y,  width * 1, height / 2);
		kick = new Action(0, false, "kick", hp * KICK_FACTOR, x, y + height / 2, width * 3 / 2, height / 2);
		block = new Action(0, false, "block", 1.00);
		knockBack = new Action(0, false,"knockback", Entity.MAX_XVELOCITY);
		
		this.skin = skin;
		this.action = "idle";
		this.direction = "right";
		buildImage();
	}
	public Player(Player player)
	{
		super(player);
	}
	
	//  Build Image   //
	public void buildImage()
	{
		String path = "/images/player/" + skin + action + direction + ".png";
		
		BufferedImageLoader loader = new BufferedImageLoader();
		image = loader.loadImage(path);
	}
}




