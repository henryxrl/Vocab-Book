package henryxrl.filebrowser;

import android.os.Environment;

import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by Henry on 07/22/2014.
 */
public class FileUtil {

	/** 获取SD路径 **/
	public static String getSDPath() {
		// 判断sd卡是否存在
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			File sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			File quBeiDir = new File(combinePath(sdDir.getPath(), "QuBei"));
			if (quBeiDir.exists()) {
				return quBeiDir.getPath();
			}
			return sdDir.getPath();
		}
		return "/sdcard";
	}

	/** 获取文件信息 **/
	public static FileInfo getFileInfo(File f) {
		FileInfo info = new FileInfo();
		info.Name = f.getName();
		info.IsDirectory = f.isDirectory();
		info.Path = f.getPath();
		calcFileContent(info, f);
		return info;
	}

	/** 获取文件扩展名 **/
	public static String getFileExt(String FileName) {
		return FileName.substring((FileName.lastIndexOf(".") + 1), FileName.length());
	}

	/** 计算文件内容 **/
	private static void calcFileContent(FileInfo info, File f) {
		if (f.isFile()) {
			info.Size += f.length();
		}
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			if (files != null && files.length > 0) {
				for (File tmp : files) {
					if (tmp.isDirectory()) {
						info.FolderCount++;
					} else if (tmp.isFile()) {
						info.FileCount++;
					}
					if (info.FileCount + info.FolderCount >= 10000) { // 超过一万不计算
						break;
					}
					//calcFileContent(info, tmp);
				}
			}
		}
	}

	/** 转换文件大小 **/
	public static String formetFileSize(long fileS) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString;
		if (fileS < 1024) {
			fileSizeString = fileS + " B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + " KB";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + " MB";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + " GB";
		}
		return fileSizeString;
	}

	/** 合并路径 **/
	public static String combinePath(String path, String fileName) {
		return path + (path.endsWith(File.separator) ? "" : File.separator) + fileName;
	}

}
