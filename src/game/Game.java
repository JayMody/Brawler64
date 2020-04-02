//   Package   //
package game;

//   Imports   //
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.SoftBevelBorder;

import main.BufferedImageLoader;
import main.Main;
import main.Sounds;
import objs.Player;
import objs.Enemy;



//   Game Class   //
public class Game implements Runnable
{	
	//  Final Variables   //
	public static final String TITLE = "Brawler64";
	public static final double TICKS_PER_SECOND = 60.0;
	public static final double MILLISECOND = TICKS_PER_SECOND / 100.0;

	//   Utilities (Static)  //
	private static BufferedImageLoader loader = new BufferedImageLoader();

	//   Objects   //
	private Player player;
	private Enemy enemy;

	//   Game Stuff   //
	private int wins;
	private int losses;
	private int round;
	private String roundText;
	private long roundCounter;

	public int playerStart;
	public int enemyStart;
	public int ground;

	//   Graphics Variables   //
	private Canvas canvas;
	private BufferStrategy bs;
	private Graphics g;

	//   Images   //
	private BufferedImage background;

	//   Loop Variables   //
	public long counter;

	private Thread thread;
	private boolean running;
	private long timer;

	//  Listener Stuff   //
	private boolean actionBool;
	private static boolean keyRight;
	private static boolean keyLeft;
	private int keyReleased;
	private int keyPressed;

	//   Frame Stuff   //
	public int screen;
	public int width;
	public int height;
	public boolean fullscreen;

	//   Music and SFX   //
	Sounds fightMusic;
	Sounds round1;
	Sounds round2;
	Sounds round3;
	Sounds hit;
	Sounds button;
	Sounds win;
	Sounds lose;

	//   Menu Stuff   //
	private JFrame frame;
	private JPanel panelGame;
	private JPanel panelMainMenu, panelSelectMenu, panelHelpMenu;
	private JPanel subPanelMain, panelPause;
	private JScrollPane subPanelHelp;
	private JLabel lblBrawler;
	private JButton btnExit;
	private JButton btnHelp;
	private JButton btnNewGame;
	private JTextArea textArea;
	private JButton btnBackToMenuHelp;
	private JPanel subPanelSelect;
	private JButton btnBackToMenuSelect;
	private JButton btnStartGame;
	private JButton btnResume, btnExitToMenuGame;
	private JLabel pauseMessage;
	private JLabel lblBackgroundMain, lblBackgroundHelp, lblBackgroundSelect;
	private JComboBox<String> comboBoxPlayer;
	private JComboBox<String> comboBoxEnemy;
	private JTextField textFieldName;
	private JLabel lblEnemyName;
	private JLabel playerPreview;
	private JLabel enemyPreview;
	private JLabel lblEnterName;
	private JLabel lblChooseDifficulty;
	private JLabel lblVs;
	private JLabel lblChooseName;


	//   Initialize   //
	public void initFrame(int screen, boolean fullscreen, int width, int height, boolean sound)
	{	
		this.screen = screen;
		this.fullscreen = fullscreen;
		this.width = width;
		this.height = height;

		ground = (int) (height * 0.80);
		playerStart = (int)(width * 0.25);
		enemyStart = (int)(width - (width * 0.25));

		//   Game Canvas
		canvas = new Canvas();
		canvas.setPreferredSize(new Dimension(width, height));
		canvas.setMaximumSize(new Dimension(width, height));
		canvas.setMinimumSize(new Dimension(width, height));
		canvas.setBounds(0, 0, width, height);
		canvas.setVisible(true);
		canvas.addKeyListener(new KeyInput(this));

		// Icons
		ArrayList<BufferedImage> icons = new ArrayList<BufferedImage>();
		icons.add(loader.loadImage("/images/common/icon16.png"));
		icons.add(loader.loadImage("/images/common/icon32.png"));
		icons.add(loader.loadImage("/images/common/icon64.png"));
		icons.add(loader.loadImage("/images/common/icon128.png"));
		icons.add(loader.loadImage("/images/common/icon256.png"));

		//   Frame   //
		frame = new JFrame();
		frame.setTitle(TITLE);
		frame.setResizable(false);
		frame.setMaximumSize(new Dimension(width, height));
		frame.setMinimumSize(new Dimension(width, height));
		frame.setBounds(0, 0, width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new CardLayout(0, 0));
		frame.requestFocus();
		frame.setIconImages(icons);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		if (fullscreen)
		{

			frame.setUndecorated(true);
		}
		frame.setVisible(true);
		frame.setLocation(Main.gs[screen].getDefaultConfiguration().getBounds().x, frame.getY());

		loadSounds(sound);

		//   Game Running   //
		running = false;
	}

