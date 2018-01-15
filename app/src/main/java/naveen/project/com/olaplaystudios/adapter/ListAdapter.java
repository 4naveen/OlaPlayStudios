package naveen.project.com.olaplaystudios.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import naveen.project.com.olaplaystudios.Album;
import naveen.project.com.olaplaystudios.R;
import naveen.project.com.olaplaystudios.data.FavouriteContract;
import naveen.project.com.olaplaystudios.data.HistoryContract;
import naveen.project.com.olaplaystudios.utils.RecyclerViewClickListener;


public class ListAdapter extends RecyclerView.Adapter<ListAdapter.SimpleViewHolder> {
    private SimpleViewHolder svHolder;
    public ArrayList<Album> albumArrayList;
    private Context mContext;
    private static RecyclerViewClickListener itemListener;

    public ListAdapter(Context context, ArrayList<Album> albumArrayList) {
        this.mContext = context;
        this.albumArrayList = albumArrayList;
    }
    public ListAdapter(Context context, ArrayList<Album> albumArrayList,RecyclerViewClickListener itemListener) {
        this.mContext = context;
        this.albumArrayList = albumArrayList;
        this.itemListener = itemListener;

    }
    @Override
    public ListAdapter.SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.indi_view_row, parent, false);
        return new SimpleViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final ListAdapter.SimpleViewHolder viewHolder, final int position) {
        Album album = albumArrayList.get(position);
        svHolder = viewHolder;
        viewHolder.song_name.setText(album.getSong_name());
        viewHolder.artists.setText(album.getArtists());
        Log.e("cover image url",album.getCover_image());
        if (isMovieFavorite(String.valueOf(position))){
            viewHolder.toggleButton1.setBackground(mContext.getDrawable(R.drawable.checkon));
        }
        else {
            viewHolder.toggleButton1.setBackground(mContext.getDrawable(R.drawable.checkoff));

        }
        Glide.with(mContext).load(album.getCover_image())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.olaplay_logo)
                .into(viewHolder.poster);

    }
    @Override
    public int getItemCount() {
        return albumArrayList.size();
    }
    public void setItemData(ArrayList<Album> itemList) {
        Log.v("my_tag", "setMovieData called with size: " + itemList.size());
        albumArrayList = itemList;
        notifyDataSetChanged();
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView song_name,artists;
        ImageView poster,play,pause,download;
        ToggleButton toggleButton,toggleButton1;
        SimpleViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.poster);
            song_name = (TextView) itemView.findViewById(R.id.title);
            artists = (TextView) itemView.findViewById(R.id.artists);
           // play = (ImageView) itemView.findViewById(R.id.play);
            toggleButton=(ToggleButton)itemView.findViewById(R.id.togglebutton);
            toggleButton1=(ToggleButton)itemView.findViewById(R.id.togglebutton1);
            download = (ImageView) itemView.findViewById(R.id.download);
            itemView.setOnClickListener(this);
            toggleButton.setOnClickListener(this);
            toggleButton1.setOnClickListener(this);

            download.setOnClickListener(this);

        }
        @Override
        public void onClick(View view) {
           // itemListener.recyclerViewListClicked(view,getLayoutPosition(),false);
            if (view.getId()==R.id.togglebutton){
                if (toggleButton.isChecked()){
                    itemListener.recyclerViewListClicked(view,getLayoutPosition(),true);
                    toggleButton.setBackground(mContext.getDrawable(R.mipmap.ic_pause_black_24dp));
                    saveToHistoryDB(getLayoutPosition());
                }
                else {
                    itemListener.recyclerViewListClicked(view,getLayoutPosition(),false);
                    toggleButton.setBackground(mContext.getDrawable(R.mipmap.ic_play_arrow_black_24dp));

                }
            }

            else {
                 itemListener.recyclerViewListClicked(view,getLayoutPosition(),false);

            }
            if (view.getId()==R.id.togglebutton1){
                if (toggleButton1.isChecked()){
                    toggleButton1.setBackground(mContext.getDrawable(R.drawable.checkon));
                    saveToDB(getLayoutPosition());
                }
                else {
                    toggleButton1.setBackground(mContext.getDrawable(R.drawable.checkoff));
                     deleteFromDB(String.valueOf(getLayoutPosition()));
                }
            }
        }
    }
    private void deleteFromDB(String movie_id) {

        mContext.getContentResolver().delete(FavouriteContract.FavoriteEntry.buildFavouriteUri(Integer.parseInt(movie_id)), null, null);

    }
    private void saveToDB(int position) {
        Album album = albumArrayList.get(position);
        ContentValues values = new ContentValues();
        values.put(FavouriteContract.FavoriteEntry.COLUMN_SONG_ID,position);
        values.put(FavouriteContract.FavoriteEntry.COLUMN_POSTER_PATH,album.cover_image);
        values.put(FavouriteContract.FavoriteEntry.COLUMN_DOWNLOAD_PATH,album.download_song_url);
        values.put(FavouriteContract.FavoriteEntry.COLUMN_SONG_NAME,album.getSong_name());
        mContext.getContentResolver().insert(FavouriteContract.FavoriteEntry.CONTENT_URI, values);

    }
    private void saveToHistoryDB(int position) {
        Album album = albumArrayList.get(position);
        ContentValues values = new ContentValues();
        values.put(HistoryContract.HistoryEntry.COLUMN_SONG_ID,position);
        values.put(HistoryContract.HistoryEntry.COLUMN_POSTER_PATH,album.cover_image);
        values.put(HistoryContract.HistoryEntry.COLUMN_DOWNLOAD_PATH,album.download_song_url);
        values.put(HistoryContract.HistoryEntry.COLUMN_SONG_NAME,album.getSong_name());
        mContext.getContentResolver().insert(HistoryContract.HistoryEntry.CONTENT_URI, values);

    }
    private boolean isMovieFavorite(String movie_id) {
        boolean isFavourite;
        Cursor c = mContext.getContentResolver().query(FavouriteContract.FavoriteEntry.buildFavouriteUri(Integer.parseInt(movie_id)), null, null, null, null);

        if (c.getCount() > 0) {
          isFavourite=true;

        } else {
            isFavourite=false;
        }
        return isFavourite;
    }

}