package henryxrl.screens;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import henryxrl.database.Vocab_db_handler;
import henryxrl.datatype.CustomScrollView;
import henryxrl.datatype.VocabBook;

public class Vocab_Screen extends Activity implements View.OnClickListener {

	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_MAX_OFF_PATH = 250;
	private static final int SWIPE_THRESHOLD_VELOCITY = 200;
	private GestureDetector gestureDetector;
	View.OnTouchListener gestureListener;


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
	private RatingBar r;
	private TextView RatingWord;

	private ProgressBar progressBar;

	private final String[] ratingText = new String[] { "尚未学习", "只如初见", "似曾相识", "略知一二", "耳熟能详", "了然于心" };

	private Vocab_db_handler db;

	public long bookNumber;
	public long listNumber;
	public long wordNumber;

	public long[] listOrderToId;
	public HashMap<Long, Long> idToListOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

	    // Override how this activity is animated into view
	    // The new activity is pulled in from the left and the current activity is kept still
	    // This has to be called before onCreate
	    overridePendingTransition(R.anim.pull_in_from_right, R.anim.hold);

        setContentView(R.layout.vocab);

	    //ActionBar bar = getActionBar();
	    //bar.setDisplayHomeAsUpEnabled(true);
	    //bar.setHomeButtonEnabled(true);

	    db = new Vocab_db_handler(getApplicationContext());

		imgPhonetic = (ImageView) findViewById(R.id.image_phonetic);
		imgTransCN = (ImageView) findViewById(R.id.image_transcn);
		imgTransEN = (ImageView) findViewById(R.id.image_transen);
		imgSent1 = (ImageView) findViewById(R.id.image_sent1);
		imgSent2 = (ImageView) findViewById(R.id.image_sent2);
		imgSynonym = (ImageView) findViewById(R.id.image_synonym);
		imgAntonym = (ImageView) findViewById(R.id.image_antonym);

	    Word = (TextView) findViewById(R.id.word);
	    Phonetic = (TextView) findViewById(R.id.phonetic);
	    TransCN = (TextView) findViewById(R.id.transCN);
	    TransEN = (TextView) findViewById(R.id.transEN);
	    Sent1 = (TextView) findViewById(R.id.sent1);
	    Sent2 = (TextView) findViewById(R.id.sent2);
	    Synonym = (TextView) findViewById(R.id.synonym);
	    Antonym = (TextView) findViewById(R.id.antonym);
	    s = (CustomScrollView) findViewById(R.id.scrollView);
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
	    r = (RatingBar) findViewById(R.id.WordRating);
	    RatingWord = (TextView) findViewById(R.id.WordRatingText);

	    progressBar = (ProgressBar) findViewById(R.id.progressBar);

	    Bundle b = getIntent().getExtras();
	    final long[] numbers = b.getLongArray("Vocab");
	    bookNumber = numbers[0];
	    listNumber = numbers[1];
	    wordNumber = numbers[2];
	    System.out.println("book: " + bookNumber + "\nlist: " + listNumber + "\nword: " + wordNumber);

	    listOrderToId = new long[numbers.length - 3];
	    idToListOrder = new HashMap<Long, Long>();
	    for (int i = 3; i < numbers.length; i++) {
		    listOrderToId[i-3] = numbers[i];
		    idToListOrder.put(numbers[i], (long)(i-3));
	    }

	    showInfo(bookNumber, listNumber, wordNumber);

	    // RatingBar action
	    r.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
		    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
			    //Toast.makeText(getApplicationContext(),"Your Selected Ratings  : " + String.valueOf(rating), Toast.LENGTH_SHORT).show();

			    // Set text
			    RatingWord.setText(ratingText[(int)rating]);

			    // Save into db
			    db.updateWordRating(bookNumber, listNumber, wordNumber, rating);

			    // update ratings
			    db.updateListRating(bookNumber, listNumber);
			    db.updateBookRating(bookNumber);

		    }
	    });

    }

	@Override
	protected void onPause() {
		// Whenever this activity is paused (i.e. looses focus because another activity is started etc)
		// Override how this activity is animated out of view
		// The new activity is kept still and this activity is pushed out to the left
		overridePendingTransition(R.anim.hold, R.anim.push_out_to_right);
		super.onPause();
	}

	private void showInfo(long bookNumber, long listNumber, long wordNumber) {
		HashMap<Long, String[]> lists = db.getVocabWordInfo(bookNumber, listNumber, wordNumber);
		String[] wordAttr = new String[9];
		for (HashMap.Entry<Long, String[]> entry : lists.entrySet()) {
			wordAttr = entry.getValue();
		}

		Word.setText(wordAttr[0]);
		setOtherTextView(wordAttr);
		r.setRating(Float.parseFloat(wordAttr[8]));
		RatingWord.setText(ratingText[(int)Float.parseFloat(wordAttr[8])]);

		//System.out.println("book " + bookNumber + " -> list " + listNumber + " -> word " + wordNumber + " -> star " + wordAttr[8]);


		// create new ProgressBar and style it
		float totalWordCount = db.getListWordCount(bookNumber, listNumber);
		int progress = (int)((idToListOrder.get(wordNumber) + 1) / totalWordCount * 100.0);
		progressBar.setProgress(progress);


		// Gesture detection
		gestureDetector = new GestureDetector(this, new MyGestureDetector());
		gestureListener = new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		};

		s.setOnClickListener(Vocab_Screen.this);
		s.setOnTouchListener(gestureListener);

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

	public class MyGestureDetector extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			try {
				if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
					return false;
				// right to left swipe
				if(e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					//Toast.makeText(Vocab_Screen.this, "Show next word!", Toast.LENGTH_SHORT).show();
					if (idToListOrder.get(wordNumber) == db.getListWordCount(bookNumber, listNumber)-1) {
						Toast.makeText(Vocab_Screen.this, "已经是最后一个词啦！", Toast.LENGTH_SHORT).show();
					}
					else {
						wordNumber = listOrderToId[(int)(idToListOrder.get(wordNumber) + 1)];
						showInfo(bookNumber, listNumber, wordNumber);
					}
				}  else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
					//Toast.makeText(Vocab_Screen.this, "Show previous word!", Toast.LENGTH_SHORT).show();
					if (idToListOrder.get(wordNumber) == 0) {
						Toast.makeText(Vocab_Screen.this, "已经是第一个词啦！", Toast.LENGTH_SHORT).show();
					}
					else {
						wordNumber = listOrderToId[(int)(idToListOrder.get(wordNumber) - 1)];
						showInfo(bookNumber, listNumber, wordNumber);
					}
				}
			} catch (Exception e) {
				// nothing
			}
			return false;
		}

	}

}