	private void loadSounds(boolean sound)
	{
		Sounds.setOn(sound);

		fightMusic = new Sounds(Sounds.FIGHT_MUSIC);
		round1 = new Sounds(Sounds.ROUND1);
		round2 = new Sounds(Sounds.ROUND2);
		round3 = new Sounds(Sounds.ROUND3);
		button = new Sounds(Sounds.BUTTON);
		win = new Sounds(Sounds.WIN);
		lose = new Sounds(Sounds.LOSE);
	}

	//   Update Entities   //
	private void updateEntities(int difficulty, String playerSkin)
	{
		// Entities
		player = new Player(playerStart, ground, playerSkin);
		enemy = new Enemy(enemyStart, ground, "enemy", difficulty);

		//   CHEAT COOODE    //
		if (playerSkin.equals("player-1"))
		{
			player.setHp(10000);
			player.setInitialHP(10000);
			player.getPunch().setValue(200);
			player.getKick().setValue(200);
		}
	}

	//   New Game   //
	private void newGame()
	{
		// Key stuff
		keyLeft = false;
		keyRight = false;

		// Background
		int mapNum = (int)Math.floor((Math.random() * 8));
		background = loader.loadImage("/images/common/gameBackground" + mapNum + ".png");

		// Rounds Stuff
		round = 1;
		wins = 0;
		losses = 0;
		roundText = "ROUND 1 FIGHT!";
		roundCounter = counter;
		timer = 0;

		round1.play();
	}

	//   New Round   //
	private void newRound(int difficulty)
	{
		round++;

		// Key stuff
		keyLeft = false;
		keyRight = false;

		// Entities
		updateEntities(difficulty, player.getSkin());

		timer = 0;


		if (round == 1)
		{
			round1.play();
		}
		else if (round == 2)
		{
			round2.play();
		}
		else if (round == 3)
		{
			round3.play();
		}

		fightMusic.play();
	}

	//   Start Thread   //
	//   Start Thread   //
	private void start()
	{
		if (running)
		{
			return;
		}

		running = true;
		thread = new Thread(this);
		thread.start();
	}

	//   Tick   //


	//   Stop Thread   //
	private void stop()
	{
		if (!running)
		{
			return;
		}

		running = false;

		try 
		{
			thread.join();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}
	}


	//   Game Loop   //
	public void run()
	{
		long lastTime = System.nanoTime(); // The last recorded time in nanoseconds that the program has been running for
		final double amountOfTicks = TICKS_PER_SECOND; // The amount of ticks allowed
		double ns = 1000000000 / amountOfTicks; // 1,000,000,000 nano seconds = 1 second
		double delta = 0; // The total time the game has been running (in ticks)
		int updates = 0; // Number of ticks run (capped at 60)
		int frames = 0; // Number of frames rendered
		long timer = System.currentTimeMillis(); // Current time ran by the program in milliseconds

		counter = 0; // For the jump

		//   Loop   //
		while (running)
		{
			long now = System.nanoTime(); // The current time in nanoseconds
			delta += (now - lastTime) / ns; // Adds the elapsed time (in ticks) since the loop has been run to delta
			lastTime = now; // Sets the lastTime to the currentTime

			// If enough time has passed to constitute a tick
			if (delta >= 1)
			{
				tick(); // Calls the tick method
				delta--; // Makes delta back to less than 0
				updates++; // 1 update has run (1 tick has been run) 
				
				// PUT THIS OUTSIDE LOOP IF YOU WANT UNCAPPED FPS
				render(); // Calls the render method
				frames++; // 1 frame has run (1 render has been run)

				counter++;
			}
			

			// If a second has passed
			if(System.currentTimeMillis() - timer > 1000)
			{
				timer += 1000; // Increase timer by 1 second
				System.out.println(updates + " Ticks, Fps " + frames); // Print the ticks and frames run
				updates = 0; // Reset updates for the next print statement
				frames = 0; // Reset updates for the next print statement
			}
		}

		stop(); // Stop the program (Unnreachable statement [safety net])
	}


