package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeSet;

public class ProcessEngine
{	
	public ProcessEngine(Image _image)
	{
		this.image = _image;
		this.bufferedImage = 
			new BufferedImage(
				this.image.getWidth(null), 
				this.image.getHeight(null), 
				BufferedImage.TYPE_INT_ARGB
			);		
		this.bufferedImage.getGraphics().drawImage(this.image, 0, 0, null);		
		this.filter();
		this.graphicsObject = (Graphics2D)this.bufferedImage.getGraphics();
		this.rotate();
		this.raster = this.bufferedImage.getRaster();
		this.matrix = new boolean[this.bufferedImage.getHeight(null)][this.bufferedImage.getWidth(null)];
		this.binarize();
	}
	
	public Map<String, LinkedList<TreeSet<Integer[]>>> shapeParse()
	{
		int number = 0;
		boolean[][] stencil = new boolean[this.matrix.length][this.matrix[0].length];
		for (int j = 0; j < this.matrix.length; ++j)
			for (int i = 0; i < this.matrix[j].length; ++i)
				stencil[j][i] = this.matrix[j][i];
		Map<String, LinkedList<TreeSet<Integer[]>>> map = 
			new HashMap<String, LinkedList<TreeSet<Integer[]>>>();		
		Comparator<Integer[]> comparator = new Comparator<Integer[]>()
		{
			public int compare(Integer[] _a, Integer[] _b)
			{
				if (_a[0] - _b[0] != 0) return _a[0] - _b[0];
				else if (_a[1] - _b[1] != 0) return _a[1] - _b[1];
				else return 0;
			}					
		};
		String figureKey = null;
		for (int y = 0; y < stencil.length; ++y)
			for (int x = 0; x < stencil[y].length; ++x)
			{				
				if (stencil[y][x]) 
				{						
					int top_y = y, bottom_y = 0, left_x = x, right_x = 0, iterator = 0;		
					figureKey = Integer.toString(number);									
					for (int x1 = left_x; stencil[top_y][x1]; ++x1) ++iterator;					
					right_x = left_x + iterator;
					for (int y1 = top_y; stencil[y1][ left_x + (right_x - left_x ) / 2 ]; ++y1)
					{						
						for (int x1 = left_x; stencil[y1][x1]; --x1) left_x = x1;						
						for (int x1 = right_x; stencil[y1][x1]; ++x1) right_x = x1;						
						bottom_y = y1;						
					}
					bottom_y += 1;
					right_x += 1;
					TreeSet<Integer[]> centerPoints = new TreeSet<Integer[]>(comparator);
					centerPoints.add(new Integer[] {top_y + (bottom_y - top_y) / 2, left_x + (right_x - left_x) / 2});
					TreeSet<Integer[]> figurePoints = new TreeSet<Integer[]>(comparator);
					for (int y1 = top_y; y1 <= bottom_y; ++y1)
						for (int x1 = left_x; x1 <= right_x; ++x1)
						{							
							figurePoints.add(new Integer[] {y1, x1});
							stencil[y1][x1] = false;															
						}			
					++number;
					LinkedList<TreeSet<Integer[]>> figureValues = new LinkedList<TreeSet<Integer[]>>();
					figureValues.add(figurePoints);
					figureValues.add(centerPoints);
					map.put(figureKey, figureValues);
				}				
			}
		return map;
	}
	
