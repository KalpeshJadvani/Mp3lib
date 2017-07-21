package com.example.kalpesh.mp3listlib.Libutility;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.MediaController;

import java.util.ArrayList;

/**
 * Created by omsai on 4/24/2017.
 */

public class ReadAllSongs {
    private Cursor cursor = null;
     public static enum SonLoadFor {
        All, Gener, Artis, Album, Musicintent,
    }
    private static volatile ReadAllSongs Instance = null;



    public static ReadAllSongs getInstance() {
        ReadAllSongs localInstance = Instance;
        if (localInstance == null) {
            synchronized (MediaController.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new ReadAllSongs();
                }
            }
        }
        return localInstance;
    }


    public void loadMusicList(final Context context, final long id, final SonLoadFor sonloadfor) {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            ArrayList<ObjectArrary> songsList = null;

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    songsList = getList(context, id, sonloadfor);

                } catch (Exception e) {
                    closeCrs();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (phonemediacontrolinterface != null) {
                    phonemediacontrolinterface.loadSongsComplete(songsList);
                }

            }
        };

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR1) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    public ArrayList<ObjectArrary> getList(final Context context, final long id, final SonLoadFor sonloadfor) {
        ArrayList<ObjectArrary> songsList = new ArrayList<>();
        String sortOrder = "";
        switch (sonloadfor) {
            case All:
                String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
//                sortOrder = MediaStore.Audio.Media.DATE_ADDED + " desc";
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = ((Activity) context).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionSongs, selection, null, sortOrder);
                songsList = getSongsFromCursor(cursor);
                break;

            case Gener:
                Uri uri = MediaStore.Audio.Genres.Members.getContentUri("external", id);
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = ((Activity) context).getContentResolver().query(uri, projectionSongs, null, null, null);
                songsList = getSongsFromCursor(cursor);
                break;

            case Artis:
                String where = MediaStore.Audio.Media.ARTIST_ID + "=" + id + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = ((Activity) context).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionSongs, where, null, sortOrder);
                songsList = getSongsFromCursor(cursor);
                break;

            case Album:
                String wherecls = MediaStore.Audio.Media.ALBUM_ID + "=" + id + " AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
                cursor = ((Activity) context).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionSongs, wherecls, null, sortOrder);
                songsList = getSongsFromCursor(cursor);
                break;

            case Musicintent:

//                String condition = MediaStore.Audio.Media.DATA + "='" + path + "' AND " + MediaStore.Audio.Media.IS_MUSIC + "=1";
//                sortOrder = MediaStore.Audio.Media.DEFAULT_SORT_ORDER;
//                cursor = ((Activity) context).getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projectionSongs, condition, null, sortOrder);
//                songsList = getSongsFromCursor(cursor);

                break;


        }
        return songsList;
    }

    private void closeCrs() {
        if (cursor != null) {
            try {
                cursor.close();
            } catch (Exception e) {
                Log.e("tmessages", e.toString());
            }
        }
    }


    private ArrayList<ObjectArrary> getSongsFromCursor(Cursor cursor) {
        ArrayList<ObjectArrary> generassongsList = new ArrayList<ObjectArrary>();
        int no=0;
        try {
            if (cursor != null && cursor.getCount() >= 1) {
                int _id = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int artist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int album_id = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                int title = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int data = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int display_name = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
                int duration = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                while (cursor.moveToNext()) {

                    int ID = cursor.getInt(_id);
                    String ARTIST = cursor.getString(artist);
                    String TITLE = cursor.getString(title);
                    String DISPLAY_NAME = cursor.getString(display_name);
                    String DURATION = cursor.getString(duration);
                    String Path = cursor.getString(data);
                    SongDetail mSongDetail = new SongDetail(ID, album_id, ARTIST, TITLE, Path, DISPLAY_NAME, DURATION);
                    generassongsList.add(new ObjectArrary(mSongDetail,no));
                    no++;
                }
            }
            closeCrs();
        } catch (Exception e) {
            closeCrs();
            e.printStackTrace();
        }
        return generassongsList;
    }


    private final String[] projectionSongs = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION};


    public static PhoneMediaControlINterface phonemediacontrolinterface;

    public static PhoneMediaControlINterface getPhonemediacontrolinterface() {
        return phonemediacontrolinterface;
    }

    public static void setPhonemediacontrolinterface(PhoneMediaControlINterface phonemediacontrolinterface) {
        ReadAllSongs.phonemediacontrolinterface = phonemediacontrolinterface;
    }
    public interface PhoneMediaControlINterface {
        public void loadSongsComplete(ArrayList<ObjectArrary> songsList);
    }
}
