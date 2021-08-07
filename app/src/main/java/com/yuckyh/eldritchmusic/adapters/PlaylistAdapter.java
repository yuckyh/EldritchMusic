package com.yuckyh.eldritchmusic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.utils.Duration;
import com.yuckyh.eldritchmusic.utils.ImageUtil;

import java.util.ArrayList;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<Playlist> mPlaylists;
    private final int mLayoutId, mMaxItem;
    private final OnItemClickListener mOnItemClickListener;

    public PlaylistAdapter(Context context, ArrayList<Playlist> playlists, int layoutId, int maxItem, OnItemClickListener onItemClickListener) {
        mContext = context;
        mPlaylists = playlists;
        mLayoutId = layoutId;
        mMaxItem = maxItem;
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

        ImageUtil util = new ImageUtil(mContext);
        util.downloadImageBitmap(playlist.getPlaylistArtUrl(),
                () -> holder.mImgViewPlaylistItemPic.setImageBitmap(util.getBitmap()));

        holder.mTxtViewPlaylistItemName.setText(playlist.getName());
        if (holder.mTxtViewPlaylistItemDuration != null) {
            holder.mTxtViewPlaylistItemDuration.setText(Duration.minutesToHours(playlist.appGetSongsTotalDuration()));
        }

        if (holder.mTxtViewPlaylistItemSongCount != null) {
            holder.mTxtViewPlaylistItemSongCount.setText(String.format("%d songs", playlist.appGetSongs().size()));
        }

        holder.itemView.setOnClickListener(v -> mOnItemClickListener.onClick(playlist.getId()));
    }

    @Override
    public int getItemCount() {
        if (mMaxItem < 0) {
            return mPlaylists.size();
        }
        return Math.min(mPlaylists.size(), mMaxItem);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTxtViewPlaylistItemName, mTxtViewPlaylistItemDuration, mTxtViewPlaylistItemSongCount;
        private final ImageView mImgViewPlaylistItemPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtViewPlaylistItemName = itemView.findViewById(R.id.txtViewPlaylistItemName);
            mTxtViewPlaylistItemDuration = itemView.findViewById(R.id.txtViewPlaylistItemDuration);
            mTxtViewPlaylistItemSongCount = itemView.findViewById(R.id.txtViewPlaylistItemSongCount);
            mImgViewPlaylistItemPic = itemView.findViewById(R.id.imgViewPlaylistItemPic);
        }
    }

    public interface OnItemClickListener {
        void onClick(String itemId);
    }
}
