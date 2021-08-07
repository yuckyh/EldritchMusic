package com.yuckyh.eldritchmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.yuckyh.eldritchmusic.utils.ImageUtil;
import com.yuckyh.eldritchmusic.utils.SpaceItemDecoration;

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

            ImageView imgPlaylistBg = findViewById(R.id.imgViewPlaylistBg);

            ImageUtil imageUtil = new ImageUtil(this);
            imageUtil.downloadImageBitmap(playlist.getPlaylistArtUrl(),
                    () -> {
                        ((ImageView)findViewById(R.id.imgViewPlaylistPic))
                                .setImageBitmap(imageUtil.getBitmap());
                        imgPlaylistBg.setImageBitmap(imageUtil.blur(.4f, 10f));
                        imageUtil.setAlpha(this, imgPlaylistBg);
                    });

            SongAdapter songAdapter = new SongAdapter(this, playlist.getSongs(), R.layout.item_song_card, -1);
            RecyclerView rvPlaylistSongs = findViewById(R.id.rvPlaylistSongs);
            rvPlaylistSongs.setAdapter(songAdapter);
            rvPlaylistSongs.addItemDecoration(new SpaceItemDecoration((int) getResources().getDimension(R.dimen.vertical_item_space), 0));

            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(0, false, true));

            int randomPosition = new Random().nextInt(playlist.getSongIds().size());
            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(randomPosition, true, true));

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                if (playlist.getOwner().getId().equals(userId)) {
                    Log.d(TAG, "onCreate: is owner");
                } else {
                    User user = UserRegistry.getInstance().fromId(userId);
                    FloatingActionButton fabFollowPlaylist = findViewById(R.id.fabFollowPlaylist);
                    if (user == null) {
                        fabFollowPlaylist.setVisibility(View.GONE);
                        return;
                    }
                    ArrayList<Playlist> playlists = new ArrayList<>();

                    if (user.getFollowedPlaylists() != null) {
                        playlists.addAll(user.getFollowedPlaylists());
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
