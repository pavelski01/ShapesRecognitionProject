package gui;

import java.awt.Graphics;
import java.awt.Image;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

public class OperatingArea extends JPanel
{	
	public OperatingArea()
	{ this.setBorder(BorderFactory.createEtchedBorder()); }
	
	public boolean loadImage(Image _image)
	{
		this.image = _image;
		this.repaint();
		return (this.image.getWidth(null) > this.getWidth()) || 
			(this.image.getHeight(null) > this.getHeight());
	}
	
	public void paintComponent(Graphics _g)
	{
		if (this.image == null) return;
		super.paintComponent(_g);//super keyword is critical
		if ((this.image.getWidth(null) > this.getWidth()) || 
				(this.image.getHeight(null) > this.getHeight())) 
					this.image = this.image.getScaledInstance(
						(this.getWidth() == 0) ? (-1) : this.getWidth(), 
						(this.getHeight() == 0) ? (-1) : this.getHeight(), 
						Image.SCALE_DEFAULT
					);
		_g.drawImage(
			this.image, this.getWidth() / 2 - this.image.getWidth(null) / 2, 
				this.getHeight() / 2 - this.image.getHeight(null) / 2, null
		);
	}
	
	private Image image;
	private static final long serialVersionUID = 1L;
}
