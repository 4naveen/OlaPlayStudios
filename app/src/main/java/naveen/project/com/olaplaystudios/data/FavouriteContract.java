package naveen.project.com.olaplaystudios.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class FavouriteContract {


    public static final String CONTENT_AUTHORITY = "naveen.project.com.olaplaystudios.data.FavoriteProvider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    public static final class FavoriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favorite";

        // Table columns
        public static final String COLUMN_SONG_ID = "song_id";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_DOWNLOAD_PATH = "song_path";
        public static final String COLUMN_SONG_NAME = "song_name";

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + TABLE_NAME;

        public static Uri buildFavouriteUri(long movie_id) {
            return ContentUris.withAppendedId(CONTENT_URI,movie_id);
        }



    }

}
