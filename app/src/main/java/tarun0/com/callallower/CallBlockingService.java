package tarun0.com.callallower;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

import tarun0.com.callallower.utils.Util;
import tarun0.com.callallower.widget.Widget;

public class CallBlockingService extends Service {
    private String TAG = "blocker";
    private Context mContext;
    TelephonyManager telephonymanager;
    StateListener phoneStateListener;
    public CallBlockingService() {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        Toast.makeText(CallBlockingService.this, getResources().getString(R.string.service_started), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Widget.TOGGLE_SERVICE_STATE).setPackage(this.getPackageName());
        sendBroadcast(intent);
        phoneStateListener = new StateListener();
        telephonymanager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class StateListener extends PhoneStateListener{
        Cursor cursor;
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            Log.d(TAG, incomingNumber);
            switch(state){
                case TelephonyManager.CALL_STATE_RINGING:
                    //Disconnect the call here...
                    Log.d(TAG, "RING");

                    try{
                        TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                        Class c = Class.forName(manager.getClass().getName());
                        Method m = c.getDeclaredMethod("getITelephony");
                        m.setAccessible(true);
                        ITelephony telephony = (ITelephony)m.invoke(manager);
                        Log.e("Incoming number", incomingNumber);
                        String test[] = new String[1];
                        test[0] = Util.setPhoneNumber(incomingNumber);
                        cursor = mContext.getContentResolver().query(ListsContract.BlackListEntry.CONTENT_URI,
                                null,
                                ListsContract.BlackListEntry.COLUMN_NUMBER + "= ?",
                                test, null);

                        if (cursor!=null && cursor.getCount() == 0) {
                            telephony.endCall();
                            Toast.makeText(getApplicationContext(), "Rejected: "+incomingNumber, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "CALL ENDED!!!");
                        }
                        else
                            Log.d(TAG, "Doesn't exist in record.");

                        //AudioManager audioManager = (AudioManager)getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                        // audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                        // audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                    } catch(Exception e){
                        Log.d("blockerError",e.getMessage());
                    }
                    finally {
                        cursor.close();
                    }

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

    @Override
    public void onDestroy() {
        telephonymanager.listen(phoneStateListener,  PhoneStateListener.LISTEN_NONE);
        Intent intent = new Intent(Widget.TOGGLE_SERVICE_STATE).setPackage(this.getPackageName());
        sendBroadcast(intent);
        Toast.makeText(CallBlockingService.this, getResources().getString(R.string.service_stopped), Toast.LENGTH_SHORT).show();
    }
}

    /*  Code to try silencing the ringer on incoming call by faking volume button key event
    TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
    Class c = Class.forName(manager.getClass().getName());
    Method m = c.getDeclaredMethod("getITelephony");
    m.setAccessible(true);
        ITelephony telephony = (ITelephony)m.invoke(manager);
        //telephony.endCall();

        AudioManager audioManager = (AudioManager)getBaseContext().getSystemService(Context.AUDIO_SERVICE);
        // audioManager.setStreamMute(AudioManager.STREAM_RING, true);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.d("blocker", "CAL ENDED!!!");*/

/*new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Log.d("blocker", "Inside key emulator");
                                    Instrumentation inst = new Instrumentation();
                                    //This is for Volume Down, change to
                                    //KEYCODE_VOLUME_UP for Volume Up.
                                    inst.sendKeyDownUpSync(KeyEvent.KEYCODE_VOLUME_DOWN);
                                }catch(Exception e){
                                    Log.e("Emulate key", "error");
                                }
                            }
                        }).start();*/