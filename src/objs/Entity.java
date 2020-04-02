package objs;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

import game.Game;
import main.BufferedImageLoader;
import main.Sounds;

public class Entity 
{
	public static final int HEIGHT = 80;
	public static final int WIDTH = 50;
	public static final int JUMP_VELOCITY = -22;
	public static final double MAX_XVELOCITY = 12;
	public static final double MAX_YVELOCITY = 20;
	public static final double Y_ACCELERATION = -1.0;

	//   Instance Variables   //
	protected String name;

	protected double hp;
	protected double initialHP;

	protected double x;
	protected double y;
	protected int newX;
	protected int newY;

	protected double velX;
	protected double velY;

	protected int width;
	protected int height;

	protected String skin;
	protected String action;
	protected String direction;
	protected BufferedImage image;

	//   Actions   //
	protected Action jump;
	protected Action punch;
	protected Action kick;
	protected Action block;  
	protected Action knockBack;
	
	//   Sounds   //
	protected static Sounds hitSFX = new Sounds(Sounds.HIT);;
	protected static Sounds blockSFX = new Sounds(Sounds.BLOCK);
	protected static Sounds swingSFX = new Sounds(Sounds.SWING);
	
	//   Image Loader   //
	private static BufferedImageLoader loader = new BufferedImageLoader();


	//   Constructors   //
	public Entity()
	{
		
	}
	public Entity(Entity entity)
	{
		this.name = entity.name;
		this.initialHP = entity.initialHP;
		this.hp = entity.hp;
		this.x = entity.x;
		this.y = entity.y;
		this.newX = entity.newX;
		this.newY = entity.newY;
		this.velX = entity.velX;
		this.velY = entity.velY;
		this.width = entity.width;
		this.width = entity.width;
		this.height = entity.height;
		this.jump = entity.jump;
		this.punch = entity.punch;
		this.kick = entity.kick;
		this.block = entity.block;
		this.skin = entity.skin;
		this.action = entity.action;
		this.direction = entity.direction;
		buildImage();
	}

	//   Build Image   //
	public void buildImage()
	{
		String path = "/" + skin + action + direction + ".png";
		image = loader.loadImage(path);
	}

	//   Tick   //
	public void tick(Game game, long counter, Entity entity)
	{
		// Initial Stuff
		Rectangle e = new Rectangle((int)(entity.getX()), (int)(entity.getY()), entity.getWidth(), entity.getHeight());	

		// Set Hitboxes
		if (direction.equals("right"))
		{
			punch.hitBox.setLocation((int)x + width, (int)(y));
			kick.hitBox.setLocation((int)x + width, (int)(y + height / 2));
		}
		else
		{
			punch.hitBox.setLocation((int)x - punch.hitBox.width, (int)(y));
			kick.hitBox.setLocation((int)x - kick.hitBox.width, (int)(y + height / 2));
		}

		// Punch
		if (punch.isActive()) // If the user activated the punch button and a punch is currently in action
		{	
			punch(counter, entity);
		}
		// Kick
		else if(kick.isActive())
		{
			kick(counter, entity);
		}
		// Block
		else if (block.isActive())
		{
			block(counter, entity);
		}
		// Movement Else
		else
		{
			if (y < game.ground)
			{
				action = "jump"; 
			}
			else if (velX != 0)
			{
				action = "move";
			}
			else
			{
				action = "idle";
			}

			if (velX > 0)
			{
				direction = "right";
			}
			else if (velX < 0)
			{
				direction = "left";
			}
		}

		if (knockBack.isActive())
		{
			knockBack(counter, entity);
		}
		
		newX = (int) (x + velX);
		newY = (int) (y + velY);

		// If jump is active
		if (jump.isActive())
		{
			jump(game, counter);
		}
		

		// Boolean stuff
		Rectangle p = new Rectangle(newX, newY, width, height);
		boolean contact = hit(p, e);
		boolean xBounds = newX > 0 && newX < game.width - width;
		boolean yBounds = newY > 0 && newY < game.height - height;


		// Boolean checks
		if (xBounds && !contact)
		{
			x = newX;
		}
		if (yBounds)
		{
			if (newY <= game.ground)
			{
				if(contact && y < game.ground)
				{
					doJump(counter);
					entity.setVelY(-1 * JUMP_VELOCITY);
				}

				y = newY;
			}
			else
			{
				y = game.ground;
			}
		}

		// Direction stuff
		if (direction.equals("right"))
		{
			punch.hitBox.setLocation((int)x + width, (int)(y));
			kick.hitBox.setLocation((int)x + width, (int)(y + height / 2));
		}
		else
		{
			punch.hitBox.setLocation((int)x - punch.hitBox.width, (int)(y));
			kick.hitBox.setLocation((int)x - kick.hitBox.width, (int)(y + height / 2));
		}

		this.buildImage();
	}


