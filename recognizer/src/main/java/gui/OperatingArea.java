package gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class OperatingArea extends JPanel
{	
	public OperatingArea()
	{ setBorder(BorderFactory.createEtchedBorder()); }
	
	public boolean loadImage(Image aImage)
	{
		image = aImage;
		repaint();
		return (image.getWidth(null) > getWidth()) || (image.getHeight(null) > getHeight());
	}
	
	public void paintComponent(Graphics g)
	{
		if (image == null) return;
		super.paintComponent(g);		
		if ((image.getWidth(null) > getWidth()) || (image.getHeight(null) > getHeight())) 
			image = image.getScaledInstance(
				(getWidth() == 0) ? (-1) : getWidth(), (getHeight() == 0) ? (-1) : getHeight(), Image.SCALE_DEFAULT
			);
		g.drawImage(image, getWidth() / 2 - image.getWidth(null) / 2, getHeight() / 2 - image.getHeight(null) / 2, null);
	}
	
	private Image image;
	private static final long serialVersionUID = 1L;
}
