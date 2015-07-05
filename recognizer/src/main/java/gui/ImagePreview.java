package gui;

import java.awt.Dimension;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class ImagePreview extends JLabel
{	
	public ImagePreview(JFileChooser _chooser)
	{
		super("", SwingConstants.CENTER);
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		this.setBorder(BorderFactory.createEtchedBorder());
		_chooser.addPropertyChangeListener(
			new PropertyChangeListener()
			{
				public void propertyChange(PropertyChangeEvent _event)
				{
					if (_event.getPropertyName() == JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)
					{
						File file = (File)_event.getNewValue();
						if (file == null)
						{
							ImagePreview.this.setIcon(null);
							return;
						}
						ImageIcon icon = new ImageIcon(file.getPath());
						if (icon.getIconWidth() > ImagePreview.this.getPreferredSize().width) 
							icon = new ImageIcon(
								icon.getImage().getScaledInstance(
									getPreferredSize().width, 
									-1, 
									Image.SCALE_DEFAULT
								)
							);
						ImagePreview.this.setIcon(icon);
					}
				}
			}
		);
	}
	
	public static final int DEFAULT_WIDTH = 100;
	public static final int DEFAULT_HEIGHT = 100;
	private static final long serialVersionUID = 1L;
}