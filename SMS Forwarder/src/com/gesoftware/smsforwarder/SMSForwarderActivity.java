package com.gesoftware.smsforwarder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class SMSForwarderActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Button prefBtn = (Button) findViewById(R.id.prefButton);
		prefBtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent settingsActivity = new Intent(getBaseContext(),
						Preferences.class);
				startActivity(settingsActivity);
			}
		});

	}
}