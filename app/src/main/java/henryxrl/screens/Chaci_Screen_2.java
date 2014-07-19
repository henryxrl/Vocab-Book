package henryxrl.screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class Chaci_Screen_2 extends Activity {

	private AutoCompleteTextView actvWord;
	private ImageButton imgButton;
	private TextView resultText;
	private final String DATABASE_FILENAME = "dictionary.db";
	private SQLiteDatabase database;

	private Bundle bundle;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chaci_2);
		database = openDatabase();
		actvWord = (AutoCompleteTextView) findViewById(R.id.actvWord);
		imgButton = (ImageButton) findViewById(R.id.actvButton);
		imgButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		resultText = (TextView) findViewById(R.id.textView);

		final DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this, null, true);

		actvWord.setAdapter(dictionaryAdapter);
		actvWord.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View view, MotionEvent motionEvent) {
				final int DRAWABLE_LEFT = 0;
				final int DRAWABLE_TOP = 1;
				final int DRAWABLE_RIGHT = 2;
				final int DRAWABLE_BOTTOM = 3;

				if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
					if(motionEvent.getRawX() >= (actvWord.getRight() - actvWord.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
						//Toast.makeText(Chaci_Screen_2.this, "clicked clear button!", Toast.LENGTH_SHORT).show();
						actvWord.setText("");
					}
				}
				return false;
			}
		});
		actvWord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
				String sql = "SELECT * FROM dictionary WHERE word = ?";
				Cursor cursor = database.rawQuery(sql, new String[] { actvWord.getText().toString() });

				//  如果查找单词，显示其中文的意思
				if (cursor.moveToFirst())
				{
					bundle = new Bundle();
					bundle.putString("word", cursor.getString(cursor.getColumnIndex("word")));
					bundle.putString("phonetic", cursor.getString(cursor.getColumnIndex("phonetic")));
					bundle.putString("transCN", cursor.getString(cursor.getColumnIndex("transCN")));

					String deliminator = "\\\\SENT\\\\";
					String sents = cursor.getString(cursor.getColumnIndex("sent"));
					String sent1 = "";
					String sent2 = "";
					if (sents != null) {
						if (sents.contains(deliminator)) {
							sent1 = sents.substring(deliminator.length());
							if (sent1.contains(deliminator)) {
								String[] temp = sent1.split(deliminator);
								// Truncate the first and the last character because of the remaining \ symbol from the deliminator
								sent1 = temp[0].substring(0, temp[0].length()-1);
								sent2 = temp[1].substring(1);
							}
						}
					}
					bundle.putString("sent1", sent1);
					bundle.putString("sent2", sent2);

					bundle.putString("transEN", cursor.getString(cursor.getColumnIndex("transEN")));
					bundle.putString("synonym", cursor.getString(cursor.getColumnIndex("synonym")));
					bundle.putString("antonym", cursor.getString(cursor.getColumnIndex("antonym")));

					actvWord.clearFocus();

					Intent i = new Intent(getApplicationContext(), Chaci_Result_Screen.class);
					i.putExtra("result", bundle);
					startActivity(i);

				}

			}
		});

	}

	@Override
	public void onResume() {
		super.onResume();
		actvWord.setText("");
		resultText.setText("");
	}



	private SQLiteDatabase openDatabase()
	{
		try
		{
			String dbPath = getBaseContext().getDatabasePath(DATABASE_FILENAME).getPath();
			if (!(new File (dbPath)).exists()) {
				InputStream is = getResources().openRawResource(R.raw.dictionary);
				FileOutputStream fos = new FileOutputStream(dbPath);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0) {
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}

			// 打开/sdcard/dictionary目录中的dictionary.db文件
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(dbPath, null);
			return database;
		}
		catch (Exception e)
		{
			System.out.println("EXCEPTION OCCURRED: " + e.toString());
		}
		return null;
	}


	class DictionaryAdapter extends CursorAdapter
	{
		private LayoutInflater layoutInflater;

		@Override
		public CharSequence convertToString(Cursor cursor)
		{
			return cursor == null ? "" : cursor.getString(cursor.getColumnIndex("_id"));
		}

		private void setView(View view, Cursor cursor)
		{
			TextView tvWordItem = (TextView) view;
			tvWordItem.setText(cursor.getString(cursor.getColumnIndex("_id")));
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor)
		{
			setView(view, cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent)
		{
			View view = layoutInflater.inflate(R.layout.chaci_item, parent, false);
			setView(view, cursor);
			return view;
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (constraint != null) {
				return database.rawQuery("SELECT word AS _id FROM dictionary WHERE word LIKE ? ", new String[] { constraint.toString() + "%" });
			}
			else {
				return null;
			}
		}

		public DictionaryAdapter(Context context, Cursor c, boolean autoRequery)
		{
			super(context, c, autoRequery);
			layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}

}
