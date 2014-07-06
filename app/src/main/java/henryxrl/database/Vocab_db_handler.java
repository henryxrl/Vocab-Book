package henryxrl.database;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.Pair;

import henryxrl.datatype.VocabBook;
import henryxrl.datatype.VocabList;
import henryxrl.datatype.VocabWord;

public class Vocab_db_handler extends SQLiteOpenHelper {

	// Logcat tag
	private static final String LOG = Vocab_db_handler.class.getName();

	// Database Version
	private static final int DATABASE_VERSION = 1;

	// Database Name
	private static final String DATABASE_NAME = "Vocab";

	// Table Names
	private static final String TABLE_VOCAB_BOOKS = "Vocab_books";
	private static final String TABLE_VOCAB_LISTS = "Vocab_lists";
	private static final String TABLE_VOCAB_WORDS = "Vocab_words";

	// Common column names
	private static final String KEY_BOOK_ID = "book_id";
	//private static final String KEY_CREATED_AT = "created_at";

	// VOCAB_BOOKS Table - column names
	private static final String KEY_BOOK_NAME = "book_name";
	//private static final String KEY_BOOK_STATUS = "status";
	private static final String KEY_BOOK_RATING = "book_rating";

	// VOCAB_LISTS Table - column names
	private static final String KEY_LIST_ID = "list_id";
	private static final String KEY_LIST_NAME = "list_name";
	private static final String KEY_LIST_RATING = "list_rating";

	// VOCAB_WORDS Table - column names
	private static final String KEY_WORD_ID = "word_id";
	private static final String KEY_WORD = "word";
	private static final String KEY_PHONETIC = "phonetic";
	private static final String KEY_TRANSCN = "transCN";
	private static final String KEY_TRANSEN = "transEN";
	private static final String KEY_SENTCN = "sentCN";
	private static final String KEY_SENTEN = "sentEN";
	private static final String KEY_SYNONYM = "synonym";
	private static final String KEY_ANTONYM = "antonym";
	private static final String KEY_WORD_RATING = "word_rating";

	// Table Create Statements
	// VOCAB_BOOKS Table create statement
	private static final String CREATE_TABLE_VOCAB_BOOKS = "CREATE TABLE "
			+ TABLE_VOCAB_BOOKS + "(" + KEY_BOOK_ID + " INTEGER PRIMARY KEY," + KEY_BOOK_NAME
			+ " TEXT," + KEY_BOOK_RATING + " FLOAT" + ")";
			// + " TEXT," + KEY_CREATED_AT + " DATETIME" + ")";

	// VOCAB_LISTS Table create statement
	private static final String CREATE_TABLE_VOCAB_LISTS = "CREATE TABLE " + TABLE_VOCAB_LISTS
			+ "(" + KEY_BOOK_ID + " INTEGER," + KEY_LIST_ID + " INTEGER,"
			+ KEY_LIST_NAME + " TEXT," + KEY_LIST_RATING + " FLOAT,"
			+ " PRIMARY KEY (" + KEY_BOOK_ID + " , " + KEY_LIST_ID + ") )";

	// VOCAB_WORDS Table create statement
	private static final String CREATE_TABLE_VOCAB_WORDS = "CREATE TABLE " + TABLE_VOCAB_WORDS
			+ "(" + KEY_BOOK_ID + " INTEGER," + KEY_LIST_ID + " INTEGER,"
			+ KEY_WORD_ID + " INTEGER," + KEY_WORD + " TEXT," + KEY_PHONETIC + " TEXT,"
			+ KEY_TRANSCN + " TEXT," + KEY_TRANSEN + " TEXT," + KEY_SENTCN + " TEXT,"
			+ KEY_SENTEN + " TEXT," + KEY_SYNONYM + " TEXT," + KEY_ANTONYM + " TEXT,"
			+ KEY_WORD_RATING + " FLOAT,"
			+ " PRIMARY KEY (" + KEY_BOOK_ID + " , " + KEY_LIST_ID + " , " + KEY_WORD_ID + ") )";

	public Vocab_db_handler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// creating required tables
		db.execSQL(CREATE_TABLE_VOCAB_BOOKS);
		db.execSQL(CREATE_TABLE_VOCAB_LISTS);
		db.execSQL(CREATE_TABLE_VOCAB_WORDS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// on upgrade drop older tables
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCAB_BOOKS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCAB_LISTS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VOCAB_WORDS);

