package markzhai.nagare.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import markzhai.nagare.R;
import markzhai.nagare.Constants;
import markzhai.nagare.fragment.AlbumsFragment;
import markzhai.nagare.fragment.RefreshableFragment;
import markzhai.nagare.fragment.SectionListFragment;
import markzhai.nagare.service.NagareService;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MusicLibrary extends FragmentActivity implements ServiceConnection {
    
    private static final String TAG = "NagareMusicLibrary";
    
    private ViewPager mViewPager;
    private TabsAdapter mTabsAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Scan for music
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        // Control Media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.pager);
        setContentView(mViewPager);

        final ActionBar bar = getActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayOptions(0, ActionBar.DISPLAY_SHOW_TITLE);

        mTabsAdapter = new TabsAdapter(this, mViewPager);
        mTabsAdapter.addTab(bar.newTab().setText("Simple"), AlbumsFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText("List"), AlbumsFragment.class, null);
        mTabsAdapter.addTab(bar.newTab().setText("Cursor"), AlbumsFragment.class, null);

        if (savedInstanceState != null) {
            bar.setSelectedNavigationItem(savedInstanceState.getInt("tab", 0));
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // MusicUtils.mService = IApolloService.Stub.asInterface(obj);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        // MusicUtils.mService = null;
    }

    @Override
    protected void onStart() {

        // Bind to Service
        // mToken = MusicUtils.bindToService(this, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(NagareService.META_CHANGED);
        super.onStart();
    }

    @Override
    protected void onStop() {
        // Unbind
        // if (MusicUtils.mService != null)
        // MusicUtils.unbindFromService(mToken);

        // TODO: clear image cache

        super.onStop();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(
                getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /**
     * Initiate the Top Actionbar
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_top, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tab", getActionBar().getSelectedNavigationIndex());
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes in tabs, and takes care of
     * switch to the correct paged in the ViewPager whenever the selected tab
     * changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener,
            ViewPager.OnPageChangeListener {
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

        public TabsAdapter(FragmentActivity activity, ViewPager pager) {
            super(activity.getSupportFragmentManager());
            mContext = activity;
            mActionBar = activity.getActionBar();
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(), info.args);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            // TODO Auto-generated method stub
            
        }
    }
}