package com.yuckyh.eldritchmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.registries.SongRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeSet;

public class MusicPlayer {
    private static final String TAG = MusicPlayer.class.getSimpleName();
    private static final FirebaseStorage STORAGE = FirebaseStorage.getInstance(FirebaseApp.getInstance());
    private static final MusicPlayer INSTANCE = new MusicPlayer();
    private final ArrayList<String> mSongQueue = new ArrayList<>();
    private final SongRegistry mRegistry = SongRegistry.getInstance();
    private final MediaPlayer mPlayer = new MediaPlayer();
    private final Handler mHandler = new Handler();
    private boolean mIsMusicLoaded = false, mIsQueueLooping, mIsLooping, mIsShuffling;
    private int mPosition;
    private Song mCurrentSong;
    private ArrayList<String> mShuffledQueue = new ArrayList<>();
    private Activity mActivity;
    private MusicPlayerListener mListener;
    private SharedPreferences mPreferences;

    public static MusicPlayer getInstance() {
        return INSTANCE;
    }

    public Song getCurrentSong() {
        return mCurrentSong;
    }

    public void init(Activity activity, MusicPlayerListener listener) {
        mActivity = activity;
        mListener = listener;

        mPreferences = activity.getBaseContext().getSharedPreferences("queue", Context.MODE_PRIVATE);
        mSongQueue.addAll(new TreeSet<>(mPreferences.getStringSet("songs", new TreeSet<>())));
        mShuffledQueue.addAll(new TreeSet<>(mPreferences.getStringSet("shuffled", new TreeSet<>())));
        mPosition = mPreferences.getInt("position", -1);
        int songPosition = mPreferences.getInt("songPosition", -1);
        mIsShuffling = mPreferences.getBoolean("isShuffling", false);
        mIsLooping = mPreferences.getBoolean("isLooping", false);
        mIsQueueLooping = mPreferences.getBoolean("isQueueLooping", false);
        boolean isReset = mPreferences.getBoolean("isReset", false);

        Log.d(TAG, "init: " + mIsLooping + mIsQueueLooping);

        if (isReset) {
            mPlayer.seekTo(songPosition);
        }

        mPlayer.setOnSeekCompleteListener(mp -> mListener.onSeek((double) mp.getCurrentPosition() / 60000));

        mPlayer.setOnCompletionListener(mp -> {
            if (!mIsLooping) {
                next();
            } else {
                reload();
                mPlayer.start();
            }
        });

        reload();
    }

    public void togglePlayPause() {
        if(mPlayer.isPlaying() && mIsMusicLoaded) {
            pause();
        } else {
            start();
        }
    }

    public void prev() {
        try {
            if(mPosition == 0 && (mIsQueueLooping || mIsLooping)) {
                mPosition = mSongQueue.size();
            }
            Song prevSong = mRegistry.fromId(getIdByPosition(mPosition - 1));

            mPosition -= 1;
            if (mCurrentSong == prevSong) {
                mPosition -= 1;
                prev();
                return;
            }
            mIsMusicLoaded = false;
            reload();
        } catch (Exception e) {
            Log.e(TAG, "prev: ", e);
        }
    }

    public void next() {
        try {
            if(mPosition == (mSongQueue.size() - 1) && (mIsQueueLooping || mIsLooping)) {
                mPosition = -1;
            }
            Song nextSong = mRegistry.fromId(getIdByPosition(mPosition + 1));

            mPosition += 1;
            if (mCurrentSong == nextSong) {
                mPosition += 1;
                next();
                return;
            }
            mIsMusicLoaded = false;
            reload();
        } catch (Exception e) {
            Log.e(TAG, "next: ", e);
        }
    }

    public void seek(int position) {
        mPlayer.seekTo(position * 100);
    }

    public void toggleLoop() {
        if (mIsQueueLooping) {
            mIsQueueLooping = false;
            mIsLooping = true;
        } else if (mIsLooping) {
            mIsLooping = false;
        } else {
            mIsQueueLooping = true;
        }

        mListener.onToggleLoop(mIsLooping, mIsQueueLooping);
    }

    public void toggleShuffle() {
        if (!mIsShuffling) {
            mShuffledQueue = new ArrayList<>(mSongQueue);
            Collections.shuffle(mShuffledQueue);
            Log.d(TAG, "toggleShuffle: " + mShuffledQueue.toString() + mPosition);
        }

        mIsShuffling = !mIsShuffling;
        mListener.onQueueShuffle(mIsShuffling);
    }

    public void save() {
        mPreferences.edit()
                .putStringSet("songs", new TreeSet<>(mSongQueue))
                .putStringSet("shuffled", new TreeSet<>(mShuffledQueue))
                .putInt("position", mPosition)
                .putInt("songPosition", mPlayer.getCurrentPosition())
                .putBoolean("isLooping", mIsLooping)
                .putBoolean("isQueueLooping", mIsQueueLooping)
                .putBoolean("isShuffling", mIsShuffling)
                .putBoolean("isReset", false)
                .apply();
        Log.d(TAG, "save: " + mShuffledQueue.toString());
    }

    private void pause() {
        mPlayer.pause();
        mListener.onSongPause();
    }

    private void start() {
        mPlayer.start();
        mListener.onSongStart();
    }

    private void reload() {
        Song previousSong = mCurrentSong;
        boolean hasDownloaded = false;

        try {
            mCurrentSong = mRegistry.fromId(getIdByPosition(mPosition));
        } catch (Exception e) {
            Log.e(TAG, "reload: ", e);
        }

        if (!mIsMusicLoaded || previousSong != mCurrentSong) {
            download();
            hasDownloaded = true;
        }

        if(!mPlayer.isPlaying()) {
            mListener.onSongPause();
        } else {
            mListener.onSongStart();
        }

        mListener.onSongReload(mCurrentSong, hasDownloaded ? 0 : mPlayer.getCurrentPosition(), mIsLooping, mIsQueueLooping, mIsShuffling);
    }

    private void download() {
        StorageReference reference = STORAGE.getReference("songs").child(mCurrentSong.getId() + ".mp3");

        mPlayer.reset();

        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            mIsMusicLoaded = true;
            try {
                mPlayer.setDataSource(mActivity.getBaseContext(), uri);
                mPlayer.prepare();
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mPlayer.isPlaying()) {
                        mListener.onSongPlay((double) mPlayer.getCurrentPosition() / 60000, true);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });
        }).addOnFailureListener(e -> Log.e(TAG, "onFailure: " + mCurrentSong.getId(), e));
    }

    private String getIdByPosition(int position) {
        try {
            return mIsShuffling ? mShuffledQueue.get(position) : mSongQueue.get(position);
        } catch (Exception e) {
            Log.d(TAG, "getIdByPosition: " + mSongQueue.toString());
            Log.d(TAG, "getIdByPosition: " + mShuffledQueue.toString());
            Log.e(TAG, "getIdByPosition: ", e);
        }
        return "";
    }

    public interface MusicPlayerListener {
        void onSongPlay(double position, boolean isSeeking);
        void onSongReload(Song song, double position, boolean isLooping, boolean isQueueLooping, boolean isShuffling);
        void onSongStart();
        void onSongPause();
        void onToggleLoop(boolean isLooping, boolean isQueueLooping);
        void onQueueShuffle(boolean isShuffling);
        void onSeek(double position);
    }
}
