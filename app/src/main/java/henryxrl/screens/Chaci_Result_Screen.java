package henryxrl.screens;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import henryxrl.datatype.CustomScrollView;

public class Chaci_Result_Screen extends Activity implements View.OnClickListener {

	private ImageView imgPhonetic;
	private ImageView imgTransCN;
	private ImageView imgTransEN;
	private ImageView imgSent1;
	private ImageView imgSent2;
	private ImageView imgSynonym;
	private ImageView imgAntonym;

	private TextView Word;
	private TextView Phonetic;
	private TextView TransCN;
	private TextView TransEN;
	private TextView Sent1;
	private TextView Sent2;
	private TextView Synonym;
	private TextView Antonym;
	private CustomScrollView s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    // Override how this activity is animated into view
	    // The new activity is pulled in from the left and the current activity is kept still
	    // This has to be called before onCreate
	    overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

        setContentView(R.layout.chaci_result);

	    imgPhonetic = (ImageView) findViewById(R.id.chaci_image_phonetic);
		imgTransCN = (ImageView) findViewById(R.id.chaci_image_transcn);
		imgTransEN = (ImageView) findViewById(R.id.chaci_image_transen);
		imgSent1 = (ImageView) findViewById(R.id.chaci_image_sent1);
		imgSent2 = (ImageView) findViewById(R.id.chaci_image_sent2);
		imgSynonym = (ImageView) findViewById(R.id.chaci_image_synonym);
		imgAntonym = (ImageView) findViewById(R.id.chaci_image_antonym);

	    Word = (TextView) findViewById(R.id.chaci_word);
	    Phonetic = (TextView) findViewById(R.id.chaci_phonetic);
	    TransCN = (TextView) findViewById(R.id.chaci_transCN);
	    TransEN = (TextView) findViewById(R.id.chaci_transEN);
	    Sent1 = (TextView) findViewById(R.id.chaci_sent1);
	    Sent2 = (TextView) findViewById(R.id.chaci_sent2);
	    Synonym = (TextView) findViewById(R.id.chaci_synonym);
	    Antonym = (TextView) findViewById(R.id.chaci_antonym);
	    s = (CustomScrollView) findViewById(R.id.chaci_scrollView);
	    final ImageView moreToScrollIndicatorUp = (ImageView) findViewById(R.id.moreToScrollIndicatorUp);
	    moreToScrollIndicatorUp.setImageResource(android.R.color.transparent);
	    final ImageView moreToScrollIndicatorDown = (ImageView) findViewById(R.id.moreToScrollIndicatorDown);
	    s.setOnScrollViewListener(new CustomScrollView.OnScrollViewListener() {
		    public void onScrollChanged(CustomScrollView scrollView, int l, int t, int oldl, int oldt) {
			    // We take the last son in the scrollview
			    View view = scrollView.getChildAt(scrollView.getChildCount() - 1);

			    int diff = (view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY()));

			    //System.out.println("getScrollY: " + scrollView.getScrollY() + "\nbottom: " + view.getBottom());

			    // if diff is zero, then the bottom has been reached
			    if (diff == 0) {
				    //Toast.makeText(Vocab_Screen.this, "THIS IS THE END!", Toast.LENGTH_SHORT).show();
				    moreToScrollIndicatorDown.setImageResource(android.R.color.transparent);
			    } else if (scrollView.getScrollY() == 0) {
				    //Toast.makeText(Vocab_Screen.this, "THIS IS THE TOP!", Toast.LENGTH_SHORT).show();
				    moreToScrollIndicatorUp.setImageResource(android.R.color.transparent);
			    } else {
				    moreToScrollIndicatorUp.setImageResource(R.drawable.arrow_up);
				    moreToScrollIndicatorDown.setImageResource(R.drawable.arrow_down);
			    }
		    }
	    });

	    showInfo();

    }

	@Override
	protected void onPause() {
		// Whenever this activity is paused (i.e. looses focus because another activity is started etc)
		// Override how this activity is animated out of view
		// The new activity is kept still and this activity is pushed out to the left
		overridePendingTransition(R.anim.hold, R.anim.push_out_to_right);
		super.onPause();
	}

	private void showInfo() {
		Bundle bundle = getIntent().getExtras();
		Bundle b = bundle.getBundle("result");

		String[] wordAttr = new String[9];
		wordAttr[0] = b.getString("word").replaceAll("\\\\n", "\\\n");
		wordAttr[1] = b.getString("phonetic").replaceAll("\\\\n", "\\\n");
		wordAttr[2] = b.getString("transCN").replaceAll("\\\\n", "\\\n");
		wordAttr[3] = b.getString("transEN").replaceAll("\\\\n", "\\\n");
		wordAttr[4] = b.getString("sent1").replaceAll("\\\\n", "\\\n");
		wordAttr[5] = b.getString("sent2").replaceAll("\\\\n", "\\\n");
		wordAttr[6] = b.getString("synonym").replaceAll("\\\\n", "\\\n");
		wordAttr[7] = b.getString("antonym").replaceAll("\\\\n", "\\\n");
		//wordAttr[8] = b.getString("word").replaceAll("\\\\n", "\\\n");

		Word.setText(wordAttr[0]);
		setOtherTextView(wordAttr);

		s.setOnClickListener(Chaci_Result_Screen.this);

		final ImageView moreToScrollIndicatorDown = (ImageView) findViewById(R.id.moreToScrollIndicatorDown);
		moreToScrollIndicatorDown.setImageResource(android.R.color.transparent);
		ViewTreeObserver vto = s.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				s.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				//int width  = s.getMeasuredWidth();
				int height = s.getMeasuredHeight();
				int totalHeight = s.getChildAt(s.getChildCount() - 1).getBottom();
				//System.out.println("HEIGHT: " + height + "\nBOTTOM: " + totalHeight);

				if (totalHeight > height) {
					//System.out.println("totalHeight: " + totalHeight + "\nheight: " + height);
					//Toast.makeText(Vocab_Screen.this, "THERE IS MORE TO SCROLL!", Toast.LENGTH_SHORT).show();
					moreToScrollIndicatorDown.setImageResource(R.drawable.arrow_down);
				}
			}
		});

	}

	private void setOtherTextView(String[] wordAttr) {
		ImageView[] img = new ImageView[] { imgPhonetic, imgTransCN, imgTransEN, imgSent1, imgSent2, imgSynonym, imgAntonym };
		int[] imgSrc = new int[] { R.drawable.pho_small, R.drawable.cn_small, R.drawable.en_small, R.drawable.li_small, R.drawable.li_small, R.drawable.syn_small, R.drawable.ant_small };
		TextView[] t = new TextView[] { Phonetic, TransCN, TransEN, Sent1, Sent2, Synonym, Antonym };

		for (int i = 0, j = 0; i < img.length && j <= i; i++) {
			// Clear TextView and ImageView first!!
			t[i].setText("");
			img[i].setImageResource(android.R.color.transparent);

			// Set TextView
			String temp = wordAttr[i+1];
			if (!temp.equals("")) {
				t[j].setText(temp);
				//img[j].setImageDrawable(getResources().getDrawable(imgSrc[i]));
				img[j].setImageResource(imgSrc[i]);
				j++;
			}
		}
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.vocab__screen, menu);
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

	@Override
	public void onClick(View v) {  }

}
