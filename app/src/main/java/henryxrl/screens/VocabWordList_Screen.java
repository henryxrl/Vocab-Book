package henryxrl.screens;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import henryxrl.database.Vocab_db_handler;
import henryxrl.datatype.PullToRefreshListView;
import henryxrl.slidingmenu.SlidingMenu;
import henryxrl.slidingmenu.app.SlidingFragmentActivity;

public class VocabWordList_Screen extends BaseSlidingMenuActivity {

	private PullToRefreshListView vocabWordPage;

	private Vocab_db_handler db;

	private ArrayList<LinkedHashMap<String, String>> data = new ArrayList<LinkedHashMap<String, String>>();

	private MenuItem m0;
	private MenuItem m1;
	private MenuItem m2;
	private MenuItem m3;
	private MenuItem m4;

	private String title;

	private long bookNumber;
	private long listNumber;

	private Long[] listOrderToId;

	// for remembering the position of the listView
	private int idx;
	private int top;

	private int sortFlag = -1;      // default sorting

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

		setContentView(R.layout.vocab_word_list);

		ActionBar bar = getActionBar();
		//bar.setDisplayHomeAsUpEnabled(true);
		bar.setHomeButtonEnabled(true);

		db = new Vocab_db_handler(getApplicationContext());

		vocabWordPage = (PullToRefreshListView) findViewById(R.id.VocabWord_list);

		m0 = (MenuItem) findViewById(R.id.menuSort0);
		m1 = (MenuItem) findViewById(R.id.menuSort1);
		m2 = (MenuItem) findViewById(R.id.menuSort2);
		m3 = (MenuItem) findViewById(R.id.menuSort3);
		m4 = (MenuItem) findViewById(R.id.menuSort4);

		//m1.setChecked(true);

		Bundle b = getIntent().getExtras();
		final long[] numbers = b.getLongArray("VocabListList");
		bookNumber = numbers[0];
		listNumber = numbers[1];

		title = (db.getVocabListInfo(bookNumber, listNumber)).get(listNumber)[0];
		setTitle(title);

