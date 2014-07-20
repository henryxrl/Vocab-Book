package henryxrl.screens;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;

import henryxrl.database.VocabDatabase;
import henryxrl.datatype.MyBinder;

public class VocabListList_Screen extends BaseSlidingMenuActivity {

	private ListView vocabListPage;

	private VocabDatabase db;

	private ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

	private String title;

	private long bookNumber;

	// for remembering the position of the listView
	private int idx;
	private int top;

	private FragmentTransaction transaction;
	private Fragment rightFrag;
	private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    // Override how this activity is animated into view
	    // The new activity is pulled in from the left and the current activity is kept still
	    // This has to be called before onCreate
	    overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

        setContentView(R.layout.vocab_list_list);

	    ActionBar bar = getActionBar();
	    //bar.setDisplayHomeAsUpEnabled(true);
	    bar.setHomeButtonEnabled(true);

	    db = new VocabDatabase(getApplicationContext());

	    vocabListPage = (ListView) findViewById(R.id.VocabList_list);

	    Bundle b = getIntent().getExtras();
	    bookNumber = b.getLong("VocabBookList");

	    title = (db.getVocabBookInfo(bookNumber)).get(bookNumber)[0];
	    setTitle(title);

	    loadDB();
    }

	@Override
	protected void onResume() {
		super.onResume();
		loadDB();

		// restore position
		vocabListPage.setSelectionFromTop(idx, top);
	}

	@Override
	protected void onPause() {
		// Whenever this activity is paused (i.e. looses focus because another activity is started etc)
		// Override how this activity is animated out of view
		// The new activity is kept still and this activity is pushed out to the left
		overridePendingTransition(R.anim.hold, R.anim.push_out_to_right);
		super.onPause();
	}

	private void loadDB() {
		data.clear();
		HashMap<String, String> item;

		HashMap<Long, String[]> lists = db.getAllVocabListInfo(bookNumber);
		int listCount = db.getVocabListCount(bookNumber);
		for (int i = 0; i < listCount; i++) {
			int fourStarsAndAboveCount = db.getList4StarAndAboveCount(bookNumber, i);
			int totalCount = db.getListWordCount(bookNumber, i);
			item = new HashMap<String, String>();
			item.put("text1", lists.get(Long.valueOf(i))[0]);
			item.put("text2", "认识 " + fourStarsAndAboveCount + " / " + totalCount + " 词");
			item.put("text3", String.format("%.1f", Float.parseFloat(lists.get(Long.valueOf(i))[1])) + " 星");
			item.put("rating", lists.get(Long.valueOf(i))[1]);
			data.add(item);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.simple_two_row_1,
				new String[] {"text1", "text2", "text3", "rating"},
				new int[] {android.R.id.text1, android.R.id.text2, R.id.text3, R.id.overallRating}
		);
		adapter.setViewBinder(new MyBinder());
		vocabListPage.setAdapter(adapter);

		vocabListPage.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		vocabListPage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.v("Click", "你点击了第"+arg2+"行");
				Intent i = new Intent(getApplicationContext(), VocabWordList_Screen.class);
				i.putExtra("VocabListList", new long[] { bookNumber, (long)arg2 });

				// remember the position!
				idx = vocabListPage.getFirstVisiblePosition();
				View v = vocabListPage.getChildAt(0);
				top = (v == null) ? 0 : v.getTop();

				// start activity
				startActivity(i);
			}
		});


		loadStat();
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
	    switch (id) {
		    case R.id.action_settings:
			    return true;
		    case android.R.id.home:
			    //Toast.makeText(Vocab_Screen.this, "Clicked!", Toast.LENGTH_SHORT).show();
			    finish();
			    return true;
		    case R.id.action_stat_list_list:
			    //Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
			    slidingMenu.toggle();
			    return true;
		    default:
			    return super.onOptionsItemSelected(item);
	    }
    }


	private void loadStat() {
		// Enable stat sliding menu
		transaction = this.getSupportFragmentManager().beginTransaction();
		rightFrag = new SlidingMenuFragment();
		bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putLong("bookNumber", bookNumber);
		bundle.putLong("listNumber", -1);
		bundle.putFloat("rating", db.getBookRating(bookNumber));
		bundle.putDouble("totalCount", db.getBookTotalWordCount(bookNumber));
	    bundle.putDouble("0star", db.getBookWordRating(bookNumber, 0));
	    bundle.putDouble("1star", db.getBookWordRating(bookNumber, 1));
	    bundle.putDouble("2star", db.getBookWordRating(bookNumber, 2));
	    bundle.putDouble("3star", db.getBookWordRating(bookNumber, 3));
	    bundle.putDouble("4star", db.getBookWordRating(bookNumber, 4));
	    bundle.putDouble("5star", db.getBookWordRating(bookNumber, 5));
		rightFrag.setArguments(bundle);
		slidingMenu.setSecondaryMenu(R.layout.right_frame);
		transaction.replace(R.id.right_frame, rightFrag);
		transaction.commit();
	}
}

