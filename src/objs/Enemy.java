package objs;

import java.awt.Rectangle;
import java.util.ArrayList;

import game.Game;
import main.BufferedImageLoader;

public class Enemy extends Entity
{
	//   Final Constant Variables   //
	public static final int DIFFICULY_EASY = 0;
	public static final int DIFFICULY_MEDIUM = 1;
	public static final int DIFFICULY_HARD = 2;

	public static final int MODE_ATTACK = 0;
	public static final int MODE_DEFEND = 1;

	public static final int HALT = 0;
	public static final int MOVE_LEFT = 1;
	public static final int MOVE_RIGHT = 2;
	public static final int DO_JUMP = 3;
	public static final int DO_PUNCH = 4;
	public static final int DO_KICK = 5;
	public static final int DO_BLOCK = 6;


	//   Field   //
	protected int mode;
	protected double punchDam;
	protected double kickDam;
	protected int tickDelay;

	protected long reactionStart;
	protected int reactionTicks;
	protected int reaction;

	//   Instance Variables   //
	protected int difficulty;

	//  Delay Tick Stuff
	protected ArrayList<Player> playerStates;
	protected Player playerDelayed;
	protected boolean firstTime;


	public Enemy()
	{

	}
	public Enemy(int x, int y, String skin, int difficulty)
	{
		name = "Enemy";

		setDifficulty(difficulty);

		playerStates = new ArrayList<Player>();
		firstTime = true;

		punchDam = 0.05;
		kickDam = 0.025;

		this.mode = MODE_ATTACK;

		this.x = x;
		this.y = y;
		this.newX = x;
		this.newY = y;

		velX = 0;
		velY = 0;

		width = 50;
		height = 80;

		jump = new Action(0, false, "jump", 0);
		punch = new Action(0, false, "punch", hp * punchDam, x, y / 2,  width * 1, height / 2);
		kick = new Action(0, false, "kick", hp * kickDam, x, y / 2, width * 3 / 2, height / 2);
		block = new Action(0, false, "block", 1.00);
		knockBack = new Action(0, false,"knockback", Entity.MAX_XVELOCITY);

		this.skin = skin + difficulty;
		this.action = "idle";
		this.direction = "right";
		buildImage();
	}
	public Enemy(Enemy enemy) 
	{
		super(enemy);

		this.mode = enemy.mode;
		this.punchDam = enemy.punchDam;
		this.kickDam = enemy.kickDam;
		this.tickDelay = enemy.tickDelay;
		this.tickDelay = enemy.tickDelay;
	}


