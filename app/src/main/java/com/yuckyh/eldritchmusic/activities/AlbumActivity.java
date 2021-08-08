package com.yuckyh.eldritchmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
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
import com.yuckyh.eldritchmusic.models.Album;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;
import com.yuckyh.eldritchmusic.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class AlbumActivity extends AppCompatActivity {
    private static final String TAG = AlbumActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Album album;
        try {
            album = AlbumRegistry.getInstance().itemFromId(getIntent().getStringExtra("id"));

            ((TextView) findViewById(R.id.txtViewAlbumName))
                    .setText(album.getName());
            ((TextView) findViewById(R.id.txtViewAlbumArtiste))
                    .setText(album.appGetArtiste().getName());

            ImageView imgViewAlbumBg = findViewById(R.id.imgViewAlbumBg);

            ImageUtil imageUtil = new ImageUtil(this);
            imageUtil.downloadImageBitmap(album.getAlbumArtUrl(),
                    () -> {
                        ((ImageView) findViewById(R.id.imgViewAlbumPic))
                                .setImageBitmap(imageUtil.getBitmap());
                        imgViewAlbumBg.setImageBitmap(imageUtil.blur(.4f, 10f));
                        imageUtil.setAlpha(this, imgViewAlbumBg);
                    });

            RecyclerView rvAlbumSongs = findViewById(R.id.rvAlbumSongs);
            Log.d(TAG, "onCreate: " + album.appGetSongs().toString());

            SongAdapter songAdapter = new SongAdapter(this, album.appGetSongs(), R.layout.item_song_card_simple, -1);
            rvAlbumSongs.setAdapter(songAdapter);

            findViewById(R.id.btnPlay).setOnClickListener(
                    v -> songAdapter.openSongPlayer(0, false, true));

            int randomPosition = new Random().nextInt(album.appGetSongs().size());
            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(randomPosition, true, true));

            findViewById(R.id.btnArtiste).setOnClickListener(
                    v -> startActivity(new Intent(this, ArtisteActivity.class)
                            .putExtra("id", album.appGetArtiste().getId())));

            FloatingActionButton fabFollowAlbum = findViewById(R.id.fabFollowAlbum);

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                User user = UserRegistry.getInstance().itemFromId(userId);
                if (user == null) {
                    fabFollowAlbum.setEnabled(false);
                    return;
                }
                ArrayList<Album> albums = new ArrayList<>();

                if (user.appGetFollowedPlaylists() != null) {
                    albums.addAll(user.appGetFollowedAlbums());
                }

                Log.d(TAG, "onCreate: " + albums.toString());

                fabFollowAlbum.setActivated(albums.contains(album));
                fabFollowAlbum.setOnClickListener(v -> {
                    if (albums.contains(album)) {
                        albums.remove(album);
                    } else {
                        albums.add(album);
                    }
                    user.setFollowedAlbums(albums);
                    UserRegistry.getInstance().writeToDb();

                    v.setActivated(albums.contains(album));
                });
            } else {
                fabFollowAlbum.setEnabled(false);
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
            finish();
        }
    }
}
