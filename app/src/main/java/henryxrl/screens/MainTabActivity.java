package henryxrl.screens;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;

public class MainTabActivity extends TabActivity implements OnCheckedChangeListener {

	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.tab);

		this.mAIntent = new Intent(this, VocabBookList_Screen.class);
		this.mBIntent = new Intent(this, Chaci_Screen.class);
		this.mCIntent = new Intent(this, Setting_Screen.class);

		((RadioButton) findViewById(R.id.radio_button0)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button1)).setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button2)).setOnCheckedChangeListener(this);

		setupIntent();

		((RadioButton) findViewById(R.id.radio_button0)).setChecked(true);
		this.mTabHost.setCurrentTabByTag("A_TAB");
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(isChecked){
			switch (buttonView.getId()) {
				case R.id.radio_button0:
					this.mTabHost.setCurrentTabByTag("A_TAB");
					break;
				case R.id.radio_button1:
					this.mTabHost.setCurrentTabByTag("B_TAB");
					break;
				case R.id.radio_button2:
					this.mTabHost.setCurrentTabByTag("C_TAB");
					break;
			}
		}

	}

	private void setupIntent() {
		this.mTabHost = getTabHost();
		this.mTabHost.addTab(buildTabSpec("A_TAB", R.string.tab_beici, R.drawable.tab_beici, this.mAIntent));
		this.mTabHost.addTab(buildTabSpec("B_TAB", R.string.tab_chaci, R.drawable.tab_chaci, this.mBIntent));
		this.mTabHost.addTab(buildTabSpec("C_TAB", R.string.tab_setting, R.drawable.tab_setting, this.mCIntent));
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon, final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel), getResources().getDrawable(resIcon)).setContent(content);
	}
}