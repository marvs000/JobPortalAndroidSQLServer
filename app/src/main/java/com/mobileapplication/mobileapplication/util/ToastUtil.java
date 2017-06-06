package com.mobileapplication.mobileapplication.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by ernestepistola on 2/16/17.
 */
public class ToastUtil {

    public static void createToast(String message, Activity activity){
        Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        System.out.println("[TOAST]: " + message);
    }

}
