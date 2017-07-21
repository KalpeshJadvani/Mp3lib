package com.example.kalpesh.mp3listlib.Libutility;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.example.kalpesh.mp3listlib.LibLayout.PlayPauseView;
import com.example.kalpesh.mp3listlib.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by omsai on 4/24/2017.
 */

public class AllSongsListAdapter extends BaseAdapter {
    private ArrayList<ObjectArrary> songList = new ArrayList<ObjectArrary>();
    private ArrayList<ObjectArrary> CopysongList;
    private Activity context = null;
    private LayoutInflater layoutInflater;
    private ImageLoader imageLoader ;
    private DisplayImageOptions options;
    private ListenerClass listenerClass;

     public AllSongsListAdapter(Activity mContext,ArrayList<ObjectArrary> List ) {
         this.context = mContext;
         this.songList = List;
         listenerClass =new ListenerClass(mContext);
         this.layoutInflater = LayoutInflater.from(context);
         this.CopysongList = new ArrayList<ObjectArrary>();
         this.CopysongList.addAll(songList);

         ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                 .threadPoolSize(5)
                 .threadPriority(Thread.MIN_PRIORITY + 2)
                 .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                 .build();

         this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                 .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                 .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();

         imageLoader = ImageLoader.getInstance();
         imageLoader.init(config);


     }



    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.inflate_allsongsitem, null);
            mViewHolder.song_row = (LinearLayout) convertView.findViewById(R.id.inflate_allsong_row);
            mViewHolder.textViewSongName = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongname);
            mViewHolder.textViewSongArtisNameAndDuration = (TextView) convertView.findViewById(R.id.inflate_allsong_textsongArtisName_duration);
            mViewHolder.imageSongThm = (ImageView) convertView.findViewById(R.id.inflate_allsong_imgSongThumb);
            mViewHolder.imagemore = (ImageView) convertView.findViewById(R.id.img_moreicon);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        ObjectArrary mDetail = songList.get(position);

        String audioDuration = "";
        try {
            audioDuration = getAudioDuration(Long.parseLong(mDetail.getSongDerail().getDuration()));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        mViewHolder.textViewSongArtisNameAndDuration.setText((audioDuration.isEmpty() ? "" : audioDuration + " | ") + mDetail.getSongDerail().getArtist());
        mViewHolder.textViewSongName.setText(mDetail.getSongDerail().getTitle());
        String contentURI = "content://media/external/audio/media/" + mDetail.getSongDerail().getId() + "/albumart";
        imageLoader.displayImage(contentURI, mViewHolder.imageSongThm, options);

      //  mViewHolder.imagemore.setImageResource(R.drawable.ic_pick_music);

        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ObjectArrary mDetail = songList.get(position);
                listenerClass.playPushbutton(mDetail);
            }
        });


        mViewHolder.imagemore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    ObjectArrary mDetail = songList.get(position);
                    listenerClass.returnIntent(mDetail);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });



        mViewHolder.imagemore.setColorFilter(Color.DKGRAY);
        if (Build.VERSION.SDK_INT > 15) {
            mViewHolder.imagemore.setImageAlpha(255);
        } else {
            mViewHolder.imagemore.setAlpha(255);
        }


        return convertView;
    }
    @Override
    public int getCount() {
        return (songList != null) ? songList.size() : 0;
    }


    class ViewHolder {
        TextView textViewSongName;
        ImageView imageSongThm;
        ImageView imagemore;
        TextView textViewSongArtisNameAndDuration;
        LinearLayout song_row;
    }



    public  String getAudioDuration(long durationLong) {
        // long totalSecs = durationLong / 1000;
        long totalSecs = durationLong;
        long hours = totalSecs / 3600;
        long minutes = (totalSecs % 3600) / 60;
        long seconds = totalSecs % 60;

        String duration = "";
        if (hours != 0) {
            duration = String.format("%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            duration = String.format("%02d:%02d", minutes, seconds);
        }

        return duration;
    }


    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        songList.clear();
        if (charText.length() == 0) {
            songList.addAll(CopysongList);
        } else {

            for (ObjectArrary wp : CopysongList) {

//                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
//
//                    songList.add(wp);
//
//                }
//            }

            if (wp.getSongDerail().getDisplay_name().toLowerCase(Locale.getDefault()).startsWith(charText))
            {
                songList.add(wp);
            }
            }

        }
        notifyDataSetChanged();
    }

}
