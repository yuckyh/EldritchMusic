package com.yuckyh.eldritchmusic.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.activities.SongPlayerActivity;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.utils.ImageUtil;
import com.yuckyh.eldritchmusic.utils.Duration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<Song> mSongs;
    private final int mLayoutId, mMaxItem;

    public SongAdapter(Context context, ArrayList<Song> songs, int layoutId, int maxItem) {
        mContext = context;
        mSongs = songs;
        mLayoutId = layoutId;
        mMaxItem = maxItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SongAdapter.ViewHolder holder, int position) {
        Song song = mSongs.get(position);

        holder.mTxtViewSongItemTitle.setText(song.getName());
        holder.mTxtViewSongItemDuration.setText(Duration.minutesToTimer(song.getDuration()));
        holder.mTxtViewSongItemArtiste.setText(song.appGetAlbum().appGetArtiste().getName());
        ImageUtil util = new ImageUtil(mContext);
        util.downloadImageBitmap(song.appGetAlbum().getAlbumArtUrl(),
                () -> holder.mImgViewSongItemAlbumArt.setImageBitmap(util.getBitmap()));

        holder.itemView.setOnClickListener(v -> openSongPlayer(position, false, false));
    }

    public void openSongPlayer(int position, boolean isShuffling, boolean isReset) {
        if (mContext instanceof Activity) {
            SortedSet<String> songIds = new TreeSet<>();
            for (Song temp : mSongs) {
                songIds.add(temp.getId());
            }

            SharedPreferences sharedPreferences = mContext.getSharedPreferences("queue", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet("songs", songIds)
                    .putInt("position", position)
                    .putBoolean("isShuffling", isShuffling)
                    .putBoolean("isReset", isReset);
            if (isReset) {
                editor.putInt("songPosition", 0);
            }
            editor.apply();

            Intent songPlayerIntent = new Intent(mContext, SongPlayerActivity.class);
            songPlayerIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            mContext.startActivity(songPlayerIntent);
            ((Activity) mContext).overridePendingTransition(R.anim.activity_slide_up, R.anim.nothing);
        }
    }

    @Override
    public int getItemCount() {
        if (mMaxItem < 0) {
            return mSongs.size();
        }
        return Math.min(mSongs.size(), mMaxItem);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTxtViewSongItemTitle, mTxtViewSongItemDuration, mTxtViewSongItemArtiste;
        private final ImageView mImgViewSongItemAlbumArt;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mImgViewSongItemAlbumArt = itemView.findViewById(R.id.imgViewSongItemAlbumArt);
            mTxtViewSongItemTitle = itemView.findViewById(R.id.txtViewSongItemTitle);
            mTxtViewSongItemArtiste = itemView.findViewById(R.id.txtViewSongItemArtiste);
            mTxtViewSongItemDuration = itemView.findViewById(R.id.txtViewSongItemDuration);
        }
    }
}
