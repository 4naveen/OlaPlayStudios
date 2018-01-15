package naveen.project.com.olaplaystudios;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

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
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.webianks.library.easyportfolio.EasyPortfolio;
import com.webianks.library.easyportfolio.Project;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import naveen.project.com.olaplaystudios.adapter.ListAdapter;
import naveen.project.com.olaplaystudios.utils.AppUrls;
import naveen.project.com.olaplaystudios.utils.RecyclerViewClickListener;

import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HomeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Album>>,RecyclerViewClickListener,NavigationView.OnNavigationItemSelectedListener {
    RecyclerView recyclerView;
    ArrayList<Album> albumArrayList;
    private static final int LOADER_ID = 111;
    private static final int RESTART_LOADER_ID = 222;
    ProgressDialog dialog;
    MaterialSearchView searchView;
    ListAdapter listAdapter;
    SwipeRefreshLayout refreshLayout;
    LinearLayoutManager layoutManager;
    FrameLayout layout;
    private ExoPlayer player;
    private int resumeWindow;
    private long resumeSeekbarPosition;

    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private static String file_path;
    NavigationView navigationView;
    DrawerLayout drawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        albumArrayList = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchView=(MaterialSearchView)findViewById(R.id.search_view);
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
            actionBar.setTitle(R.string.app_name);

        }
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.inflateHeaderView(R.layout.nav_header_home);
        refreshLayout=(SwipeRefreshLayout)findViewById(R.id.swr);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(HomeActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        listAdapter=new ListAdapter(this,albumArrayList,this);

        if (savedInstanceState != null) {
            albumArrayList = savedInstanceState.getParcelableArrayList("albumArrayList");
            recyclerView.setAdapter(new ListAdapter(HomeActivity.this, albumArrayList,this));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }
        else {
            ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                getSupportLoaderManager().initLoader(LOADER_ID, null,this).forceLoad();
            }
            else {
                final Snackbar snackbar = Snackbar.make(layout, "please check your internet connectivity!", Snackbar.LENGTH_LONG);
                View v = snackbar.getView();
                v.setMinimumWidth(1000);
                TextView tv = (TextView) v.findViewById(android.support.design.R.id.snackbar_text);
                tv.setTextColor(Color.WHITE);
                snackbar.show();
            }
        }

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);

                int topRowVerticalPosition =
                        (recyclerView == null || recyclerView.getChildCount() == 0) ? 0 : recyclerView.getChildAt(0).getTop();
                refreshLayout.setEnabled(topRowVerticalPosition >= 0);

            }
        });
        refreshLayout.setColorSchemeColors(Color.RED,Color.GREEN,Color.BLUE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(false);
                        //setActorAdapterData2();
                        getSupportLoaderManager().restartLoader(RESTART_LOADER_ID,null,HomeActivity.this);
                        if (albumArrayList!=null){
                            updateUI(albumArrayList);
                            recyclerView.setAdapter(listAdapter);
                        }


                    }
                },2000);
            }
        });
    }
    private void updateUI(ArrayList<Album> data) {
        listAdapter.setItemData(data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        menu.findItem(R.id.action_search).setVisible(true);
        searchView.setMenuItem(menuItem);
        // searchView.showSearch();
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Album> subalbumArrayList = new ArrayList<>();
                for (int i = 0; i < albumArrayList.size(); i++) {
                    if (StringUtils.containsIgnoreCase(albumArrayList.get(i).getSong_name(),newText)) {
                        subalbumArrayList.add(albumArrayList.get(i));
                    }
                }
                recyclerView.setAdapter(new ListAdapter(HomeActivity.this,subalbumArrayList));
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public Loader<ArrayList<Album>> onCreateLoader(int id, Bundle args) {
        if (id==RESTART_LOADER_ID){
          //do nothing
        }
        if (id==LOADER_ID){
            dialog = new ProgressDialog(this);
            dialog.setMessage(getResources().getString(R.string.loading_msg));
            dialog.setTitle(getResources().getString(R.string.connection_msg));
            dialog.show();
            dialog.setCancelable(true);
        }

        return new SongLoader(this, AppUrls.ALBUM_LIST_URL);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Album>> loader, ArrayList<Album> data) {
        Log.e("datalist size",String.valueOf(data.size()));

        albumArrayList=data;
        listAdapter=new ListAdapter(HomeActivity.this, albumArrayList,this);
        recyclerView.setAdapter(listAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if (dialog.isShowing()){
            dialog.dismiss();

        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Album>> loader) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable("list_state", layoutManager.onSaveInstanceState());
        outState.putParcelableArrayList("albumArrayList",albumArrayList);
        // outState.putIntArray("scroll_position",new int[]{recyclerView.getScrollX(),recyclerView.getScrollY()});
        super.onSaveInstanceState(outState);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("destroy","onDestroy()...");
        player.release();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            player.release();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if (player!=null){
            player.release();
        }
        finish();

    }

    @Override
    public void recyclerViewListClicked(View v, int position,boolean checked) {
        listAdapter=new ListAdapter(HomeActivity.this,albumArrayList,this);
        Log.e("position",String.valueOf(position));
       // check=false;
        Album album=albumArrayList.get(position);
        if (v.getId()==R.id.togglebutton){
            if (checked==true){
                Log.e("song url",album.getDownload_song_url());
                initPlayer(album.getDownload_song_url());
            }
          else {
                player.stop();
                player.release();

            }
        }
        if (v.getId()==R.id.download){
            file_path=album.getDownload_song_url();
            if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }

        }
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
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(HomeActivity.this, Util.getUserAgent(HomeActivity.this, "BakingApp"), bandwidthMeterA);
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationView.getMenu().findItem(item.getItemId()).setChecked(true);
        switch (item.getItemId()) {
            case R.id.home: {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            }
            case R.id.history: {
                startActivity(new Intent(this,HistoryActivity.class));
                break;
            }
            case R.id.playlist: {
                 startActivity(new Intent(this,PlayListActivity.class));
                break;
            }
            case R.id.portfolio: {
                List<Project> projectList = new ArrayList<>();

                Project project1 = new Project();
                project1.setProjectName("LCRM(Product)");
                project1.setProjectDesc("Lcrm is complete functional crm and Sales System ..Lcrm concentrates on the needs of the existing customers and attract new customers with its impressive feature .this products allow you to manage leads,opportunities ,sales team target ,invoices and more to increase one's company productivity.");
                project1.setProjectLink("https://play.google.com/store/apps/details?id=com.project.lorvent.lcrm");

                Project project2 = new Project();
                project2.setProjectName("Way2Freshers(Product)");
                project2.setProjectDesc("It helps you to find jobs with your matching skills and location and also helps to make preparation for varoius written and intervies exams and campus placements and  gives info about varoius Banks loans and schlorships provided by many government and private oraganisation");
                project2.setProjectLink("https://play.google.com/store/apps/details?id=com.project.lorvent.way2freshers");

                Project project3 = new Project();
                project3.setProjectName("BridgeCall(Product)");
                project3.setProjectDesc("This app basically used for calling purpose of international communication. To communicate with international contacts we must have bridge numbers of respected countries, with these bridge and contact numbers we can communicate with international numbers. So if you have the bridge numbers of different countries with you, you can add them in the bridge numbers list in the app and after you can select the required calling contact number");
                project3.setProjectLink("https://play.google.com/store/apps/details?id=com.project.lorvent.bridgecall");

                projectList.add(project1);
                projectList.add(project2);
                projectList.add(project3);


                new EasyPortfolio.Builder(this)
                        .withGithubUrl("https://github.com/4naveen")
                        .withPlayStoreUrl("https://play.google.com/store/apps/details?id=com.project.lorvent.lcrm")
                        .withLinkedInUrl("https://www.linkedin.com/in/naveen-kumar-27581692/")
                        .withProjectList(projectList)
                        .build()
                        .start();
                break;
            }
        }
        return false;
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
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            resumeSeekbarPosition = savedInstanceState.getLong("resumeSeekbarPosition", C.TIME_UNSET);
            player.seekTo(resumeSeekbarPosition);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                boolean courseLocationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (courseLocationAccepted) {
                    new DownloadFileFromURL(this).execute(file_path);

                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(INTERNET)) {
                            showMessageOKCancel("You need to allow access the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }
    }
    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener)
    {
        new AlertDialog.Builder(HomeActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    @Override
    protected void onResume() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        }

        super.onResume();
    }


}
