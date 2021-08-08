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

import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.activities.AlbumActivity;
import com.yuckyh.eldritchmusic.models.Album;
import com.yuckyh.eldritchmusic.utils.Duration;
import com.yuckyh.eldritchmusic.utils.ImageUtil;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {
    private final Context mContext;
    private final ArrayList<Album> mAlbums;
    private final int mMaxItem;

    public AlbumAdapter(Context context, ArrayList<Album> albums, int maxItem) {
        mContext = context;
        mAlbums = albums;
        mMaxItem = maxItem;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_album_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Album album = mAlbums.get(position);

        ImageUtil util = new ImageUtil(mContext);
        Log.d("AAA", "onBindViewHolder: " + album.getAlbumArtUrl());
        util.downloadImageBitmap(album.getAlbumArtUrl(),
                () -> holder.mImgViewAlbumItemArt.setImageBitmap(util.getBitmap()));

        holder.mTxtViewAlbumItemName.setText(album.getName());
        holder.mTxtViewAlbumItemDuration.setText(Duration.minutesToHours(album.appGetSongsTotalDuration()));
        holder.mTxtViewAlbumItemYear.setText(String.valueOf(album.getYear()));

        holder.itemView.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, AlbumActivity.class)
                .putExtra("id", album.getId())));
    }

    @Override
    public int getItemCount() {
        if (mMaxItem < 0) {
            return mAlbums.size();
        }
        return Math.min(mAlbums.size(), mMaxItem);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTxtViewAlbumItemName;
        private final TextView mTxtViewAlbumItemDuration;
        private final TextView mTxtViewAlbumItemYear;
        private final ImageView mImgViewAlbumItemArt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTxtViewAlbumItemName = itemView.findViewById(R.id.txtViewAlbumItemName);
            mTxtViewAlbumItemDuration = itemView.findViewById(R.id.txtViewAlbumItemDuration);
            mTxtViewAlbumItemYear = itemView.findViewById(R.id.txtViewAlbumItemYear);
            mImgViewAlbumItemArt = itemView.findViewById(R.id.imgViewAlbumItemAlbumArt);
        }
    }
}
