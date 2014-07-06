package henryxrl.datatype;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import henryxrl.database.Vocab_db_handler;
import henryxrl.screens.R;
import henryxrl.screens.VocabWordList_Screen;

/**
 * Created by Henry on 07/01/2014.
 */
public class CustomAdapter extends ArrayAdapter<LinkedHashMap<String, String>> {

	/** To cache views of item */
	private static class ViewHolder {
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