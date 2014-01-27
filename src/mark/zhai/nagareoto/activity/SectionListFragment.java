package mark.zhai.nagareoto.activity;

import java.util.ArrayList;

import mark.zhai.nagareoto.R;
import mark.zhai.nagareoto.model.Song;
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
 * A dummy fragment representing a section of the app, but that simply displays dummy text.
 */
public class SectionListFragment extends ListFragment {

    private static final String TAG = "NagareSectionListFragment";
    private int mNum;
    private CursorAdapter mAdapter;
    private Context mContext;

    /**
     * Create a new instance of CountingFragment, providing "num" as an argument.
     */
    static SectionListFragment newInstance(int num) {
        SectionListFragment f = new SectionListFragment();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putInt("num", num);
        f.setArguments(args);

        return f;
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

    // MessageList.java - Line 4800
    public class SectionListAdapter extends CursorAdapter {
        private Context mContext;
        private LayoutInflater mInflater;

        public SectionListAdapter(Context context) {
            super(context, null, true);
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Resource resource = context.getResources();
            // Drawable mSelectedIcon = resources.getDrawable(R.drawable.btn_eject);

        }

//        /**
//         * Override to avoid refresh unless user forces
//         */
//        @Override
//        protected void onContentChanged() {
//            Log.i(TAG, "SectionListAdapter onContentChanged");
//            return;
//        }

        /**
         * Do the job of requerying list and notifying the UI of changed data Make sure we call
         * notifyDataSetChanged on the UI thread
         */
        private synchronized void refreshList() {
            Log.i(TAG, "SectionListAdapter refreshList");
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return mInflater.inflate(R.layout.fragment_pager_list, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // TODO Auto-generated method stub

        }
    }
}