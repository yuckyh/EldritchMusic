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
        mFollowedArtisteIds = mFollowedAlbumIds = mFollowedPlaylistIds = mRecentlyPlayedSongIds = mFavouriteIds = new ArrayList<>();
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    @Override
    public void setObjectsFromRefs() {
        setRecentlyPlayedSongs(SongRegistry.getInstance().refListToObjectList(mRecentlyPlayedSongIds));
        setFavourites(SongRegistry.getInstance().refListToObjectList(mFavouriteIds));
        setFollowedAlbums(AlbumRegistry.getInstance().refListToObjectList(mFollowedAlbumIds));
        setFollowedArtistes(ArtisteRegistry.getInstance().refListToObjectList(mFollowedArtisteIds));
        setFollowedPlaylists(PlaylistRegistry.getInstance().refListToObjectList(mFollowedPlaylistIds));
    }

    @Override
    public void setRefsFromObjects() {
        setRefArrayToObjArray(mRecentlyPlayedSongIds, mRecentlyPlayedSongs);
        setRefArrayToObjArray(mFavouriteIds, mFavourites);
        setRefArrayToObjArray(mFollowedAlbumIds, mFollowedAlbums);
        setRefArrayToObjArray(mFollowedArtisteIds, mFollowedArtistes);
        setRefArrayToObjArray(mFollowedPlaylistIds, mFollowedPlaylists);
    }

    public ArrayList<Album> appGetFollowedAlbums() {
        return mFollowedAlbums;
    }

    public void setFollowedAlbums(ArrayList<Album> followedAlbums) {
        mFollowedAlbums = followedAlbums;
    }

    public ArrayList<Artiste> appGetFollowedArtistes() {
        return mFollowedArtistes;
    }

    public void setFollowedArtistes(ArrayList<Artiste> followedArtistes) {
        mFollowedArtistes = followedArtistes;
    }

    public ArrayList<Playlist> appGetFollowedPlaylists() {
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
    }

    public ArrayList<DocumentReference> getFollowedArtisteIds() {
        return mFollowedArtisteIds;
    }

    public void setFollowedArtisteIds(ArrayList<DocumentReference> followedArtisteIds) {
        mFollowedArtisteIds = followedArtisteIds;
    }

    public ArrayList<DocumentReference> getFollowedPlaylistIds() {
        return mFollowedPlaylistIds;
    }

    public void setFollowedPlaylistIds(ArrayList<DocumentReference> followedPlaylistIds) {
        mFollowedPlaylistIds = followedPlaylistIds;
    }

    public void addPlaylist(Playlist playlist) {
        mCreatedPlaylists.add(playlist);
    }

    public ArrayList<Playlist> appGetCreatedPlaylists() {
        return mCreatedPlaylists;
    }

    public ArrayList<Song> appGetFavourites() {
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
    }

    public void setRecentlyPlayedSongIds(ArrayList<DocumentReference> recentlyPlayedSongIds) {
        mRecentlyPlayedSongIds = recentlyPlayedSongIds;
    }

    public void setFavourites(ArrayList<Song> favourites) {
        mFavourites = favourites;
    }

    public ArrayList<Song> appGetRecentlyPlayedSongs() {
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
