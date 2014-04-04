/**
 * 
 */
package markzhai.nagare;

/**
 * @author ZhaiYf
 * 
 */
public final class Constants {
    public static final String ACTION_TOGGLE_PLAYBACK = "mark.zhai.nagareotoaction.TOGGLE_PLAYBACK";
    public static final String ACTION_PLAY = "mark.zhai.nagareotoaction.PLAY";
    public static final String ACTION_PAUSE = "mark.zhai.nagareotoaction.PAUSE";
    public static final String ACTION_STOP = "mark.zhai.nagareotoaction.STOP";
    public static final String ACTION_SKIP = "mark.zhai.nagareotoaction.SKIP";
    public static final String ACTION_REWIND = "mark.zhai.nagareotoaction.REWIND";

    public static final int NUM_ITEMS = 5;

    // SharedPreferences
    public final static String NAGARE = "流れ音", NAGARE_PREFERENCES = "nagarepreferences",
            ARTIST_KEY = "artist", ALBUM_KEY = "album", ALBUM_ID_KEY = "albumid",
            NUMALBUMS = "num_albums", GENRE_KEY = "genres", ARTIST_ID = "artistid",
            NUMWEEKS = "numweeks", PLAYLIST_NAME_FAVORITES = "Favorites",
            PLAYLIST_NAME = "playlist", WIDGET_STYLE = "widget_type",
            THEME_PACKAGE_NAME = "themePackageName",
            THEME_DESCRIPTION = "themeDescription", THEME_PREVIEW = "themepreview",
            THEME_TITLE = "themeTitle", VISUALIZATION_TYPE = "visualization_type",
            UP_STARTS_ALBUM_ACTIVITY = "upStartsAlbumActivity",
            TABS_ENABLED = "tabs_enabled";

    // Image Loading Constants
    public final static String TYPE_ARTIST = "artist", TYPE_ALBUM = "album",
            TYPE_GENRE = "genre", TYPE_SONG = "song", TYPE_PLAYLIST = "playlist",
            ALBUM_SUFFIX = "albartimg", ARTIST_SUFFIX = "artstimg",
            PLAYLIST_SUFFIX = "plylstimg", GENRE_SUFFIX = "gnreimg",
            SRC_FIRST_AVAILABLE = "first_avail", SRC_LASTFM = "last_fm",
            SRC_FILE = "from_file", SRC_GALLERY = "from_gallery", SIZE_NORMAL = "normal",
            SIZE_THUMB = "thumb";

    // Bundle & Intent type
    public final static String MIME_TYPE = "mimetype", INTENT_ACTION = "action",
            DATA_SCHEME = "file";

    // Storage Volume
    public final static String EXTERNAL = "external";
    
    public final static String INTENT_ADD_TO_PLAYLIST = "markzhai.nagare.ADD_TO_PLAYLIST",
            INTENT_PLAYLIST_LIST = "playlistlist",
            INTENT_CREATE_PLAYLIST = "markzhai.nagare.CREATE_PLAYLIST",
            INTENT_RENAME_PLAYLIST = "markzhai.nagare.RENAME_PLAYLIST",
            INTENT_KEY_RENAME = "rename", INTENT_KEY_DEFAULT_NAME = "default_name";
}