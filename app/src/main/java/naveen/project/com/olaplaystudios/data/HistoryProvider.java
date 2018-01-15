package naveen.project.com.olaplaystudios.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class HistoryProvider extends ContentProvider {
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private HistoryDbHelper mOpenHelper;
    private static final int FAVORITE = 1;
    private  static final int FAVORITE_ID = 2;

   /* static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, "favorite", FAVORITE);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, "favorite*//*", FAVORITE_ID);
    }*/
    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = HistoryContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, HistoryContract.HistoryEntry.TABLE_NAME, FAVORITE);
        matcher.addURI(authority, HistoryContract.HistoryEntry.TABLE_NAME + "/#", FAVORITE_ID);

        return matcher;
    }



    @Override
    public boolean onCreate() {
        mOpenHelper = new HistoryDbHelper(getContext());
        return true;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE: {
                long _id = db.insert(HistoryContract.HistoryEntry.TABLE_NAME, null, values);
                // insert unless it is already contained in the database
                if (_id > 0) {
                    Log.i("inserted","successfully");
                    returnUri = HistoryContract.HistoryEntry.buildFavouriteUri(_id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            case FAVORITE:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }

            case FAVORITE_ID:
            {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        HistoryContract.HistoryEntry.TABLE_NAME,
                        projection,
                        HistoryContract.HistoryEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

         }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match){
            case FAVORITE:
                numDeleted = db.delete(
                        HistoryContract.HistoryEntry.TABLE_NAME, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        HistoryContract.HistoryEntry.TABLE_NAME + "'");
                break;

            case FAVORITE_ID:

                numDeleted = db.delete(HistoryContract.HistoryEntry.TABLE_NAME,
                        HistoryContract.HistoryEntry.COLUMN_SONG_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        HistoryContract.HistoryEntry.TABLE_NAME + "'");
                Log.i("deleted","successfully");

                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }


        return numDeleted;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }



    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            /**
             * Get all list of favorite
             */
            case FAVORITE:
                return HistoryContract.HistoryEntry.CONTENT_TYPE;

            /**
             * Get a particular movie
             */
            case FAVORITE_ID:
                return HistoryContract.HistoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


}
