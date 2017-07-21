package com.example.kalpesh.mp3listlib.Libutility;

import android.app.Activity;

import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.util.Log;


import com.example.kalpesh.mp3listlib.Mp3List;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by omsai on 4/24/2017.
 */

public class MediaLibController {
    ArrayList<ObjectArrary> songList;
    Activity mContext;
    private boolean useFrontSpeaker = false;
    private MediaPlayer audioPlayer = null;

    private final Object progressTimerSync = new Object();
    private final Object sync = new Object();
    private Timer progressTimer = null;
    private int ignoreFirstProgress = 0;
    ListenerClass listenerClass;
    private int lastProgress = 0;
    private boolean isPaused = true;
    private long lastPlayPcm;
    private long currentTotalPcmDuration;
    private boolean processRun=false;
    public MediaLibController(Activity context, ArrayList<ObjectArrary> List){
          this.songList=List;
          this.mContext=context;
          listenerClass=new ListenerClass(context);
      }



    public boolean isAudioPaused() {
        return isPaused;
    }


    public boolean playAudio(ObjectArrary Detail) {

        SongDetail mSongDetail=Detail.getSongDerail();

        if (mSongDetail == null) {
            return false;
        }
        listenerClass.updateDetail(Detail);
        if(audioPlayer!=null){
            if(audioPlayer.isPlaying()){
               cleanupPlayer();
            }
        }
        try {
            audioPlayer = new MediaPlayer();
            audioPlayer.setAudioStreamType(useFrontSpeaker ? AudioManager.STREAM_VOICE_CALL : AudioManager.STREAM_MUSIC);
            audioPlayer.setDataSource(mSongDetail.getPath());
            audioPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {


                }
            });
            audioPlayer.prepare();
            audioPlayer.start();
            startProgressTimer();
            isPaused=false;
            lastProgress = 0;
        } catch (Exception e) {
            if (audioPlayer != null) {
                audioPlayer.release();
                audioPlayer = null;
            }
            return false;
        }



     return true;
    }




    public boolean pauseAudio(SongDetail messageObject) {
        // stopProximitySensor();

            if (audioPlayer == null || messageObject == null) {

            return false;
             }
         stopProgressTimer();

        try {
            if (audioPlayer != null) {
                audioPlayer.pause();

                isPaused=true;
            }
           // NotificationMyManager.getInstance().postNotificationName(NotificationMyManager.audioPlayStateChanged, MusicPreferance.playingSongDetail.getId());

        } catch (Exception e) {
            Log.e("tmessages", e.toString());

            return false;
        }
        return true;
    }

    public boolean resumeAudio(SongDetail messageObject) {
        if (audioPlayer == null || messageObject == null) {
            return false;
        }
        try {
            startProgressTimer();

            if (audioPlayer != null) {
                audioPlayer.start();
            }

            isPaused = false;
           // NotificationMyManager.getInstance().postNotificationName(NotificationMyManager.audioPlayStateChanged, MusicPreferance.playingSongDetail.getId());
        } catch (Exception e) {
            return false;
        }
        return true;
    }


    /**
     * When Get Stop Player, clear the Object instance
     *

     */
    public void cleanupPlayer() {
        if (audioPlayer != null) {
            try {
                audioPlayer.reset();
            } catch (Exception e) {
            }
            try {
                audioPlayer.stop();
            } catch (Exception e) {
            }
            try {
                audioPlayer.release();
                audioPlayer = null;
            } catch (Exception e) {
            }
        }
        stopProgressTimer();

        isPaused = true;
    }

    public boolean getProgreThreadrun()
    {
        return processRun;
    }

    public void stopProgressTimer() {

        synchronized (progressTimerSync) {
            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                    processRun=false;
                } catch (Exception e) {
                    Log.e("tmessages", e.toString());
                }
            }
        }
    }

    public void startProgressTimer() {

        synchronized (progressTimerSync) {

            if (progressTimer != null) {
                try {
                    progressTimer.cancel();
                    progressTimer = null;
                } catch (Exception e) {
                    // FileLog.e("tmessages", e);
                }
            }
            processRun=true;
            progressTimer = new Timer();
            progressTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    synchronized (sync) {
                        MusicHandler.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                if (audioPlayer != null  && !isPaused) {
                                    try {
                                        if (ignoreFirstProgress != 0) {
                                            ignoreFirstProgress--;
                                            return;
                                        }
                                        int progress;
                                        float value;
                                        if (audioPlayer != null) {
                                            progress = audioPlayer.getCurrentPosition();
                                            value = (float) lastProgress / (float) audioPlayer.getDuration();
                                            if (progress <= lastProgress) {
                                                return;
                                            }
                                        } else {
                                            progress = (int) (lastPlayPcm / 48.0f);
                                            value = (float) lastPlayPcm / (float) currentTotalPcmDuration;
                                            if (progress == lastProgress) {
                                                return;
                                            }
                                        }
                                        lastProgress = progress;

                                        Mp3List.Lastplay.getSongDerail().audioProgress = value;
                                        Mp3List.Lastplay.getSongDerail().audioProgressSec = lastProgress / 1000;

                                        listenerClass.updateSeek();

                                    } catch (Exception e) {
                                    }
                                }
                            }
                        });
                    }
                }
            }, 0, 17);
        }
    }

    /**
     * seekToProgress functionsl for Audio Progress
     *
     * @param mSongDetail
     * @param progress
     * @return
     */

    public boolean seekToProgress(SongDetail mSongDetail, float progress) {
        if (audioPlayer == null) {
            return false;
        }
        try {

            if (audioPlayer != null) {
                int seekTo = (int) (audioPlayer.getDuration() * progress);
                audioPlayer.seekTo(seekTo);
                lastProgress = seekTo;
            }


        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