	public int[] shapeRecognize(
		Map<String, LinkedList<TreeSet<Integer[]>>> _map, 
		ShapePreview.Shape _shape
	)
	{
		int found = 0, object = 0;
		for (Map.Entry<String, LinkedList<TreeSet<Integer[]>>> entry : _map.entrySet())
		{
			++object;
			LinkedList<TreeSet<Integer[]>> valuesList = entry.getValue();
			TreeSet<Integer[]> set1 = valuesList.getFirst();
			TreeSet<Integer[]> set2 = valuesList.getLast();
			int A_y = -1, A_x = -1, B_y = -1, B_x = -1, C_y = -1, C_x = -1, 
				D_y = -1, D_x = -1, E_y = -1, E_x = -1, F_y = -1, F_x = -1,
				G_y = -1, G_x = -1, H_y = -1, H_x = -1,	
				center_y = -1, center_x = -1, 
				y_min = -1, x_min = -1, y_max = -1, x_max = -1;
			for (Integer[] points : set1)
			{
				if (y_max != -1 && x_max != -1 && y_min != -1 && x_min != -1)
				{
					if (y_max < points[0]) y_max = points[0];
					if (y_min > points[0]) y_min = points[0];
					if (x_max < points[1]) x_max = points[1];
					if (x_min > points[1]) x_min = points[1];					
				}
				else
				{
					y_max = y_min = points[0];
					x_max = x_min = points[1];
				}
			}
			for (Integer[] points : set1)
			{
				if (y_min == points[0] && x_min == points[1])
				{
					A_y = points[0];
					A_x = points[1];
				}
				if (y_min == points[0] && x_max == points[1])
				{
					C_y = points[0];
					C_x = points[1];
				}
				if (y_max == points[0] && x_min == points[1])
				{
					G_y = points[0];
					G_x = points[1];
				}
				if (y_max == points[0] && x_max == points[1])
				{
					E_y = points[0];
					E_x = points[1];
				}
			}
			B_y = A_y;
			B_x = A_x + (C_x - A_x) / 2;
			D_y = C_y + (E_y - C_y) / 2;
			D_x = C_x;
			F_y = E_y;
			F_x = B_x;
			H_y = D_y;
			H_x = G_x;
			this.graphicsObject.setPaint(Color.RED);
			this.graphicsObject.setStroke(new BasicStroke(13.0F));
			this.graphicsObject.drawLine(A_x, A_y, A_x, A_y); 
			this.graphicsObject.drawLine(B_x, B_y, B_x, B_y);
			this.graphicsObject.drawLine(C_x, C_y, C_x, C_y);
			this.graphicsObject.drawLine(D_x, D_y, D_x, D_y);
			this.graphicsObject.drawLine(E_x, E_y, E_x, E_y);
			this.graphicsObject.drawLine(F_x, F_y, F_x, F_y);
			this.graphicsObject.drawLine(G_x, G_y, G_x, G_y);
			this.graphicsObject.drawLine(H_x, H_y, H_x, H_y);			
			for (Integer[] points : set2)
			{
				center_y = points[0];
				center_x = points[1];
			}
			this.graphicsObject.setPaint(Color.WHITE);
			this.graphicsObject.drawLine(center_x, center_y, center_x, center_y);
			int distance_y = (center_y - B_y) / 6, distance_x = (B_x - A_x) / 3;
			int delta = (int) ( (center_x - A_x) * ( 2 - Math.sqrt(2) ) ) / 4;			
			if (_shape != ShapePreview.Shape.TRIANGLE)
			{
				this.graphicsObject.setPaint(Color.GREEN);
				this.graphicsObject.drawLine(A_x + delta, A_y + delta, A_x + delta, A_y + delta);
				this.graphicsObject.drawLine(B_x, B_y + delta, B_x, B_y + delta);
				this.graphicsObject.drawLine(C_x - delta, C_y + delta, C_x - delta, C_y + delta);
				this.graphicsObject.drawLine(D_x - delta, D_y, D_x - delta, D_y);
				this.graphicsObject.drawLine(E_x - delta, E_y - delta, E_x - delta, E_y - delta);
				this.graphicsObject.drawLine(F_x, F_y - delta, F_x, F_y - delta);
				this.graphicsObject.drawLine(G_x + delta, G_y - delta, G_x + delta, G_y - delta);
				this.graphicsObject.drawLine(H_x + delta, H_y, H_x + delta, H_y);
			}
			else
			{
				this.graphicsObject.setPaint(Color.GREEN);
				this.graphicsObject.drawLine(A_x + distance_x, A_y + distance_y, A_x + distance_x, A_y + distance_y);
				this.graphicsObject.drawLine(B_x, B_y + distance_y, B_x, B_y + distance_y);
				this.graphicsObject.drawLine(C_x - distance_x, C_y + distance_y, C_x - distance_x, C_y + distance_y);
				this.graphicsObject.drawLine(E_x - distance_x, E_y - distance_y, E_x - distance_x, E_y - distance_y);				
				this.graphicsObject.drawLine(F_x, F_y - (F_y - center_y) / 2, F_x, F_y - (F_y - center_y) / 2);
				this.graphicsObject.drawLine(G_x + distance_x, G_y - distance_y, G_x + distance_x, G_y - distance_y);
			}
			switch (_shape)
			{
				case BOX:
					if 
					(
						this.matrix[ A_y + delta ][ A_x + delta ] &&
							this.matrix[ B_y + delta ][B_x] &&
								this.matrix[ C_y + delta ][ C_x - delta ] &&
									this.matrix[D_y][D_x - delta] &&
										this.matrix[ E_y - delta ][ E_x - delta ] &&
											this.matrix[ F_y - delta ][F_x] &&
												this.matrix[ G_y - delta ][ G_x + delta ] &&
													this.matrix[H_y][ H_x + delta ]
					)
					{
						++found;
						this.graphicsObject.setPaint(Color.RED);
						this.graphicsObject.drawLine(center_x - delta, center_y - delta, center_x + delta, center_y + delta);
						this.graphicsObject.drawLine(center_x + delta, center_y - delta, center_x - delta, center_y + delta);
					}							
					break;
				case CIRCLE:
					if 
					(
						!this.matrix[ A_y + delta ][ A_x + delta ] &&
							this.matrix[ B_y + delta ][B_x] &&
								!this.matrix[ C_y + delta ][ C_x - delta ] &&
									this.matrix[D_y][D_x - delta] &&
										!this.matrix[ E_y - delta ][ E_x - delta ] &&
											this.matrix[ F_y - delta ][F_x] &&
												!this.matrix[ G_y - delta ][ G_x + delta ] &&
													this.matrix[H_y][ H_x + delta ]
					)
					{
						++found;
						this.graphicsObject.setPaint(Color.RED);
						this.graphicsObject.drawLine(center_x - delta, center_y - delta, center_x + delta, center_y + delta);
						this.graphicsObject.drawLine(center_x + delta, center_y - delta, center_x - delta, center_y + delta);
					}
					break;
				case TRIANGLE:
					if
					(
						this.matrix[ A_y + distance_y ][ A_x + distance_x ] && 
							this.matrix[ B_y + distance_y ][B_x] && 
								this.matrix[ C_y + distance_y ][ C_x - distance_x ] && 
									!this.matrix[ E_y - distance_y ][ E_x - distance_x ] && 
										this.matrix[ F_y - (F_y - center_y) / 2 ][F_x] &&
											!this.matrix[ G_y - distance_y ][ G_x + distance_x ]
					)
					{
						++found;
						this.graphicsObject.setPaint(Color.RED);
						this.graphicsObject.drawLine(center_x - delta, center_y - delta, center_x + delta, center_y + delta);
						this.graphicsObject.drawLine(center_x + delta, center_y - delta, center_x - delta, center_y + delta);
					}
					break;
			}
		}
		return new int[] {found, object};
	}
	
