package henryxrl.screens;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Chaci_Screen extends Activity{

	private EditText ed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chaci);

		ed = (EditText) findViewById(R.id.editText);
		ed.setHint("请输入想要查询的单词");
		ed.setFocusable(false);
		ed.setFocusableInTouchMode(false);

		ed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(getApplicationContext(), Chaci_Screen_2.class);
				startActivity(i);
			}
		});

	}

}
