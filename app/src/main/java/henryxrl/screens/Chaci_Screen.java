package henryxrl.screens;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class Chaci_Screen extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		TextView tv = new TextView(this);
		tv.setText("查词界面");
		tv.setGravity(Gravity.CENTER);
		setContentView(tv);
	}
	
}
