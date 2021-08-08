package com.yuckyh.eldritchmusic.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.UserRegistry;
import com.yuckyh.eldritchmusic.utils.ColorUtil;
import com.yuckyh.eldritchmusic.utils.ImageUtil;
import com.yuckyh.eldritchmusic.utils.MusicPlayer;
import com.yuckyh.eldritchmusic.utils.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

public class SongPlayerActivity extends AppCompatActivity {
    private static final String TAG = SongPlayerActivity.class.getSimpleName();
    private ImageView mImgViewAlbumArt, mImgViewSongPlayerBg;
    private ImageButton mImgBtnPlayPause, mImgBtnLoop, mImgBtnShuffle, mImgBtnPrev, mImgBtnNext;
    private SeekBar mSeekBarSong;
    private TextView mTxtViewSongTitle, mTxtViewArtiste, mTxtViewPlayPosition, mTxtViewPlayDuration;
    private final MusicPlayer mMusicPlayer = new MusicPlayer();
    private Menu mMenu;
    private FloatingActionButton mFabAddToPlaylist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_player);

        init();
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
                mTxtViewArtiste.setText(song.appGetAlbum().appGetArtiste().getName());
                mTxtViewPlayPosition.setText(Duration.minutesToTimer(position));
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
                setTitle("Now Playing");
                tintImageButton(mImgBtnPlayPause, true);
            }

            @Override
            public void onSongPause() {
                mImgBtnPlayPause.setActivated(false);
                setTitle("Paused");
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

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            mFabAddToPlaylist.setEnabled(false);
        }

        mFabAddToPlaylist.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddToPlaylistActivity.class).putExtra("id", mMusicPlayer.getCurrentSong().getId());
            startActivity(intent);
        });

        mImgBtnPlayPause.setOnClickListener(v -> mMusicPlayer.togglePlayPause());
        mImgBtnLoop.setOnClickListener(v -> mMusicPlayer.toggleLoop());
        mImgBtnShuffle.setOnClickListener(v -> mMusicPlayer.toggleShuffle());
        mImgBtnPrev.setOnClickListener(v -> mMusicPlayer.prev(this));
        mImgBtnNext.setOnClickListener(v -> mMusicPlayer.next(this));
        actionBar.setHomeAsUpIndicator(R.drawable.ic_round_expand_more_24);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.song_player_menu, menu);
        mMenu = menu;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            mMenu.findItem(R.id.menu_song_add_favourites).setVisible(false);
            mMenu.findItem(R.id.menu_song_remove_from_favourites).setEnabled(false);
            return false;
        }
        Song currentSong = mMusicPlayer.getCurrentSong();
        try {
            User user = UserRegistry.getInstance().itemFromId(currentUser.getUid());
            ArrayList<Song> favourites = new ArrayList<>(new HashSet<>(user.appGetFavourites()));

            toggleFavouritesVisibility(favourites.contains(currentSong));
        } catch (Exception e) {
            Log.e(TAG, "onOptionsItemSelected: ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.menu_song_go_album) {
            startActivity(new Intent(this, AlbumActivity.class)
                    .putExtra("id", mMusicPlayer.getCurrentSong().appGetAlbum().getId()));
            return true;
        }

        if (item.getItemId() == R.id.menu_song_go_artiste) {
            startActivity(new Intent(this, ArtisteActivity.class)
                    .putExtra("id", mMusicPlayer.getCurrentSong().appGetAlbum().appGetArtiste().getId()));
            return true;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            mMenu.findItem(R.id.menu_song_add_favourites).setEnabled(false);
            mMenu.findItem(R.id.menu_song_remove_from_favourites).setEnabled(false);
            return false;
        }

        Song currentSong = mMusicPlayer.getCurrentSong();
        try {
            User user = UserRegistry.getInstance().itemFromId(currentUser.getUid());
            ArrayList<Song> favourites = new ArrayList<>(new HashSet<>(user.appGetFavourites()));

            if (favourites.contains(currentSong) && item.getItemId() == R.id.menu_song_remove_from_favourites) {
                toggleFavouritesVisibility(true);
                favourites.remove(currentSong);
            } else if (item.getItemId() == R.id.menu_song_add_favourites) {
                toggleFavouritesVisibility(false);
                favourites.add(currentSong);
            }

            user.setFavourites(favourites);
            UserRegistry.getInstance().writeToDb();
            invalidateOptionsMenu();
            return true;
        } catch (Exception e) {
            Log.e(TAG, "onOptionsItemSelected: ", e);
        }

        return super.onOptionsItemSelected(item);
    }

    private void toggleFavouritesVisibility(boolean isAdded) {
        mMenu.findItem(R.id.menu_song_add_favourites).setEnabled(!isAdded);
        mMenu.findItem(R.id.menu_song_remove_from_favourites).setEnabled(isAdded);
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
        mImgBtnPrev = findViewById(R.id.imgBtnPrev);
        mImgBtnNext = findViewById(R.id.imgBtnNext);
        mFabAddToPlaylist = findViewById(R.id.fabAddToPlaylist);
        mSeekBarSong = findViewById(R.id.seekBarSong);
        mTxtViewSongTitle = findViewById(R.id.txtViewSongTitle);
        mTxtViewArtiste = findViewById(R.id.txtViewArtiste);
        mTxtViewPlayPosition = findViewById(R.id.txtViewPlayPosition);
        mTxtViewPlayDuration = findViewById(R.id.txtViewPlayDuration);
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

    private void tintImageButton(ImageButton imageButton, boolean flag) {
        if (flag) {
            imageButton.setImageTintList(ColorStateList.valueOf(ColorUtil.getColorFromAttr(getTheme(), R.attr.colorPrimary)));
        } else {
            imageButton.setImageTintList(ColorStateList.valueOf(ColorUtil.getColorFromAttr(getTheme(), R.attr.colorOnBackground)));
        }
    }

    private void downloadAlbumArt(Song song) {
        ImageUtil util = new ImageUtil(this);
        util.downloadImageBitmap(song.appGetAlbum().getAlbumArtUrl(),
                () -> {
                    mImgViewAlbumArt.setImageBitmap(util.getBitmap());

                    Bitmap blurredBg = util.blur(.4f, 10f);
                    mImgViewSongPlayerBg.setImageBitmap(blurredBg);

                    util.setAlpha(this, mImgViewSongPlayerBg);

                    Log.d(TAG, "onCreate: image downloaded");
                });
    }
}
