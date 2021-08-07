package com.yuckyh.eldritchmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;
import com.yuckyh.eldritchmusic.utils.ImageUtil;

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
            playlist = PlaylistRegistry.getInstance().itemFromId(getIntent().getStringExtra("id"));

            setTitle(playlist.getName());
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

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                FloatingActionButton fabFollowPlaylist = findViewById(R.id.fabFollowPlaylist);
                if (playlist.appGetOwner().getId().equals(userId)) {
                    fabFollowPlaylist.setImageIcon(Icon.createWithResource(this, R.drawable.ic_round_edit_24));
                    fabFollowPlaylist.setOnClickListener(v -> {
                        Log.d(TAG, "onCreate: is owner");
                        PlaylistRegistry.getInstance().writeToDb();
                    });
                } else {
                    User user = UserRegistry.getInstance().itemFromId(userId);
                    if (user == null) {
                        fabFollowPlaylist.setVisibility(View.GONE);
                        return;
                    }
                    ArrayList<DocumentReference> playlistIds = new ArrayList<>();

                    if (user.appGetFollowedPlaylists() != null) {
                        playlistIds.addAll(user.getFollowedPlaylistIds());
                    }

                    Log.d(TAG, "onCreate: " + playlistIds.toString());

                    DocumentReference playlistId = PlaylistRegistry.getInstance().refFromId(playlist.getId());

                    fabFollowPlaylist.setActivated(playlistIds.contains(playlistId));
                    fabFollowPlaylist.setOnClickListener(v -> {
                        if (playlistIds.contains(playlistId)) {
                            playlistIds.remove(playlistId);
                        } else {
                            playlistIds.add(playlistId);
                        }
                        user.setFollowedPlaylistIds(playlistIds);
                        UserRegistry.getInstance().writeToDb();

                        v.setActivated(playlistIds.contains(playlistId));
                    });
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
            finish();
        }
    }
}
