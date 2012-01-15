/**
 * 
 */
package com.gesoftware.smsforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * @author Sharon
 * 
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {

		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {

			// Determine if SMS forwarding is enabled
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context);
			if (prefs.getBoolean("enableForwardingPref", false)) {
				// ---get the SMS message passed in---
				Bundle bundle = intent.getExtras();
				SmsMessage[] msgs = null;
				if (bundle != null) {
					// ---retrieve the SMS message received---
					Object[] pdus = (Object[]) bundle.get("pdus");
					msgs = new SmsMessage[pdus.length];
					for (int i = 0; i < msgs.length; i++) {
						msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
						String strOrig = msgs[i].getOriginatingAddress();
						String smsMessageText = msgs[i].getMessageBody()
								.toString();

						forwardSmsToEmail(context, strOrig, smsMessageText);

					}
				}
			}
		}
	}

	/**
	 * 
	 */
	final private void forwardSmsToEmail(Context context,
			String originatingAddress, String string) {

		String msgSubject = "SMS From " + originatingAddress;
		String msgBody = string;

		// Get the xml/preferences.xml preferences
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		String username = prefs.getString("usernamePref", "");
		String password = prefs.getString("passPref", "");
		String forwardingAddress = prefs.getString("emailAddressPref", "");

		Mail m = new Mail(context, username, password);

		String[] toArr = { forwardingAddress };
		m.setRecipients(toArr);
		m.setSender(originatingAddress);
		m.setSubject(msgSubject);
		m.setBody(msgBody);

		try {
			if (!(m.send())) {
				Log.e(SMSForwarderConstants.TAG,
						context.getString(R.string.msg_email_fail));
			}
		} catch (Exception e) {
			Log.e(SMSForwarderConstants.TAG,
					context.getString(R.string.msg_email_send_exception), e);
		}
	}

}
