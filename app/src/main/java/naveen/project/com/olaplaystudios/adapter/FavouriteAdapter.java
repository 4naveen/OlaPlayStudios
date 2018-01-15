package naveen.project.com.olaplaystudios.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import naveen.project.com.olaplaystudios.R;
import naveen.project.com.olaplaystudios.data.FavouriteContract;

/**
 * Created by sam_chordas on 7/23/15.
 */
public class FavouriteAdapter extends CursorAdapter {

    private static final String LOG_TAG = FavouriteAdapter.class.getSimpleName();
    private Context mContext;
    private static int sLoaderID;
    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView textView;
        ToggleButton toggleButton;
        public ViewHolder(View view){
            imageView = (ImageView) view.findViewById(R.id.thumbnail);
           textView = (TextView) view.findViewById(R.id.title);
            //toggleButton=(ToggleButton)view.findViewById(R.id.togglebutton);
        }
    }

    public FavouriteAdapter(Context context, Cursor c, int flags, int loaderID){
        super(context, c, flags);
        Log.d(LOG_TAG, "FlavAdapter");
        mContext = context;
        sLoaderID = loaderID;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        int layoutId = R.layout.indi_view;

        Log.d(LOG_TAG, "In new View");

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(final View view, Context context, final Cursor cursor){
        ViewHolder viewHolder = (ViewHolder) view.getTag();


        viewHolder.textView.setText(cursor.getString(cursor.getColumnIndex(FavouriteContract.FavoriteEntry.COLUMN_SONG_NAME)));
        String image_url = cursor.getString(cursor.getColumnIndex(FavouriteContract.FavoriteEntry.COLUMN_POSTER_PATH));
        Glide.with(mContext).load(image_url)
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.olaplay_logo)
                .into(viewHolder.imageView);
    }

}
