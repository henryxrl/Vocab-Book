package henryxrl.filebrowser;

import henryxrl.screens.R;

/**
 * Created by Henry on 07/22/2014.
 */
public class FileInfo {
	public String Name;
	public String Path;
	public long Size;
	public boolean IsDirectory = false;
	public int FileCount = 0;
	public int FolderCount = 0;

	public int getIconResourceId() {
		if (IsDirectory) {
			return R.drawable.folder;
		}
		if (FileUtil.getFileExt(Name).compareToIgnoreCase("txt") == 0) {
			return R.drawable.txt;
		}
		if (FileUtil.getFileExt(Name).compareToIgnoreCase("xml") == 0) {
			return R.drawable.xml;
		}
		return -1;
	}
}
