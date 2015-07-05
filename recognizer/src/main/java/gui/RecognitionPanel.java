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
	RecognitionPanel(JFrame _frame)
	{
		this.recognitionFrame = _frame;
		this.initialize();
	}
	
	private void initialize()
	{
		this.setLayout(new BorderLayout());
		this.operatingPanel = new OperatingArea();
		String classDirectory = RecognitionPanel.class.getProtectionDomain().
			getCodeSource().getLocation().getPath();
		classDirectory = 
			classDirectory.substring(0, classDirectory.lastIndexOf(RecognitionPanel.SEPARATOR));
		try
		{
			this.imgExtender = 
				ImageIO.read(
					new File(
						classDirectory + RecognitionPanel.SEPARATOR +
							"recognizer-images" + RecognitionPanel.SEPARATOR +
								this.imgTitle
						)
				);
			this.isScaled = this.operatingPanel.loadImage(this.imgExtender);
			this.imgTitle = this.isScaled ? this.imgTitle.concat(" (SCALED)") : this.imgTitle;
			this.recognitionFrame.setTitle("Image Recognition - " + this.imgTitle);
		}
		catch (IOException ioe) {}
		catch (Exception e) {}
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new GridLayout(1, 2));
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 1));
		buttonPanel.setBorder(BorderFactory.createEtchedBorder());
		this.labelButtonPanel =
			new JLabel(
				this.shapes[this.shapeCode].name() + " DETECTION", 
				SwingConstants.CENTER
			);
		this.labelButtonPanel.setBorder(BorderFactory.createEtchedBorder());
		JPanel subButtonPanel = new JPanel();
		subButtonPanel.setLayout(new GridLayout(1, 3));
		subButtonPanel.setBorder(BorderFactory.createEtchedBorder());
		this.chooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "bmp", "jpg", "jpeg", "gif");
		this.chooser.setFileFilter(filter);
		this.chooser.setAccessory(new ImagePreview(this.chooser));
		Image palette = null;
		if (classDirectory != null)
			try
			{
				palette = 
					ImageIO.read(
						RecognitionPanel.class.getResource(
							RecognitionPanel.SEPARATOR + "icon" +
								RecognitionPanel.SEPARATOR + "palette.gif"
						)
					);
			}
			catch (IOException ioe) {}
			catch (Exception e) {}
		if (palette != null) this.chooser.setFileView(new FileIconView(filter, new ImageIcon(palette)));
		if (classDirectory != null)
			this.chooser.setCurrentDirectory(
				new File(classDirectory + RecognitionPanel.SEPARATOR + "recognizer-images")
			);
		this.previewLabel = new ShapePreview(this.shapes[this.shapeCode]);
		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent _event)
			{
				char action = _event.getActionCommand().toLowerCase().charAt(0);
				switch (action)
				{
					case 'p'://process
						if (RecognitionPanel.this.imgExtender != null)
						{
							RecognitionPanel.this.engine = new ProcessEngine(RecognitionPanel.this.imgExtender);
							int[] count =
								RecognitionPanel.this.engine.shapeRecognize(
									RecognitionPanel.this.engine.shapeParse(),
									RecognitionPanel.this.shapes[RecognitionPanel.this.shapeCode]
								);
							String conjugation1 =
								(
									(count[0] == 0 || count[0] > 1) && 
										RecognitionPanel.this.shapes[RecognitionPanel.this.shapeCode] == ShapePreview.Shape.BOX
								) ? 
								"ES" :
								(count[0] == 0 || count[0] > 1) ? "S" : "";
							String conjugation2 = (count[1] == 1) ? "" : "S";
							RecognitionPanel.this.labelButtonPanel.setText(
								count[0] + " " + RecognitionPanel.this.shapes[RecognitionPanel.this.shapeCode].name() +
									conjugation1 + " RECOGNIZED / " + count[1] + " OBJECT" + conjugation2 + " DETECTED"
							);
							RecognitionPanel.this.operatingPanel.loadImage(RecognitionPanel.this.engine.afterProcess());
						}
						break;
					case 'l'://load
						if (RecognitionPanel.this.chooser.showOpenDialog(RecognitionPanel.this) == JFileChooser.APPROVE_OPTION)
							try
							{
								RecognitionPanel.this.isScaled =
									RecognitionPanel.this.operatingPanel.loadImage(
										RecognitionPanel.this.imgExtender =
											ImageIO.read(
												new File(RecognitionPanel.this.chooser.getSelectedFile().getPath())
											)
									);
								RecognitionPanel.this.imgTitle = RecognitionPanel.this.chooser.getSelectedFile().getName();
								RecognitionPanel.this.imgTitle =
									RecognitionPanel.this.isScaled ?
										RecognitionPanel.this.imgTitle.concat(" (SCALED)") :
										RecognitionPanel.this.imgTitle;
								RecognitionPanel.this.recognitionFrame.setTitle(
									imgTitle.equals("") ? "Image Recognition" : ("Image Recognition - " + RecognitionPanel.this.imgTitle)
								);
								RecognitionPanel.this.labelButtonPanel.setText(
									RecognitionPanel.this.shapes[RecognitionPanel.this.shapeCode].name() + " DETECTION"
								);
							}
							catch (IOException e) {}
					   	break;
					case 'n'://next
						if (++RecognitionPanel.this.shapeCode >= RecognitionPanel.this.shapes.length) RecognitionPanel.this.shapeCode = 0;
						RecognitionPanel.this.previewLabel.setForm(RecognitionPanel.this.shapes[RecognitionPanel.this.shapeCode]);
						RecognitionPanel.this.labelButtonPanel.setText(RecognitionPanel.this.shapes[RecognitionPanel.this.shapeCode].name() + " DETECTION");
						break;
				}
			}
		};
		this.makeButton("Process", listener, subButtonPanel);
		this.makeButton("Load", listener, subButtonPanel);
		this.makeButton("Next", listener, subButtonPanel);
		buttonPanel.add(this.labelButtonPanel);
		buttonPanel.add(subButtonPanel);
		bottomPanel.add(buttonPanel);
		bottomPanel.add(this.previewLabel);
		this.add(this.operatingPanel, BorderLayout.CENTER);
		this.add(bottomPanel, BorderLayout.SOUTH);
	}

	private void makeButton(String _label, ActionListener _listener, JPanel _panel)
	{
		JButton button = new JButton(_label);
		button.addActionListener(_listener);
		_panel.add(button);
	}
	
	private ShapePreview previewLabel;
	private ShapePreview.Shape[] shapes =
	{
		ShapePreview.Shape.BOX,
		ShapePreview.Shape.CIRCLE,
		ShapePreview.Shape.TRIANGLE
	};
	
	private OperatingArea operatingPanel;
	private ProcessEngine engine;
	private Image imgExtender;
	private JFileChooser chooser;
	private JFrame recognitionFrame;
	private JLabel labelButtonPanel;
	private String imgTitle = "test0.jpg";
	private boolean isScaled;
	private int shapeCode = 0;
	private static String SEPARATOR = System.getProperty("file.separator");
	private static final long serialVersionUID = 1L;
}
