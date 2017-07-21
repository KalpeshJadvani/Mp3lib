package com.example.kalpesh.mp3listlib;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.example.kalpesh.mp3listlib.LibLayout.PlayPauseView;
import com.example.kalpesh.mp3listlib.LibLayout.Slider;
import com.example.kalpesh.mp3listlib.LibLayout.SlidingUpPanelLayout;
import com.example.kalpesh.mp3listlib.Libutility.AllSongsListAdapter;
import com.example.kalpesh.mp3listlib.Libutility.ListenerClass;
import com.example.kalpesh.mp3listlib.Libutility.MediaLibController;
import com.example.kalpesh.mp3listlib.Libutility.MusicHandler;
import com.example.kalpesh.mp3listlib.Libutility.ObjectArrary;
import com.example.kalpesh.mp3listlib.Libutility.ReadAllSongs;
import com.example.kalpesh.mp3listlib.Libutility.SongDetail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Mp3List extends Activity implements ListenerClass.MediaListener ,View.OnClickListener, Slider.OnValueChangedListener{
    public ArrayList<ObjectArrary> songList = new ArrayList<ObjectArrary>();
    public Activity context;
    public AllSongsListAdapter mAllSongsListAdapter;
    public ListView recycler_songslist;
    public MediaLibController mediaLibController;
    public static ObjectArrary Lastplay;

    private SlidingUpPanelLayout mLayout;
    private boolean isExpand = false;
    public int seekValuincres;

    public static String title;

    private PlayPauseView btn_playpause;
    private PlayPauseView btn_playpausePanel;
    private Slider audio_progress;
    private boolean isDragingStart = false;

    private ImageLoadingListener animateFirstListener ;
    private DisplayImageOptions options;
    private ImageLoader imageLoader;

    private ImageView songAlbumbg;
    private ImageView img_bottom_slideone;

    private ImageView imgbtn_backward;
    private ImageView imgbtn_forward;

    private TextView txt_playesongname;
    private TextView txt_songartistname;

    private TextView txt_timeprogress;
    private TextView txt_timetotal;
    MusicHandler musicHandler;
    Toolbar toolbar;

    MenuItem myActionMenuItem ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);

        setContentView(R.layout.activity_mp3_list);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        title = getIntent().getExtras().getString("title");

        toolbar.setTitle(title);

        toolbar.setTitleTextColor(Color.WHITE);

        context = Mp3List.this;

        recycler_songslist = (ListView) findViewById(R.id.recycler_allSongs);

        loadAllSongs();

        toolbar.inflateMenu(R.menu.search_menu);


        musicHandler = new MusicHandler(getApplicationContext());

        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        btn_playpause = (PlayPauseView) findViewById(R.id.btn_play);

        audio_progress = (Slider) findViewById(R.id.audio_progress_control);

        btn_playpausePanel = (PlayPauseView) findViewById(R.id.bottombar_play);

        songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg_mid);
        img_bottom_slideone = (ImageView) findViewById(R.id.img_bottom_slideone);

        imgbtn_backward = (ImageView) findViewById(R.id.btn_backward);
        imgbtn_forward = (ImageView) findViewById(R.id.btn_forward);

        txt_playesongname = (TextView) findViewById(R.id.txt_playesongname);
        txt_songartistname = (TextView) findViewById(R.id.txt_songartistname);

        txt_timeprogress = (TextView) findViewById(R.id.slidepanel_time_progress);
        txt_timetotal = (TextView) findViewById(R.id.slidepanel_time_total);

        btn_playpausePanel.setOnClickListener(this);
        btn_playpause.setOnClickListener(this);

        imgbtn_backward.setOnClickListener(this);
        imgbtn_forward.setOnClickListener(this);


        final TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);
        final int coloraccent = typedvaluecoloraccent.data;
        audio_progress.setBackgroundColor(coloraccent);
        audio_progress.setValue(0);

        audio_progress.setOnValueChangedListener(this);


        btn_playpausePanel.Pause();
        btn_playpause.Pause();


        mLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                if (slideOffset == 0.0f) {
                    isExpand = false;

                } else if (slideOffset > 0.0f && slideOffset < 1.0f) {
                    // if (isExpand) {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f -
                    // slideOffset);
                    // } else {
                    // slidepanelchildtwo_topviewone.setAlpha(1.0f -
                    // slideOffset);
                    // slidepanelchildtwo_topviewtwo.setAlpha(1.0f);
                    // }

                } else {
                    isExpand = true;

                }

            }

            @Override
            public void onPanelExpanded(View panel) {

                isExpand = true;
            }

            @Override
            public void onPanelCollapsed(View panel) {

                isExpand = false;
            }

            @Override
            public void onPanelAnchored(View panel) {

            }

            @Override
            public void onPanelHidden(View panel) {

            }

        });

        audio_progress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                final int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if(Lastplay!=null){

                           if(!mediaLibController.isAudioPaused())
                            mediaLibController.stopProgressTimer();
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:

                        break;
                    case MotionEvent.ACTION_UP:

                        if(Lastplay!=null){

                            if(!mediaLibController.isAudioPaused()) {
                                mediaLibController.seekToProgress(Lastplay.getSongDerail(), (float) seekValuincres / 100);
                                mediaLibController.resumeAudio(Lastplay.getSongDerail());

                            }
                        }else {
                            Toast.makeText(context,"Please loard a song.... ",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }


                return false;
            }
        });
        setupMenu ();
    }


    private void setupMenu ()
    {


        Menu  mMenu = toolbar.getMenu();

        if (mMenu != null)
        {
            final SearchView searchView = (SearchView) MenuItemCompat.getActionView(mMenu.findItem(R.id.action_search));
            SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.onActionViewExpanded();
            searchView.setIconifiedByDefault(false);
            searchView.clearFocus();

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {

                        mAllSongsListAdapter.filter(newText);

                    return false;
                }
            });
        }

