/**
 * 
 */
package com.gesoftware.smsforwarder;

import com.gesoftware.smsforwarder.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Sharon
 *
 */
public class Preferences extends PreferenceActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}
