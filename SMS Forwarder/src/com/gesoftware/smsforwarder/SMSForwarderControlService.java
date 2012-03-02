/**
 * 
 */
package com.gesoftware.smsforwarder;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * @author Sharon
 * 
 */
public class SMSForwarderControlService extends Service {
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.i(SMSForwarderConstants.TAG, "Called");
		// Create some random data

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
				.getApplicationContext());

		int[] allWidgetIds = intent
				.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);

		ComponentName thisWidget = new ComponentName(getApplicationContext(),
				SMSForwarderWidgetProvider.class);
		int[] allWidgetIds2 = appWidgetManager.getAppWidgetIds(thisWidget);
		Log.w(SMSForwarderConstants.TAG, "From Intent" + String.valueOf(allWidgetIds.length));
		Log.w(SMSForwarderConstants.TAG, "Direct" + String.valueOf(allWidgetIds2.length));

		for (int widgetId : allWidgetIds) {
			
			// fetching our remote views
			RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
					R.layout.widget);

			// enable/disable intent
			final Intent enableIntent = new Intent(getApplicationContext(),	SMSForwarderWidgetProvider.class);
			enableIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,	widgetId);
			enableIntent.setAction(SMSForwarderConstants.ACTION_ENABLE_DISABLE);
			final PendingIntent pendingEnableIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, enableIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			// configure intent
			final Intent configureIntent = new Intent(getApplicationContext(), Preferences.class);
			configureIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			final PendingIntent pendingConfigureIntent = PendingIntent.getActivity(getApplicationContext(), 0, configureIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);
			
			// bind click events to the pending intents
			remoteViews.setOnClickPendingIntent(R.id.configure_text, pendingConfigureIntent);
			remoteViews.setOnClickPendingIntent(R.id.icon, pendingEnableIntent);
			
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());

			if (prefs.getBoolean("enableForwardingPref", false)) {
				remoteViews.setImageViewResource(R.id.icon, R.drawable.icon_enabled);
			} else {
				remoteViews.setImageViewResource(R.id.icon, R.drawable.icon_disabled);
			}
			
			AppWidgetManager manager = AppWidgetManager.getInstance(getApplicationContext());
			manager.updateAppWidget(thisWidget, remoteViews);
		}
		stopSelf();

		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
