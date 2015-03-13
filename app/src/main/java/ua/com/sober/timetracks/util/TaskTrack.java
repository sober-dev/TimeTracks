package ua.com.sober.timetracks.util;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import ua.com.sober.timetracks.provider.ContractClass;

/**
 * Created by dmitry.hmel on 13.03.2015.
 */
public class TaskTrack {
    private Context context;
    private long taskID;
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

    public void StartTrack() {
        trackStartTime = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID, taskID);
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_START_TIME, trackStartTime);
        trackUri = context.getContentResolver().insert(ContractClass.TaskTracks.CONTENT_URI, cv);
        setStatus(ContentUris.parseId(trackUri));
        Log.w("SQLite", "StartTrack, result Uri : " + trackUri.toString());
    }

    public void StopTrack() {
        trackStopTime = System.currentTimeMillis();
        trackUri = ContentUris.withAppendedId(ContractClass.TaskTracks.CONTENT_URI, status);
        ContentValues cv = new ContentValues();
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME, trackStopTime);
        context.getContentResolver().update(trackUri, cv, null, null);
        setStatus(0);
        Log.w("SQLite", "StopTrack, result Uri : " + trackUri.toString());
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
