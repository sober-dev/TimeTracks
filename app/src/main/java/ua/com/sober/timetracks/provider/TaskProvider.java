package ua.com.sober.timetracks.provider;

import android.annotation.TargetApi;
import android.content.*;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import java.util.HashMap;

/**
 * Created by dmitry.hmel on 12.03.2015.
 */
public class TaskProvider extends ContentProvider {

    private static final int DATABASE_VERSION = 1;
    private static HashMap<String, String> sTaskProjectionMap;
    private static HashMap<String, String> sTaskTracksProjectionMap;
    private static final int TASKS = 1;
    private static final int TASKS_ID = 2;
    private static final int TASK_TRACKS = 3;
    private static final int TASK_TRACKS_ID = 4;
    private static final UriMatcher sUriMatcher;
    private DatabaseHelper dbHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ContractClass.AUTHORITY, "tasks", TASKS);
        sUriMatcher.addURI(ContractClass.AUTHORITY, "tasks/#", TASKS_ID);
        sUriMatcher.addURI(ContractClass.AUTHORITY, "task_tracks", TASK_TRACKS);
        sUriMatcher.addURI(ContractClass.AUTHORITY, "task_tracks/#", TASK_TRACKS_ID);
        sTaskProjectionMap = new HashMap<String, String>();
        for(int i=0; i < ContractClass.Tasks.DEFAULT_PROJECTION.length; i++) {
            sTaskProjectionMap.put(
                    ContractClass.Tasks.DEFAULT_PROJECTION[i],
                    ContractClass.Tasks.DEFAULT_PROJECTION[i]);
        }
        sTaskTracksProjectionMap = new HashMap<String, String>();
        for(int i=0; i < ContractClass.TaskTracks.DEFAULT_PROJECTION.length; i++) {
            sTaskTracksProjectionMap.put(
                    ContractClass.TaskTracks.DEFAULT_PROJECTION[i],
                    ContractClass.TaskTracks.DEFAULT_PROJECTION[i]);
        }
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = null;
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                qb.setTables(ContractClass.Tasks.TABLE_NAME);
                qb.setProjectionMap(sTaskProjectionMap);
                orderBy = ContractClass.Tasks.DEFAULT_SORT_ORDER;
                break;
            case TASKS_ID:
                qb.setTables(ContractClass.Tasks.TABLE_NAME);
                qb.setProjectionMap(sTaskProjectionMap);
                qb.appendWhere(ContractClass.Tasks._ID + "=" + uri.getPathSegments().get(ContractClass.Tasks.TASKS_ID_PATH_POSITION));
                orderBy = ContractClass.Tasks.DEFAULT_SORT_ORDER;
                break;
            case TASK_TRACKS:
                qb.setTables(ContractClass.TaskTracks.TABLE_NAME);
                qb.setProjectionMap(sTaskTracksProjectionMap);
                orderBy = ContractClass.TaskTracks.DEFAULT_SORT_ORDER;
                break;
            case TASK_TRACKS_ID:
                qb.setTables(ContractClass.TaskTracks.TABLE_NAME);
                qb.setProjectionMap(sTaskTracksProjectionMap);
                qb.appendWhere(ContractClass.TaskTracks._ID + "=" + uri.getPathSegments().get(ContractClass.TaskTracks.TASK_TRACKS_ID_PATH_POSITION));
                orderBy = ContractClass.TaskTracks.DEFAULT_SORT_ORDER;
                break;
            default:
