package gui;

import java.io.File;
import javax.swing.Icon;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

public class FileIconView extends FileView
{
	public FileIconView(FileFilter _filter, Icon _icon)
	{
		this.filter = _filter;
		this.icon = _icon;
	}
	
	public Icon getIcon(File _file)
	{
		if (!_file.isDirectory() && this.filter.accept(_file)) return this.icon;
		else return null;
	}
	
	private FileFilter filter;
	private Icon icon;
}