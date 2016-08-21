package tarun0.com.callallower.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Created by Tarun on 19/08/2016.
 */
public class Util {
    public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Method to remove spaces/+/codes and return only digits of the number
    public static String setPhoneNumber(String str) {
        String number = str
                .replaceAll("\\s+","")
                .replaceAll("-","")
                .replaceAll("\\(","")
                .replaceAll("\\)","");
        if (number.startsWith("+")) {
            number = number.substring(3);
        } else if (number.startsWith("0")) {
            number = number.substring(1);
        }
        return number;
    }
}
