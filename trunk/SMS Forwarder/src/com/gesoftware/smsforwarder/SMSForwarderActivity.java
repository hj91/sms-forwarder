package com.gesoftware.smsforwarder;

import com.gesoftware.smsforwarder.receiver.SMSBroadcastReceiver;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;

public class SMSForwarderActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        IntentFilter intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        SMSBroadcastReceiver receiver = new SMSBroadcastReceiver(getApplicationContext());
        this.registerReceiver(receiver, intentFilter);
    }
}