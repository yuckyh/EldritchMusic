package com.yuckyh.eldritchmusic.activities;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;
import com.yuckyh.eldritchmusic.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class PlaylistActivity extends AppCompatActivity {
    private static final String TAG = PlaylistActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Playlist playlist;
        try {
            playlist = PlaylistRegistry.getInstance().itemFromId(getIntent().getStringExtra("id"));

            ((TextView) findViewById(R.id.txtViewPlaylistName))
                    .setText(playlist.getName());
            ((TextView) findViewById(R.id.txtViewPlaylistOwner))
                    .setText(playlist.appGetOwner().getName());

            ImageView imgPlaylistBg = findViewById(R.id.imgViewPlaylistBg);

            ImageUtil imageUtil = new ImageUtil(this);
            imageUtil.downloadImageBitmap(playlist.getPlaylistArtUrl(),
                    () -> {
                        ((ImageView) findViewById(R.id.imgViewPlaylistPic))
                                .setImageBitmap(imageUtil.getBitmap());
                        imgPlaylistBg.setImageBitmap(imageUtil.blur(.4f, 10f));
                        imageUtil.setAlpha(this, imgPlaylistBg);
                    });

            SongAdapter songAdapter = new SongAdapter(this, playlist.appGetSongs(), R.layout.item_song_card, -1);
            RecyclerView rvPlaylistSongs = findViewById(R.id.rvPlaylistSongs);
            rvPlaylistSongs.setAdapter(songAdapter);

            findViewById(R.id.btnPlay).setOnClickListener(
                    v -> songAdapter.openSongPlayer(0, false, true));

            int randomPosition = new Random().nextInt(playlist.getSongIds().size());
            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(randomPosition, true, true));

            FloatingActionButton fabFollowPlaylist = findViewById(R.id.fabFollowPlaylist);

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (playlist.appGetOwner().getId().equals(userId)) {
                    fabFollowPlaylist.setImageIcon(Icon.createWithResource(this, R.drawable.ic_round_edit_24));
                    fabFollowPlaylist.setOnClickListener(v -> {
                        Intent editPlaylistIntent = new Intent(this, EditPlaylistActivity.class).putExtra("id", playlist.getId());
                        startActivity(editPlaylistIntent, ActivityOptions.makeSceneTransitionAnimation(this,
                                Pair.create(rvPlaylistSongs, "rvPlaylistSongs"),
                                Pair.create(fabFollowPlaylist, "fabFollowPlaylist")).toBundle());
                    });
                } else {
                    User user = UserRegistry.getInstance().itemFromId(userId);
                    if (user == null) {
                        fabFollowPlaylist.setEnabled(false);
                        return;
                    }
                    ArrayList<Playlist> playlists = new ArrayList<>();

                    if (user.appGetFollowedPlaylists() != null) {
                        playlists.addAll(user.appGetFollowedPlaylists());
                    }

                    Log.d(TAG, "onCreate: " + playlists.toString());

                    fabFollowPlaylist.setActivated(playlists.contains(playlist));
                    fabFollowPlaylist.setOnClickListener(v -> {
                        if (playlists.contains(playlist)) {
                            playlists.remove(playlist);
                        } else {
                            playlists.add(playlist);
                        }
                        user.setFollowedPlaylists(playlists);
                        UserRegistry.getInstance().writeToDb();

                        v.setActivated(playlists.contains(playlist));
                    });
                }
            } else {
                fabFollowPlaylist.setEnabled(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
            finish();
        }
    }
}