	//   Game Tick   //
	private void tick()
	{
		// If the game is running
		if (running == true)
		{
			timer++;

			if (keyRight == false && keyLeft == false)
			{
				player.halt();
			}
			else if (keyPressed == KeyEvent.VK_LEFT && keyLeft == true)
			{
				player.moveLeft();
			}
			else if (keyPressed == KeyEvent.VK_RIGHT && keyRight == true)
			{
				player.moveRight();
			}
			else if (keyLeft == true)
			{
				player.moveLeft();
			}
			else if (true)
			{
				player.moveRight();
			}


			player.tick(this, counter, enemy); // Update the player
			enemy.tick(this, counter, player); // Update the kick


			// Checks for win
			if (enemy.getHp() <= 0)
			{
				roundText = player.getName() + " WINS ROUND " + round + ", ROUND " + (round + 1) +  " FIGHT!";
				roundCounter = counter;
				wins++;
				winCheck();
			}
			else if (player.getHp() <= 0)
			{
				roundText = enemy.getName() + " WINS ROUND " + round + ", ROUND " + (round + 1) +  " FIGHT!!";
				roundCounter = counter;
				losses++;
				winCheck();
			}
			else if (timer / 60  > 120)
			{

				roundCounter = counter;
				losses++;

				if (player.getHp() / player.getInitialHP() > enemy.getHp() / enemy.getInitialHP())
				{
					roundText = player.getName() + " WINS ROUND " + round + ", ROUND " + (round + 1) +  " FIGHT!!";
					wins++;
				}
				else
				{
					roundText = enemy.getName() + " WINS ROUND " + round + ", ROUND " + (round + 1) +  " FIGHT!!";
					losses++;
				}
				winCheck();
			}
		}
		else // If the game was stopped
		{
			running = false;
			this.stop();
		}

	}

	private void winCheck()
	{
		if (wins >= 2)
		{
			panelPause.setVisible(true);
			canvas.setVisible(false);
			btnResume.setEnabled(false);
			pauseMessage.setText("Congrats You Won!");

			running = false;
			fightMusic.pause();
			win.play();
		}
		else if (losses >= 2)
		{
			panelPause.setVisible(true);
			canvas.setVisible(false);
			btnResume.setEnabled(false);
			pauseMessage.setText("BooHoo You Lost!");

			running = false;
			fightMusic.pause();
			lose.play();
		}
		else
		{
			newRound(enemy.getDifficulty());
		}
	}

