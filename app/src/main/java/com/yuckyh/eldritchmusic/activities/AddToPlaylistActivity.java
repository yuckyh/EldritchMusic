package com.yuckyh.eldritchmusic.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.yuckyh.eldritchmusic.R;
import com.yuckyh.eldritchmusic.adapters.PlaylistAdapter;
import com.yuckyh.eldritchmusic.models.Playlist;
import com.yuckyh.eldritchmusic.models.Song;
import com.yuckyh.eldritchmusic.models.User;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;
import com.yuckyh.eldritchmusic.registries.UserRegistry;

import java.util.Objects;

public class AddToPlaylistActivity extends AppCompatActivity {
    private static final String TAG = AddToPlaylistActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_playlist);
        Objects.requireNonNull(getSupportActionBar()).hide();
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        PlaylistRegistry playlistRegistry = PlaylistRegistry.getInstance();

        setTitle("Choose/Create a Playlist");
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            try {
                User user = UserRegistry.getInstance().itemFromId(userId);
                if (user == null) {
                    finish();
                    return;
                }
                RecyclerView rvUserPlayList = findViewById(R.id.rvUserPlaylist);
                rvUserPlayList.setAdapter(new PlaylistAdapter(this,
                        user.appGetCreatedPlaylists(),
                        R.layout.item_playlist_card,
                        -1,
                        playlist -> {
                            try {
                                playlist.addSong(SongRegistry.getInstance().itemFromId(id));
                                playlistRegistry.writeToDb();
                                finish();
                            } catch (Exception e) {
                                Log.e(TAG, "onCreate: ", e);
                            }
                        }));

                findViewById(R.id.fabCreatePlaylist).setOnClickListener(v -> {
                    View view = getLayoutInflater().inflate(R.layout.dialog_create_playlist, null);
                    MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this, R.style.Theme_EldritchMusic)
                            .setTitle("Create a Playlist")
                            .setNegativeButton("Cancel", (dialog1, which) -> {
                            })
                            .setView(view);

                    TextInputEditText editTxtPlaylistName = view.findViewById(R.id.editTxtPlaylistName);

                    editTxtPlaylistName.setOnEditorActionListener((v1, actionId, event) -> {
                        if (editTxtPlaylistName.getText() == null) {
                            return false;
                        }
                        String name = editTxtPlaylistName.getText().toString();
                        if (name.length() > 20) {
                            editTxtPlaylistName.setError("Maximum 20 characters");
                            return false;
                        } else if (name.length() < 5) {
                            editTxtPlaylistName.setError("Minimum 5 characters");
                        }
                        return true;
                    });

                    builder.setPositiveButton("Ok", (dialog1, which) -> {
                        if (editTxtPlaylistName.getText() == null) {
                            return;
                        }

                        String name = editTxtPlaylistName.getText().toString();

                        if (editTxtPlaylistName.getError() == null) {
                            Playlist playlist = new Playlist(playlistRegistry.autoId(), name, user, id);
                            playlistRegistry.addToList(playlist);
                            playlistRegistry.writeToDb();
                            finish();
                        }
                    });

                    AlertDialog dialog = builder.show();
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                });
            } catch (Exception e) {
                Log.e(TAG, "onCreate: ", e);
            }
        }
    }
}
