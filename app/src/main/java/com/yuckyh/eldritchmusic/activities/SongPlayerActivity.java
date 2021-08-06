package com.yuckyh.eldritchmusic.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.DragEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.res.ResourcesCompat;
import androidx.palette.graphics.Palette;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.utils.ColorUtil;
import com.yuckyh.eldritchmusic.utils.BitmapUtil;
import com.yuckyh.eldritchmusic.utils.MusicPlayer;
import com.yuckyh.eldritchmusic.utils.Duration;

import java.util.Objects;

public class SongPlayerActivity extends AppCompatActivity {
    private static final String TAG = SongPlayerActivity.class.getSimpleName();
    private ImageView mImgViewAlbumArt, mImgViewSongPlayerBg;
    private ImageButton mImgBtnPlayPause, mImgBtnLoop, mImgBtnShuffle;
    private SeekBar mSeekBarSong;
    private TextView mTxtViewSongTitle, mTxtViewArtiste, mTxtViewPlayPosition, mTxtViewPlayDuration;
    private MusicPlayer mMusicPlayer;
    private FloatingActionButton mFabAddToPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);

        init();
        ImageButton imgBtnPrev = findViewById(R.id.imgBtnPrev);
        ImageButton imgBtnNext = findViewById(R.id.imgBtnNext);
        ActionBar actionBar = Objects.requireNonNull(getSupportActionBar());

        mMusicPlayer.init(this, new MusicPlayer.MusicPlayerListener() {
            @Override
            public void onSongPlay(double position, boolean isSeeking) {
                if (isSeeking) {
                    mSeekBarSong.setProgress((int) (position * 600));
                    mTxtViewPlayPosition.setText(Duration.minutesToTimer(position));
                }
            }

            @Override
            public void onSongReload(Song song, double position, boolean isLooping, boolean isQueueLooping, boolean isShuffling) {
                mTxtViewSongTitle.setText(song.getName());
                mTxtViewArtiste.setText(song.getAlbum().getArtiste().getName());
                mTxtViewPlayPosition.setText("0:00");
                mTxtViewPlayDuration.setText(Duration.minutesToTimer(song.getDuration()));
                mSeekBarSong.setMax((int) (song.getDuration() * 600));
                mSeekBarSong.setProgress((int) (position * 600));
                renderLoopButton(isLooping, isQueueLooping);
                tintImageButton(mImgBtnShuffle, isShuffling);
                downloadAlbumArt(song);
            }

            @Override
            public void onSongStart() {
                mImgBtnPlayPause.setActivated(true);
                setTitle("Now Playing: " + mMusicPlayer.getCurrentSong().getName());
                tintImageButton(mImgBtnPlayPause, true);
            }

            @Override
            public void onSongPause() {
                mImgBtnPlayPause.setActivated(false);
                setTitle("Paused: " + mMusicPlayer.getCurrentSong().getName());
                tintImageButton(mImgBtnPlayPause, false);
            }

            @Override
            public void onToggleLoop(boolean isLooping, boolean isQueueLooping) {
                renderLoopButton(isLooping, isQueueLooping);
            }

            @Override
            public void onQueueShuffle(boolean isShuffling) {
                tintImageButton(mImgBtnShuffle, isShuffling);
                if (isShuffling) {
                    Snackbar.make(mImgViewAlbumArt.getRootView(), "Queue Shuffled", Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onSeek(double position) {
                mSeekBarSong.setProgress((int) (position * 600));
                mTxtViewPlayPosition.setText(Duration.minutesToTimer(position));
            }
        });

        mSeekBarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mTxtViewPlayPosition.setText(Duration.minutesToTimer((double) progress / 600));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMusicPlayer.togglePlayPause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMusicPlayer.seek(seekBar.getProgress());
                mMusicPlayer.togglePlayPause();
            }
        });

        mFabAddToPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddToPlaylistActivity.class).putExtra("id", mMusicPlayer.getCurrentSong().getId());
            startActivity(intent);
        });

        mImgBtnPlayPause.setOnClickListener(v -> mMusicPlayer.togglePlayPause());
        mImgBtnLoop.setOnClickListener(v -> mMusicPlayer.toggleLoop());
        mImgBtnShuffle.setOnClickListener(v -> mMusicPlayer.toggleShuffle());
        imgBtnPrev.setOnClickListener(v -> mMusicPlayer.prev());
        imgBtnNext.setOnClickListener(v -> mMusicPlayer.next());
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_expand_more_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.nothing, R.anim.activity_slide_down);
        mMusicPlayer.save();
    }

    private void init() {
        mImgViewAlbumArt = findViewById(R.id.imgViewAlbumArt);
        mImgViewSongPlayerBg = findViewById(R.id.imgViewSongPlayerBg);
        mImgBtnPlayPause = findViewById(R.id.imgBtnPlayPause);
        mImgBtnLoop = findViewById(R.id.imgBtnLoop);
        mImgBtnShuffle = findViewById(R.id.imgBtnShuffle);
        mFabAddToPlaylist = findViewById(R.id.fabAddToPlaylist);
        mSeekBarSong = findViewById(R.id.seekBarSong);
        mTxtViewSongTitle = findViewById(R.id.txtViewSongTitle);
        mTxtViewArtiste = findViewById(R.id.txtViewArtiste);
        mTxtViewPlayPosition = findViewById(R.id.txtViewPlayPosition);
        mTxtViewPlayDuration = findViewById(R.id.txtViewPlayDuration);
        mMusicPlayer = MusicPlayer.getInstance();
    }

    private void tintImageButton(ImageButton imageButton, boolean flag) {
        if (flag) {
            imageButton.setImageTintList(ColorStateList.valueOf(ColorUtil.getColorFromAttr(getTheme(), R.attr.colorPrimary)));
        } else {
            imageButton.setImageTintList(ColorStateList.valueOf(ColorUtil.getColorFromAttr(getTheme(), R.attr.colorOnBackground)));
        }
    }

    private void renderLoopButton(boolean isLooping, boolean isQueueLooping) {
        int drawableId;
        if (isLooping) {
            drawableId = R.drawable.ic_round_repeat_one_24;
        } else {
            drawableId = R.drawable.ic_round_repeat_24;
        }

        tintImageButton(mImgBtnLoop, isLooping || isQueueLooping);

        mImgBtnLoop.setImageDrawable(ResourcesCompat.getDrawable(getResources(), drawableId, getTheme()));
    }

    private void downloadAlbumArt(Song song) {
        BitmapUtil util = new BitmapUtil();
        util.downloadImageBitmap(song.getAlbum().getAlbumArtUrl(),
                () -> {
                    Bitmap bitmap = util.getBitmap();
                    mImgViewAlbumArt.setImageBitmap(bitmap);

                    Bitmap blurredBg = BitmapUtil.blur(getBaseContext(), bitmap, .4f, 10f);
                    mImgViewSongPlayerBg.setImageBitmap(blurredBg);

                    Palette palette = Palette.from(bitmap).generate();
                    @ColorInt int color;

                    int nightModeFlags = getResources().getConfiguration().uiMode &
                            Configuration.UI_MODE_NIGHT_MASK;

                    if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
                        color = palette.getLightVibrantColor(ColorUtil.getColorFromAttr(getTheme(), Color.WHITE));

                        double luminance = ColorUtil.getLuminance(color);
                        mImgViewSongPlayerBg.setAlpha((float) (luminance));
                    } else {
                        color = palette.getDarkVibrantColor(ColorUtil.getColorFromAttr(getTheme(), Color.WHITE));

                        double luminance = ColorUtil.getLuminance(color);
                        mImgViewSongPlayerBg.setAlpha((float) (1 - luminance));
                    }

                    Log.d(TAG, "onCreate: image downloaded");
                });
    }
}
