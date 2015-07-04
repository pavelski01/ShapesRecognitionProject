package gui;

import java.awt.Dimension;
import javax.swing.JFrame;

public class RecognitionFrame extends JFrame
{	
	public RecognitionFrame()
	{
		this.setTitle("Image Recognition");
		this.setPreferredSize(
			new Dimension(
				RecognitionFrame.DEFAULT_WIDTH, 
				RecognitionFrame.DEFAULT_HEIGHT
			)
		);
		RecognitionPanel panel = new RecognitionPanel(this);
		this.add(panel);
		this.pack();
	}
	
	public static final int DEFAULT_WIDTH = 1080;
	public static final int DEFAULT_HEIGHT = 600;
	private static final long serialVersionUID = 1L;
}
