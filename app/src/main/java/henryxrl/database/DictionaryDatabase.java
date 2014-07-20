package henryxrl.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import henryxrl.datatype.VocabWord;
import henryxrl.screens.R;

/**
 * Created by Henry on 07/20/2014.
 */
public class DictionaryDatabase {
	private static final String LOG = DictionaryDatabase.class.getName();

	public SQLiteDatabase dict;
	private final String DATABASE_FILENAME = "dictionary.db";

	private Context context;

	public DictionaryDatabase(Context c) {
		this.context = c;
		this.dict = openDatabase();
	}

	private SQLiteDatabase openDatabase() {
		try {
			String dbPath = context.getDatabasePath(DATABASE_FILENAME).getPath();
			if (!(new File (dbPath)).exists()) {
				InputStream is = context.getResources().openRawResource(R.raw.dictionary);
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
		} catch (Exception e) {
			System.out.println("EXCEPTION OCCURRED: " + e.toString());
		}
		return null;
	}

	public VocabWord lookUp(String word, boolean pho, boolean cn, boolean en, boolean s1, boolean s2, boolean syn, boolean ant) {
		VocabWord vocabWord = new VocabWord(-1L);
		vocabWord.word = word.replaceAll("\\\\n", "\\\n");

		String selectQuery = "SELECT * FROM dictionary WHERE word = ? LIMIT 1";

		Log.e(LOG, selectQuery);

		//SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = dict.rawQuery(selectQuery, new String[] { word });

		//  如果查找单词，显示其中文的意思
		if (cursor.moveToFirst()) {
			if (pho)
				vocabWord.phonetic = cursor.getString(cursor.getColumnIndex("phonetic")).replaceAll("\\\\n", "\\\n");

			if (cn)
				vocabWord.transCN = cursor.getString(cursor.getColumnIndex("transCN")).replaceAll("\\\\n", "\\\n");

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
						sent1 = temp[0].substring(0, temp[0].length() - 1);
						sent2 = temp[1].substring(1);
					}
				}
			}
			if (s1)
				vocabWord.sentCN = sent1.replaceAll("\\\\n", "\\\n");
			if (s2)
				vocabWord.sentEN = sent2.replaceAll("\\\\n", "\\\n");

			if (en)
				vocabWord.transEN = cursor.getString(cursor.getColumnIndex("transEN")).replaceAll("\\\\n", "\\\n");

			if (syn)
				vocabWord.synonym = cursor.getString(cursor.getColumnIndex("synonym")).replaceAll("\\\\n", "\\\n");

			if (ant)
				vocabWord.antonym = cursor.getString(cursor.getColumnIndex("antonym")).replaceAll("\\\\n", "\\\n");

		}

		return vocabWord;
	}

}