//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener()
//        {

//        }


    }
    private void loadAllSongs() {

            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {

                    // No explanation needed, we can request the permission.

                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }


            ReadAllSongs readAllSongs = ReadAllSongs.getInstance();
            ReadAllSongs.setPhonemediacontrolinterface(new ReadAllSongs.PhoneMediaControlINterface() {

                @Override
                public void loadSongsComplete(ArrayList<ObjectArrary> songsList_) {

                    songList = songsList_;

                    mAllSongsListAdapter = new AllSongsListAdapter(context, songList);

                    recycler_songslist.setAdapter(mAllSongsListAdapter);

                    mediaLibController = new MediaLibController(context, songList);

                    mAllSongsListAdapter.notifyDataSetChanged();
                }

            });
            readAllSongs.loadMusicList(context, -1, ReadAllSongs.SonLoadFor.All);

    }


    @Override
    public void onClick(View v) {

        int viewId = v.getId();
        if (viewId == R.id.bottombar_play) {

            PlayPauseEvent(v);

        } else if(viewId == R.id.btn_play) {

            PlayPauseEvent(v);

        } else if(viewId == R.id.btn_forward) {
            if(Lastplay!=null){
                int ObjectNo = Lastplay.getObjNo();
                if(songList.size()-1>ObjectNo){
                    mediaLibController.playAudio(songList.get(ObjectNo+1));
                }

                btn_playpausePanel.Play();
                btn_playpause.Play();
            }else {
                Toast.makeText(context,"Please loard a song.... ",Toast.LENGTH_SHORT).show();
            }


        } else if(viewId == R.id.btn_backward) {

            if(Lastplay!=null){
                int ObjectNo = Lastplay.getObjNo();
                if(ObjectNo>0){
                    mediaLibController.playAudio(songList.get(ObjectNo-1));
                }
                btn_playpausePanel.Play();
                btn_playpause.Play();
            }else {
                Toast.makeText(context,"Please loard a song.... ",Toast.LENGTH_SHORT).show();
            }



        }

    }

    private void PlayPauseEvent(View v) {


         if(Lastplay!=null){
            if (mediaLibController.isAudioPaused()) {
                  mediaLibController.resumeAudio(Lastplay.getSongDerail());
                btn_playpausePanel.Play();
                btn_playpause.Play();

            } else {
                mediaLibController.pauseAudio(Lastplay.getSongDerail());
                btn_playpausePanel.Pause();
                btn_playpause.Pause();

            }
         }else {
             Toast.makeText(context,"Please loard a song.... ",Toast.LENGTH_SHORT).show();
         }


    }



    public void loadSongsDetails(SongDetail mDetail) {

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(5)
                .threadPriority(Thread.MIN_PRIORITY + 2)
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .build();

        animateFirstListener = new AnimateFirstDisplayListener();

        this.options = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.bg_default_album_art)
                .showImageForEmptyUri(R.drawable.bg_default_album_art).showImageOnFail(R.drawable.bg_default_album_art).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565).build();


        imageLoader = ImageLoader.getInstance();
        imageLoader.init(config);


        String contentURI = "content://media/external/audio/media/" + mDetail.getId() + "/albumart";
        imageLoader.displayImage(contentURI, songAlbumbg, options, animateFirstListener);
        imageLoader.displayImage(contentURI, img_bottom_slideone, options, animateFirstListener);
        txt_playesongname.setText(mDetail.getTitle());
        txt_songartistname.setText(mDetail.getArtist());

        if (txt_timetotal != null) {
            long duration = Long.valueOf(mDetail.getDuration());
            txt_timetotal.setText(duration != 0 ? String.format("%d:%02d", duration / 60, duration % 60) : "-:--");
        }

        updateProgress(mDetail);

    }
    private void updateProgress(SongDetail mSongDetail) {
        if (audio_progress != null) {
            // When SeekBar Draging Don't Show Progress
            if (!isDragingStart) {

                // Progress Value comming in point it range 0 to 1
                audio_progress.setValue((int) (mSongDetail.audioProgress * 100));
            }
            String timeString = String.format("%d:%02d", mSongDetail.audioProgressSec / 60, mSongDetail.audioProgressSec % 60);
            txt_timeprogress.setText(timeString);
        }
    }


    private static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }

    }


    @Override
    protected void onStop() {
        mediaLibController.cleanupPlayer();
        finish();
        super.onStop();


    }

    @Override
    protected void onDestroy() {
        mediaLibController.cleanupPlayer();
        super.onDestroy();

    }

    @Override
    public void onBackPressed() {
        if (isExpand) {
            mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void returnIntent(ObjectArrary Detail) {
        mediaLibController.cleanupPlayer();
        returnResult(Detail);

    }

    @Override
    public void nextSong(ObjectArrary Detail) {

    }

    @Override
    public void prevSong(ObjectArrary Detail) {

    }

    @Override
    public void updateSeek() {
        updateProgress(Lastplay.getSongDerail());
    }

    @Override
    public void playPushbutton(ObjectArrary Detail) {
        mediaLibController.playAudio(Detail);
        btn_playpausePanel.Play();
        btn_playpause.Play();
    }

    @Override
    public void updateDetail(ObjectArrary Detail) {

           Lastplay = Detail;
           loadSongsDetails(Detail.getSongDerail());
    }

    @Override
    public void onValueChanged(int value) {
        seekValuincres = value;
    }

    private void returnResult(ObjectArrary Detail) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", Detail.getSongDerail().path);
        setResult(RESULT_OK, returnIntent);
        finish();
    }


}