	//   Tick Method
	public void tick(Game game, long counter, Player player)
	{
		//   Delay
		if (firstTime)
		{
			for (int x = 0; x < tickDelay; x++)
			{
				playerStates.add(new Player(player));
			}

			firstTime = false;
		}
		else
		{
			playerStates.add(0, new Player(player));
			playerStates.remove(tickDelay);
		}

		playerDelayed = playerStates.get(tickDelay - 1);



		// Set Up
		boolean actionBool = false;  // Resets actionBool from last tick
		Rectangle p = new Rectangle((int)(playerDelayed.getX()), (int)(playerDelayed.getY()), playerDelayed.getWidth(), playerDelayed.getHeight());
		Rectangle rightClose = new Rectangle((int)(x + width), (int) y, 75, game.height);
		Rectangle leftClose = new Rectangle((int)(x - 75), (int) y, 75, game.height);

		Rectangle aboveBelow = new Rectangle((int)(x - 5), 0, width + 10, game.height);

		double displacementLeft = x - playerDelayed.getX(); // Dispacement from the leftmost side of the player
		double displacementRight = x - (playerDelayed.getX() + playerDelayed.getWidth()); // Displacement from the rightmost side of the player


		//Check switch mode
		if (counter % 120 == 0) 
		{
			double switchChance = Math.random();

			if (switchChance <= ModeFactors.update(this, playerDelayed))
			{
				if (mode == MODE_DEFEND)
				{
					mode = MODE_ATTACK;
					ModeFactors.switchMode(mode);
				}

			}
			else
			{
				if (mode == MODE_ATTACK)
				{
					mode = MODE_DEFEND;
					ModeFactors.switchMode(mode);
				}
			}
		}



		if (mode == MODE_ATTACK)
		{
			actionBool = punch.isActive() || kick.isActive() || block.isActive();

			double punchChance = 0.12;
			double kickChance = 0.06;
			double jumpStratChance = 0.01;
			double jumpChance = 0.006;
			double reactiveChance = 0.02;

			if (difficulty == DIFFICULY_EASY)
			{
				punchChance = 0.10;
				kickChance = 0.05;
				jumpStratChance = 0.008;
				jumpChance = 0.006;
				reactiveChance = 0.02;
			}
			else if (difficulty == DIFFICULY_HARD)
			{
				punchChance = 0.13;
				kickChance = 0.05;
				jumpStratChance = 0.012;
				jumpChance = 0.007;
				reactiveChance = 0.04;
			}

			if (displacementLeft > 0)
			{
				//direction = "left";
			}
			else
			{
				//direction = "right";
			}


			if (counter < reactionStart + reactionTicks)
			{
				if (reaction == HALT)
				{
					halt();
				}
				else if (reaction == MOVE_LEFT)
				{
					moveLeft();
				}
				else if (reaction == MOVE_RIGHT)
				{
					moveRight();
				}
			}
			else
			{
				reactionStart = counter;

				if(!hit(p, leftClose) && displacementLeft < -50)
				{
					reaction = MOVE_RIGHT;
					reactionTicks = 2;
				}
				else if((!hit(p, rightClose) && displacementRight > 50))
				{
					reaction = MOVE_LEFT;
					reactionTicks = 2;
				}
				else if (hit(p, rightClose))
				{
					if (playerDelayed.getDirection().equals("right"))
					{
						jumpStratChance = 0.05;
					}

					if (Math.random() < jumpStratChance)
					{
						reaction = MOVE_RIGHT;
						reaction = MOVE_LEFT;
						reactionTicks = 30;

						if (!jump.isActive())
						{
							doJump(counter);
						}
					}
					else if (Math.random() < jumpStratChance / 2)
					{
						reaction = MOVE_LEFT;
						reactionTicks = 30;
					}

				}
				else if (hit(p, leftClose))
				{
					if (playerDelayed.getDirection().equals("left"))
					{
						jumpStratChance = 0.05;
					}

					if (Math.random() < jumpStratChance)
					{
						reaction = MOVE_LEFT;
						reactionTicks = 30;

						if (!jump.isActive())
						{
							doJump(counter);
						}
					}	
					else if (Math.random() < jumpStratChance / 2)
					{
						reaction = MOVE_RIGHT;
						reactionTicks = 30;
					}
				}
				else if (hit(aboveBelow, p))
				{
					if (x < game.height / 2)
					{
						reaction = MOVE_RIGHT;
						reactionTicks = 40;
					}
					else
					{
						reaction = MOVE_LEFT;
						reactionTicks = 40;
					}
				}
				else
				{
					reaction = HALT;
				}

				//  Jump
				if (Math.random() < jumpChance &&  !jump.isActive())
				{        
					doJump(counter);
				}
				else if (Math.random() < reactiveChance && player.getJump().isActive() && !jump.isActive())
				{
					doJump(counter);
				}
			}

			// Attack When In Range
			if (hit(kick.hitBox, p) && !actionBool) // If player is in punching range
			{
				if (Math.random() <= punchChance) // 5% chance per tick the AI will punch
				{
					doPunch(counter);
				}
				else if (Math.random() <= kickChance) //2% chance per tick the AI will kick
				{
					doKick(counter);
				}
			}
			else if (hit(kick.hitBox, p) && !actionBool) // If player is in kicking range
			{
				if (Math.random() <= kickChance) // 5% chance per tick the AI will kick
				{
					doKick(counter);
				}
			}     
		}
		//DEFEND MODE
		else if (mode == MODE_DEFEND)
		{
			actionBool = punch.isActive() || kick.isActive() || block.isActive() || jump.isActive();
			double relativeX =  Math.abs((displacementRight + displacementLeft) / 2);

			if (counter < reactionStart + reactionTicks)
			{
				if (reaction == HALT)
				{
					halt();
				}
				else if (reaction == MOVE_LEFT)
				{
					moveLeft();
				}
				else if (reaction == MOVE_RIGHT)
				{
					moveRight();
				}
			}
			else
			{
				reactionStart = counter;

				if ((x >= (game.width - width - 250) || x <= 250) && (displacementLeft >= -300 && displacementRight <= 300)) 
				{
					if (!jump.isActive() && !block.isActive())
					{
						doJump(counter);
					}

					if (x <= 150)
					{
						reaction = MOVE_RIGHT;
						reactionTicks = 30;
					}
					else
					{
						reaction = MOVE_LEFT;
						reactionTicks = 30;
					}
				}
				else if (!actionBool)
				{
					int min = (int)(Math.random() * 150.0 + 400.0);
					int max = (int)(Math.random() * 100.0 + 600.0);

					double idleChance =  0.2;

					if (idleChance >= Math.random() && relativeX > 300.0)
					{
						reaction = HALT;
						reactionTicks = 10;
					}
					else
					{
						if (displacementLeft >= 0) 
						{
							if(relativeX <= min && relativeX >= 0)
							{
								reaction = MOVE_RIGHT;
								reactionTicks = 5;
							}
							else if(relativeX >= max)
							{
								reaction = MOVE_LEFT;
								reactionTicks = 5;
							}
						}

						if (displacementLeft <= 0) 
						{
							if(relativeX <= min && relativeX >= 0)
							{
								reaction = MOVE_LEFT;
								reactionTicks = 5;
							}
							else if(relativeX >= max)
							{
								reaction = MOVE_RIGHT;
								reactionTicks = 5;
							}
						}
					}
				}
			}


			// this is the block and attack functuon for the defensive AI mode while player is within range
			if((hit(leftClose, p) || hit(rightClose, p)) && (playerDelayed.getPunch().isActive() || playerDelayed.getKick().isActive()) && !jump.isActive()) 
			{
				double blockChance = 0.30;

				if (difficulty == DIFFICULY_EASY)
				{
					blockChance = 0.20;
				}
				else if (difficulty == DIFFICULY_HARD)
				{
					blockChance = 0.45;
				}

				if (Math.random() < blockChance)
				{
					doBlock(counter);
				}
			}
		}

		// Ticks the enemy
		super.tick(game, counter, player);
	}

	public int getMode() {
		return mode;
	}
	public double getPunchDam() {
		return punchDam;
	}
	public double getKickDam() {
		return kickDam;
	}
	public int getDifficulty() {
		return difficulty;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public void setPunchDam(double punchDam) {
		this.punchDam = punchDam;
	}
	public void setKickDam(double kickDam) {
		this.kickDam = kickDam;
	}
	public void setDifficulty(int difficulty) 
	{
		if (difficulty == DIFFICULY_EASY)
		{
			name = "Dark Luster";
			initialHP = 1000;
			hp = 1000;
			tickDelay = 15;
		}
		else if (difficulty == DIFFICULY_MEDIUM)
		{
			name = "Masseria";
			initialHP = 1000;
			hp = 1000;
			tickDelay = 10;
		}
		else if(difficulty == DIFFICULY_HARD)
		{
			name = "Ricardo the Cyborg";
			initialHP = 1200;
			hp = 1200;
			tickDelay = 5;
		}

		this.difficulty = difficulty;
	}

	//  Build Image   //
	public void buildImage()
	{
		String path = "/images/enemy/" + skin + action + direction + ".png";

		BufferedImageLoader loader = new BufferedImageLoader();
		image = loader.loadImage(path);
	}
}