package com.yuckyh.eldritchmusic.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Objects;

public class EditPlaylistActivity extends AppCompatActivity {
    private static final String TAG = EditPlaylistActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_playlist);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
        }

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

            ArrayList<Song> songs = new ArrayList<>(playlist.appGetSongs());

            SongAdapter songAdapter = new SongAdapter(this, songs, R.layout.item_song_card_edit, -1);
            RecyclerView rvPlaylistSongs = findViewById(R.id.rvPlaylistSongs);
            rvPlaylistSongs.setAdapter(songAdapter);

            findViewById(R.id.fabPlaylistEditConfirm).setOnClickListener(v -> {
                playlist.setSongs(songs);
                PlaylistRegistry.getInstance().writeToDb();
                finish();
            });
            findViewById(R.id.fabPlaylistEditCancel).setOnClickListener(v -> finish());
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }
}
