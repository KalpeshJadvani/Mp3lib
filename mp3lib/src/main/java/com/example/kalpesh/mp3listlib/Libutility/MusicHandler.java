package com.example.kalpesh.mp3listlib.Libutility;

import android.content.Context;
import android.os.Handler;

/**
 * Created by omsai on 4/25/2017.
 */

public class MusicHandler {

    public static volatile Handler applicationHandler = null;
    Context applicationContext;

    public MusicHandler(Context Context){

        this.applicationContext=Context;

        this.applicationHandler = new Handler(applicationContext.getMainLooper());

    }



    public static void runOnUIThread(Runnable runnable) {
        runOnUIThread(runnable, 0);
    }

    public static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            applicationHandler.post(runnable);
        } else {
            applicationHandler.postDelayed(runnable, delay);
        }
    }
}
