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
import com.yuckyh.eldritchmusic.adapters.AlbumAdapter;
import com.yuckyh.eldritchmusic.adapters.SongAdapter;
import com.yuckyh.eldritchmusic.models.Album;
import com.yuckyh.eldritchmusic.models.Artiste;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.ArtisteRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;
import com.yuckyh.eldritchmusic.utils.ImageUtil;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class ArtisteActivity extends AppCompatActivity {
    private static final String TAG = ArtisteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artiste);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Artiste artiste;
        try {
            artiste = ArtisteRegistry.getInstance().itemFromId(getIntent().getStringExtra("id"));

            ((TextView) findViewById(R.id.txtViewArtisteName))
                    .setText(artiste.getName());

            ImageView imgViewArtisteBg = findViewById(R.id.imgViewArtisteBg);

            ImageUtil imageUtil = new ImageUtil(this);
            imageUtil.downloadImageBitmap(artiste.getProfileUrl(),
                    () -> {
                        ((ImageView) findViewById(R.id.imgViewArtistePic))
                                .setImageBitmap(imageUtil.getBitmap());
                        imgViewArtisteBg.setImageBitmap(imageUtil.blur(.4f, 10f));
                        imageUtil.setAlpha(this, imgViewArtisteBg);
                    });

            RecyclerView rvArtisteSongs = findViewById(R.id.rvArtisteSongs);
            Log.d(TAG, "onCreate: " + artiste.appGetSongs().toString());

            SongAdapter songAdapter = new SongAdapter(this, artiste.appGetSongs(), R.layout.item_song_card_simple, 10);
            rvArtisteSongs.setAdapter(songAdapter);

            RecyclerView rvArtisteAlbum = findViewById(R.id.rvArtisteAlbums);
            rvArtisteAlbum.setAdapter(new AlbumAdapter(this, artiste.appGetAlbums(), 10));

            findViewById(R.id.btnPlay).setOnClickListener(
                    v -> songAdapter.openSongPlayer(0, false, true));

            int randomPosition = new Random().nextInt(artiste.appGetSongs().size());
            findViewById(R.id.btnShuffle).setOnClickListener(
                    v -> songAdapter.openSongPlayer(randomPosition, true, true));

            FloatingActionButton fabFollowArtiste = findViewById(R.id.fabFollowArtiste);

            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                User user = UserRegistry.getInstance().itemFromId(userId);
                if (user == null) {
                    fabFollowArtiste.setEnabled(false);
                    return;
                }
                ArrayList<Artiste> artistes = new ArrayList<>();

                if (user.appGetFollowedPlaylists() != null) {
                    artistes.addAll(user.appGetFollowedArtistes());
                }

                Log.d(TAG, "onCreate: " + artistes.toString());

                fabFollowArtiste.setActivated(artistes.contains(artiste));
                fabFollowArtiste.setOnClickListener(v -> {
                    if (artistes.contains(artiste)) {
                        artistes.remove(artiste);
                    } else {
                        artistes.add(artiste);
                    }
                    user.setFollowedArtistes(artistes);
                    UserRegistry.getInstance().writeToDb();

                    v.setActivated(artistes.contains(artiste));
                });
            } else {
                fabFollowArtiste.setEnabled(false);
            }

        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }
}
