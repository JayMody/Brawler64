package main;

import java.io.BufferedInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioPlayer 
{
	private Clip clip;
	
	public AudioPlayer(String path)
	{
		try
		{
			InputStream audioSrc = getClass().getResourceAsStream(path);
			InputStream bufferedIn = new BufferedInputStream(audioSrc);
			
			AudioInputStream ais = AudioSystem.getAudioInputStream(bufferedIn);
			AudioFormat baseFormat = ais.getFormat();
			AudioFormat decodeFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
			
			AudioInputStream dais = AudioSystem.getAudioInputStream(decodeFormat, ais);
			clip = AudioSystem.getClip();
			clip.open(dais);
		}
		catch(NullPointerException ne)
		{
			System.out.println(path);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void play(int frames) 
	{
		if (clip == null) return;
		stop();
		clip.setFramePosition(frames);
		clip.start();
	}
	
	public void stop()
	{
		if (clip.isRunning()) clip.stop();
	}
	
	public void close() 
	{
		stop();
		clip.close();
	}
	
	public int getFramePosition()
	{
		return clip.getFramePosition();
	}
}
