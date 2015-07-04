package gui;

import java.awt.EventQueue;
import javax.swing.JFrame;

public class RecognitionProject 
{	
	public static void main(String[] _args) 
	{
		EventQueue.invokeLater(
			new Runnable()
			{
				public void run()
				{
					RecognitionFrame frame = new RecognitionFrame();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
				}
			}
		);
	}
}