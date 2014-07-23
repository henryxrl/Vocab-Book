package henryxrl.filebrowser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import henryxrl.screens.R;

/**
 * Created by Henry on 07/22/2014.
 */
public class FileAdapter extends BaseAdapter {

	private LayoutInflater _inflater;
	private List<FileInfo> _files;

	public FileAdapter(Context context, List<FileInfo> files) {
		_files = files;
		_inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return _files.size();
	}

	@Override
	public Object getItem(int position) {
		return _files.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		if (convertView == null) { // convertView 可利用，如果传入为null，执行初始化操作
			// 载入xml文件为View
			convertView = _inflater.inflate(R.layout.file_item, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.file_name);
			holder.icon = (ImageView) convertView.findViewById(R.id.file_icon);
			holder.info = (TextView) convertView.findViewById(R.id.file_info);
			holder.folder_file_action = (ImageButton) convertView.findViewById(R.id.folder_file_action_indicator);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 更新View信息
		FileInfo f = _files.get(position);
		holder.name.setText(f.Name);
		holder.icon.setImageResource(f.getIconResourceId());
		if (f.IsDirectory) {
			String toDisplay;
			int count = f.FileCount + f.FolderCount;
			if (count == 0) {
				toDisplay = "Empty folder";
			} else {
				String folderText = " folders";
				String fileText = " files";
				if (f.FileCount == 0 || f.FileCount == 1) {
					fileText = " file";
				}
				if (f.FolderCount == 0 || f.FolderCount == 1) {
					folderText = " folder";
				}

				toDisplay = String.valueOf(f.FolderCount) + folderText + "; " + String.valueOf(f.FileCount) + fileText;
			}
			holder.info.setText(toDisplay);
			holder.folder_file_action.setImageResource(R.drawable.arrow_next);
			holder.folder_file_action.setClickable(false);
		} else {
			holder.info.setText(String.valueOf(FileUtil.formetFileSize(f.Size)));
			holder.folder_file_action.setImageResource(R.drawable.file_check);
			holder.folder_file_action.setClickable(false);
			holder.folder_file_action.setSelected(false);
		}

		return convertView;
	}

	/* class ViewHolder */
	private class ViewHolder {
		TextView name;
		ImageView icon;
		TextView info;
		ImageButton folder_file_action;
	}

}
