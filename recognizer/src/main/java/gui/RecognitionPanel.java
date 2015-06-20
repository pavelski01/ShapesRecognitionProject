package gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class RecognitionPanel extends JPanel
{	
	RecognitionPanel(JFrame frame)
	{
		this.recognitionFrame = frame;
		setLayout(new BorderLayout());
		operatingPanel = new OperatingArea();
		String classDirectory = null;		
		classDirectory = RecognitionPanel.class.getProtectionDomain().
			getCodeSource().getLocation().getPath();
		classDirectory = classDirectory.substring(0, classDirectory.lastIndexOf(SEPARATOR));		
		try
		{
			img = ImageIO.read(new File(classDirectory + SEPARATOR + "recognizer-images" + SEPARATOR + imgTitle));
			isScaled = operatingPanel.loadImage(img);			
			imgTitle = isScaled ? imgTitle.concat(" (SCALED)") : imgTitle.concat("");
			recognitionFrame.setTitle("Image Recognition - " + imgTitle);
		} 
		catch (IOException e) {}		
		bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 2));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 1));
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		labelButtonPanel = new JLabel(shapes[shapeIterator].name() + " DETECTION", SwingConstants.CENTER);
		labelButtonPanel.setBorder(BorderFactory.createEtchedBorder());
		subButtonPanel = new JPanel();
		subButtonPanel.setLayout(new GridLayout(1, 3));
		subButtonPanel.setBorder(BorderFactory.createEtchedBorder());
		chooser = new JFileChooser();
		filter = new FileNameExtensionFilter("Image files", "bmp", "jpg", "jpeg", "gif");
		chooser.setFileFilter(filter);
		chooser.setAccessory(new ImagePreview(chooser));		
		Image image = null;
		if (classDirectory != null)
			try
			{ 
				image = 
					ImageIO.read(
						RecognitionPanel.class.getResource(SEPARATOR + "icon" + SEPARATOR + "palette.gif")
					);
				
			}
			catch (IOException ioEX) {}
			catch (Exception EX) {}
	    if (image != null) chooser.setFileView(new FileIconView(filter, new ImageIcon(image)));
	    if (classDirectory != null) chooser.setCurrentDirectory(new File(classDirectory + SEPARATOR + "PredefinedImages"));
	    previewLabel = new ShapePreview(shapes[shapeIterator]);
	    listener = new ActionListener()
	    {
			public void actionPerformed(ActionEvent event)
			{
				char action = event.getActionCommand().toLowerCase().charAt(0);
				switch (action)
				{
					case 'p'://process
						if (img != null)
						{
							engine = new ProcessEngine(img);
							int[] count = engine.shapeRecognize(engine.shapeParse(), shapes[shapeIterator]);
							String conjugation1 = ((count[0] == 0 || count[0] > 1) && shapes[shapeIterator] == ShapePreview.Shape.BOX) ? "ES" : 
								(count[0] == 0 || count[0] > 1) ? "S" : "",
									conjugation2 = (count[1] == 1) ? "" : "S";
							labelButtonPanel.setText( count[0] + " " + shapes[shapeIterator].name() + 
								conjugation1 + " RECOGNIZED / " + count[1] + " OBJECT" + conjugation2
									+ " DETECTED");
							operatingPanel.loadImage(engine.afterProcess());													
						}
						break;
					case 'l'://load
						if (chooser.showOpenDialog(RecognitionPanel.this) == JFileChooser.APPROVE_OPTION)
							try 
							{
								isScaled = operatingPanel.loadImage(img = ImageIO.read(new File(chooser.getSelectedFile().getPath())));
								imgTitle = chooser.getSelectedFile().getName();
								imgTitle = isScaled ? imgTitle.concat(" (SCALED)") : imgTitle.concat("");
								recognitionFrame.setTitle(imgTitle.equals("") ? "Image Recognition" : ("Image Recognition - " + imgTitle));
								labelButtonPanel.setText(shapes[shapeIterator].name() + " DETECTION");
							}
							catch (IOException e) {}
					   	break;
					case 'n'://next
						if (++shapeIterator >= shapes.length) shapeIterator = 0;
						previewLabel.setForm(shapes[shapeIterator]);
						labelButtonPanel.setText(shapes[shapeIterator].name() + " DETECTION");
						break;			
				}
			}
		};	    
		makeButton("Process");
		makeButton("Load");
		makeButton("Next");				
		buttonPanel.add(labelButtonPanel);
		buttonPanel.add(subButtonPanel);		
		bottomPanel.add(buttonPanel);
		bottomPanel.add(previewLabel);		
		add(operatingPanel, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	private void makeButton(String label)
	{
		JButton button = new JButton(label);
		button.addActionListener(listener);
		subButtonPanel.add(button);
	}
	
	private String imgTitle = "test0.jpg";
	private int shapeIterator = 0;
	private boolean isScaled;
	private Image img;
	private ActionListener listener;
	private FileNameExtensionFilter filter;
	private JFileChooser chooser;
	private JLabel labelButtonPanel;
	private JPanel bottomPanel;
	private JPanel buttonPanel;
	private JPanel subButtonPanel;
	private JFrame recognitionFrame;
	private OperatingArea operatingPanel;
	private ProcessEngine engine;
	private ShapePreview previewLabel;
	private ShapePreview.Shape[] shapes = 
		{ ShapePreview.Shape.BOX, ShapePreview.Shape.CIRCLE, ShapePreview.Shape.TRIANGLE };
	private static String SEPARATOR = System.getProperty("file.separator");
	private static final long serialVersionUID = 1L;
}
