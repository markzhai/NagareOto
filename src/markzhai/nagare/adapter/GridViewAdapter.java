package markzhai.nagare.adapter;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import markzhai.nagare.cache.ImageInfo;
import markzhai.nagare.cache.ImageProvider;
import markzhai.nagare.views.ViewHolderGrid;
import static markzhai.nagare.Constants.*;

public abstract class GridViewAdapter extends SimpleCursorAdapter {
    private static final String TAG = "NagareGridViewAdapter";

    private WeakReference<ViewHolderGrid> holderReference;

    protected Context mContext;
    private ImageProvider mImageProvider;
    public String mGridType = null, mLineOneText = null, mLineTwoText = null;

    public String[] mImageData = null;

    public long mPlayingId = 0, mCurrentId = 0;

    public GridViewAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        mImageProvider = ImageProvider.getInstance((Activity) mContext);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view = super.getView(position, convertView, parent);
        Cursor mCursor = (Cursor) getItem(position);
        setupViewData(mCursor);
        // ViewHolderGrid
        final ViewHolderGrid viewholder;
        if (view != null) {
            viewholder = new ViewHolderGrid(view);
            holderReference = new WeakReference<ViewHolderGrid>(viewholder);
            view.setTag(holderReference.get());
        } else {
            viewholder = (ViewHolderGrid) convertView.getTag();
        }

        holderReference.get().mViewHolderLineOne.setText(mLineOneText);
        holderReference.get().mViewHolderLineTwo.setText(mLineTwoText);

        ImageInfo mInfo = new ImageInfo();
        mInfo.type = mGridType;
        mInfo.size = SIZE_THUMB;
        mInfo.source = SRC_FIRST_AVAILABLE;
        mInfo.data = mImageData;
        mImageProvider.loadImage(viewholder.mViewHolderImage, mInfo);

        return view;
    }

    public abstract void setupViewData(Cursor mCursor);
}
