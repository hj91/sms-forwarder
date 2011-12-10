package com.gesoftware.smsforwarder;

import com.gesoftware.smsforwarder.receiver.SMSBroadcastReceiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class SMSForwarderActivity extends Activity {
	
	public static final String SEND_MSG_INTENT = "gesoftware.smsforwarder.intent.action.TEST";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        SMSBroadcastReceiver receiver = new SMSBroadcastReceiver(getApplicationContext());
        this.registerReceiver(receiver, intentFilter);
        
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
        }, new IntentFilter(SEND_MSG_INTENT));
    }
    
	private void forwardSmsToEmail(String originatingAddress, String string) {
		
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		String[] recipients = new String[]{"gary.evely@blueyonder.co.uk", "",};
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SMS From " + originatingAddress);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, string);

		emailIntent.setType("text/plain");

		startActivity(Intent.createChooser(emailIntent, "Send mail..."));	
	}

}