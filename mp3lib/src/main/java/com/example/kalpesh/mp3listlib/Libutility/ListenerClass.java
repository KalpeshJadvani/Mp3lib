package com.example.kalpesh.mp3listlib.Libutility;

import android.app.Activity;
import android.util.Log;

/**
 * Created by omsai on 4/24/2017.
 */

public class ListenerClass {
     MediaListener mediaListener;
     public ListenerClass(Activity activity){

         mediaListener=(MediaListener)activity;

     }

    public void nextSons(ObjectArrary Detail){
        mediaListener.nextSong(Detail);
    }
    public void prevSong(ObjectArrary Detail){
        mediaListener.prevSong(Detail);
    }
    public void playPushbutton(ObjectArrary Detail){
        mediaListener.playPushbutton(Detail);
           }
    public void updateDetail(ObjectArrary Detail){
        mediaListener.updateDetail(Detail);
    }


    public void updateSeek(){
        mediaListener.updateSeek();
    }
      public void returnIntent(ObjectArrary Detail){
        mediaListener.returnIntent(Detail);
    }



    public interface MediaListener {
        void returnIntent(ObjectArrary Detail);
        void nextSong(ObjectArrary Detail);
        void prevSong(ObjectArrary Detail);
        void updateSeek();
        void playPushbutton(ObjectArrary Detail);
        void updateDetail(ObjectArrary Detail);
    }

}
