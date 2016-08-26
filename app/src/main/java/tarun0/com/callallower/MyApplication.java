package tarun0.com.callallower;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by Tarun on 19/08/2016.
 */
public class MyApplication extends Application {

    public Tracker mTracker;

    synchronized public void startTracking() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.track_app);
            analytics.enableAutoActivityReports(this);
            analytics.setLocalDispatchPeriod(2);
        }
    }

    public Tracker getmTracker() {
        startTracking();
        return mTracker;
    }
}
