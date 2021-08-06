package com.yuckyh.eldritchmusic.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.activities.PlaylistActivity;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.utils.Duration;
import com.yuckyh.eldritchmusic.utils.BitmapUtil;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<Playlist> mPlaylists;
    private final int mLayoutId;
    private final OnItemClickListener mOnItemClickListener;

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists, int layoutId, OnItemClickListener onItemClickListener) {
        mContext = context;
        mPlaylists = playlists;
        mLayoutId = layoutId;
        mOnItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(mLayoutId, parent, false);
        return new PlaylistAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Playlist playlist = mPlaylists.get(position);

        BitmapUtil util = new BitmapUtil();
        util.downloadImageBitmap(playlist.getPlaylistArtUrl(),
                () -> holder.mImgViewPlaylistItemPic.setImageBitmap(util.getBitmap()));

        holder.mTxtViewPlaylistItemName.setText(playlist.getName());
        holder.mTxtViewPlaylistItemDuration.setText(Duration.minutesToHours(playlist.getSongsTotalDuration()));
        holder.mTxtViewPlaylistItemSongCount.setText(String.format("%d songs", playlist.getSongs().size()));

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.onClick(playlist.getId()));

        if (holder.mMcvPlaylistItem == null) {
            return;
        }

        holder.mMcvPlaylistItem.setOnClickListener(v -> mOnItemClickListener.onClick(playlist.getId()));
        holder.itemView.setOnClickListener(v -> {});
    }

    @Override
    public int getItemCount() {
        return mPlaylists.size();
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTxtViewPlaylistItemName, mTxtViewPlaylistItemDuration, mTxtViewPlaylistItemSongCount;
        private final ImageView mImgViewPlaylistItemPic;
        private final MaterialCardView mMcvPlaylistItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtViewPlaylistItemName = itemView.findViewById(R.id.txtViewPlaylistItemName);
            mTxtViewPlaylistItemDuration = itemView.findViewById(R.id.txtViewPlaylistItemDuration);
            mTxtViewPlaylistItemSongCount = itemView.findViewById(R.id.txtViewPlaylistItemSongCount);
            mImgViewPlaylistItemPic = itemView.findViewById(R.id.imgViewPlaylistItemPic);
            mMcvPlaylistItem = itemView.findViewById(R.id.mcvPlaylistItem);
        }
    }

    public interface OnItemClickListener {
        void onClick(String itemId);
    }
}