		// create new tables
		onCreate(db);
	}

	// ------------------------ VOCAB_BOOKS Table methods ----------------//

	/**
	 * Creating a VocabBook
	 */
	public long createVocabBookInfo(VocabBook vocabBook) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_BOOK_ID, vocabBook.id);
		values.put(KEY_BOOK_NAME, vocabBook.name);
		//values.put(KEY_CREATED_AT, getDateTime());
		values.put(KEY_BOOK_RATING, 0);

		// insert row
		long book_id = db.insert(TABLE_VOCAB_BOOKS, null, values);

		return book_id;
	}

	/**
	 * get single VocabBook info
	 */
	public HashMap<Long, String[]> getVocabBookInfo(long book_id) {
		HashMap<Long, String[]> vbInfo = new HashMap<Long, String[]>();
		String selectQuery = "SELECT  * FROM " + TABLE_VOCAB_BOOKS + " WHERE "
				+ KEY_BOOK_ID + " = " + book_id;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		/*if (c != null)
			c.moveToFirst();*/

		if (c.moveToFirst()) {
			vbInfo.put(c.getLong(c.getColumnIndex(KEY_BOOK_ID)), new String[]{c.getString(c.getColumnIndex(KEY_BOOK_NAME)), c.getString(c.getColumnIndex(KEY_BOOK_RATING))});
			//vb.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));
		}

		c.close();

		return vbInfo;
	}

	/**
	 * getting all VocabBooks info
	 * */
	public HashMap<Long, String[]> getAllVocabBooksInfo() {
		HashMap<Long, String[]> vocabBookInfoList = new HashMap<Long, String[]>();
		String selectQuery = "SELECT  * FROM " + TABLE_VOCAB_BOOKS;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				vocabBookInfoList.put(c.getLong(c.getColumnIndex(KEY_BOOK_ID)), new String[] { c.getString(c.getColumnIndex(KEY_BOOK_NAME)), c.getString(c.getColumnIndex(KEY_BOOK_RATING)) });
			} while (c.moveToNext());
		}

		c.close();

		return vocabBookInfoList;
	}

	/**
	 * getting VocabBook count
	 */
	public int getVocabBookCount() {
		String countQuery = "SELECT  * FROM " + TABLE_VOCAB_BOOKS;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * getting VocabBook 4-star-and-above count
	 */
	public int getBook4StarAndAboveCount(long book_id) {
		String countQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE " + KEY_BOOK_ID + " = " + book_id + " and " + KEY_WORD_RATING + " >= 4";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * getting VocabBook total word count
	 */
	public int getBookTotalWordCount(long book_id) {
		String countQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE " + KEY_BOOK_ID + " = " + book_id;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * Deleting a VocabBook
	 */
	public void deleteVocabBook(long book_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_VOCAB_BOOKS, KEY_BOOK_ID + " = " + book_id, null);
				//new String[] { String.valueOf(book_id) });
	}

	// ------------------------ VOCAB_LISTS Table methods ----------------//

	/**
	 * Creating a VocabList
	 */
	public long createVocabListInfo(long book_id, VocabList vocabList) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_BOOK_ID, book_id);
		values.put(KEY_LIST_ID, vocabList.id);
		values.put(KEY_LIST_NAME, vocabList.name);
		//values.put(KEY_CREATED_AT, getDateTime());
		values.put(KEY_LIST_RATING, 0);

		// insert row
		long list_id = db.insert(TABLE_VOCAB_LISTS, null, values);

		return list_id;
	}

	/**
	 * getting all VocabList info
	 * */
	public HashMap<Long, String[]> getAllVocabListInfo(long book_id) {
		HashMap<Long, String[]> vocabListInfoList = new HashMap<Long, String[]>();
		String selectQuery = "SELECT  * FROM " + TABLE_VOCAB_LISTS + " WHERE "
				+ KEY_BOOK_ID + " = " + book_id;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				vocabListInfoList.put(c.getLong(c.getColumnIndex(KEY_LIST_ID)), new String[] { c.getString(c.getColumnIndex(KEY_LIST_NAME)), c.getString(c.getColumnIndex(KEY_LIST_RATING)) });
			} while (c.moveToNext());
		}

		c.close();

		return vocabListInfoList;
	}

	/**
	 * get single VocabList info
	 */
	public HashMap<Long, String[]> getVocabListInfo(long book_id, long list_id) {
		HashMap<Long, String[]> vlInfo = new HashMap<Long, String[]>();
		String selectQuery = "SELECT  * FROM " + TABLE_VOCAB_LISTS + " WHERE "
				+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = " + list_id;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			vlInfo.put(c.getLong(c.getColumnIndex(KEY_LIST_ID)), new String[]{c.getString(c.getColumnIndex(KEY_LIST_NAME)), c.getString(c.getColumnIndex(KEY_LIST_RATING))});
		}

		c.close();

		return vlInfo;
	}

	/**
	 * getting VocabBook count
	 */
	public int getVocabListCount(long book_id) {
		String countQuery = "SELECT  * FROM " + TABLE_VOCAB_LISTS + " WHERE "
				+ KEY_BOOK_ID + " = " + book_id;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * getting VocabWord 4-star-and-above count
	 */
	public int getList4StarAndAboveCount(long book_id, long list_id) {
		String countQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE " + KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = " + list_id + " and " + KEY_WORD_RATING + " >= 4";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * Deleting all VocabList
	 */
	public void deleteAllVocabList(long book_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_VOCAB_LISTS, KEY_BOOK_ID + " = ?",
				new String[] { String.valueOf(book_id) });
	}

	// ------------------------ VOCAB_WORDS Table methods ----------------//

	/**
	 * Creating VocabWord
	 */
	public long createVocabWord(long book_id, long list_id, VocabWord vocabWord) {
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues values = new ContentValues();
		values.put(KEY_BOOK_ID, book_id);
		values.put(KEY_LIST_ID, list_id);
		values.put(KEY_WORD_ID, vocabWord.id);
		values.put(KEY_WORD, vocabWord.word);
		values.put(KEY_PHONETIC, vocabWord.phonetic);
		values.put(KEY_TRANSCN, vocabWord.transCN);
		values.put(KEY_TRANSEN, vocabWord.transEN);
		values.put(KEY_SENTCN , vocabWord.sentCN);
		values.put(KEY_SENTEN , vocabWord.sentEN);
		values.put(KEY_SYNONYM, vocabWord.synonym);
		values.put(KEY_ANTONYM, vocabWord.antonym);
		//values.put(KEY_CREATED_AT, getDateTime());
		values.put(KEY_WORD_RATING, 0);

		long word_id = db.insert(TABLE_VOCAB_WORDS, null, values);

		return word_id;
	}

	/**
	 * getting all VocabWord info in a list
	 * */
	public HashMap<Long, String[]> getAllVocabWordInfo(long book_id, long list_id) {
		HashMap<Long, String[]> vocabListInfoList = new HashMap<Long, String[]>();
		String selectQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
				+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = " + list_id;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				vocabListInfoList.put(c.getLong(c.getColumnIndex(KEY_WORD_ID)), new String[] { c.getString(c.getColumnIndex(KEY_WORD)), c.getString(c.getColumnIndex(KEY_TRANSCN)), c.getString(c.getColumnIndex(KEY_WORD_RATING)) });
			} while (c.moveToNext());
		}

		c.close();

		return vocabListInfoList;
	}

	/**
	 * getting VocabWord count in a list
	 */
	public int getVocabWordCount(long book_id, long list_id) {
		String countQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
				+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = " + list_id;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(countQuery, null);

		int count = cursor.getCount();
		cursor.close();

		return count;
	}

	/**
	 * getting one Vocab Word info
	 * */
	public HashMap<Long, String[]> getVocabWordInfo(long book_id, long list_id, long word_id) {
		HashMap<Long, String[]> vocabWord = new HashMap<Long, String[]>();
		String selectQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
				+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = " + list_id + " and " + KEY_WORD_ID + " = " + word_id;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			vocabWord.put(c.getLong(c.getColumnIndex(KEY_WORD_ID)),
					new String[] { c.getString(c.getColumnIndex(KEY_WORD)), c.getString(c.getColumnIndex(KEY_PHONETIC)),
							c.getString(c.getColumnIndex(KEY_TRANSCN)), c.getString(c.getColumnIndex(KEY_TRANSEN)),
							c.getString(c.getColumnIndex(KEY_SENTCN)), c.getString(c.getColumnIndex(KEY_SENTEN)),
							c.getString(c.getColumnIndex(KEY_SYNONYM)), c.getString(c.getColumnIndex(KEY_ANTONYM)),
							c.getString(c.getColumnIndex(KEY_WORD_RATING)) }
			);
		}

		//System.out.println("book " + book_id + " -> list " + list_id + " -> word " + word_id + " -> star " + c.getString(c.getColumnIndex(KEY_WORD_RATING)));

		c.close();

		return vocabWord;
	}

	/**
	 * Deleting all VocabWord
	 */
	public void deleteAllVocabWord(long book_id) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_VOCAB_WORDS, KEY_BOOK_ID + " = ?",
				new String[] { String.valueOf(book_id) });
	}

	// ------------------------ Rating methods ----------------//

	/**
	 * update rating
	 */
	public void updateWordRating(long book_id, long list_id, long word_id, float rating) {
		String selectQuery = "UPDATE " + TABLE_VOCAB_WORDS + " SET " + KEY_WORD_RATING + " = " + rating
				+ " WHERE " + KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = " + list_id + " and " + KEY_WORD_ID + " = " + word_id;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			System.out.println("book " + book_id + " -> list " + list_id + " -> word " + word_id + " -> star " + c.getString(c.getColumnIndex(KEY_WORD_RATING)));
		}

		c.close();

	}

	public void updateListRating(long book_id, long list_id) {
		int wordCount = getVocabWordCount(book_id, list_id);

		HashMap<Long, String[]> wordInfo = getAllVocabWordInfo(book_id, list_id);
		float totalRating = 0f;
		for (int i = 0; i < wordCount; i++) {
			totalRating += Float.parseFloat(wordInfo.get(Long.valueOf(i))[2]);
		}

		float listRating = totalRating / wordCount;

		String selectQuery = "UPDATE " + TABLE_VOCAB_LISTS + " SET " + KEY_LIST_RATING + " = " + listRating
				+ " WHERE " + KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = " + list_id;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			System.out.println("book " + book_id + " -> list " + list_id + " -> star " + c.getString(c.getColumnIndex(KEY_LIST_RATING)));
		}

		c.close();
	}

	public void updateBookRating(long book_id) {
		int listCount = getVocabListCount(book_id);

		HashMap<Long, String[]> listInfo = getAllVocabListInfo(book_id);
		float totalRating = 0f;
		for (int i = 0; i < listCount; i++) {
			totalRating += Float.parseFloat(listInfo.get(Long.valueOf(i))[1]);
		}

		float bookRating = totalRating / listCount;

		String selectQuery = "UPDATE " + TABLE_VOCAB_BOOKS + " SET " + KEY_BOOK_RATING + " = " + bookRating
				+ " WHERE " + KEY_BOOK_ID + " = " + book_id;

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		if (c.moveToFirst()) {
			System.out.println("book " + book_id + " -> star " + c.getString(c.getColumnIndex(KEY_BOOK_RATING)));
		}

		c.close();
	}


	// ------------------------ Other methods ----------------//
	public LinkedHashMap<Long, String[]> sortWordList(long book_id, long list_id, int flag) {
		// flag == -1: default sorting
		// flag == 0: order by rating asc; flag == 1: order by rating desc;
		// flag == 2: order alphabetically asc; flag == 3: order alphabetically desc;
		// flag == 4: order by id

		LinkedHashMap<Long, String[]> vocabListInfoList = new LinkedHashMap<Long, String[]>();
		String selectQuery = "";
		switch (flag) {
			case -1:
				selectQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
						+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = " + list_id;
				break;
			case 0:
				selectQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
						+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = "
						+ list_id + " ORDER BY " + KEY_WORD_RATING + " ASC, " + KEY_WORD_ID + " ASC";
				break;
			case 1:
				selectQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
						+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = "
						+ list_id + " ORDER BY " + KEY_WORD_RATING + " DESC, " + KEY_WORD_ID + " ASC";
				break;
			case 2:
				selectQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
						+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = "
						+ list_id + " ORDER BY " + KEY_WORD + " ASC";
				break;
			case 3:
				selectQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
						+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = "
						+ list_id + " ORDER BY " + KEY_WORD + " DESC";
				break;
			case 4:
				selectQuery = "SELECT  * FROM " + TABLE_VOCAB_WORDS + " WHERE "
						+ KEY_BOOK_ID + " = " + book_id + " and " + KEY_LIST_ID + " = "
						+ list_id + " ORDER BY " + KEY_WORD_ID;
				break;
		}

		Log.e(LOG, selectQuery);

		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (c.moveToFirst()) {
			do {
				//System.out.println("id: " + c.getLong(c.getColumnIndex(KEY_WORD_ID)) + "\nword: " + c.getLong(c.getColumnIndex(KEY_WORD)) + "\ntrans: " + c.getLong(c.getColumnIndex(KEY_TRANSCN)) + "\nrating: " + c.getLong(c.getColumnIndex(KEY_WORD_RATING)));
				vocabListInfoList.put(c.getLong(c.getColumnIndex(KEY_WORD_ID)), new String[]{c.getString(c.getColumnIndex(KEY_WORD)), c.getString(c.getColumnIndex(KEY_TRANSCN)), c.getString(c.getColumnIndex(KEY_WORD_RATING))});
			} while (c.moveToNext());
		}

		c.close();

		return vocabListInfoList;
	}


	/**
	 * Closing database
	 */
	public void closeDB() {
		SQLiteDatabase db = this.getReadableDatabase();
		if (db != null && db.isOpen())
			db.close();
	}

	/**
	 * get datetime
	 * */
	/*private String getDateTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
		return dateFormat.format(date);
	}*/
}