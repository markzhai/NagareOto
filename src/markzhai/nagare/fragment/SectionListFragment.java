package markzhai.nagare.fragment;

import java.util.ArrayList;

import markzhai.nagare.R;
import markzhai.nagare.Constants;
import markzhai.nagare.model.Song;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.provider.MediaStore.Audio;

/**
 * Section list fragment representing a section of the app.
 */
public class SectionListFragment extends ListFragment {

    private static final String TAG = "NagareSectionListFragment";
    private int mNum;
    private CursorAdapter mAdapter;
    private Context mContext;
    
    private static SectionListFragment[] fragmentArray = new SectionListFragment[Constants.NUM_ITEMS];

    /**
     * Create a new instance of Fragment, providing "num" as an argument.
     */
    public static SectionListFragment newInstance(int num) {
        if (fragmentArray[num] != null) {
            return fragmentArray[num];
        } else {
            SectionListFragment f = new SectionListFragment();
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);
            fragmentArray[num] = f;
            return f;
        }
    }

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        mContext = this.getActivity();
    }

    /**
     * The Fragment's UI is just a simple text view showing its instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pager_list, container, false);
        View tv = v.findViewById(R.id.text);
        ((TextView) tv).setText("Fragment #" + mNum);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i(TAG, "Item clicked: " + id);
    }

    @Override
    public void onStart() {
        super.onStart();
        String[] projection = {Audio.Albums._ID, Audio.Albums.ALBUM, Audio.Albums.ARTIST, Audio.Albums.ALBUM_ART};
        CursorLoader loader = new CursorLoader(mContext, Audio.Albums.EXTERNAL_CONTENT_URI, projection, null, null, null);
        Cursor cur = loader.loadInBackground();
        
        Log.i(TAG, "Query finished. " + (cur == null ? "Returned NULL." : "Returned a cursor."));
        if (cur == null) {
            Log.e(TAG, "Failed to retrieve music: cursor is null :-(");
            return;
        }
        if (!cur.moveToFirst()) {
            Log.e(TAG, "Failed to move cursor to first row (no query results).");
            return;
        }
        ListAdapter adapter = new SimpleCursorAdapter(
                mContext, R.layout.fragment_pager_list,
                cur, new String[] {Audio.Albums.ALBUM, Audio.Albums.ALBUM_ART}, new int[] {
                R.id.sectionListText, R.id.sectionListImg}, 0);
        setListAdapter(adapter);
    }
}