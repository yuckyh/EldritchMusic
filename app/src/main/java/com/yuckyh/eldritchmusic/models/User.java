package com.yuckyh.eldritchmusic.models;

import com.google.firebase.firestore.DocumentReference;
import com.yuckyh.eldritchmusic.registries.AlbumRegistry;
import com.yuckyh.eldritchmusic.registries.ArtisteRegistry;
import com.yuckyh.eldritchmusic.registries.PlaylistRegistry;
import com.yuckyh.eldritchmusic.registries.SongRegistry;

import java.util.ArrayList;

public class User extends Model {
    private String mId, mName;
    private ArrayList<Album> mFollowedAlbums;
    private final ArrayList<Playlist> mCreatedPlaylists = new ArrayList<>();
    private ArrayList<Artiste> mFollowedArtistes;
    private ArrayList<Playlist> mFollowedPlaylists;
    private ArrayList<Song> mRecentlyPlayedSongs, mFavourites;
    private ArrayList<DocumentReference> mFollowedAlbumIds, mFollowedArtisteIds, mFollowedPlaylistIds, mRecentlyPlayedSongIds, mFavouriteIds;

    public User() {
        super();
    }

    public User(String id, String name) {
        super();
        mId = id;
        mName = name;
        mFollowedArtisteIds = mFollowedAlbumIds = mFollowedPlaylistIds = new ArrayList<>();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public ArrayList<Album> getFollowedAlbums() {
        return mFollowedAlbums;
    }

    public void setFollowedAlbums(ArrayList<Album> followedAlbums) {
        mFollowedAlbums = followedAlbums;
    }

    public ArrayList<Artiste> getFollowedArtistes() {
        return mFollowedArtistes;
    }

    public void setFollowedArtistes(ArrayList<Artiste> followedArtistes) {
        mFollowedArtistes = followedArtistes;
    }

    public ArrayList<Playlist> getFollowedPlaylists() {
        return mFollowedPlaylists;
    }

    public void setFollowedPlaylists(ArrayList<Playlist> followedPlaylists) {
        mFollowedPlaylists = followedPlaylists;
    }

    public ArrayList<DocumentReference> getFollowedAlbumIds() {
        return mFollowedAlbumIds;
    }

    public void setFollowedAlbumIds(ArrayList<DocumentReference> followedAlbumIds) {
        mFollowedAlbumIds = followedAlbumIds;
        setFollowedAlbums(AlbumRegistry.getInstance().refListToObjectList(mFollowedAlbumIds));
    }

    public ArrayList<DocumentReference> getFollowedArtisteIds() {
        return mFollowedArtisteIds;
    }

    public void setFollowedArtisteIds(ArrayList<DocumentReference> followedArtisteIds) {
        mFollowedArtisteIds = followedArtisteIds;
        setFollowedArtistes(ArtisteRegistry.getInstance().refListToObjectList(mFollowedArtisteIds));
    }

    public ArrayList<DocumentReference> getFollowedPlaylistIds() {
        return mFollowedPlaylistIds;
    }

    public void setFollowedPlaylistIds(ArrayList<DocumentReference> followedPlaylistIds) {
        mFollowedPlaylistIds = followedPlaylistIds;
        setFollowedPlaylists(PlaylistRegistry.getInstance().refListToObjectList(mFollowedPlaylistIds));
    }

    public void addPlaylist(Playlist playlist) {
        mCreatedPlaylists.add(playlist);
    }

    public ArrayList<Playlist> getCreatedPlaylists() {
        return mCreatedPlaylists;
    }

    public ArrayList<Song> getFavourites() {
        return mFavourites;
    }

    public ArrayList<DocumentReference> getFavouriteIds() {
        return mFavouriteIds;
    }

    public ArrayList<DocumentReference> getRecentlyPlayedSongIds() {
        return mRecentlyPlayedSongIds;
    }

    public void setFavouriteIds(ArrayList<DocumentReference> favouriteIds) {
        mFavouriteIds = favouriteIds;
        setFavourites(SongRegistry.getInstance().refListToObjectList(mFavouriteIds));
    }

    public void setRecentlyPlayedSongIds(ArrayList<DocumentReference> recentlyPlayedSongIds) {
        mRecentlyPlayedSongIds = recentlyPlayedSongIds;
        setRecentlyPlayedSongs(SongRegistry.getInstance().refListToObjectList(mRecentlyPlayedSongIds));
    }

    public void setFavourites(ArrayList<Song> favourites) {
        mFavourites = favourites;
    }

    public ArrayList<Song> getRecentlyPlayedSongs() {
        return mRecentlyPlayedSongs;
    }

    public void setRecentlyPlayedSongs(ArrayList<Song> recentlyPlayedSongs) {
        mRecentlyPlayedSongs = recentlyPlayedSongs;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
