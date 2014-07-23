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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import henryxrl.database.DictionaryDatabase;
import henryxrl.datatype.VocabWord;

public class Chaci_Screen_2 extends Activity {

	private AutoCompleteTextView actvWord;
	//private ImageButton imgButton;
	private TextView resultText;
	private DictionaryDatabase dictionary;

	private Bundle bundle;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.chaci_2);
		//database = openDatabase();
		dictionary = new DictionaryDatabase(getBaseContext());
		actvWord = (AutoCompleteTextView) findViewById(R.id.actvWord);
		//imgButton = (ImageButton) findViewById(R.id.actvButton);
		/*imgButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});*/
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

				VocabWord vocabWord = dictionary.lookUp(actvWord.getText().toString(), true, true, true, true, true, true, true);

				bundle = new Bundle();
				bundle.putParcelable("vocabWord", vocabWord);

				actvWord.clearFocus();

				Intent i = new Intent(getApplicationContext(), Chaci_Result_Screen.class);
				i.putExtra("result", bundle);
				startActivity(i);

			}
		});


	}

	@Override
	public void onResume() {
		super.onResume();
		actvWord.setText("");
		resultText.setText("");
	}



	class DictionaryAdapter extends CursorAdapter {
		private LayoutInflater layoutInflater;

		@Override
		public CharSequence convertToString(Cursor cursor) {
			return cursor == null ? "" : cursor.getString(cursor.getColumnIndex("_id"));
		}

		private void setView(View view, Cursor cursor) {
			TextView tvWordItem = (TextView) view;
			tvWordItem.setText(cursor.getString(cursor.getColumnIndex("_id")));
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor)
		{
			setView(view, cursor);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			View view = layoutInflater.inflate(R.layout.chaci_item, parent, false);
			setView(view, cursor);
			return view;
		}

		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			if (constraint != null) {
				return dictionary.dict.rawQuery("SELECT word AS _id FROM dictionary WHERE word LIKE ? ", new String[] { constraint.toString() + "%" });
			}
			else {
				return null;
			}
		}

		public DictionaryAdapter(Context context, Cursor c, boolean autoRequery) {
			super(context, c, autoRequery);
			layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}

}