//                Log.w("test", "Error!!!");
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c;
        if (sortOrder != null) {
            c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        } else {
            c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);
        }
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                return ContractClass.Tasks.CONTENT_TYPE;
            case TASKS_ID:
                return ContractClass.Tasks.CONTENT_ITEM_TYPE;
            case TASK_TRACKS:
                return ContractClass.TaskTracks.CONTENT_TYPE;
            case TASK_TRACKS_ID:
                return ContractClass.TaskTracks.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != TASKS && sUriMatcher.match(uri) != TASK_TRACKS) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        }
        else {
            values = new ContentValues();
        }
        long rowId = -1;
        Uri rowUri = Uri.EMPTY;
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                if (values.containsKey(ContractClass.Tasks.COLUMN_NAME_TASK_NAME) == false) {
                    values.put(ContractClass.Tasks.COLUMN_NAME_TASK_NAME, "");
                }
                if (values.containsKey(ContractClass.Tasks.COLUMN_NAME_STATUS) == false) {
                    values.put(ContractClass.Tasks.COLUMN_NAME_STATUS, 0);
                }
                if (values.containsKey(ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME) == false) {
                    values.put(ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME, 0);
                }
                rowId = db.insert(ContractClass.Tasks.TABLE_NAME,
                        null,
                        values);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(ContractClass.Tasks.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
            case TASK_TRACKS:
                if (values.containsKey(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID) == false) {
                    values.put(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID, -1);
                }
                if (values.containsKey(ContractClass.TaskTracks.COLUMN_NAME_START_TIME) == false) {
                    values.put(ContractClass.TaskTracks.COLUMN_NAME_START_TIME, 0);
                }
                if (values.containsKey(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME) == false) {
                    values.put(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME, 0);
                }
                rowId = db.insert(ContractClass.TaskTracks.TABLE_NAME,
                        null,
                        values);
                if (rowId > 0) {
                    rowUri = ContentUris.withAppendedId(ContractClass.TaskTracks.CONTENT_ID_URI_BASE, rowId);
                    getContext().getContentResolver().notifyChange(rowUri, null);
                }
                break;
        }
        return rowUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String finalWhere;
        int count;
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                count = db.delete(ContractClass.Tasks.TABLE_NAME,selection,selectionArgs);
                break;
            case TASKS_ID:
                finalWhere = ContractClass.Tasks._ID + " = " + uri.getPathSegments().get(ContractClass.Tasks.TASKS_ID_PATH_POSITION);
                if (selection != null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.delete(ContractClass.Tasks.TABLE_NAME,finalWhere,selectionArgs);
                break;
            case TASK_TRACKS:
                count = db.delete(ContractClass.TaskTracks.TABLE_NAME,selection,selectionArgs);
                break;
            case TASK_TRACKS_ID:
                finalWhere = ContractClass.TaskTracks._ID + " = " + uri.getPathSegments().get(ContractClass.TaskTracks.TASK_TRACKS_ID_PATH_POSITION);
                if (selection != null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.delete(ContractClass.TaskTracks.TABLE_NAME,finalWhere,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        String finalWhere;
        String id;
        switch (sUriMatcher.match(uri)) {
            case TASKS:
                count = db.update(ContractClass.Tasks.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TASKS_ID:
                id = uri.getPathSegments().get(ContractClass.Tasks.TASKS_ID_PATH_POSITION);
                finalWhere = ContractClass.Tasks._ID + " = " + id;
                if (selection !=null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.update(ContractClass.Tasks.TABLE_NAME, values, finalWhere, selectionArgs);
                break;
            case TASK_TRACKS:
                count = db.update(ContractClass.TaskTracks.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TASK_TRACKS_ID:
                id = uri.getPathSegments().get(ContractClass.TaskTracks.TASK_TRACKS_ID_PATH_POSITION);
                finalWhere = ContractClass.TaskTracks._ID + " = " + id;
                if (selection !=null) {
                    finalWhere = finalWhere + " AND " + selection;
                }
                count = db.update(ContractClass.TaskTracks.TABLE_NAME, values, finalWhere, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

        private static final String DATABASE_NAME = "timetracks.db";

        public static final String DATABASE_TASKS_TABLE = "tasks";
        public static final String TASK_NAME_COLUMN = "task_name";
        public static final String TOTAL_TIME_COLUMN = "total_time";
        public static final String STATUS_COLUMN = "status";

        public static final String DATABASE_TASK_TRACKS_TABLE = "task_tracks";
        public static final String TASK_ID_COLUMN = "task_id";
        public static final String START_TIME_COLUMN = "start_time";
        public static final String STOP_TIME_COLUMN = "stop_time";
        public static final String TASK_ID_INDEX = "task_id_index";

        private static final String DATABASE_CREATE_TASK_TABLE_SCRIPT =
                "create table " + DATABASE_TASKS_TABLE + " ("
                        + BaseColumns._ID + " integer primary key autoincrement, "
                        + TASK_NAME_COLUMN + " text not null, "
                        + STATUS_COLUMN + " integer, "
                        + TOTAL_TIME_COLUMN + " integer, unique(" + TASK_NAME_COLUMN + ") on conflict replace);";

        private static final String DATABASE_CREATE_TASK_TRACKS_TABLE_SCRIPT =
                "create table " + DATABASE_TASK_TRACKS_TABLE + " ("
                        + BaseColumns._ID + " integer primary key autoincrement, "
                        + TASK_ID_COLUMN + " integer not null, "
                        + START_TIME_COLUMN + " integer, "
                        + STOP_TIME_COLUMN + " integer, "
                        + "foreign key (" + TASK_ID_COLUMN + ") references " + DATABASE_TASKS_TABLE + "(" + BaseColumns._ID + ") " +"on delete cascade);";

        private static final String DATABASE_CREATE_TASK_ID_INDEX_SCRIPT =
                "create index " + TASK_ID_INDEX + " on " + DATABASE_TASK_TRACKS_TABLE + "(" + TASK_ID_COLUMN + ");";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
            super(context, name, factory, version, errorHandler);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
//            Log.w("SQLite", "Script: " + DATABASE_CREATE_TASK_TABLE_SCRIPT);
            db.execSQL(DATABASE_CREATE_TASK_TABLE_SCRIPT);
//            Log.w("SQLite", "Script: " + DATABASE_CREATE_TASK_TRACKS_TABLE_SCRIPT);
            db.execSQL(DATABASE_CREATE_TASK_TRACKS_TABLE_SCRIPT);
//            Log.w("SQLite", "Script: " + DATABASE_CREATE_TASK_ID_INDEX_SCRIPT);
            db.execSQL(DATABASE_CREATE_TASK_ID_INDEX_SCRIPT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            Log.w("SQLite", "Updated with version " + oldVersion + " to version " + newVersion);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TASKS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TASK_TRACKS_TABLE);
            onCreate(db);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onConfigure(SQLiteDatabase db) {
            super.onConfigure(db);
            db.setForeignKeyConstraintsEnabled(true);
        }

//        @Override
//        public void onOpen(SQLiteDatabase db) {
//            super.onOpen(db);
//            db.execSQL("PRAGMA foreign_keys = ON;");
//        }

    }

}