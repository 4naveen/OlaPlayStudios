package naveen.project.com.olaplaystudios.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import naveen.project.com.olaplaystudios.data.HistoryContract.HistoryEntry;

public class HistoryDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "history.db";

    public HistoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_HISTORY_TABLE = "CREATE TABLE " + HistoryEntry.TABLE_NAME + " (" +
                HistoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                HistoryEntry.COLUMN_SONG_ID + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_DOWNLOAD_PATH + " TEXT NOT NULL, " +
                HistoryEntry.COLUMN_SONG_NAME + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_HISTORY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + HistoryEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
