/**
 * 
 */
package com.gesoftware.smsforwarder.receiver;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * @author Sharon
 *
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
	
	public static final String SEND_MSG_INTENT = "gesoftware.smsforwarder.intent.action.TEST";
	
	Context mContext;
	
	public SMSBroadcastReceiver(Context context) {
		mContext = context;
	}
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		
		 if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
	
			//---get the SMS message passed in---
	        Bundle bundle = intent.getExtras();        
	        SmsMessage[] msgs = null;
	        String str = "";            
	        if (bundle != null)
	        {
	            //---retrieve the SMS message received---
	            Object[] pdus = (Object[]) bundle.get("pdus");
	            msgs = new SmsMessage[pdus.length];            
	            for (int i = 0; i < msgs.length; i++){
	                msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);  
	                String strOrig = msgs[i].getOriginatingAddress();
	                str += "SMS from " +  strOrig;                    
	                str += " :";
	                String smsMessageText = msgs[i].getMessageBody().toString();
	                str += smsMessageText;
	                str += "\n";        

		            //forwardSmsToEmail(strOrig, smsMessageText);
		            sendMsgToActivity(strOrig, smsMessageText);

	            }
	            //---display the new SMS message---
	            Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	        }
		 }
	}
	
	private void forwardSmsToEmail(String originatingAddress, String string) {
		
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		String[] recipients = new String[]{"gary.evely@blueyonder.co.uk", "",};
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, recipients);
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "SMS From " + originatingAddress);
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, string);

		emailIntent.setType("text/plain");

		mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));	
	}
	
	private void sendMsgToActivity(String originatingAddress, String string) {
		Intent i = new Intent();
		i.setAction(SEND_MSG_INTENT);
		i.putExtra("From", originatingAddress);
		i.putExtra("Message", string);
		mContext.sendBroadcast(i);
	}

}
