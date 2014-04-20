package markzhai.nagare.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import markzhai.nagare.ui.fragment.AudioPlayerFragment;
import markzhai.nagare.INagareService;
import markzhai.nagare.R;
import markzhai.nagare.ui.fragments.list.TracksFragment;
import markzhai.nagare.helper.utils.NagareUtils;
import markzhai.nagare.helper.utils.MusicUtils;
import markzhai.nagare.preferences.SettingsHolder;
import markzhai.nagare.service.NagareService;
import markzhai.nagare.service.ServiceToken;
import static markzhai.nagare.Constants.INTENT_ADD_TO_PLAYLIST;
import static markzhai.nagare.Constants.INTENT_PLAYLIST_LIST;
import static markzhai.nagare.Constants.MIME_TYPE;
import static markzhai.nagare.Constants.PLAYLIST_QUEUE;

/**
 * @Note This is the "holder" for the @TracksFragment(queue) and @AudioPlayerFragment
 */

public class AudioPlayerHolder extends Activity implements ServiceConnection {

    private ServiceToken mToken;

    // Options
    private static final int FAVORITE = 0;
    private static final int SEARCH = 1;
    private static final int EFFECTS_PANEL = 0;

    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle icicle) {
        // Control Media volume
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Layout
        setContentView(R.layout.audio_player_browser);

        // Set up the colorstrip
        initColorstrip();

        // Set up the ActionBar
        initActionBar();

        // Important!
        initPager();
        super.onCreate(icicle);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // If an activity is requesting access to this activity, and
        // the activity is in the stack, the the fragments may need
        // be refreshed. Update the page adapter
        if (mPagerAdapter != null) {
            mPagerAdapter.refresh();
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder obj) {
        MusicUtils.mService = INagareService.Stub.asInterface(obj);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        MusicUtils.mService = null;
    }

    /**
     * Update the MenuItems in the ActionBar
     */
    private final BroadcastReceiver mMediaStatusReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            invalidateOptionsMenu();
        }

    };

    @Override
    protected void onStart() {
        // Bind to Service
        mToken = MusicUtils.bindToService(this, this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(NagareService.META_CHANGED);
        filter.addAction(NagareService.PLAYSTATE_CHANGED);

        registerReceiver(mMediaStatusReceiver, filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        // Unbind
        if (MusicUtils.mService != null) {
            MusicUtils.unbindFromService(mToken);
            mToken = null;
        }

        unregisterReceiver(mMediaStatusReceiver);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, FAVORITE, 0, R.string.cd_favorite).setShowAsAction(
                MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.add(0, SEARCH, 0, R.string.cd_search).setIcon(R.drawable.apollo_holo_light_search)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.overflow_now_playing, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem favorite = menu.findItem(FAVORITE);
        MenuItem search = menu.findItem(SEARCH);
        if (MusicUtils.mService != null && MusicUtils.getCurrentAudioId() != -1) {
            if (MusicUtils.isFavorite(this, MusicUtils.getCurrentAudioId())) {
                favorite.setIcon(R.drawable.apollo_holo_light_favorite_selected);
            } else {
                favorite.setIcon(R.drawable.apollo_holo_light_favorite_normal);
                // Theme chooser
                ThemeUtils.setActionBarItem(this, favorite, "apollo_favorite_normal");
            }
        }
        // Theme chooser
        ThemeUtils.setActionBarItem(this, search, "apollo_search");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home: {
            Intent intent = new Intent();
            intent.setClass(this, MusicLibrary.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            break;
        }
        case FAVORITE: {
            MusicUtils.toggleFavorite();
            invalidateOptionsMenu();
            break;
        }
        case SEARCH: {
            onSearchRequested();
            break;
        }
        case R.id.add_to_playlist: {
            Intent intent = new Intent(INTENT_ADD_TO_PLAYLIST);
            long[] list = new long[1];
            list[0] = MusicUtils.getCurrentAudioId();
            intent.putExtra(INTENT_PLAYLIST_LIST, list);
            startActivity(intent);
            break;
        }
        case R.id.eq: {
            Intent i = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
            i.putExtra(AudioEffect.EXTRA_AUDIO_SESSION, MusicUtils.getCurrentAudioId());
            startActivityForResult(i, EFFECTS_PANEL);
            break;
        }
        case R.id.play_store: {
            NagareUtils.shopFor(this, MusicUtils.getArtistName());
            break;
        }
        case R.id.share: {
            shareCurrentTrack();
            break;
        }
        case R.id.settings: {
            startActivityForResult(new Intent(this, SettingsHolder.class), 0);
            break;
        }
        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intent);
    }

    private void initActionBar() {
        NagareUtils.showUpTitleOnly(getActionBar());

        // The ActionBar Title and UP ids are hidden.
        int titleId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
        int upId = Resources.getSystem().getIdentifier("up", "id", "android");

        TextView actionBarTitle = (TextView) findViewById(titleId);
        ImageView actionBarUp = (ImageView) findViewById(upId);
    }

    /**
     * @return Share intent
     * @throws RemoteException
     */
    private String shareCurrentTrack() {
        if (MusicUtils.getTrackName() == null || MusicUtils.getArtistName() == null) {

        }

        Intent shareIntent = new Intent();
        String currentTrackMessage = getResources().getString(R.string.now_listening_to) + " "
                + MusicUtils.getTrackName() + " " + getResources().getString(R.string.by) + " "
                + MusicUtils.getArtistName();

        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, currentTrackMessage);

        startActivity(Intent.createChooser(shareIntent,
                getResources().getString(R.string.share_track_using)));
        return currentTrackMessage;
    }

    /**
     * Initiate ViewPager and PagerAdapter
     */
    public void initPager() {
        // Initiate PagerAdapter
        mPagerAdapter = new PagerAdapter(getFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putString(MIME_TYPE, Audio.Playlists.CONTENT_TYPE);
        bundle.putLong(BaseColumns._ID, PLAYLIST_QUEUE);
        mPagerAdapter.addFragment(new TracksFragment(bundle));
        // Artists
        mPagerAdapter.addFragment(new AudioPlayerFragment());

        // Initiate ViewPager
        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setPageMargin(getResources().getInteger(R.integer.viewpager_margin_width));
        mViewPager.setPageMarginDrawable(R.drawable.viewpager_margin);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(1);
    }
}