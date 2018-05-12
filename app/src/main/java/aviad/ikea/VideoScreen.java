package aviad.ikea;

import android.animation.AnimatorSet;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;

/**
 * Created by Aviad on 01/05/2018.
 */

public class VideoScreen extends BaseActivity implements View.OnClickListener {

    private SimpleExoPlayer player;
    private PlayerView playerView;
    private final int GENERAL_DURATION = 600;
    private VideoView videoView;
    private boolean nowPlaying = false , isLoadingVid = false;
    private ImageView btnNext, btnPre, bigPlay,btnSkip;
    private PlayPauseView btnPlay;
    private boolean firstPlay = true;
    private long[] videoStops = {9, 8500,13500,15770,24270,29110,35200,43000,52180,61990,71000,83480,
            90000,95000,98860,103110,107100,115000,119850,124600,128600,132500,135500,140700,144350,
            146900,149000,153300,155200,159600,164900,169000,174300,178480,184400,192000,194770,198750,201980,206000, 216350};
    private CheckBox checkBox;
    private ProgressBar progressBar;
    private LinearLayout layoutToHide;
    private int videoPartCounter = 1;
    private Handler handler;
    private Runnable runnable;
    private boolean autoStop = true;
    private boolean isLandScape = false;
    private LinearLayout btnsLayout;
    private boolean isLayoutInvis = false;
    private long lastPosition = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(savedInstanceState==null) {
            setContentView(R.layout.video_player_screen);
            initViews();
            runnable = new Runnable() {
                @Override
                public void run() {
                    if (autoStop) {
                        player.setPlayWhenReady(false);
                        playerView.setPlayer(player);
                        pressedPlay();
                    }
                    videoPartCounter++;
                }
            };
            handler = new Handler();
            startVideo();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setContentView(R.layout.video_player_screen_land);
            isLandScape = true;
            resumeView();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            setContentView(R.layout.video_player_screen);
            isLandScape = false;
            resumeView();
            bigPlay.setVisibility(View.GONE);

        }
    }

    private void resumeView() {
        handler.removeCallbacks(runnable);
        initViews();
        startVideo();
        progressBar.setVisibility(View.VISIBLE);
        player.seekTo(videoStops[videoPartCounter-1]);
        player.setPlayWhenReady(false);
        nowPlaying = false;
        if(!btnPlay.isPlay()){
            btnPlay.toggle();
            //btnPlay.setIsPlay();
        }
    }

    private void initViews() {
        playerView = findViewById(R.id.player_view);
        btnNext = findViewById(R.id.next_btn);
        btnPre = findViewById(R.id.back_btn);
        bigPlay = findViewById(R.id.big_play);
        btnPlay = findViewById(R.id.play_pause_view_large);
        progressBar = findViewById(R.id.progress_bar_video);
        btnSkip = findViewById(R.id.skip_btn);
        checkBox = findViewById(R.id.check_box);
        btnsLayout = findViewById(R.id.btns_layouot);

        btnSkip.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        bigPlay.setOnClickListener(this);
        if(isLandScape){
            layoutToHide = findViewById(R.id.layout_hide);
            layoutToHide.setOnClickListener(this);
        }
        int colorCodeDark = getResources().getColor(R.color.yellow);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorCodeDark));
        }
        setClickable(false);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                autoStop = isChecked;
                if(isChecked){
                    handler.removeCallbacks(runnable);
                            /*if(videoPartCounter==0 && firstPlay){
                                handler.postDelayed(runnable,videoStops[1]-player.getCurrentPosition());
                            }else */
                    if(videoPartCounter!=40) {
                        handler.postDelayed(runnable, videoStops[videoPartCounter] - player.getCurrentPosition());
                    }
                }
            }
        });
        if(!firstPlay){
            bigPlay.setVisibility(View.GONE);
        }
        if(isLandScape){
        }
    }


    private void playOrPause() {
        if (!nowPlaying) {
            player.setPlayWhenReady(false);
        } else {
            player.setPlayWhenReady(true);
        }
        playerView.setPlayer(player);
    }

    private void startVideo() {
        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());
        playerView.setPlayer(player);

        final RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.vid_ikea));
        try {
            rawResourceDataSource.open(dataSpec);

            DataSource.Factory factory = new DataSource.Factory() {
                @Override
                public DataSource createDataSource() {
                    return rawResourceDataSource;
                }
            };
            MediaSource videoSource = new ExtractorMediaSource.Factory(factory).createMediaSource(rawResourceDataSource.getUri());
            player.prepare(videoSource);

        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }
        playerView.setPlayer(player);
        playerView.hideController();
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                if(visibility == 0){
                    playerView.hideController();
                }
            }
        });

        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState){
                    case Player.STATE_IDLE:
                        break;
                    case Player.STATE_BUFFERING:
                        break;
                    case Player.STATE_READY:
                        progressBar.setVisibility(View.INVISIBLE);
                        isLoadingVid = false;
                        setClickable(true);

                        if(playWhenReady){
                            handler.removeCallbacks(runnable);
                            /*if(videoPartCounter==0 && firstPlay){
                                handler.postDelayed(runnable,videoStops[1]-player.getCurrentPosition());
                            }else */
                            if(videoPartCounter!=41) {
                                handler.postDelayed(runnable, videoStops[videoPartCounter] - player.getCurrentPosition());
                            }
                            }
                        break;
                    case Player.STATE_ENDED:
                        startLastScreen();
                        break;

                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                    if(reason == 1){
                        lastPosition = player.getCurrentPosition();
                    }
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            @Override
            public void onSeekProcessed() {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.big_play:
            case R.id.play_pause_view_large:
                pressedPlay();
                playOrPause();
                break;
            case R.id.next_btn:
                if(!isLoadingVid){
                    skipVideo(true);
                }
                break;
            case R.id.back_btn:
                if(!isLoadingVid){
                    skipVideo(false);
                }
                break;
            case R.id.skip_btn:
                startLastScreen();
                break;
            case R.id.layout_hide:
                if(isLandScape){
                    if(!isLayoutInvis){
                        AnimatorSet fadeOut = new AnimUtility.AnimUtilsBuilder().animateViewToFadeOut(btnsLayout, GENERAL_DURATION).build();
                        new AnimUtility.AnimUtilsBuilder().bindSets(fadeOut).start();
                        isLayoutInvis = true;
                    }else{
                        AnimatorSet fadeIn = new AnimUtility.AnimUtilsBuilder().animateViewToFadeIn(btnsLayout, GENERAL_DURATION).build();
                        new AnimUtility.AnimUtilsBuilder().bindSets(fadeIn).start();
                        isLayoutInvis = false;
                    }
                }
                break;
        }
    }

    private void pressedPlay() {
        if (firstPlay) {
            AnimatorSet fadeOut = new AnimUtility.AnimUtilsBuilder().animateViewToFadeOut(bigPlay, GENERAL_DURATION).build();
            new AnimUtility.AnimUtilsBuilder().bindSets(fadeOut).start();
            setClickable(true);
        }
        nowPlaying = !nowPlaying;
        btnPlay.toggle();
    }

    private void setClickable(boolean isClickable) {
        btnNext.setClickable(isClickable);
        btnPre.setClickable(isClickable);
        btnPlay.setClickable(isClickable);
    }

    private void skipVideo(boolean isNext) {
        isLoadingVid = true;
        setClickable(false);
        progressBar.setVisibility(View.VISIBLE);
        long currentPosition = player.getCurrentPosition();
        long seekTo = 0;
        for (int i = videoStops.length -1; i >=0 ; i--) {

            if (currentPosition +10 > videoStops[i]) {
                if (!isNext) {
                    if(i!=0){
                        seekTo = videoStops[i-1];
                        videoPartCounter--;
                    }else{
                        seekTo = 10;
                    }
                } else {
                    if(i!=videoStops.length-1){
                        seekTo = videoStops[i + 1];
                        videoPartCounter++;
                    }else{
                        startLastScreen();
                    }
                }
                player.seekTo(seekTo);
                playerView.setPlayer(player);
                i = 0;

            }

        }
    }

    private void startLastScreen() {
        Intent intent = new Intent(this, LastScreen.class);
        startActivity(intent);
        finish();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (player == null) {
            startVideo();
        }
    }
    private void releasePlayer() {
        handler.removeCallbacks(runnable);
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }
}
