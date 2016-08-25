package tarun0.com.callallower.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import tarun0.com.callallower.CallBlockingService;
import tarun0.com.callallower.R;
import tarun0.com.callallower.utils.Util;

/**
 * Implementation of App Widget functionality.
 */
public class Widget extends AppWidgetProvider {
    public static String TOGGLE_SERVICE_STATE = "TOGGLE_SERVICE_STATE";
    public static final String TAG_STOP_SERVICE = "STOP_CALL_BLOCKING_SERVICE";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);

        if (Util.isServiceRunning(CallBlockingService.class, context)) {
           // views.setTextViewText(R.id.appwidget_button, "ON");
            views.setImageViewResource(R.id.appwidget_button, R.drawable.widget_off);
        } else {
           // views.setTextViewText(R.id.appwidget_button, "OFF");
            views.setImageViewResource(R.id.appwidget_button, R.drawable.widget_on);
        }

        Intent intent = new Intent(context, Widget.class);
        intent.setAction(TAG_STOP_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_button, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(TOGGLE_SERVICE_STATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            onUpdate(context, appWidgetManager, appWidgetIds);
        }
        else if (intent.getAction().equals(TAG_STOP_SERVICE)) {
            if (Util.isServiceRunning(CallBlockingService.class, context)) {
                Intent i = new Intent(context, CallBlockingService.class);
                context.stopService(i);
            } else {
                Intent i = new Intent(context, CallBlockingService.class);
                context.startService(i);
            }
        }
        super.onReceive(context, intent);
    }
}

