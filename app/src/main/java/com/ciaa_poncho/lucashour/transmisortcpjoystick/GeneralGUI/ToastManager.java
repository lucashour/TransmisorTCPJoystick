package com.ciaa_poncho.lucashour.transmisortcpjoystick.GeneralGUI;

import android.app.Activity;
import android.widget.Toast;

public class ToastManager {

    public static void displayInformationMessage(String message, Toast toast, Activity activity){
        if (!message.equals(""))
            showToastMessage(message, toast, activity);
    }

    public static void showToastMessage(String message, Toast toast, Activity activity){
        showToast(message, Toast.LENGTH_SHORT, toast, activity);
    }

    public static void showLongToastMessage(String message, Toast toast, Activity activity){
        showToast(message, Toast.LENGTH_LONG, toast, activity);
    }

    private static void showToast(String message, int duration, Toast toast, Activity activity){
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(activity.getApplicationContext(), message, duration);
        toast.show();
    }
}