	//   Rendering   //
	private void render()
	{
		//   Initial Graphics Settings   //
		bs = canvas.getBufferStrategy();

		if (bs == null)
		{
			canvas.createBufferStrategy(3);
			return;
		}

		g = bs.getDrawGraphics();
		Font defaultFont = new Font("arial", Font.BOLD , 40);
		g.setFont(defaultFont);

		//   Main Rendering   //
		// Background
		g.drawImage(background, 0, 0, canvas.getWidth(), canvas.getHeight(), canvas);

		// Timer
		Rectangle timerText = new Rectangle(width/2, 70, 0, 0);
		drawCenteredString(g, "" + (120 - (timer/60)), timerText, defaultFont);

		// Player Names
		g.setFont(new Font("arial", Font.BOLD , 25));
		g.setColor(Color.WHITE);
		g.drawString(player.getName(), 200, 45);
		g.drawString(enemy.getName(), width - 200 - 500, 45);

		// HP Bars
		g.setColor(Color.RED);
		g.fillRect(200, 60, (int)(player.getHp() / player.getInitialHP() * 1000 / 2), 20);
		g.fillRect(width - 200 - 500, 60, (int)(enemy.getHp() / enemy.getInitialHP() * 1000 / 2), 20);

		// HP Bar outlines
		g.setColor(Color.WHITE);
		g.drawRect(200, 60, 500, 20);
		g.drawRect(width - 200 - 500, 60, 500, 20);

		// Text Middle
		if (counter < roundCounter + 120)
		{
			Rectangle middleText = new Rectangle(width / 2 , height / 2, 0, 0);
			drawCenteredString(g, roundText, middleText, defaultFont);
		}

		// Round Boxes
		if (wins == 1)
		{
			g.setColor(Color.GREEN);
			g.fillOval(200, 90, 30, 30);
			g.setColor(Color.RED);
			g.fillOval(235, 90, 30, 30);
		}
		else if (wins == 2)
		{
			g.setColor(Color.GREEN);
			g.fillOval(200, 90, 30, 30);
			g.fillOval(235, 90, 30, 30);
		}
		else
		{
			g.setColor(Color.RED);
			g.fillOval(200, 90, 30, 30);
			g.fillOval(235, 90, 30, 30);
		}

		if (losses == 1)
		{
			g.setColor(Color.GREEN);
			g.fillOval(width - 200 - 500, 90, 30, 30);
			g.setColor(Color.RED);
			g.fillOval(width - 200 - 500 + 35, 90, 30, 30);
		}
		else if (losses == 2)
		{
			g.setColor(Color.GREEN);
			g.fillOval(width - 200 - 500, 90, 30, 30);
			g.fillOval(width - 200 - 500 + 35, 90, 30, 30);

		}
		else
		{
			g.setColor(Color.RED);
			g.fillOval(width - 200 - 500, 90, 30, 30);
			g.fillOval(width - 200 - 500 + 35, 90, 30, 30);
		}

		// Hitbox outlines
//		g.drawRect(player.getKick().getHitBox().x, player.getKick().getHitBox().y, player.getKick().getHitBox().width, player.getKick().getHitBox().height);
//		g.drawRect(player.getPunch().getHitBox().x, player.getPunch().getHitBox().y, player.getPunch().getHitBox().width, player.getPunch().getHitBox().height);
//
//		g.drawRect(enemy.getKick().getHitBox().x, enemy.getKick().getHitBox().y, enemy.getKick().getHitBox().width, enemy.getKick().getHitBox().height);
//		g.drawRect(enemy.getPunch().getHitBox().x, enemy.getPunch().getHitBox().y, enemy.getPunch().getHitBox().width, enemy.getPunch().getHitBox().height);


		// Character images
		g.drawImage(player.getImage(), (int)player.getX(), (int)player.getY(), canvas);
		g.drawImage(enemy.getImage(), (int)enemy.getX(), (int)enemy.getY(), canvas);

		// Player Punch Image 
		Rectangle pHit = player.getPunch().getHitBox();
		g.drawImage(player.getPunch().getActionImage(), pHit.x, pHit.y, pHit.width, pHit.height, canvas);

		// Player Kick Image 
		Rectangle kHit = player.getKick().getHitBox();
		g.drawImage(player.getKick().getActionImage(), kHit.x, kHit.y, kHit.width, kHit.height, canvas);

		// Enemy Punch Image 
		Rectangle epHit = enemy.getPunch().getHitBox();
		g.drawImage(enemy.getPunch().getActionImage(), epHit.x, epHit.y, epHit.width, epHit.height, canvas);

		// Enemy Kick Image 
		Rectangle ekHit = enemy.getKick().getHitBox();
		g.drawImage(enemy.getKick().getActionImage(), ekHit.x, ekHit.y, ekHit.width, ekHit.height, canvas);

		//   Close Graphics   //
		g.dispose();
		bs.show();
	}

