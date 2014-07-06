package henryxrl.screens;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import henryxrl.database.Vocab_db_handler;
import henryxrl.datatype.MyBinder;
import henryxrl.datatype.VocabBook;
import henryxrl.datatype.VocabList;
import henryxrl.datatype.VocabWord;


public class VocabBookList_Screen extends Activity {

	private String FILE_NAME = "gre_full";
	private int FILE_ID;

	private ListView vocabBookPage;
	private Button btnLoad;
	private Button btnDelete;
	private ProgressDialog pd;

	private Vocab_db_handler db;

	private PrepareXML pXML;

	// for remembering the position of the listView
	private int idx;
	private int top;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocab_book_list);

		Context c = VocabBookList_Screen.this;
		Resources res = c.getResources();
		FILE_ID = res.getIdentifier(FILE_NAME, "raw", c.getPackageName());

		btnLoad = (Button) findViewById(R.id.btnLoad);
		btnLoad.setOnClickListener(new CreateDatabaseOnClickListener());
		btnDelete = (Button) findViewById(R.id.btnDelete);
		btnDelete.setOnClickListener(new DeleteDatabaseOnClickListener());

		db = new Vocab_db_handler(getApplicationContext());

		vocabBookPage = (ListView) findViewById(R.id.VocabBook_list);

		loadDB();
    }

	@Override
	protected void onResume() {
		super.onResume();
		loadDB();

		// restore position
		vocabBookPage.setSelectionFromTop(idx, top);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		db.closeDB();
	}

	private void loadDB() {
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> item;

		HashMap<Long, String[]> books = db.getAllVocabBooksInfo();
		int bookCount = db.getVocabBookCount();
		for (int i = 0; i < bookCount; i++) {
			int fourStarsAndAboveCount = db.getBook4StarAndAboveCount(i);
			int totalCount = db.getBookTotalWordCount(i);
			item = new HashMap<String, String>();
			item.put("text1", books.get(Long.valueOf(i))[0]);
			item.put("text2", "认识 " + fourStarsAndAboveCount + " / " + totalCount + " 词");
			item.put("text3", String.format("%.1f", Float.parseFloat(books.get(Long.valueOf(i))[1])) + " 星");
			item.put("rating", books.get(Long.valueOf(i))[1]);
			data.add(item);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.simple_two_row_1,
				new String[] {"text1", "text2", "text3", "rating"},
				new int[] {android.R.id.text1, android.R.id.text2, R.id.text3, R.id.overallRating}
		);
		adapter.setViewBinder(new MyBinder());
		vocabBookPage.setAdapter(adapter);

		vocabBookPage.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		vocabBookPage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.v("Click", "你点击了第"+arg2+"行");
				Intent i = new Intent(getApplicationContext(), VocabListList_Screen.class);
				i.putExtra("VocabBookList", (long)arg2);

				// remember the position!
				idx = vocabBookPage.getFirstVisiblePosition();
				View v = vocabBookPage.getChildAt(0);
				top = (v == null) ? 0 : v.getTop();

				// start activity
				startActivity(i);
			}
		});
	}

	private ArrayList<HashMap<String, String>> parseTXTWithTrans()
	{
		ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();

		try
		{
			//InputStream inputStream = getClass().getClassLoader().getResourceAsStream("src/files/GRE.txt");
			InputStream inputStream = getResources().openRawResource(FILE_ID);
			if (inputStream != null)
			{
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String s;

				HashMap<String, String> item;
				while ((s = bufferedReader.readLine()) != null)
				{
					String[] parts = s.split("\t");
					String part1 = parts[0];    // vocab
					String part2 = parts[1];    // vocab trans
					item = new HashMap<String, String>();
					item.put("line1", part1);
					item.put("line2", part2);
					ret.add(item);
				}

				inputStream.close();
			}
		}
		catch (FileNotFoundException e)
		{
			Log.e(VocabBookList_Screen.class.getName(), "File Not Found: " + e.toString());
		}
		catch (IOException e)
		{
			Log.e(VocabBookList_Screen.class.getName(), "Cannot Read File: " + e.toString());
		}

		return ret;
	}

	private ArrayList<HashMap<String, String>> parseTXTWithoutTrans()
	{
		ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();

		try
		{
			//InputStream inputStream = getClass().getClassLoader().getResourceAsStream("src/files/GRE.txt");
			InputStream inputStream = getResources().openRawResource(FILE_ID);
			if (inputStream != null)
			{
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String s;

				HashMap<String, String> item;
				while ((s = bufferedReader.readLine()) != null)
				{
					item = new HashMap<String, String>();
					item.put("line1", s);
					item.put("line2", "");
					ret.add(item);
				}

				inputStream.close();
			}
		}
		catch (FileNotFoundException e)
		{
			Log.e(VocabBookList_Screen.class.getName(), "File Not Found: " + e.toString());
		}
		catch (IOException e)
		{
			Log.e(VocabBookList_Screen.class.getName(), "Cannot Read File: " + e.toString());
		}

		return ret;
	}

	private void parseXML() throws ParserConfigurationException, SAXException {
		try
		{
			final InputStream inputStream = getResources().openRawResource(FILE_ID);
			if (inputStream != null)
			{
				SAXParserFactory parserFactor = SAXParserFactory.newInstance();
				SAXParser parser = parserFactor.newSAXParser();
				SAXHandler handler = new SAXHandler();
				parser.parse(inputStream, handler);

				inputStream.close();

			}
		}
		catch (FileNotFoundException e)
		{
			Log.e(VocabBookList_Screen.class.getName(), "File Not Found: " + e.toString());
		}
		catch (IOException e)
		{
			Log.e(VocabBookList_Screen.class.getName(), "Cannot Read File: " + e.toString());
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vocablistlist_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
	    int id = item.getItemId();
	    if (id == R.id.action_settings) {
		    return true;
	    }
	    switch (id) {
		    case android.R.id.home:
			    //Toast.makeText(Vocab_Screen.this, "Clicked!", Toast.LENGTH_SHORT).show();
			    finish();
			    return true;
		    default:
			    return super.onOptionsItemSelected(item);
	    }
    }

	public class PrepareXML extends AsyncTask<Void, Long, Void> {

		@Override
		protected void onProgressUpdate(Long... progress) {
			pd.setProgress((int) (long) progress[0]);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pd = new ProgressDialog(VocabBookList_Screen.this);
			pd.setCancelable(false);

			final InputStream inputStream = getResources().openRawResource(FILE_ID);
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String name = "";
			String line;
			int wordCount = 0;
			try {
				while ((line = bufferedReader.readLine()) != null)
				{
					if (line.contains("<book"))
						name = line.substring((line.indexOf("\"") + 1), line.lastIndexOf("\""));
					if (line.contains("<item>"))
						wordCount++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			pd.setMax(wordCount);
			pd.setMessage("正在导入书籍……\n\n《" + name + "》");
			pd.setProgressNumberFormat("已导入 %1d / %2d 词");
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setProgress(0);
			pd.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				parseXML();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			// Close progressbar
			pd.dismiss();
			Toast.makeText(VocabBookList_Screen.this, "Load successful!", Toast.LENGTH_SHORT).show();
			loadDB();
		}

	}

	/**
	 * The Handler for SAX Events.
	 */
	public class SAXHandler extends DefaultHandler {
		private boolean inWord;
		private boolean inPhonetic;
		private boolean inTransCN;
		private boolean inTransEN;
		private boolean inSent1;
		private boolean inSent2;
		private boolean inSynonym;
		private boolean inAntonym;
		private StringBuilder word;
		private StringBuilder phonetic;
		private StringBuilder transCN;
		private StringBuilder transEN;
		private StringBuilder sent1;
		private StringBuilder sent2;
		private StringBuilder synonym;
		private StringBuilder antonym;

		private VocabBook book;
		private long bookID = -1L;
		private VocabList list;
		private long listID = -1L;
		private VocabWord item;
		private long itemID = -1L;
		private long itemCount = 0L;

		private boolean bookIDChanged = false;
		private boolean listIDChanged = false;


		@Override
		//Triggered when the start of tag is found.
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (qName.equals("book")) {
				bookID++;

				book = new VocabBook(bookID);

				book.name = attributes.getValue("id");

				bookIDChanged = true;

			} else if (qName.equals("list")) {
				if (bookIDChanged)
					listID = -1L;

				listID++;

				bookIDChanged = false;

				list = new VocabList(listID);

				list.name = attributes.getValue("id");

				listIDChanged = true;

			} else if (qName.equals("item")) {
				if (listIDChanged)
					itemID = -1L;

				itemID++;

				listIDChanged = false;

				item = new VocabWord(itemID);

			} else if (qName.equals("word")) {
				inWord = true;
				word = new StringBuilder();
			} else if (qName.equals("phonetic")) {
				inPhonetic = true;
				phonetic = new StringBuilder();
			} else if (qName.equals("transCN")) {
				inTransCN = true;
				transCN = new StringBuilder();
			} else if (qName.equals("transEN")) {
				inTransEN = true;
				transEN = new StringBuilder();
			} else if (qName.equals("sent1")) {
				inSent1 = true;
				sent1 = new StringBuilder();
			} else if (qName.equals("sent2")) {
				inSent2 = true;
				sent2 = new StringBuilder();
			} else if (qName.equals("synonym")) {
				inSynonym = true;
				synonym = new StringBuilder();
			} else if (qName.equals("antonym")) {
				inAntonym = true;
				antonym = new StringBuilder();
			}
		}

		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (qName.equals("word")) {
				item.word = word.toString();
				inWord = false;
			} else if (qName.equals("phonetic")) {
				item.phonetic = phonetic.toString();
				inPhonetic = false;
			} else if (qName.equals("transCN")) {
				item.transCN = transCN.toString();
				inTransCN = false;
			} else if (qName.equals("transEN")) {
				item.transEN = transEN.toString();
				inTransEN = false;
			} else if (qName.equals("sent1")) {
				item.sentCN = sent1.toString();
				inSent1 = false;
			} else if (qName.equals("sent2")) {
				item.sentEN = sent2.toString();
				inSent2 = false;
			} else if (qName.equals("synonym")) {
				item.synonym = synonym.toString();
				inSynonym = false;
			} else if (qName.equals("antonym")) {
				item.antonym = antonym.toString();
				inAntonym = false;
			} else if (qName.equals("item")) {
				list.list.add(item);
				db.createVocabWord(bookID, listID, item);

				itemCount++;
				pXML.onProgressUpdate(itemCount);

				//System.out.println("Done with vocab " + item.word);
			} else if (qName.equals("list")) {
				book.list.add(list);
				db.createVocabListInfo(bookID, list);

				//System.out.println("Done with list " + list.name);
			} else if (qName.equals("book")) {
				db.createVocabBookInfo(book);

				System.out.println("Done with book " + book.name);
			}
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			String s = String.copyValueOf(ch, start, length).trim();

			if (inWord) {
				word.append((word.length() > 0) ? ("\n" + s) : s);
			} else if (inPhonetic) {
				phonetic.append((phonetic.length() > 0) ? ("\n" + s) : s);
			} else if (inTransCN) {
				transCN.append((transCN.length() > 0) ? ("\n" + s) : s);
			} else if (inTransEN) {
				transEN.append((transEN.length() > 0) ? ("\n" + s) : s);
			} else if (inSent1) {
				sent1.append((sent1.length() > 0) ? ("\n" + s) : s);
			} else if (inSent2) {
				sent2.append((sent2.length() > 0) ? ("\n" + s) : s);
			} else if (inSynonym) {
				synonym.append((synonym.length() > 0) ? ("\n" + s) : s);
			} else if (inAntonym) {
				antonym.append((antonym.length() > 0) ? ("\n" + s) : s);
			}
		}

	}

	// createDatabaseButton点击事件监听器
	public class CreateDatabaseOnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			// 创建了一个DatabaseHelper对象，只执行这句话是不会创建或打开连接的
			//Vocab_db_handler db = new Vocab_db_handler(VocabBookList_Screen.this);
			// 只有调用了DatabaseHelper的getWritableDatabase()方法或者getReadableDatabase()方法之后，才会创建或打开一个连接
			//SQLiteDatabase sqliteDatabase = dbHelper.getReadableDatabase();

			/*try {
				parseXML();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			}*/

			//final InputStream inputStream = getResources().openRawResource(FILE_ID);
			pXML = new PrepareXML();
			pXML.execute();

			//loadDB();
		}

	}

	// deleteButton点击事件监听器
	public class DeleteDatabaseOnClickListener implements View.OnClickListener {
		public void onClick(View v) {
			//创建DatabaseHelper对象
			//Vocab_db_handler dbHelper = new Vocab_db_handler(VocabBookList_Screen.this);
			//获得可写的SQLiteDatabase对象
			//SQLiteDatabase sqliteDatabase = dbHelper.getWritableDatabase();
			//调用SQLiteDatabase对象的delete方法进行删除操作
			//第一个参数String：表名
			//第二个参数String：条件语句
			//第三个参数String[]：条件值
			//sqliteDatabase.delete("user", "id=?", new String[]{"1"});
			//System.out.println("----------delete----------");
			db.deleteVocabBook(0);
			System.out.println("Done with db.deleteVocabBook(0)");
			db.deleteAllVocabList(0);
			System.out.println("Done with db.deleteAllVocabList(0)");
			db.deleteAllVocabWord(0);
			System.out.println("Done with db.deleteAllVocabWord(0, t)\nDelete database complete!");

			vocabBookPage.setAdapter(null);
		}
	}

}

