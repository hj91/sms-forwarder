package com.gesoftware.smsforwarder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class SMSForwarderActivity extends Activity {
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        registerReceiver(new BroadcastReceiver(){
            @Override
			public void onReceive(Context context, Intent intent) {
            	
            	Bundle extras = intent.getExtras();
            	if (extras != null) {
	        		String from = extras.getString("From");
	        		String sms = extras.getString("Message");
	        		if ((from != null) && (sms != null)) {
	        			forwardSmsToEmail(from, sms);
	        		}
            	}
			}
        }, new IntentFilter(SMSForwarderConstants.SEND_MSG_INTENT));
    }

	/**
	 * 
	 */
	final private void forwardSmsToEmail(String originatingAddress, String string) {
		
		String msgSubject = "SMS From " + originatingAddress;
		String msgBody = string;
		
		final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		String[] recipients = new String[]{"gary.evely@blueyonder.co.uk", ""};
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"gary.evely@blueyonder.co.uk"});
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, msgSubject);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, msgBody);

		emailIntent.setType("text/html");

		startActivity(Intent.createChooser(emailIntent, "Send mail..."));	
}

}