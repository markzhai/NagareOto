package mark.zhai.nagareoto.activity;

import java.util.Locale;

import mark.zhai.nagareoto.Constants;
import mark.zhai.nagareoto.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends FragmentActivity implements OnItemClickListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the sections.
     * We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will keep every loaded
     * fragment in memory. If this becomes too memory intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);

        // Create the adapter that will return a fragment for each of the three primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        

        // Watch for button clicks.
        Button button = (Button)findViewById(R.id.goto_first);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        button = (Button)findViewById(R.id.goto_last);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mViewPager.setCurrentItem(Constants.NUM_ITEMS-1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.albums, menu);
        return true;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return SectionListFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return Constants.NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
            case 0:
                return getString(R.string.section_album).toUpperCase(l);
            case 1:
                return getString(R.string.section_artist).toUpperCase(l);
            case 2:
                return getString(R.string.section_recent).toUpperCase(l);
            case 3:
                return getString(R.string.section_genre).toUpperCase(l);
            case 4:
                return getString(R.string.section_playlist).toUpperCase(l);
            }
            return null;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub

    }
}
