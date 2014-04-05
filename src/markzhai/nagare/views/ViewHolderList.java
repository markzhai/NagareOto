package markzhai.nagare.views;

import markzhai.nagare.R;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class ViewHolderList {
    public final ImageView mViewHolderImage, mQuickContextDivider, mQuickContextTip;
    public final TextView mViewHolderLineOne;
    public final TextView mViewHolderLineTwo;
    public final FrameLayout mQuickContext;

    public ViewHolderList(View view) {
        mViewHolderImage = (ImageView) view.findViewById(R.id.listview_item_image);
        mViewHolderLineOne = (TextView) view.findViewById(R.id.listview_item_line_one);
        mViewHolderLineTwo = (TextView) view.findViewById(R.id.listview_item_line_two);
        mQuickContext = (FrameLayout) view.findViewById(R.id.track_list_context_frame);
        mQuickContextDivider = (ImageView) view.findViewById(R.id.quick_context_line);
        mQuickContextTip = (ImageView) view.findViewById(R.id.quick_context_tip);
    }
}