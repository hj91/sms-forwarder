/**
 * 
 */
package com.gesoftware.smsforwarder;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * @author Sharon
 * 
 */
public class SMSForwarderWidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		Log.w(SMSForwarderConstants.TAG, "onUpdate method called");
		// Get all ids
		ComponentName thisWidget = new ComponentName(context,
				SMSForwarderWidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

		// Build the intent to call the service
		Intent intent = new Intent(context.getApplicationContext(),
				SMSForwarderControlService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, allWidgetIds);

		// Update the widgets via the service
		context.startService(intent);
		
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
	    Log.i(SMSForwarderConstants.TAG, "onReceive called with " + intent.getAction());
	    RemoteViews remoteViews = new RemoteViews(context.getPackageName (), R.layout.widget);
	    if (intent.getAction().equals(SMSForwarderConstants.ACTION_ENABLE_DISABLE)) {
	    	Log.d(SMSForwarderConstants.TAG, "enabling or disabling intent received");
			// Toggle status of SMS forwarding
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(context.getApplicationContext());
			SharedPreferences.Editor editor = prefs.edit();
			editor.putBoolean("enableForwardingPref", !(prefs.getBoolean("enableForwardingPref", false)));
			editor.commit();
			refreshStatusIcon(context, remoteViews);
	    } else if (intent.getAction().equals(SMSForwarderConstants.ACTION_REFRESH_ICON)) {
	    	refreshStatusIcon(context, remoteViews);
	    } else {
	        super.onReceive(context, intent);
	    }
	    ComponentName cn = new ComponentName(context, SMSForwarderWidgetProvider.class);
	    AppWidgetManager.getInstance(context).updateAppWidget(cn, remoteViews);	 
	}

	public void refreshStatusIcon(final Context context,
			final RemoteViews remoteViews) {
		
		SharedPreferences prefs = PreferenceManager
			.getDefaultSharedPreferences(context.getApplicationContext());

		if (prefs.getBoolean("enableForwardingPref", false)) {
			remoteViews.setImageViewResource(R.id.icon, R.drawable.icon_enabled);
		} else {
			remoteViews.setImageViewResource(R.id.icon, R.drawable.icon_disabled);
		}
	}
}
