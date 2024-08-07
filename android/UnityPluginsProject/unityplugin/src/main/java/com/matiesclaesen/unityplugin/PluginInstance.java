package com.matiesclaesen.unityplugin;

import android.app.Activity;
import android.widget.Toast;

public class PluginInstance {
    public int Add(int i, int j) {
        return i+j;
    }

    private static Activity unityActivity;

    public static void receiveUnityActivity(Activity activity) {
        unityActivity = activity;
    }

    public void showMessage(String message) {
        Toast.makeText(unityActivity, message, Toast.LENGTH_SHORT).show();
    }
}
