package tarun0.com.callallower;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Tarun on 19/08/2016.
 */
public class MyApplication extends Application {

    public Tracker mTtracker;

    public void startTracking() {
        if (mTtracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTtracker = analytics.newTracker(R.xml.track_app);
            analytics.enableAutoActivityReports(this);
            analytics.setLocalDispatchPeriod(2);
            analytics.getLogger()
                    .setLogLevel(Logger.LogLevel.VERBOSE);
        }
    }

    public Tracker getmTracker() {
        startTracking();
        return mTtracker;
    }
}