		load();
	}

	@Override
	protected void onResume() {
		super.onResume();
		load();

		// restore position
		vocabWordPage.setSelectionFromTop(idx, top);
	}

	@Override
	protected void onPause() {
		// Whenever this activity is paused (i.e. looses focus because another activity is started etc)
		// Override how this activity is animated out of view
		// The new activity is kept still and this activity is pushed out to the left
		overridePendingTransition(R.anim.hold, R.anim.push_out_to_right);
		super.onPause();
	}

	private void load() {
		// flag == 0: order by rating asc; flag == 1: order by rating desc;
		// flag == 2: order alphabetically asc; flag == 3: order alphabetically desc;
		// flag == 4: order by id

		//System.out.println("sortFlag: " + sortFlag);

		setupData();


		/*SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.simple_two_row_2,
				new String[] {"line1", "line2", "rating"},
				new int[] {android.R.id.text1, android.R.id.text2, R.id.overallRating}
		);

		adapter.setViewBinder(new MyBinder());*/

		CustomAdapter adapter = new CustomAdapter(this, R.layout.simple_two_row_2, data, bookNumber, listNumber, listOrderToId, db);

		vocabWordPage.setAdapter(adapter);

		vocabWordPage.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		vocabWordPage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Log.v("Click", "你点击了第" + arg2 + "行");
				Intent i = new Intent(getApplicationContext(), Vocab_Screen.class);


				// arg2-1 because we have PullToRefreshListView!
				i.putExtra("Vocab", join_longLongArrays(new long[] {bookNumber, listNumber, listOrderToId[arg2-1]}, listOrderToId));
				//i.putExtra("Vocab", join_longLongArrays(new long[] {bookNumber, listNumber, arg2}, listOrderToId));

				// remember the position!
				idx = vocabWordPage.getFirstVisiblePosition();
				View v = vocabWordPage.getChildAt(0);
				top = (v == null) ? 0 : v.getTop();

				// start activity
				startActivity(i);
			}
		});

		vocabWordPage.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
			@Override
			public void onRefresh() {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {
						SystemClock.sleep(500);

						setupData();

						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						super.onPostExecute(result);
						//adapter.notifyDataSetChanged();

						CustomAdapter adapter = new CustomAdapter(VocabWordList_Screen.this, R.layout.simple_two_row_2, data, bookNumber, listNumber, listOrderToId, db);

						vocabWordPage.setAdapter(adapter);

						vocabWordPage.onRefreshComplete();
					}
				}.execute();
			}
		});


		loadStat();
	}

	private void setupData() {
		// flag == 0: order by rating asc; flag == 1: order by rating desc;
		// flag == 2: order alphabetically asc; flag == 3: order alphabetically desc;
		// flag == 4: order by id

		//System.out.println("sortFlag: " + sortFlag);

		data.clear();
		LinkedHashMap<String, String> item;

		LinkedHashMap<Long, String[]> lists = db.sortWordList(bookNumber, listNumber, sortFlag);
		listOrderToId = new Long[lists.size()];
		int listOrderToId_idx = 0;
		for (Long id : lists.keySet()) {
			listOrderToId[listOrderToId_idx] = id;
			listOrderToId_idx++;
		}

		for (Map.Entry<Long, String[]> entry : lists.entrySet()) {
			Long id = entry.getKey();
			item = new LinkedHashMap<String, String>();
			//System.out.println("word: " + lists.get(id)[0] + "\ntrans: " + lists.get(id)[1] + "\nrating: " + lists.get(id)[2]);
			item.put("line1", lists.get(id)[0]);
			item.put("line2", lists.get(id)[1]);
			item.put("rating", lists.get(id)[2]);
			data.add(item);
		}
	}

	private long[] join_longLongArrays (long[] l1, Long[] l2) {
		long[] result = new long[l1.length + l2.length];

		int result_idx = 0;
		for (long aL1 : l1) {
			result[result_idx] = aL1;
			result_idx++;
		}

		for (long aL2 : l2) {
			result[result_idx] = aL2;
			result_idx++;
		}

		return result;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.vocabwordlist_menu, menu);
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
			case R.id.action_stat_word_list:
				//Toast.makeText(this, "Clicked!", Toast.LENGTH_SHORT).show();
				slidingMenu.toggle();
				return true;
			case R.id.menuSort0:
				//Toast.makeText(this, "Clicked1", Toast.LENGTH_SHORT).show();
				sortFlag = 0;
				load();
				item.setChecked(true);
				//setCheck(R.id.menuSort0);
				return true;
			case R.id.menuSort1:
				//Toast.makeText(this, "Clicked2", Toast.LENGTH_SHORT).show();
				sortFlag = 1;
				load();
				item.setChecked(true);
				//setCheck(R.id.menuSort1);
				return true;
			case R.id.menuSort2:
				//Toast.makeText(this, "Clicked3", Toast.LENGTH_SHORT).show();
				sortFlag = 2;
				load();
				item.setChecked(true);
				//setCheck(R.id.menuSort2);
				return true;
			case R.id.menuSort3:
				//Toast.makeText(this, "Clicked4", Toast.LENGTH_SHORT).show();
				sortFlag = 3;
				load();
				item.setChecked(true);
				//setCheck(R.id.menuSort3);
				return true;
			case R.id.menuSort4:
				//Toast.makeText(this, "Clicked5", Toast.LENGTH_SHORT).show();
				sortFlag = 4;
				load();
				item.setChecked(true);
				//setCheck(R.id.menuSort4);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	/*private void setCheck(int id) {
		m0.setChecked(false);
		m1.setChecked(false);
		m2.setChecked(false);
		m3.setChecked(false);
		m4.setChecked(false);
		System.out.println("here!");
		MenuItem m = (MenuItem) findViewById(id);
		m.setChecked(true);
	}*/

	private void loadStat() {
		// Enable stat sliding menu
		transaction = this.getSupportFragmentManager().beginTransaction();
		rightFrag = new SlidingMenuFragment();
		bundle = new Bundle();
		bundle.putString("title", title);
		bundle.putLong("bookNumber", bookNumber);
		bundle.putLong("listNumber", listNumber);
		bundle.putFloat("rating", db.getListRating(bookNumber, listNumber));
		bundle.putDouble("totalCount", db.getListWordCount(bookNumber, listNumber));
		bundle.putDouble("0star", db.getListWordRating(bookNumber, listNumber, 0));
		bundle.putDouble("1star", db.getListWordRating(bookNumber, listNumber, 1));
		bundle.putDouble("2star", db.getListWordRating(bookNumber, listNumber, 2));
		bundle.putDouble("3star", db.getListWordRating(bookNumber, listNumber, 3));
		bundle.putDouble("4star", db.getListWordRating(bookNumber, listNumber, 4));
		bundle.putDouble("5star", db.getListWordRating(bookNumber, listNumber, 5));
		rightFrag.setArguments(bundle);
		slidingMenu.setSecondaryMenu(R.layout.right_frame);
		transaction.replace(R.id.right_frame, rightFrag);
		transaction.commit();
	}



	class CustomAdapter extends ArrayAdapter<LinkedHashMap<String, String>> {

		/** To cache views of item */
		private class ViewHolder {
			private TextView line1;
			private TextView line2;
			private RatingBar r;

			/**
			 * General constructor
			 */
			ViewHolder() {
				// nothing to do here
			}
		}

		/** Inflater for list items **/
		private final LayoutInflater inflater;

		/** Other needed info **/
		private final long bookNumber;
		private final long listNumber;
		private final Vocab_db_handler db;
		private final Long[] listOrderToId;

		/**
		 * General constructor
		 *
		 * @param context
		 * @param resource
		 * @param objects
		 */
		public CustomAdapter(final Context context, final int resource, final ArrayList<LinkedHashMap<String, String>> objects, long b, long l, Long[] mapping, Vocab_db_handler d) {
			super(context, resource, objects);
			this.inflater = LayoutInflater.from(context);
			this.bookNumber = b;
			this.listNumber = l;
			this.db = d;
			this.listOrderToId = mapping;
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent) {

			View itemView = convertView;
			ViewHolder holder;
			final HashMap<String, String> item = getItem(position);

			if(null == itemView) {
				itemView = this.inflater.inflate(R.layout.simple_two_row_2, parent, false);

				holder = new ViewHolder();

				holder.line1 = (TextView)itemView.findViewById(android.R.id.text1);
				holder.line2 = (TextView)itemView.findViewById(android.R.id.text2);
				holder.r = (RatingBar) itemView.findViewById(R.id.overallRating);

				itemView.setTag(holder);
			} else {
				holder = (ViewHolder)itemView.getTag();
			}

			holder.line1.setText(item.get("line1"));
			holder.line2.setText(item.get("line2"));
			//holder.r.setRating(Float.parseFloat(item.get("rating")));     // Need to set after db has been updated. Get rating info directly from db so that rating won't be lost after scrolling listview

			//final CustomAdapter ad = this;
			holder.r.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
				public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
					if (fromUser) {
						// Save into db
						db.updateWordRating(bookNumber, listNumber, listOrderToId[position], rating);

						// update ratings
						db.updateListRating(bookNumber, listNumber);
						db.updateBookRating(bookNumber);

						//notifyDataSetChanged();
						loadStat();
					}
				}
			});


			// show rating in UI
			float newR = 0f;
			HashMap<Long, String[]> lists = db.getVocabWordInfo(bookNumber, listNumber, listOrderToId[position]);
			for (Map.Entry<Long, String[]> entry : lists.entrySet()) {
				Long id = entry.getKey();
				newR = Float.parseFloat(lists.get(id)[8]);
			}
			holder.r.setRating(newR);
			//holder.r.setRating(Float.parseFloat(item.get("rating")));

			return itemView;
		}
	}

}
