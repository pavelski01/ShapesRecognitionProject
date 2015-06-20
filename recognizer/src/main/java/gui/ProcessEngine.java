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
	public ProcessEngine(Image aImage)
	{
		image = aImage;
		bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);		
		bufferedImage.getGraphics().drawImage(image, 0, 0, null);		
		filter();
		graphicsObject = (Graphics2D) bufferedImage.getGraphics();
		rotate();
		raster = bufferedImage.getRaster();
		matrix = new boolean[bufferedImage.getHeight(null)][bufferedImage.getWidth(null)];
		binarize();
	}
	
	public Map<String, LinkedList<TreeSet<Integer[]>>> shapeParse()
	{
		int number = 0;
		boolean[][] stencil = new boolean[matrix.length][matrix[0].length];
		for (int j = 0; j < matrix.length; ++j)
			for (int i = 0; i < matrix[j].length; ++i)
				stencil[j][i] = matrix[j][i];
		map = new HashMap<String, LinkedList<TreeSet<Integer[]>>>();		
		comparator = new Comparator<Integer[]>()
		{
			public int compare(Integer[] a, Integer[] b)
			{
				if (a[0] - b[0] != 0) return a[0] - b[0];
				else if (a[1] - b[1] != 0) return a[1] - b[1];
				else return 0;
			}					
		};		
		for (int y = 0; y < stencil.length; ++y)
			for (int x = 0; x < stencil[y].length; ++x)
			{				
				if (stencil[y][x]) {						
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
	
	public int[] shapeRecognize(Map<String, LinkedList<TreeSet<Integer[]>>> map, ShapePreview.Shape shape)
	{
		int found = 0, object = 0;
		for (Map.Entry<String, LinkedList<TreeSet<Integer[]>>> entry : map.entrySet())
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
			graphicsObject.setPaint(Color.RED);
			graphicsObject.setStroke(new BasicStroke(13.0F));
			graphicsObject.drawLine(A_x, A_y, A_x, A_y); 
			graphicsObject.drawLine(B_x, B_y, B_x, B_y);
			graphicsObject.drawLine(C_x, C_y, C_x, C_y);
			graphicsObject.drawLine(D_x, D_y, D_x, D_y);
			graphicsObject.drawLine(E_x, E_y, E_x, E_y);
			graphicsObject.drawLine(F_x, F_y, F_x, F_y);
			graphicsObject.drawLine(G_x, G_y, G_x, G_y);
			graphicsObject.drawLine(H_x, H_y, H_x, H_y);			
			for (Integer[] points : set2)
			{
				center_y = points[0];
				center_x = points[1];
			}
			graphicsObject.setPaint(Color.WHITE);
			graphicsObject.drawLine(center_x, center_y, center_x, center_y);
			int distance_y = (center_y - B_y) / 6, distance_x = (B_x - A_x) / 3;
			int delta = (int) ( (center_x - A_x) * ( 2 - Math.sqrt(2) ) ) / 4;			
			if (shape != ShapePreview.Shape.TRIANGLE)
			{
				graphicsObject.setPaint(Color.GREEN);
				graphicsObject.drawLine(A_x + delta, A_y + delta, A_x + delta, A_y + delta);
				graphicsObject.drawLine(B_x, B_y + delta, B_x, B_y + delta);
				graphicsObject.drawLine(C_x - delta, C_y + delta, C_x - delta, C_y + delta);
				graphicsObject.drawLine(D_x - delta, D_y, D_x - delta, D_y);
				graphicsObject.drawLine(E_x - delta, E_y - delta, E_x - delta, E_y - delta);
				graphicsObject.drawLine(F_x, F_y - delta, F_x, F_y - delta);
				graphicsObject.drawLine(G_x + delta, G_y - delta, G_x + delta, G_y - delta);
				graphicsObject.drawLine(H_x + delta, H_y, H_x + delta, H_y);
			}
			else
			{
				graphicsObject.setPaint(Color.GREEN);
				graphicsObject.drawLine(A_x + distance_x, A_y + distance_y, A_x + distance_x, A_y + distance_y);
				graphicsObject.drawLine(B_x, B_y + distance_y, B_x, B_y + distance_y);
				graphicsObject.drawLine(C_x - distance_x, C_y + distance_y, C_x - distance_x, C_y + distance_y);
				graphicsObject.drawLine(E_x - distance_x, E_y - distance_y, E_x - distance_x, E_y - distance_y);				
				graphicsObject.drawLine(F_x, F_y - (F_y - center_y) / 2, F_x, F_y - (F_y - center_y) / 2);
				graphicsObject.drawLine(G_x + distance_x, G_y - distance_y, G_x + distance_x, G_y - distance_y);
			}
			switch (shape)
			{
				case BOX:
					if 
					(
						matrix[ A_y + delta ][ A_x + delta ] &&
							matrix[ B_y + delta ][B_x] &&
								matrix[ C_y + delta ][ C_x - delta ] &&
									matrix[D_y][D_x - delta] &&
										matrix[ E_y - delta ][ E_x - delta ] &&
											matrix[ F_y - delta ][F_x] &&
												matrix[ G_y - delta ][ G_x + delta ] &&
													matrix[H_y][ H_x + delta ]
					)
					{
						++found;
						graphicsObject.setPaint(Color.RED);
						graphicsObject.drawLine(center_x - delta, center_y - delta, center_x + delta, center_y + delta);
						graphicsObject.drawLine(center_x + delta, center_y - delta, center_x - delta, center_y + delta);
					}							
					break;
				case CIRCLE:
					if 
					(
						!matrix[ A_y + delta ][ A_x + delta ] &&
							matrix[ B_y + delta ][B_x] &&
								!matrix[ C_y + delta ][ C_x - delta ] &&
									matrix[D_y][D_x - delta] &&
										!matrix[ E_y - delta ][ E_x - delta ] &&
											matrix[ F_y - delta ][F_x] &&
												!matrix[ G_y - delta ][ G_x + delta ] &&
													matrix[H_y][ H_x + delta ]
					)
					{
						++found;
						graphicsObject.setPaint(Color.RED);
						graphicsObject.drawLine(center_x - delta, center_y - delta, center_x + delta, center_y + delta);
						graphicsObject.drawLine(center_x + delta, center_y - delta, center_x - delta, center_y + delta);
					}
					break;
				case TRIANGLE:
					if
					(
						matrix[ A_y + distance_y ][ A_x + distance_x ] && 
							matrix[ B_y + distance_y ][B_x] && 
								matrix[ C_y + distance_y ][ C_x - distance_x ] && 
									!matrix[ E_y - distance_y ][ E_x - distance_x ] && 
										matrix[ F_y - (F_y - center_y) / 2 ][F_x] &&
											!matrix[ G_y - distance_y ][ G_x + distance_x ]
					)
					{
						++found;
						graphicsObject.setPaint(Color.RED);
						graphicsObject.drawLine(center_x - delta, center_y - delta, center_x + delta, center_y + delta);
						graphicsObject.drawLine(center_x + delta, center_y - delta, center_x - delta, center_y + delta);
					}
					break;
			}
		}
		return new int[] {found, object};
	}
	
	public Image afterProcess()
	{
		rotate();
		return (Image)bufferedImage;
	}
	
	private void binarize()
	{	
		int[] sample = new int[4];
		for (int y = 0; y < bufferedImage.getHeight(null); ++y)
			for (int x = 0; x < bufferedImage.getWidth(null); ++x)
			{
				raster.getPixel(x, y, sample);
				sample[0] = (sample[0] + sample[1] + sample[2]) / 3;			
				sample[0] = sample[1] = sample[2] = (sample[0] > 245) ? 255 : 0;
				sample[3] = 255;
				raster.setPixel(x, y, sample);
				matrix[y][x] = (sample[0] == 0) ? true : false;
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
		kernel = new Kernel(3, 3, elements);
		C_operation = new ConvolveOp(kernel);
		C_operation.filter(bufferedImage, null);
	}
	
	private void rotate()
	{
		transform = AffineTransform.getRotateInstance(Math.toRadians(180), image.getWidth(null) / 2, image.getHeight(null) / 2);
		AT_operation = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);		
		graphicsObject.drawImage(AT_operation.filter(bufferedImage, null), 0, 0, null);
	}
	
	private boolean[][] matrix;
	private String figureKey;
	private Comparator<Integer[]> comparator;
	private Map<String, LinkedList<TreeSet<Integer[]>>> map;	
	private AffineTransform transform;
	private AffineTransformOp AT_operation;
	private Kernel kernel;
	private ConvolveOp C_operation;
	private Image image;
	private BufferedImage bufferedImage;
	private Graphics2D graphicsObject;
	private WritableRaster raster;	
}
