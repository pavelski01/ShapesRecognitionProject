package gui;

import java.awt.Dimension;
import javax.swing.JFrame;

public class RecognitionFrame extends JFrame
{	
	public RecognitionFrame()
	{
		setTitle("Image Recognition");
		setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		RecognitionPanel panel = new RecognitionPanel(this);
		add(panel);
		pack();
	}
	
	public static final int DEFAULT_WIDTH = 1080;
	public static final int DEFAULT_HEIGHT = 600;
	private static final long serialVersionUID = 1L;
}
