package markzhai.nagare.utils;

import markzhai.nagare.R;
import markzhai.nagare.cache.ImageInfo;
import markzhai.nagare.cache.ImageProvider;
import android.app.Activity;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AlbumColumns;
import android.provider.MediaStore.Audio.ArtistColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import static markzhai.nagare.Constants.*;

public class NagareUtils {

    /**
     * @return A custom ContextMenu header
     */
    public static View setHeaderLayout(String Type, Cursor cursor, Activity activity) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View header = inflater.inflate(R.layout.context_menu_header, null, false);

        // Artist image
        final ImageView mHanderImage = (ImageView) header.findViewById(R.id.header_image);
        String albumId = "", artistName = "", albumName = "";

        if (Type == TYPE_ALBUM) {
            albumName = cursor.getString(cursor.getColumnIndexOrThrow(AlbumColumns.ALBUM));
            artistName = cursor.getString(cursor.getColumnIndexOrThrow(AlbumColumns.ARTIST));
            albumId = cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID));
        } else {
            artistName = cursor.getString(cursor.getColumnIndexOrThrow(ArtistColumns.ARTIST));
        }

        ImageInfo mInfo = new ImageInfo();
        mInfo.type = Type;
        mInfo.size = SIZE_THUMB;
        mInfo.source = SRC_FIRST_AVAILABLE;
        mInfo.data = (Type == TYPE_ALBUM ? new String[] { albumId, artistName, albumName }
                : new String[] { artistName });

        ImageProvider.getInstance(activity).loadImage(mHanderImage, mInfo);

        // Set artist name
        TextView headerText = (TextView) header.findViewById(R.id.header_text);
        headerText.setText((Type == TYPE_ALBUM ? albumName : artistName));
        headerText.setBackgroundColor((activity).getResources().getColor(R.color.transparent_black));
        return header;
    }

    /**
     * Replace the characters not allowed in file names with underscore
     * 
     * @param name
     * @return
     */
    public static String escapeForFileSystem(String name) {
        return name.replaceAll("[\\\\/:*?\"<>|]+", "_");
    }
}
