package com.yuckyh.eldritchmusic.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.utils.ColorUtil;
import com.yuckyh.eldritchmusic.utils.Duration;
import com.yuckyh.eldritchmusic.utils.ImageUtil;
import com.yuckyh.eldritchmusic.utils.MusicPlayer;

public class MiniPlayerFragment extends Fragment {
    private final MusicPlayer mMusicPlayer = MusicPlayer.getInstance();
    private SeekBar mSeekBarSong;
    private ImageView mImgViewAlbumArt;
    private ImageButton mImgBtnPlayPauseMini, mImgBtnLoopMini, mImgBtnShuffleMini, mImgBtnPrevMini, mImgBtnNextMini;
    private TextView mTxtViewSongTitle, mTxtViewArtiste, mTxtViewPlayPosition, mTxtViewPlayDuration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mini_player, container, false);
        mSeekBarSong = view.findViewById(R.id.seekBarSong);
        mImgBtnPlayPauseMini = view.findViewById(R.id.imgBtnPlayPauseMini);
        mImgBtnLoopMini = view.findViewById(R.id.imgBtnLoopMini);
        mImgBtnShuffleMini = view.findViewById(R.id.imgBtnShuffleMini);
        mImgBtnPrevMini = view.findViewById(R.id.imgBtnPrevMini);
        mImgBtnNextMini = view.findViewById(R.id.imgBtnNextMini);
        mSeekBarSong = view.findViewById(R.id.seekBarSong);
        mTxtViewSongTitle = view.findViewById(R.id.txtViewSongTitle);
        mTxtViewArtiste = view.findViewById(R.id.txtViewArtiste);
        mTxtViewPlayPosition = view.findViewById(R.id.txtViewPlayPosition);
        mTxtViewPlayDuration = view.findViewById(R.id.txtViewPlayDuration);
        mImgViewAlbumArt = view.findViewById(R.id.imgViewAlbumArt);

        mMusicPlayer.init(requireActivity(), new MusicPlayer.MusicPlayerListener() {
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
                tintImageButton(mImgBtnShuffleMini, isShuffling);
                ImageUtil util = new ImageUtil(view.getContext());
                util.downloadImageBitmap(song.appGetAlbum().getAlbumArtUrl(),
                        () -> mImgViewAlbumArt.setImageBitmap(util.getBitmap()));
            }

            @Override
            public void onSongStart() {
                mImgBtnPlayPauseMini.setActivated(true);
                tintImageButton(mImgBtnPlayPauseMini, true);
            }

            @Override
            public void onSongPause() {
                mImgBtnPlayPauseMini.setActivated(false);
                tintImageButton(mImgBtnPlayPauseMini, false);
            }

            @Override
            public void onToggleLoop(boolean isLooping, boolean isQueueLooping) {
                renderLoopButton(isLooping, isQueueLooping);
            }

            @Override
            public void onQueueShuffle(boolean isShuffling) {
                tintImageButton(mImgBtnShuffleMini, isShuffling);
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

        mImgBtnPlayPauseMini.setOnClickListener(v -> mMusicPlayer.togglePlayPause());
        mImgBtnLoopMini.setOnClickListener(v -> mMusicPlayer.toggleLoop());
        mImgBtnShuffleMini.setOnClickListener(v -> mMusicPlayer.toggleShuffle());
        mImgBtnPrevMini.setOnClickListener(v -> mMusicPlayer.prev(requireActivity()));
        mImgBtnNextMini.setOnClickListener(v -> mMusicPlayer.next(requireActivity()));

        return view;
    }

    private void renderLoopButton(boolean isLooping, boolean isQueueLooping) {
        int drawableId;
        if (isLooping) {
            drawableId = R.drawable.ic_round_repeat_one_24;
        } else {
            drawableId = R.drawable.ic_round_repeat_24;
        }

        tintImageButton(mImgBtnLoopMini, isLooping || isQueueLooping);

        mImgBtnLoopMini.setImageDrawable(ResourcesCompat.getDrawable(getContext().getResources(), drawableId, getContext().getTheme()));
    }

    private void tintImageButton(ImageButton imageButton, boolean flag) {
        if (flag) {
            imageButton.setImageTintList(ColorStateList.valueOf(ColorUtil.getColorFromAttr(getContext().getTheme(), R.attr.colorPrimary)));
        } else {
            imageButton.setImageTintList(ColorStateList.valueOf(ColorUtil.getColorFromAttr(getContext().getTheme(), R.attr.colorOnBackground)));
        }
    }
}
