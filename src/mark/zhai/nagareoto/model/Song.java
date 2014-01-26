package mark.zhai.nagareoto.model;

import android.content.ContentUris;
import android.net.Uri;

public class Song {
    private long mId;
    private String mTitle;
    private String mArtist;
    private String mAlbum;
    private String mGenre;
    private long mDuration;

    public Song() {}

    public Song(long id, String artist, String title, String album, long duration) {
        this.mId = id;
        this.mTitle = title;
        this.mArtist = artist;
        this.mAlbum = album;
        this.mDuration = duration;
    }

    public long getmId() {
        return mId;
    }

    public void setmId(long id) {
        this.mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    public String getGenre() {
        return mGenre;
    }

    public void setGenre(String genre) {
        this.mGenre = genre;
    }

    public long getmDuration() {
        return mDuration;
    }

    public void setmDuration(long duration) {
        this.mDuration = duration;
    }

    public Uri getURI() {
        return ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, mId);
    }
}