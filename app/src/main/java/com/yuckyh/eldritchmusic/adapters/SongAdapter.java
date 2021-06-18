package com.yuckyh.eldritchmusic.adapters;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.activities.HomeActivity;
import com.yuckyh.eldritchmusic.activities.SongPlayerActivity;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.utils.ImageViewUtils;
import com.yuckyh.eldritchmusic.utils.SongDuration;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private static final String TAG = SongAdapter.class.getSimpleName();
    private final Context mContext;
    private final ArrayList<Song> songs;

    public SongAdapter(Context context, ArrayList<Song> songs) {
        super();
        mContext = context;
        this.songs = songs;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.mContext).inflate(R.layout.song_item, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SongAdapter.SongViewHolder holder, int position) {
        Song song = this.songs.get(position);
        ConstraintLayout layout = holder.layout;

        ImageView imgViewSongItemAlbumArt = layout.findViewById(R.id.imgViewSongItemAlbumArt);
        ImageViewUtils.downloadImageBitmap(song.getAlbum().getAlbumArtPath(),
                () -> imgViewSongItemAlbumArt.setImageBitmap(ImageViewUtils.bitmap));

        TextView txtViewSongItemTitle = layout.findViewById(R.id.txtViewSongItemTitle);
        txtViewSongItemTitle.setText(song.getName());

        TextView txtViewSongItemArtiste = layout.findViewById(R.id.txtViewSongItemArtiste);
        txtViewSongItemArtiste.setText(song.getAlbum().getArtiste().getName());

        TextView txtViewSongItemDuration = layout.findViewById(R.id.txtViewSongItemDuration);
        txtViewSongItemDuration.setText(SongDuration.parse(song.getDuration()));

        holder.itemView.setOnClickListener(v -> {
            if (mContext.getClass() == HomeActivity.class) {
                mContext.startActivity(new Intent(mContext, SongPlayerActivity.class).putExtra("id", song.getId()));
                ((HomeActivity) mContext).overridePendingTransition(R.anim.activity_slide_up, R.anim.nothing);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.songs.size();
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout layout;

        public SongViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.clSongItem);
        }
    }
}
