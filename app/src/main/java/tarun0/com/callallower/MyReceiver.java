package tarun0.com.callallower;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    private String TAG = "blocker receiver";
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction())) {
            Log.d(TAG, "not my job");
        }
        else {
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);

            switch (tm.getCallState()) {
                case TelephonyManager.CALL_STATE_RINGING:  // incoming call
                    Log.d(TAG, "RINGING" + intent.getStringExtra("incoming_number"));

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "OFFHOOK");
                    break;

                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "IDLE");
                    break;
            }
        }
    }
}
