package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ShapePreview extends JLabel
{
	public ShapePreview(ShapePreview.Shape _form)
	{
		super("", SwingConstants.CENTER);
		this.setPreferredSize(new Dimension(ShapePreview.DEFAULT_WIDTH, ShapePreview.DEFAULT_HEIGHT));
		this.setBorder(BorderFactory.createEtchedBorder());
		this.form = _form;
	}
	
	public void loadImage(String _name)
	{
		ImageIcon icon = new ImageIcon(_name);
		if (icon.getIconWidth() > this.getPreferredSize().width) 
			icon = new ImageIcon(
				icon.getImage().getScaledInstance(
					(this.getPreferredSize().width == 0) ? 
						(-1) : 
						this.getPreferredSize().width, 
					(-1), Image.SCALE_DEFAULT
				)
			);
		if (icon != null) this.setIcon(icon);
		this.repaint();
	}
	
	public void setForm(Shape _shape)
	{
		this.form = _shape;
		this.repaint();
	}
	
	public void paintComponent(Graphics _g)
	{
		Graphics2D g2 = (Graphics2D)_g;	
		double side = 70;
		double leftX = this.getWidth() / 2 - side / 2;
		double topY = this.getHeight() / 2 - side / 2;
		Rectangle2D rectangle = new Rectangle2D.Double(leftX, topY, side, side);
		switch (this.form)
		{
			case BOX:						
				g2.setPaint(Color.BLACK);		
				g2.fill(rectangle);
				g2.setPaint(Color.RED);
				g2.setStroke(new BasicStroke(3.5F));
				g2.draw(rectangle);
				break;
			case CIRCLE:
				Ellipse2D ellipse = new Ellipse2D.Double();
				ellipse.setFrame(rectangle);
				g2.setPaint(Color.BLACK);		
				g2.fill(ellipse);
				g2.setPaint(Color.RED);
				g2.setStroke(new BasicStroke(3.5F));
				g2.draw(ellipse);
				break;
			case TRIANGLE:
				Point p1 = new Point((int)(this.getWidth() / 2), (int)(this.getHeight() / 2 - (2 * side / 3)));
				Point p2 = new Point((int)(this.getWidth() / 2 + (side / 2)), (int)(this.getHeight() / 2 + (side / 3)));
				Point p3 = new Point((int)(this.getWidth() / 2 - (side / 2)), (int)(this.getHeight() / 2 + (side / 3)));
				Polygon triangle = new Polygon(new int[] { p1.x, p2.x, p3.x }, new int[] { p1.y, p2.y, p3.y }, 3);
				g2.setPaint(Color.BLACK);
				g2.fillPolygon(triangle);
				g2.setPaint(Color.RED);
				g2.setStroke(new BasicStroke(3.5F));
				g2.drawPolygon(triangle);
		}
	}
	
	public enum Shape { BOX, CIRCLE, TRIANGLE }
	private Shape form;
	public static final int DEFAULT_WIDTH = 180;
	public static final int DEFAULT_HEIGHT = 150;
	private static final long serialVersionUID = 1L;
}
