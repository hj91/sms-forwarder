/**
 * 
 */
package com.gesoftware.smsforwarder.receiver.test;

import android.content.Intent;
import android.telephony.SmsMessage;
import android.os.Bundle;
import android.os.Parcelable;
import android.test.AndroidTestCase;

import com.gesoftware.smsforwarder.SMSBroadcastReceiver;

/**
 * @author Sharon
 *
 */
public class SMSBroadcastReceiverTest extends AndroidTestCase {
	    private SMSBroadcastReceiver mReceiver;

	    @Override
	    protected void setUp() throws Exception
	    {
	        super.setUp();

	        mReceiver = new SMSBroadcastReceiver(getContext());
	    }

	    public void testReceiveSMS()
	    {
	    	SmsMessage[] messages = new SmsMessage[1];
	    	
	    	String pdu = "07911326040000F0040B911346610089F60000208062917314080CC8F71D14969741F977FD07";
	    	SmsMessage sms = SmsMessage.createFromPdu(fromHexString(pdu));
	    	messages[0] = sms;

	    	Bundle bundle = new Bundle();
	    	bundle.putSerializable("pdus", messages);
	        Intent intent = new Intent().setAction("android.provider.Telephony.SMS_RECEIVED");
	        intent.putExtras(bundle);

	        mReceiver.onReceive(getContext(), intent);        
	    }

	    private static byte[] fromHexString(final String encoded) {
	        if ((encoded.length() % 2) != 0)
	            throw new IllegalArgumentException("Input string must contain an even number of characters");

	        final byte result[] = new byte[encoded.length()/2];
	        final char enc[] = encoded.toCharArray();
	        for (int i = 0; i < enc.length; i += 2) {
	            StringBuilder curr = new StringBuilder(2);
	            curr.append(enc[i]).append(enc[i + 1]);
	            result[i/2] = (byte) Integer.parseInt(curr.toString(), 16);
	        }
	        return result;
	    }

}
