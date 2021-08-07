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
import com.yuckyh.eldritchmusic.models.Album;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;
import com.yuckyh.eldritchmusic.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Random;

public class AlbumActivity extends AppCompatActivity {
    private static final String TAG = AlbumActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Album album;
        try {
            album = AlbumRegistry.getInstance().fromId(getIntent().getStringExtra("id"));

            setTitle(album.getName());
            ((TextView)findViewById(R.id.txtViewPlaylistName))
                    .setText(album.getName());
            ((TextView)findViewById(R.id.txtViewPlaylistOwner))
                    .setText(album.getArtiste().getName());

            ImageView imgViewPlaylistBg = findViewById(R.id.imgViewPlaylistBg);

            ImageUtil imageUtil = new ImageUtil(this);
            imageUtil.downloadImageBitmap(album.getAlbumArtUrl(),
                    () -> {
                        ((ImageView)findViewById(R.id.imgViewPlaylistPic))
                                .setImageBitmap(imageUtil.getBitmap());
                        imgViewPlaylistBg.setImageBitmap(imageUtil.blur(.4f, 10f));
                        imageUtil.setAlpha(this, imgViewPlaylistBg);
                    });

            RecyclerView rvPlaylistSongs = findViewById(R.id.rvPlaylistSongs);

            SongAdapter songAdapter = new SongAdapter(this, album.getSongs(), R.layout.item_song_card, -1);
            rvPlaylistSongs.setAdapter(songAdapter);

            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(0, false, true));

            int randomPosition = new Random().nextInt(album.getSongs().size());
            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(randomPosition, true, true));

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                User user = UserRegistry.getInstance().fromId(userId);
                FloatingActionButton fabFollowPlaylist = findViewById(R.id.fabFollowPlaylist);
                if (user == null) {
                    fabFollowPlaylist.setVisibility(View.GONE);
                    return;
                }
                ArrayList<Album> albums = new ArrayList<>();

                if (user.getFollowedPlaylists() != null) {
                    albums.addAll(user.getFollowedAlbums());
                }

                Log.d(TAG, "onCreate: " + albums.toString());

                fabFollowPlaylist.setActivated(albums.contains(album));
                fabFollowPlaylist.setOnClickListener(v -> {
                    if (albums.contains(album)) {
                        albums.remove(album);
                    } else {
                        albums.add(album);
                    }
                    user.setFollowedAlbums(albums);

                    v.setActivated(albums.contains(album));
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
            finish();
        }
    }
}
