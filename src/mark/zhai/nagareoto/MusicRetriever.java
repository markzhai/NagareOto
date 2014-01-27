/*   
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mark.zhai.nagareoto;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mark.zhai.nagareoto.model.Song;

/**
 * Retrieves and organizes media to play. Before being used, you must call {@link #prepare()}, which will
 * retrieve all of the music on the user's device (by performing a query on a content resolver). After that,
 * it's ready to retrieve a random song, with its title and URI, upon request.
 */
public class MusicRetriever {
    final String TAG = "NagareMusicRetriever";

    ContentResolver mContentResolver;

    // the items (songs) we have queried
    List<Song> mSongs = new ArrayList<Song>();

    Random mRandom = new Random();

    public MusicRetriever(ContentResolver cr) {
        mContentResolver = cr;
    }

    /**
     * Loads music data. This method may take long, so be sure to call it asynchronously without blocking the
     * main thread.
     */
    public void prepare() {
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
        Log.i(TAG, "Querying media...");
        Log.i(TAG, "URI: " + uri.toString());

        // Perform a query on the content resolver. The URI we're passing specifies that we
        // want to query for all audio media on external storage (e.g. SD card)
        Cursor cur = mContentResolver.query(uri, null, Audio.Media.IS_MUSIC + " = 1", null, null);
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));
        if (cur == null) {
            // Query failed...
            Log.e(TAG, "Failed to retrieve music: cursor is null :-(");
            return;
        }
        if (!cur.moveToFirst()) {
            // Nothing to query. There is no music on the device. How boring.
            Log.e(TAG, "Failed to move cursor to first row (no query results).");
            return;
        }

        Log.i(TAG, "Listing...");

        // retrieve the indices of the columns where the ID, title, etc. of the song are
        int artistColumn = cur.getColumnIndex(Audio.Media.ARTIST);
        int titleColumn = cur.getColumnIndex(Audio.Media.TITLE);
        int albumColumn = cur.getColumnIndex(Audio.Media.ALBUM);
        int durationColumn = cur.getColumnIndex(Audio.Media.DURATION);
        int idColumn = cur.getColumnIndex(Audio.Media._ID);

        Log.i(TAG, "Title column index: " + String.valueOf(titleColumn));
        Log.i(TAG, "ID column index: " + String.valueOf(titleColumn));

        // add each song to mItems
        do {
            Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn));
            mSongs.add(new Song(cur.getLong(idColumn), cur.getString(artistColumn), cur
                    .getString(titleColumn), cur.getString(albumColumn), cur.getLong(durationColumn)));
            Log.i(TAG, String.valueOf(cur.getLong(idColumn)) + ": " + cur.getString(titleColumn));
        } while (cur.moveToNext());

        Log.i(TAG, "Done querying media. MusicRetriever is ready.");
    }

    public ContentResolver getContentResolver() {
        return mContentResolver;
    }

    /** Returns a random Item. If there are no items available, returns null. */
    public Song getRandomItem() {
        if (mSongs.size() <= 0)
            return null;
        return mSongs.get(mRandom.nextInt(mSongs.size()));
    }
    
    public String getArtFromAlbum(String albumId) {
        Cursor cur = getContentResolver().query(Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[] {Audio.Albums.ALBUM_ART}, Audio.Albums._ID + "=?", new String[] {albumId}, null);

        cur.moveToFirst();
        String art = cur.getString(cur.getColumnIndex(Audio.Albums.ALBUM_ART));

        // ImageView ivalbumart = (ImageView)findViewById(R.id.albumart);
        // ivalbumart.setImageURI(Uri.fromFile(new File(art)));
        return art;
    }

    public Cursor getAlbumAlbumcursor(Context context, Cursor cursor) {
        String where = null;
        ContentResolver cr = context.getContentResolver();
        final Uri uri = Audio.Albums.EXTERNAL_CONTENT_URI;
        final String id = Audio.Albums._ID;
        //final String albumId = Audio.Albums.ALBUM_ID;
        final String albumName = Audio.Albums.ALBUM;
        final String artist = Audio.Albums.ARTIST;
        final String[] columns = {id, albumName, artist};
        cursor = cr.query(uri, columns, where, null, null);
        return cursor;
    }

    public Cursor getTrackTrackcursor(Context context, Cursor cursor) {
        final String track_id = Audio.Media._ID;
        final String track_no = Audio.Media.TRACK;
        final String track_name = Audio.Media.TITLE;
        final String artist = Audio.Media.ARTIST;
        final String duration = Audio.Media.DURATION;
        final String album = Audio.Media.ALBUM;
        final String composer = Audio.Media.COMPOSER;
        final String year = Audio.Media.YEAR;
        final String path = Audio.Media.DATA;
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;

        ContentResolver cr = context.getContentResolver();
        final String[] columns = { track_id, track_no, artist, track_name, album, duration, path, year,
                composer };
        cursor = cr.query(uri, columns, null, null, null);
        return cursor;
    }

    public Cursor getandroidPlaylistcursor(Context context, Cursor cursor) {
        ContentResolver resolver = context.getContentResolver();
        final Uri uri = Audio.Playlists.EXTERNAL_CONTENT_URI;
        final String id = Audio.Playlists._ID;
        final String name = Audio.Playlists.NAME;
        final String[] columns = { id, name };
        final String criteria = Audio.Playlists.NAME.length() + " > 0 ";
        final Cursor crplaylists = resolver.query(uri, columns, criteria, null, name + "    `ASC");
        return crplaylists;
    }
}
