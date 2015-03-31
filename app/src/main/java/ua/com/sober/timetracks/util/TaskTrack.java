package ua.com.sober.timetracks.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import ua.com.sober.timetracks.provider.ContractClass;

import static ua.com.sober.timetracks.provider.ContractClass.TaskTracks.CONTENT_URI;

/**
 * Created by dmitry.hmel on 13.03.2015.
 */
public class TaskTrack {
    private Context context;
    private long taskID;
    private long trackID;
    private long status;
    private long trackStartTime;
    private long trackStopTime;
    private long totalTime;
    private Uri trackUri;

    public TaskTrack(Context context, long taskID, long status, long totalTime) {
        this.context = context;
        this.taskID = taskID;
        this.status = status;
        this.totalTime = totalTime;
    }

    public void startTrack() {
        stopPreviosTrack();
        trackStartTime = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID, taskID);
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_START_TIME, trackStartTime);
        trackUri = context.getContentResolver().insert(CONTENT_URI, cv);
        trackID = ContentUris.parseId(trackUri);
        setStatus(trackID);
        Log.w("SQLite", "startTrack, result Uri : " + trackUri.toString());
    }

    private void stopPreviosTrack() {
        long trackID;
        long taskID;
        long stopTime;
        Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        trackID = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks._ID));
        taskID = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID));
        stopTime = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME));
        cursor.close();
        if(stopTime == 0) {
            
        }
        Log.w("SQLite","trackID: " + trackID + ", taskID: " + taskID + ", stopTime: " + stopTime);

    }

    public void stopTrack() {
        trackStopTime = System.currentTimeMillis();
        trackUri = ContentUris.withAppendedId(CONTENT_URI, status);
        ContentValues cv = new ContentValues();
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME, trackStopTime);
        context.getContentResolver().update(trackUri, cv, null, null);
        setStatus(0);
        Log.w("SQLite", "stopTrack, result Uri : " + trackUri.toString());
    }

    private void setStatus(long status) {
        Uri taskUri = ContentUris.withAppendedId(ContractClass.Tasks.CONTENT_URI, taskID);
        ContentValues cv = new ContentValues();
        cv.put(ContractClass.Tasks.COLUMN_NAME_STATUS, status);
        if (status == 0) {
            totalTime = totalTime + trackStopTime;
            cv.put(ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME, totalTime);
        } else {
            totalTime = totalTime - trackStartTime;
            cv.put(ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME, totalTime);
        }
        context.getContentResolver().update(taskUri, cv, null, null);
    }

}
