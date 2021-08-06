package com.yuckyh.eldritchmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;
import com.yuckyh.eldritchmusic.utils.BitmapUtil;

import java.util.ArrayList;
import java.util.Random;

public class PlaylistActivity extends AppCompatActivity {
    private static final String TAG = PlaylistActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Playlist playlist;
        try {
            playlist = PlaylistRegistry.getInstance().fromId(getIntent().getStringExtra("id"));

            setTitle(playlist.getName());
            ((TextView)findViewById(R.id.txtViewPlaylistName))
                    .setText(playlist.getName());
            ((TextView)findViewById(R.id.txtViewPlaylistOwner))
                    .setText(playlist.getOwner().getName());

            BitmapUtil bitmapUtil = new BitmapUtil();
            bitmapUtil.downloadImageBitmap(playlist.getPlaylistArtUrl(),
                    () -> ((ImageView)findViewById(R.id.imgViewPlaylistPic))
                            .setImageBitmap(bitmapUtil.getBitmap()));

            SongAdapter songAdapter = new SongAdapter(this, playlist.getSongs(), R.layout.item_song_card);
            ((RecyclerView)findViewById(R.id.rvPlaylistSongs)).setAdapter(songAdapter);

            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(0, false, true));

            int randomPosition = new Random().nextInt(playlist.getSongIds().size());
            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(randomPosition, true, true));

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (playlist.getId().equals(userId)) {

                } else {
                    User user = UserRegistry.getInstance().fromId(userId);
                    FloatingActionButton fabFollowPlaylist = findViewById(R.id.fabFollowPlaylist);
                    if (user == null) {
                        fabFollowPlaylist.setVisibility(View.GONE);
                        return;
                    }
                    ArrayList<Playlist> playlists = user.getFollowedPlaylists();

                    if (playlists == null) {
                        return;
                    }

                    fabFollowPlaylist.setActivated(playlists.contains(playlist));
                    fabFollowPlaylist.setOnClickListener(v -> {
                        if (playlists.contains(playlist)) {
                            playlists.remove(playlist);
                        } else {
                            playlists.add(playlist);
                        }

                        v.setActivated(playlists.contains(playlist));
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
            finish();
        }
    }
}
