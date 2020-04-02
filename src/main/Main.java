package main;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import game.Game;

public class Main 
{
	static JFrame frame;
	static JPanel panel;
	static JComboBox<String> fullscreenBox;
	static JComboBox<String> screenBox;
	static ButtonGroup soundSwitch;
	static JRadioButton soundOn;
	static JRadioButton soundOff;
	static JButton launch;

	static int resolution;
	static boolean fullscreen;
	static int screen;

	static public GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	static public GraphicsDevice[] gs = ge.getScreenDevices();

	public static void main(String[] args)
	{
		resolution = ge.getDefaultScreenDevice().getDisplayMode().getWidth();
		fullscreen = false;
		screen = 0;

		frame = new JFrame("Brawler64 Launcher");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(ge.getDefaultScreenDevice().getDisplayMode().getWidth() / 2 - 200, ge.getDefaultScreenDevice().getDisplayMode().getHeight() / 2 - 200, 400, 400);
		frame.setResizable(false);
		frame.setVisible(true);

		panel = new JPanel();
		panel.setBackground(Color.DARK_GRAY);
		panel.setLayout(null);
		frame.add(panel);

		fullscreenBox = new JComboBox<String>();
		fullscreenBox.setBounds(100, 60, 200, 20);
		panel.add(fullscreenBox);
		fullscreenBox.addItem("Fullscreen (recommended)");
		fullscreenBox.addItem("Windowed");
		fullscreenBox.setSelectedIndex(0);

		screenBox = new JComboBox<String>();
		screenBox.setBounds(100, 120, 200, 20);
		panel.add(screenBox);
		for (int x = 0; x < gs.length; x++)
		{
			if (gs[x] == ge.getDefaultScreenDevice())
			{
				screenBox.addItem("Screen " + (x + 1) + " (default)");
				screenBox.setSelectedIndex(x);
			}
			else
			{
				screenBox.addItem("Screen " + (x + 1));
			}
		}
		
		soundOn = new JRadioButton("Sound ON");
		soundOn.setBorder(null);
		soundOn.setBackground(null);
		soundOn.setForeground(Color.white);
		soundOn.setBounds(150, 180, 200, 20);
		soundOn.setSelected(true);
		panel.add(soundOn);

		soundOff = new JRadioButton("Sound Off");
		soundOff.setBorder(null);
		soundOff.setBackground(null);
		soundOff.setForeground(Color.white);
		soundOff.setBounds(150, 200, 200, 20);
		panel.add(soundOff);
		
		soundSwitch = new ButtonGroup();
		soundSwitch.add(soundOn);
		soundSwitch.add(soundOff);
		
		launch = new JButton("Launch Game");
		launch.setBounds(100, 260, 200, 20);
		launch.setVisible(true);
		panel.add(launch);

		frame.repaint();

		launch.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent e) 
			{
				screen = screenBox.getSelectedIndex();

				switch (fullscreenBox.getSelectedIndex()){
				case 0:
					fullscreen = true;
					break;
				case 1:
					fullscreen = false;
					break;
				}

				Game game = new Game();
				game.initFrame(screen, fullscreen, resolution, (int) (resolution * 9/16), soundOn.isSelected());
				game.initialize();
				frame.dispose();
			}
		});
	}
}