	//   Key Listeners (INGAME)  //
	public void keyPressed(KeyEvent e) 
	{
		actionBool = player.getPunch().isActive() || player.getKick().isActive() || player.getBlock().isActive();
		keyPressed = e.getKeyCode();

		if (keyPressed == KeyEvent.VK_ESCAPE)
		{
			if (running)
			{
				fightMusic.pause();
				running = false;
				canvas.setVisible(false);
				panelPause.setVisible(true);
				pauseMessage.setText("Paused");
			}
		}
		if (keyPressed == KeyEvent.VK_P)
		{
			running = true;
			stop();
			System.exit(0);
		}
		if (keyPressed == KeyEvent.VK_RIGHT)
		{
			keyRight = true;
			player.setDirection("right");
		}
		if (keyPressed == KeyEvent.VK_LEFT)
		{
			keyLeft = true;
			player.setDirection("left");
		}
		if (keyPressed == KeyEvent.VK_A && !actionBool)	
		{
			player.doPunch(counter);
		}
		if (keyPressed == KeyEvent.VK_S && !actionBool)
		{
			player.doKick(counter);
		}
		if (keyPressed == KeyEvent.VK_D && !actionBool && !player.getJump().isActive())
		{
			player.doBlock(counter);
		}
		if (keyPressed == KeyEvent.VK_SPACE && !player.getJump().isActive())
		{
			player.doJump(counter);
		}
	}
	// Key Released Code (INGAME)
	public void keyReleased(KeyEvent e) 
	{
		keyReleased = e.getKeyCode();

		if (keyReleased == KeyEvent.VK_RIGHT)
		{
			keyRight = false;
		}
		if (keyReleased == KeyEvent.VK_LEFT)
		{
			keyLeft = false;
		}
	}


	//  Initializes the Game Frame and Menu

