package henryxrl.screens;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import henryxrl.filebrowser.FileAdapter;
import henryxrl.filebrowser.FileInfo;
import henryxrl.filebrowser.FileUtil;

/**
 * Created by Henry on 07/22/2014.
 */
public class FileActivity extends ListActivity {

	private TextView _filePath;
	private List<FileInfo> _files;
	private String _rootPath = FileUtil.getSDPath();
	private String _currentPath = _rootPath;
	private final String TAG = "Main";

	private Button importBtn;
	//private ImageButton folder_file_action;
	private int selectedCount = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Override how this activity is animated into view
		// The new activity is pulled in from the left and the current activity is kept still
		// This has to be called before onCreate
		overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

		setContentView(R.layout.file_browser);

		ActionBar bar = getActionBar();
		//bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);

		_filePath = (TextView) findViewById(R.id.file_path);
		importBtn = (Button) findViewById(R.id.importBtn);
		//folder_file_action = (ImageButton) findViewById(R.id.folder_file_action_indicator);

		// 获取当前目录的文件列表
		viewFiles(_currentPath);

		setTitle("选择文件");

	}

	/** 行被点击事件处理 **/
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		FileInfo f = _files.get(position);

		if (f.IsDirectory) {
			viewFiles(f.Path);
		} else {
			//openFile(f.Path);
			ImageButton folder_file_action = (ImageButton) v.findViewById(R.id.folder_file_action_indicator);
			folder_file_action.setSelected(!folder_file_action.isSelected());
			if (folder_file_action.isSelected()) {
				selectedCount++;
			} else {
				selectedCount--;
			}
			if (selectedCount > 0) {
				importBtn.setEnabled(true);
			} else {
				importBtn.setEnabled(false);
			}
			importBtn.setText(String.format("%s (%d)", getString(R.string.importBtnText), selectedCount));
		}
	}

	/** 重定义返回键事件 **/
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 拦截back按键
		if (keyCode == KeyEvent.KEYCODE_BACK) {

			selectedCount = 0;
			importBtn.setEnabled(false);
			importBtn.setText(String.format("%s (%d)", getString(R.string.importBtnText), selectedCount));

			File f = new File(_currentPath);
			String parentPath = f.getParent();
			if (parentPath != null) {
				viewFiles(parentPath);
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 获取从PasteFile传递过来的路径 **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (Activity.RESULT_OK == resultCode) {
			Bundle bundle = data.getExtras();
			if (bundle != null && bundle.containsKey("CURRENTPATH")) {
				viewFiles(bundle.getString("CURRENTPATH"));
			}
		}
	}

	/** 创建菜单 **/
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}

	/** 菜单事件 **/
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
			case android.R.id.home:
				//Toast.makeText(Vocab_Screen.this, "Clicked!", Toast.LENGTH_SHORT).show();
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/** 获取该目录下所有文件 **/
	private void viewFiles(String filePath) {

		selectedCount = 0;
		importBtn.setEnabled(false);
		importBtn.setText(String.format("%s (%d)", getString(R.string.importBtnText), selectedCount));

		ArrayList<FileInfo> tmp = getFiles(FileActivity.this, filePath);
		if (tmp != null) {
			// 清空数据
			if (_files != null) {
				_files.clear();
			}

			_files = tmp;
			// 设置当前目录
			_currentPath = filePath;
			_filePath.setText(filePath);
			// 绑定数据
			setListAdapter(new FileAdapter(this, _files));
		}
	}

	/** 打开文件 **/
	/*private void openFile(String path) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		File f = new File(path);
		String type = FileUtil.getMIMEType(f.getName());
		intent.setDataAndType(Uri.fromFile(f), type);
		startActivity(intent);
	}*/


	/** 获取一个文件夹下的所有子目录、TXT和XML文件 **/
	public static ArrayList<FileInfo> getFiles(Activity activity, String path) {
		File f;
		File[] files = null;
		try { // 读取文件
			f = new File(path);
			files = f.listFiles();
			if (files == null) {
				Toast.makeText(activity, String.format("无法打开: %1$s", path), Toast.LENGTH_SHORT).show();
				return null;
			}
		} catch (Exception ex) {
			Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_SHORT).show();
		}

		ArrayList<FileInfo> fileList = new ArrayList<FileInfo>();
		// 获取文件列表
		for (File file : files) {
			if (file.isDirectory() || FileUtil.getFileExt(file.getName()).compareToIgnoreCase("txt") == 0 || FileUtil.getFileExt(file.getName()).compareToIgnoreCase("xml") == 0) {
				fileList.add(FileUtil.getFileInfo(file));
			}
		}

		// 排序
		Collections.sort(fileList, new FileComparator());

		return fileList;
	}

	static class FileComparator implements Comparator<FileInfo> {

		public int compare(FileInfo file1, FileInfo file2) {
			// 文件夹排在前面
			if (file1.IsDirectory && !file2.IsDirectory) {
				return -1;
			} else if (!file1.IsDirectory && file2.IsDirectory) {
				return 1;
			}
			// 相同类型按名称排序
			return file1.Name.compareToIgnoreCase(file2.Name);
		}
	}


}
