package tarun0.com.callallower;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class MyPhoneStateListener extends Service {
    private String TAG = "blocker";
    public MyPhoneStateListener() {
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "inservice");
        StateListener phoneStateListener = new StateListener();
        TelephonyManager telephonymanager = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        telephonymanager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    class StateListener extends PhoneStateListener{

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
                        if (MainActivity.blocked.contains(incomingNumber)) {
                            telephony.endCall();
                            Toast.makeText(getApplicationContext(), "Rejected: "+incomingNumber, Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "CALL ENDED!!!");
                        }
                        else
                            Toast.makeText(getApplicationContext(), "Doesn't exist in record.", Toast.LENGTH_LONG).show();

                        //AudioManager audioManager = (AudioManager)getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                       // audioManager.setStreamMute(AudioManager.STREAM_RING, true);
                        //audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

                        //Toast.makeText(getApplicationContext(), "Works fine!", Toast.LENGTH_LONG).show();


                    } catch(Exception e){
                        Log.d("blockerError",e.getMessage());
                    }

                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "OFFHOOK");
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "Why are you always IDLE? :/");
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {

    }
}



    /*TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
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