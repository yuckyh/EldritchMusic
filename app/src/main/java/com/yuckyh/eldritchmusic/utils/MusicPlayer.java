package com.yuckyh.eldritchmusic.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.yuckyh.eldritchmusic.activities.HomeActivity;
import com.yuckyh.eldritchmusic.activities.SongPlayerActivity;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.SongRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class MusicPlayer {
    private static final String TAG = MusicPlayer.class.getSimpleName();
    private static final FirebaseStorage STORAGE = FirebaseStorage.getInstance(FirebaseApp.getInstance());
    private static final MusicPlayer INSTANCE = new MusicPlayer();
    private static final MediaPlayer mPlayer = new MediaPlayer();
    private static ArrayList<String> mSongQueue, mShuffledQueue;
    private static final Handler mHandler = new Handler();
    private static boolean mIsMusicLoaded = false, mIsQueueLooping, mIsLooping, mIsShuffling;
    private static int mPosition, mSongPosition;
    private static Song mCurrentSong;
    private MusicPlayerListener mListener;
    private static SharedPreferences mPreferences;

    public static MusicPlayer getInstance() {
        return INSTANCE;
    }

    public Song getCurrentSong() {
        return mCurrentSong;
    }

    public void init(Activity activity, MusicPlayerListener listener) {
        mListener = listener;

        mPreferences = activity.getSharedPreferences("queue", Context.MODE_PRIVATE);

        mSongQueue = mShuffledQueue = new ArrayList<>();
        mSongQueue.addAll(new TreeSet<>(mPreferences.getStringSet("songs", new TreeSet<>())));
        mShuffledQueue.addAll(new TreeSet<>(mPreferences.getStringSet("shuffled", new TreeSet<>())));
        mPosition = mPreferences.getInt("position", -1);
        mSongPosition = mPreferences.getInt("songPosition", -1);
        mIsShuffling = mPreferences.getBoolean("isShuffling", false);
        mIsLooping = mPreferences.getBoolean("isLooping", false);
        mIsQueueLooping = mPreferences.getBoolean("isQueueLooping", false);
        boolean isReset = mPreferences.getBoolean("isReset", false);

        Log.d(TAG, "init: " + mIsLooping + mIsQueueLooping);

        if (isReset) {
            mPlayer.seekTo(mSongPosition);
        }

        mPlayer.setOnSeekCompleteListener(mp -> mListener.onSeek((double) mp.getCurrentPosition() / 60000));

        mPlayer.setOnCompletionListener(mp -> {
            if (!mIsLooping) {
                next(activity);
            } else {
                reload(activity, true);
                mPlayer.start();
            }
        });

        reload(activity, activity instanceof SongPlayerActivity);
    }

    public void togglePlayPause() {
        if(mPlayer.isPlaying() && mIsMusicLoaded) {
            pause();
        } else {
            start();
        }
    }

    public void prev(Activity activity) {
        try {
            if (mPosition == 0 && (mIsQueueLooping || mIsLooping)) {
                mPosition = mSongQueue.size();
            }
            Song prevSong = SongRegistry.getInstance().itemFromId(getIdByPosition(mPosition - 1));

            mPosition -= 1;
            if (mCurrentSong == prevSong) {
                mPosition -= 1;
                prev(activity);
                return;
            }
            mIsMusicLoaded = false;
            reload(activity, true);
        } catch (Exception e) {
            Log.e(TAG, "prev: ", e);
        }
    }

    public void next(Activity activity) {
        try {
            if (mPosition == (mSongQueue.size() - 1) && (mIsQueueLooping || mIsLooping)) {
                mPosition = -1;
            }
            Song nextSong = SongRegistry.getInstance().itemFromId(getIdByPosition(mPosition + 1));

            mPosition += 1;
            if (mCurrentSong == nextSong) {
                mPosition += 1;
                next(activity);
                return;
            }
            mIsMusicLoaded = false;
            reload(activity, true);
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

    private void reload(Activity activity, boolean autoStart) {
        Song previousSong = mCurrentSong;
        boolean hasDownloaded = false;

        try {
            mCurrentSong = SongRegistry.getInstance().itemFromId(getIdByPosition(mPosition));
        } catch (Exception e) {
            Log.e(TAG, "reload: ", e);
            return;
        }

        if (!mIsMusicLoaded || previousSong != mCurrentSong) {
            download(activity, autoStart);
            hasDownloaded = true;
        }

        if (!mPlayer.isPlaying()) {
            mListener.onSongPause();
        } else {
            mListener.onSongStart();
        }

        mListener.onSongReload(mCurrentSong, hasDownloaded ? 0 : (double) mPlayer.getCurrentPosition() / 60000, mIsLooping, mIsQueueLooping, mIsShuffling);
    }

    private void download(Activity activity, boolean autoStart) {
        if (mCurrentSong == null) {
            return;
        }

        StorageReference reference = STORAGE.getReference("songs").child(mCurrentSong.getId() + ".mp3");

        mPlayer.reset();

        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            mIsMusicLoaded = true;
            try {
                mPlayer.setDataSource(activity, uri);
                mPlayer.prepare();
                if (autoStart) {
                    start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mPlayer.isPlaying()) {
                        mListener.onSongPlay((double) mPlayer.getCurrentPosition() / 60000, true);
                    }
                    mHandler.postDelayed(this, 100);
                }
            });

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                return;
            }
            try {
                User user = UserRegistry.getInstance().itemFromId(currentUser.getUid());
                ArrayList<Song> recents = user.appGetRecentlyPlayedSongs();
                for (Song song : recents) Log.d(TAG, "download: " + song.getId());
                Log.d(TAG, "download: before" + mCurrentSong.getId() + " " + recents.get(0).getId());
                if (!recents.contains(mCurrentSong)) {
                    Collections.reverse(recents);
                    if (recents.size() == 20) {
                        recents.remove(0);
                    }
                    recents.add(mCurrentSong);
                    Collections.reverse(recents);
                }
                user.setRecentlyPlayedSongs(recents);
                for (Song song : recents) Log.d(TAG, "download: " + song.getId());
                Log.d(TAG, "download: after" + mCurrentSong.getId() + " " + recents.get(0).getId());
                UserRegistry.getInstance().writeToDb();
            } catch (Exception e) {
                Log.e(TAG, "onCreate: ", e);
            }
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
