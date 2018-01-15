package naveen.project.com.olaplaystudios;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import naveen.project.com.olaplaystudios.adapter.FavouriteAdapter;
import naveen.project.com.olaplaystudios.data.FavouriteContract;
import naveen.project.com.olaplaystudios.data.HistoryContract;

public class HistoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int CURSOR_LOADER_ID = 0;
    private FavouriteAdapter historyAdapter;
    private GridView mGridView;
    ArrayList<Album> albumArrayList;
    private ExoPlayer player;
    private int resumeWindow;
    private long resumeSeekbarPosition;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    boolean favourite,isFavorite;
    String download_url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Recent History");

        }

        getSupportLoaderManager().initLoader(CURSOR_LOADER_ID, null,this);
        // initialize our FlavorAdapter
        historyAdapter = new FavouriteAdapter(this, null, 0, CURSOR_LOADER_ID);
        // initialize mGridView to the GridView in fragment_main.xml
        mGridView = (GridView) findViewById(R.id.flavors_grid);
        // set mGridView adapter to our CursorAdapter
        mGridView.setAdapter(historyAdapter);
        favourite=false;
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int uriId = position + 1;
                Uri uri = ContentUris.withAppendedId(FavouriteContract.FavoriteEntry
                                .CONTENT_URI,
                        uriId);
                final Cursor cursor =getContentResolver().query(uri,
                        new String[]{FavouriteContract.FavoriteEntry.COLUMN_SONG_ID,FavouriteContract.FavoriteEntry.COLUMN_DOWNLOAD_PATH},
                        null,
                        new String[]{String.valueOf(position+1)},
                        null);
                cursor.moveToFirst();
                // Log.i("imageid",cursor.getString(cursor.getColumnIndex("movie_id")));
                final ImageView imageView=(ImageView)view.findViewById(R.id.play);
                download_url=cursor.getString(cursor.getColumnIndex("song_path"));
                Log.e("url",download_url);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Log.e("view ","clicked");
                        // favourite=true;
                        if (favourite) {
                            favourite = false;
                            imageView.setImageResource(R.drawable.ic_play_circle_outline_white_24dp);
                            Log.e("fav ",String.valueOf(favourite));
                            Log.e("url",download_url);
                            initPlayer(download_url);
                        } else {
                            favourite = true;

                            imageView.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp);
                            Log.e("fav ",String.valueOf(favourite));
                            if (player!=null){
                                player.stop();
                                player.release();
                            }
                        }
                    }
                });


            }
        });
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                HistoryContract.HistoryEntry.CONTENT_URI,
                null,
                null,
                null,
                null);    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        historyAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        historyAdapter.swapCursor(null);
    }


    private void initializeMediaSession() {

        // Create a MediaSessionCompat.
        mMediaSession = new MediaSessionCompat(this, "session");

        // Enable callbacks from MediaButtons and TransportControls.
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        // Do not let MediaButtons restart the player when the app is not visible.
        mMediaSession.setMediaButtonReceiver(null);

        // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());
        // MySessionCallback has methods that handle callbacks from a media controller.
        mMediaSession.setCallback(new MySessionCallback());

        // Start the Media Session since the activity is active.
        mMediaSession.setActive(true);

    }
    private  void initPlayer(String url)
    {
        Handler mainHandler = new Handler();
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        Uri mp4VideoUri =Uri.parse(url);
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(HistoryActivity.this, Util.getUserAgent(HistoryActivity.this, "BakingApp"), bandwidthMeterA);
        String userAgent = Util.getUserAgent(this,getResources().getString(R.string.app_name));

// Default parameters, except allowCrossProtocolRedirects is true
    /*    DefaultHttpDataSourceFactory httpDataSourceFactory = new DefaultHttpDataSourceFactory(
                userAgent,
                null *//* listener *//*,
                DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                true *//* allowCrossProtocolRedirects *//*
        );

        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                HomeActivity.this,
                null *//* listener *//*,
                httpDataSourceFactory
        );*/

        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(mp4VideoUri, dataSourceFactory, extractorsFactory, null, null);
        initializeMediaSession();

        //final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);
        boolean haveResumePosition = resumeWindow != C.INDEX_UNSET;

        if (haveResumePosition) {
            player.seekTo(resumeWindow, resumeSeekbarPosition);
            player.prepare(videoSource,!haveResumePosition,false);
            player.setPlayWhenReady(true);
        }
        else {
            player.prepare(videoSource);
            player.setPlayWhenReady(true);
        }

    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            player.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            player.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            player.seekTo(0);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
