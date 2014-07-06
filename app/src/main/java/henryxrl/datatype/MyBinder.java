package henryxrl.datatype;

import android.content.Context;
import android.view.View;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

import henryxrl.screens.R;

/**
 * Created by Henry on 07/01/2014.
 */
public class MyBinder implements SimpleAdapter.ViewBinder {
	@Override
	public boolean setViewValue(View view, Object data, String textRepresentation) {
		if(view.getId() == R.id.overallRating){
			String stringval = (String) data;
			float ratingValue = Float.parseFloat(stringval);
			RatingBar ratingBar = (RatingBar) view;
			ratingBar.setRating(ratingValue);
			return true;
		}
		return false;
	}
}
