package gui;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public class FileIconView extends FileView
{
	public FileIconView(FileFilter aFilter, Icon anIcon)
	{
		filter = aFilter;
		icon = anIcon;
	}
	
	public Icon getIcon(File file)
	{
		if (!file.isDirectory() && filter.accept(file)) return icon;
		else return null;
	}
	
	private FileFilter filter;
	private Icon icon;
}
