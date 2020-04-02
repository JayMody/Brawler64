package main;

public class Sounds 
{
	private static boolean on;
	private AudioPlayer audioPlayer;
	private int frames;

	public final static String NULL = "/sound/sfx/null.wav";
	public final static String ROUND1 = "/sound/sfx/round1.wav";
	public final static String ROUND2 = "/sound/sfx/round2.wav";
	public final static String ROUND3 = "/sound/sfx/round3.wav";
	public final static String FIGHT_MUSIC = "/sound/music/fightMusic.wav";
	public final static String BUTTON = "/sound/sfx/button.wav";
	public final static String HIT = "/sound/sfx/hit.wav";
	public final static String WIN = "/sound/sfx/win.wav";
	public final static String LOSE = "/sound/sfx/lose.wav";
	public final static String SWING = "/sound/sfx/swing.wav";
	public final static String JUMP = "/sound/sfx/jump.wav";
	public final static String BLOCK = "/sound/sfx/block.wav";

	public Sounds(String path)
	{
		if (!on)
		{
			path = NULL;
		}
		audioPlayer = new AudioPlayer(path);
		frames = 0;
	}

	public void play()
	{
		frames = 0;
		audioPlayer.play(frames);
	}

	public void resume()
	{
		audioPlayer.play(frames);
	}

	public void pause()
	{
		audioPlayer.stop();
		frames = audioPlayer.getFramePosition();
	}

	public void stop()
	{
		audioPlayer.close();
	}

	public static void setOn(boolean on1)
	{
		on = on1;
	}
}
