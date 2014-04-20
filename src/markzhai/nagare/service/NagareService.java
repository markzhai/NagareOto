package markzhai.nagare.service;

import java.lang.ref.WeakReference;

import com.andrew.apolloMod.service.ApolloService;

import android.app.Notification;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import markzhai.nagare.INagareService;

public class NagareService {
    private Notification status;

    public static final String NAGARE_PACKAGE_NAME = "markzhai.nagare";
    public static final String MUSIC_PACKAGE_NAME = "com.android.music";
    public static final String PLAYSTATE_CHANGED = "markzhai.nagare.playstatechanged";
    public static final String META_CHANGED = "markzhai.nagare.metachanged";


    /**
     * Opens the specified file and readies it for playback.
     * 
     * @param path The full path of the file to be opened.
     */
    public boolean open(String path) {
        synchronized (this) {
            if (path == null) {
                return false;
            }

            // if mCursor is null, try to associate path with a database cursor
            if (mCursor == null) {

                ContentResolver resolver = getContentResolver();
                Uri uri;
                String where;
                String selectionArgs[];
                if (path.startsWith("content://media/")) {
                    uri = Uri.parse(path);
                    where = null;
                    selectionArgs = null;
                } else {
                    // Remove schema for search in the database
                    // Otherwise the file will not found
                    String data = path;
                    if( data.startsWith("file://") ){
                        data = data.substring(7);
                    }
                    uri = MediaStore.Audio.Media.getContentUriForPath(path);
                    where = MediaColumns.DATA + "=?";
                    selectionArgs = new String[] {
                        data
                    };
                }

                try {
                    mCursor = resolver.query(uri, mCursorCols, where, selectionArgs, null);
                    if (mCursor != null) {
                        if (mCursor.getCount() == 0) {
                            mCursor.close();
                            mCursor = null;
                        } else {
                            mCursor.moveToNext();
                            ensurePlayListCapacity(1);
                            mPlayListLen = 1;
                            mPlayList[0] = mCursor.getLong(IDCOLIDX);
                            mPlayPos = 0;
                        }
                    }
                } catch (UnsupportedOperationException ex) {
                }

                updateAlbumBitmap();
            }
            mFileToPlay = path;
            mPlayer.setDataSource(mFileToPlay);
            if (mPlayer.isInitialized()) {
                mOpenFailedCounter = 0;
                return true;
            }
            stop(true);
            return false;
        }
    }

    
     static class ServiceStub extends INagareService.Stub {
        WeakReference<NagareService> mService;

        ServiceStub(NagareService service) {
            mService = new WeakReference<NagareService>(service);
        }
        
        @Override
        public void openFile(String path) {
            mService.get().open(path);
        }

        @Override
        public void open(long[] list, int position) {
            mService.get().open(list, position);
        }

        @Override
        public long getIdFromPath(String path) {
            return mService.get().getIdFromPath(path);
        }

        @Override
        public int getQueuePosition() {
            return mService.get().getQueuePosition();
        }

        @Override
        public void setQueuePosition(int index) {
            mService.get().setQueuePosition(index);
        }

        @Override
        public boolean isPlaying() {
            return mService.get().isPlaying();
        }

        @Override
        public void stop() {
            mService.get().stop();
        }

        @Override
        public void pause() {
            mService.get().pause();
        }

        @Override
        public void play() {
            mService.get().play();
        }

        @Override
        public void prev() {
            mService.get().prev();
        }

        @Override
        public void next() {
            mService.get().gotoNext(true);
        }

        @Override
        public String getTrackName() {
            return mService.get().getTrackName();
        }

        @Override
        public String getAlbumName() {
            return mService.get().getAlbumName();
        }

        @Override
        public Bitmap getAlbumBitmap() {
            return mService.get().getAlbumBitmap();
        }

        @Override
        public long getAlbumId() {
            return mService.get().getAlbumId();
        }

        @Override
        public String getArtistName() {
            return mService.get().getArtistName();
        }

        @Override
        public long getArtistId() {
            return mService.get().getArtistId();
        }

        @Override
        public void enqueue(long[] list, int action) {
            mService.get().enqueue(list, action);
        }

        @Override
        public long[] getQueue() {
            return mService.get().getQueue();
        }

        @Override
        public String getPath() {
            return mService.get().getPath();
        }

        @Override
        public long getAudioId() {
            return mService.get().getAudioId();
        }

        @Override
        public long position() {
            return mService.get().position();
        }

        @Override
        public long duration() {
            return mService.get().duration();
        }

        @Override
        public long seek(long pos) {
            return mService.get().seek(pos);
        }

        @Override
        public void setShuffleMode(int shufflemode) {
            mService.get().setShuffleMode(shufflemode);
        }

        @Override
        public int getShuffleMode() {
            return mService.get().getShuffleMode();
        }

        @Override
        public int removeTracks(int first, int last) {
            return mService.get().removeTracks(first, last);
        }

        @Override
        public void moveQueueItem(int from, int to) {
            mService.get().moveQueueItem(from, to);
        }

        @Override
        public int removeTrack(long id) {
            return mService.get().removeTrack(id);
        }

        @Override
        public void setRepeatMode(int repeatmode) {
            mService.get().setRepeatMode(repeatmode);
        }

        @Override
        public int getRepeatMode() {
            return mService.get().getRepeatMode();
        }

        @Override
        public int getMediaMountedCount() {
            return mService.get().getMediaMountedCount();
        }

        @Override
        public int getAudioSessionId() {
            return mService.get().getAudioSessionId();
        }

        @Override
        public void addToFavorites(long id) throws RemoteException {
            mService.get().addToFavorites(id);
        }

        @Override
        public void removeFromFavorites(long id) throws RemoteException {
            mService.get().removeFromFavorites(id);
        }

        @Override
        public boolean isFavorite(long id) throws RemoteException {
            return mService.get().isFavorite(id);
        }

        @Override
        public void toggleFavorite() throws RemoteException {
            mService.get().toggleFavorite();
        }

        public void notifyChange(String what) {
            mService.get().notifyChange(what);
        }

    };

    private final IBinder mBinder = new ServiceStub(this);
}