	public Image afterProcess()
	{
		rotate();
		return (Image)this.bufferedImage;
	}
	
	private void binarize()
	{	
		int[] sample = new int[4];
		for (int y = 0; y < this.bufferedImage.getHeight(null); ++y)
			for (int x = 0; x < this.bufferedImage.getWidth(null); ++x)
			{
				this.raster.getPixel(x, y, sample);
				sample[0] = (sample[0] + sample[1] + sample[2]) / 3;			
				sample[0] = sample[1] = sample[2] = (sample[0] > 245) ? 255 : 0;
				sample[3] = 255;
				this.raster.setPixel(x, y, sample);
				this.matrix[y][x] = (sample[0] == 0) ? true : false;
			}
	}
	
	private void filter()
	{
		float[] elements =
		{
			-1f, -2f, -1f,
			-2f, 5f, -2f,
			-1f, -2f, -1f
		};
		Kernel kernel = new Kernel(3, 3, elements);
		ConvolveOp convolveOperation = new ConvolveOp(kernel);
		convolveOperation.filter(this.bufferedImage, null);
	}
	
	private void rotate()
	{
		AffineTransform transform = 
			AffineTransform.getRotateInstance(
				Math.toRadians(180), 
				this.image.getWidth(null) / 2, 
				this.image.getHeight(null) / 2
			);
		AffineTransformOp affineTransformoperation = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);		
		this.graphicsObject.drawImage(affineTransformoperation.filter(this.bufferedImage, null), 0, 0, null);
	}
	
	private BufferedImage bufferedImage;
	private Graphics2D graphicsObject;
	private Image image;
	private WritableRaster raster;	
	private boolean[][] matrix;	
}
