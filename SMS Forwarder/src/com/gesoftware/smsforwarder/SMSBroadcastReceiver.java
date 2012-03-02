/**
 * 
 */
package com.gesoftware.smsforwarder;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * @author Sharon
 * 
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {
	
	private boolean tempInternetConnection = false;

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
		
		if (!isNetworkConnected(context)) {
			tempInternetConnection = true;
			enableInternet(context, true); 
		}

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
		
		if (tempInternetConnection && isNetworkConnected(context)) {
			tempInternetConnection = false;
			// Close any open network connection
			enableInternet(context, false);
		}
	}

	private boolean isNetworkConnected(Context context) {         
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);         
		NetworkInfo network = cm.getActiveNetworkInfo();         
		if (network != null) {             
			return network.isAvailable();         
		}         
		
		return false;     
	} 

	private void enableInternet(Context context, boolean enable) {
		WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE); 
		wifiManager.setWifiEnabled(enable); 
		
		try {
			ConnectivityManager mgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE); 
			Method dataMtd;
			dataMtd = ConnectivityManager.class.getDeclaredMethod("setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true); 
			dataMtd.invoke(mgr, enable);  
			
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