	//   Initialize Game and GUI   //
	public void initialize() 
	{
		//   Game Panel   //
		panelGame = new JPanel();
		panelGame.setBackground(Color.DARK_GRAY);
		panelGame.setLayout(null);
		frame.getContentPane().add(panelGame, "name_1732318983842");

		panelGame.add(canvas); //Adds the game canvas to the game panel

		//   Pause
		panelPause = new JPanel();
		panelPause.setBounds(width / 2 - 125, height / 2 - 200, 250, 400);
		panelPause.setBackground(Color.DARK_GRAY);
		panelPause.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panelGame.add(panelPause);

		pauseMessage = new JLabel("Paused");
		pauseMessage.setFont(new Font("arial", Font.BOLD, 20));
		pauseMessage.setForeground(Color.WHITE);
		panelPause.add(pauseMessage);

		btnResume = new JButton("Resume");
		panelPause.add(btnResume);

		btnExitToMenuGame = new JButton("Exit to Menu");
		panelPause.add(btnExitToMenuGame);


		//   Menu Panels   //
		panelMainMenu = new JPanel();
		panelMainMenu.setBackground(Color.DARK_GRAY);
		panelMainMenu.setLayout(null);
		frame.getContentPane().add(panelMainMenu, "name_1645524961803");

		panelHelpMenu = new JPanel();
		panelHelpMenu.setBackground(Color.DARK_GRAY);
		panelHelpMenu.setLayout(null);
		frame.getContentPane().add(panelHelpMenu, "name_1681777234948");

		panelSelectMenu = new JPanel();
		panelSelectMenu.setBackground(Color.DARK_GRAY);
		panelSelectMenu.setLayout(null);
		frame.getContentPane().add(panelSelectMenu, "name_1674088095888");


		//   Main Menu   //
		subPanelMain = new JPanel();
		subPanelMain.setBounds(width / 2 - 100, height / 2 - 100, 200, 200);
		subPanelMain.setBackground(Color.DARK_GRAY);
		panelMainMenu.add(subPanelMain);
		subPanelMain.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		lblBackgroundMain = new JLabel(new ImageIcon(loader.loadImage("/images/common/menuBackground.jpg")));
		lblBackgroundMain.setBounds(0, 0, width, height);

		lblBrawler = new JLabel("Brawler 64");
		subPanelMain.add(lblBrawler);
		lblBrawler.setHorizontalAlignment(SwingConstants.CENTER);
		lblBrawler.setHorizontalTextPosition(SwingConstants.CENTER);
		lblBrawler.setForeground(Color.WHITE);
		lblBrawler.setFont(new Font("Tekton Pro", Font.PLAIN, 40));

		btnNewGame = new JButton("New Game");
		subPanelMain.add(btnNewGame);

		btnHelp = new JButton("Help/Instructions");
		subPanelMain.add(btnHelp);

		btnExit = new JButton("Exit Game");
		subPanelMain.add(btnExit);


		//   Select Menu   //
		subPanelSelect = new JPanel();
		subPanelSelect.setBackground(Color.DARK_GRAY);
		subPanelSelect.setBounds(850, 685, 200, 100);
		panelSelectMenu.add(subPanelSelect);
		subPanelSelect.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		lblBackgroundSelect = new JLabel(new ImageIcon(loader.loadImage("/images/common/menuBackground.jpg")));
		lblBackgroundSelect.setBounds(0, 0, width, height);

		btnStartGame = new JButton("Start Game");
		btnStartGame.setFont(new Font("Tahoma", Font.PLAIN, 14));
		subPanelSelect.add(btnStartGame);

		btnBackToMenuSelect = new JButton("Back to Menu");
		btnBackToMenuSelect.setFont(new Font("Tahoma", Font.PLAIN, 14));
		subPanelSelect.add(btnBackToMenuSelect);

		lblVs = new JLabel("VS");
		lblVs.setHorizontalAlignment(SwingConstants.CENTER);
		lblVs.setFont(new Font("Tahoma", Font.BOLD, 30));
		lblVs.setForeground(Color.WHITE);
		lblVs.setBounds(549, 372, 800, 295);
		panelSelectMenu.add(lblVs);

		lblEnterName = new JLabel("Enter Name");
		lblEnterName.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnterName.setFont(new Font("arial", Font.PLAIN, 16));
		lblEnterName.setForeground(Color.WHITE);
		lblEnterName.setBounds(549, 372, 200, 25);
		panelSelectMenu.add(lblEnterName);

		lblChooseDifficulty = new JLabel("Choose Diffuclty");
		lblChooseDifficulty.setHorizontalAlignment(SwingConstants.CENTER);
		lblChooseDifficulty.setFont(new Font("arial", Font.PLAIN, 16));
		lblChooseDifficulty.setForeground(Color.WHITE);
		lblChooseDifficulty.setBounds(1149, 612, 200, 25);
		panelSelectMenu.add(lblChooseDifficulty);

		lblChooseName = new JLabel("Choose Name");
		lblChooseName.setHorizontalAlignment(SwingConstants.CENTER);
		lblChooseName.setForeground(Color.WHITE);
		lblChooseName.setFont(new Font("Arial", Font.PLAIN, 16));
		lblChooseName.setBounds(549, 612, 200, 25);
		panelSelectMenu.add(lblChooseName);

		lblEnemyName = new JLabel();
		lblEnemyName.setFont(new Font("arial", Font.PLAIN, 16));
		lblEnemyName.setForeground(Color.WHITE);
		lblEnemyName.setHorizontalAlignment(SwingConstants.CENTER);
		lblEnemyName.setBounds(1149, 372, 200, 25);
		panelSelectMenu.add(lblEnemyName);

		textFieldName = new JTextField();
		textFieldName.setText("Player");
		textFieldName.setFont(new Font("arial", Font.PLAIN, 16));
		textFieldName.setBounds(549, 402, 200, 20);
		panelSelectMenu.add(textFieldName);

		comboBoxEnemy = new JComboBox<String>();
		comboBoxEnemy .setBounds(1149, 647, 200, 20);
		panelSelectMenu.add(comboBoxEnemy);
		comboBoxEnemy.addItem("Easy");
		comboBoxEnemy.addItem("Medium");
		comboBoxEnemy.addItem("Hard");

		comboBoxPlayer = new JComboBox<String>();
		comboBoxPlayer.setBounds(549, 647, 200, 20);
		panelSelectMenu.add(comboBoxPlayer);
		comboBoxPlayer.addItem("Skin1");
		comboBoxPlayer.addItem("Skin2");
		comboBoxPlayer.addItem("EA DLC CHEAT MODE $399");

		enemyPreview = new JLabel();
		enemyPreview.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		enemyPreview.setHorizontalAlignment(SwingConstants.CENTER);
		enemyPreview.setBounds(1224, 472, 55, 88);
		panelSelectMenu.add(enemyPreview);

		playerPreview = new JLabel();
		playerPreview.setBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		playerPreview.setHorizontalAlignment(SwingConstants.CENTER);
		playerPreview.setBounds(624, 472, 55, 88);
		panelSelectMenu.add(playerPreview);


		//   Help Menu   //
		lblBackgroundHelp = new JLabel(new ImageIcon(loader.loadImage("/images/common/menuBackground.jpg")));
		lblBackgroundHelp.setBounds(0, 0, width, height);
		//panelHelpMenu.add(lblBackgroundHelp);

		textArea = new JTextArea();
		textArea.setFont(new Font("Arial", Font.PLAIN, 20));
		textArea.setForeground(Color.white);
		textArea.setLineWrap(true);
		textArea.setBackground(Color.DARK_GRAY);
		textArea.setBorder(null);
		textArea.setEditable(false);
		textArea.setText("Welcome to Brawler64."
				+ "\n\nThe rules of the game are simple, kill or be killed."
				+ "\n\nTo win the game, you must win 2 rounds before your enemy does."
				+ "\nEach round lasts until someone dies or the 2 minute timer runs out.."
				+ "\nA round is won by killing your enemy, or having the most health when the timer runs out."
				+ "\n\nYou can hurt your enemy by punching or kicking him."
				+ "\nPunch does more damage than kick, but has a shorter range."
				+ "\nKick does less damage than punch, but has a longer range."
				+ "\nYou can also use block to stop your enemies attacks, and jump to evade and flank your foe."
				+ "\n\n\nCONTROLS:"
				+ "\nPause - ESC"
				+ "\nMove Left - Left Arrow"
				+ "\nMove Right - Right Arrow"
				+ "\nJump - Spacebar"
				+ "\nPunch - A"
				+ "\nKick - S"
				+ "\nBlock - D");

		btnBackToMenuHelp = new JButton("Back to Menu");
		btnBackToMenuHelp.setBounds(width / 2 - 100, height - 200, 200, 25);
		panelHelpMenu.add(btnBackToMenuHelp);
		
		subPanelHelp = new JScrollPane(textArea);
		subPanelHelp.setBackground(null);
		subPanelHelp.setBorder(null);
		subPanelHelp.setBounds(width / 2 - 500, height / 2 - 350, 1000, 700);
		panelHelpMenu.add(subPanelHelp);

		//   Initial Setup   //
		panelGame.setVisible(false);
		panelHelpMenu.setVisible(false);
		panelSelectMenu.setVisible(false);
		panelMainMenu.setVisible(true);
		panelPause.setVisible(false);

		comboBoxPlayer.setSelectedIndex(0);
		comboBoxEnemy.setSelectedIndex(1);


		//   Action Listners   //
		//   New Game Button   //
		btnNewGame.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				button.play();

				switchPanel(panelMainMenu, panelSelectMenu);

				setSkins();
			}
		});
		//   Help Button   //
		btnHelp.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				button.play();

				switchPanel(panelMainMenu, panelHelpMenu);
			}
		});
		//   Exit Button   //
		btnExit.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				button.play();

				stop();
				System.exit(0);
			}
		});
		//   Back to Menu   //
		btnBackToMenuHelp.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				button.play();

				switchPanel(panelHelpMenu, panelMainMenu);
			}
		});
		btnBackToMenuSelect.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				button.play();

				switchPanel(panelSelectMenu, panelMainMenu);
			}
		});
		btnExitToMenuGame.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				button.play();

				switchPanel(panelGame, panelMainMenu);
				running = true;
				stop();
			}
		});
		//   Start Game   //
		btnStartGame.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				button.play();

				fightMusic.play();

				setSkins();
				newGame();

				start();
				btnResume.setEnabled(true);
				panelPause.setVisible(false);
				canvas.setVisible(true);
				switchPanel(panelSelectMenu, panelGame);
				canvas.requestFocus();
			}
		});
		//   Resume Game   //
		btnResume.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				button.play();

				fightMusic.resume();

				roundCounter = 0;

				keyLeft = false;
				keyRight = false;

				panelPause.setVisible(false);
				canvas.setVisible(true);
				canvas.requestFocus();
				start();
			}
		});
		//   Update Previews   //
		comboBoxPlayer.addItemListener(new ItemListener() 
		{
			public void itemStateChanged(ItemEvent arg0) 
			{
				setSkins();
			}
		});
		comboBoxEnemy.addItemListener(new ItemListener() 
		{
			public void itemStateChanged(ItemEvent arg0) 
			{
				setSkins();
			}
		});
	}

	//   Functions   //
	//   Functions   //
	//   Switch Panels: Makes arg0 invisible and arg1 visible
	public void switchPanel(JPanel oldPanel, JPanel newPanel)
	{
		oldPanel.setVisible(false);
		newPanel.setVisible(true);
	}
	//   Similar to Graphics.drawString but the string is centered
	public void drawCenteredString(Graphics g, String text, Rectangle rect, Font font) 
	{
		FontMetrics metrics = g.getFontMetrics(font);
		int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
		int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
		g.setFont(font);
		g.drawString(text, x, y);
	}
	//    Updates the Player and Enemy
	public void setSkins()
	{
		int difficulty = 0;

		if (((String) comboBoxEnemy.getSelectedItem()).equalsIgnoreCase("easy"))
		{
			difficulty = Enemy.DIFFICULY_EASY;
		}
		else if (((String) comboBoxEnemy.getSelectedItem()).equalsIgnoreCase("medium"))
		{
			difficulty = Enemy.DIFFICULY_MEDIUM;	
		}
		else if (((String) comboBoxEnemy.getSelectedItem()).equalsIgnoreCase("hard"))
		{
			difficulty = Enemy.DIFFICULY_HARD;
		}

		String playerSkin = "player0";

		if (((String) comboBoxPlayer.getSelectedItem()).equalsIgnoreCase("skin1"))
		{
			playerSkin = "player0";
		}
		else if (((String) comboBoxPlayer.getSelectedItem()).equalsIgnoreCase("skin2"))
		{
			playerSkin = "player1";
		}
		else if (((String) comboBoxPlayer.getSelectedItem()).equalsIgnoreCase("EA DLC CHEAT MODE $399")) // CHEAT
		{
			playerSkin = "player-1";
		}

		updateEntities(difficulty, playerSkin);
		lblEnemyName.setText(enemy.getName());
		playerPreview.setIcon(new ImageIcon(player.getImage()));
		enemyPreview.setIcon(new ImageIcon(enemy.getImage()));

		if (textFieldName.getText().equals(""))
		{
			player.setName("Player");
		}
		else if (textFieldName.getText().length() > 20)
		{
			player.setName("bruh why your name so long");
		}
		else
		{
			player.setName(textFieldName.getText());
		}
	}
}