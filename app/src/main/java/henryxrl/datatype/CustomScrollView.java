package henryxrl.datatype;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * Created by Henry on 07/04/2014.
 */
public class CustomScrollView extends ScrollView {
	public CustomScrollView(Context context) {
		super(context);
	}

	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public interface OnScrollViewListener {
		void onScrollChanged(CustomScrollView v, int l, int t, int oldl, int oldt);
	}

	private OnScrollViewListener mOnScrollViewListener;

	public void setOnScrollViewListener(OnScrollViewListener l) {
		this.mOnScrollViewListener = l;
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		mOnScrollViewListener.onScrollChanged(this, l, t, oldl, oldt);
		super.onScrollChanged( l, t, oldl, oldt );
	}
}