	//   Getters and Setters   //
	public String getName() {
		return name;
	}
	public double getHp() {
		return hp;
	}
	public double getInitialHP() {
		return initialHP;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int getNewX() {
		return newX;
	}
	public int getNewY() {
		return newY;
	}
	public double getVelX() {
		return velX;
	}
	public double getVelY() {
		return velY;
	}
	public int getWidth() {
		return width;
	}
	public int getHeight() {
		return height;
	}
	public String getSkin() {
		return skin;
	}
	public String getAction(){
		return action;
	}
	public String getDirection() {
		return direction;
	}
	public BufferedImage getImage() {
		return image;
	}
	public Action getJump() {
		return jump;
	}
	public Action getPunch() {
		return punch;
	}
	public Action getKick() {
		return kick;
	}
	public Action getBlock() {
		return block;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setHp(double hp) {
		this.hp = hp;
	}
	public void setInitialHP(double initialHP) {
		this.initialHP = initialHP;
	}
	public void setX(double x) {
		this.x = x;
	}
	public void setY(double y) {
		this.y = y;
	}
	public void setNewX(int newX) {
		this.newX = newX;
	}
	public void setNewY(int newY) {
		this.newY = newY;
	}
	public void setVelX(double velX) {
		this.velX = velX;
	}
	public void setVelY(double velY) {
		this.velY = velY;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void setSkin(String skin) {
		this.skin = skin;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	public void setJump(Action jump) {
		this.jump = jump;
	}
	public void setPunch(Action punch) {
		this.punch = punch;
	}
	public void setKick(Action kick) {
		this.kick = kick;
	}
	public void setBlock(Action block) {
		this.block = block;
	}

	public static final int haltFactor = 2; //MUST BE A FACTOR OF MAX_XVELOCITY

	//   Movement Functions
	public void moveRight()
	{
		if (velX < 0)
		{
			velX += haltFactor * 2;
		}
		else if (velX < MAX_XVELOCITY)
		{
			velX ++;
		}
	}
	public void moveLeft()
	{
		if (velX > 0)
		{
			velX -= haltFactor * 2;
		}
		else if (velX > (-1 * MAX_XVELOCITY))
		{
			velX--;
		}
	}
	public void halt()
	{
		if (velX >= haltFactor)
		{
			velX -= haltFactor;
		}
		else if (velX <= -haltFactor)
		{
			velX += haltFactor;
		}
		else
		{
			velX = 0;
		}
	}
	
	
	//   Actions   //
	public void jump(Game game, long counter)
	{
		velY = velY + 1;
		newY = (int) (y + velY);

		if (newY >= game.ground) // Runs when the player reaches the ground once again
		{
			jump.setActive(false); // The jump is complete
			velY = 0;
			newY = game.ground;
		}
	}
	public void punch(long counter, Entity entity)
	{
		Rectangle e = new Rectangle((int)(entity.getX()), (int)(entity.getY()), entity.getWidth(), entity.getHeight());
		
		if (counter == punch.getTicks() + 10) // If 1/6th of a second has passed since the start of the punch
		{
			action = "punch"; // Show the punch animation

			if (hit(punch.hitBox, e))
			{
				entity.damage(punch.getValue());
				entity.doKnockBack(counter);
			}
			else
			{
				swingSFX.play();
			}
		}
		else if (counter == punch.getTicks() + 20) // If 1/6th of a second has passed since the start of the punch
		{
			action = "idle"; // Show the idle animation
		}
		else if (counter == punch.getTicks() + 30) // If 3/6th of a second has passed since the start of the punch
		{
			punch.setActive(false); // Stop the punch action
		}
		
		if (action.equals("punch"))
		{
			punch.activateActionImage(direction);
		}
		else
		{
			punch.disableActionImage();
		}

	}
	public void kick(long counter, Entity entity)
	{
		Rectangle e = new Rectangle((int)(entity.getX()), (int)(entity.getY()), entity.getWidth(), entity.getHeight());

		if (counter == kick.getTicks() + 10)
		{
			action = "kick";
			
			if (hit(kick.hitBox, e))
			{
				entity.damage(kick.getValue());
				entity.doKnockBack(counter);
			}
			else
			{
				swingSFX.play();
			}
		}
		else if (counter == kick.getTicks() + 20)
		{
			action = "idle";
		}
		else if (counter == kick.getTicks() + 30)
		{
			kick.setActive(false);
		}
		
		if (action.equals("kick"))
		{
			kick.activateActionImage(direction);
		}
		else
		{
			kick.disableActionImage();
		}
	}
	public void block(long counter, Entity entity)
	{
		if (counter > block.getTicks() + 5 && counter < block.getTicks() + 30)
		{
			velX = 0;
			action = "block";
			block.setValue(0.00);
		}
		else if (counter == block.getTicks() + 30)
		{
			action = "idle";
		}
		else if (counter == block.getTicks() + 50)
		{
			block.setActive(false);
			block.setValue(1.00);
		}
	}
	protected void knockBack(long counter, Entity entity)
	{
		if (counter < knockBack.getTicks() + 20 || knockBack.getValue() > 0)
		{
			int blockReduction = 1;
			if (block.isActive())
			{
				blockReduction = 2;
			}
			
			if (entity.getX() < x)
			{
				velX = knockBack.getValue() / blockReduction;
			}
			else
			{
				velX = -1 * (knockBack.getValue() / blockReduction);
			}
			
			knockBack.setValue(knockBack.getValue() - 1);
		}
		else
		{
			knockBack.setActive(false);
		}
	}


	//   Do Actions   //
	public void doJump(long counter)
	{
		jump.setTicks(counter); // Initializes the time the jump started
		jump.setActive(true); // Tells the program that a jump is currently in progress
		velY = JUMP_VELOCITY; // Sets the y velocity of the player to make them go up
	}

	public void doPunch(long counter)
	{
		if (!punch.isActive())
		{
			punch.setTicks(counter);
			punch.setActive(true);
		}
	}

	public void doKick(long counter)
	{ 
		if (!kick.isActive())
		{
			kick.setTicks(counter);
			kick.setActive(true);
		}
	}

	public void doBlock(long counter)
	{
		if (!block.isActive())
		{
			block.setTicks(counter);
			block.setActive(true);
		}
	}

	public void doKnockBack(long counter)
	{
		if (!knockBack.isActive())
		{
			knockBack.setTicks(counter);
			knockBack.setActive(true);
			knockBack.setValue(MAX_XVELOCITY * 2);
		}
	}

	
	//   Collision   //
	protected static boolean hit(Rectangle r1, Rectangle r2) 
	{
		return r1.x < r2.x + r2.width && r1.x + r1.width > r2.x && r1.y < r2.y + r2.height && r1.y + r1.height > r2.y;
	}


	//   Do Damage   //
	protected void damage(double incoming)
	{
		if (block.getValue() == 0.00)
		{
			blockSFX.play();
		}
		else
		{
			hitSFX.play();
		}
		
		hp = hp - (block.getValue() * incoming);
	}
}




