package ua.com.sober.timetracks.util;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.view.View;

import ua.com.sober.timetracks.MainActivity;
import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.provider.ContractClass;

import static ua.com.sober.timetracks.provider.ContractClass.TaskTracks;
import static ua.com.sober.timetracks.provider.ContractClass.Tasks;

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
    private String taskName;
    private Uri trackUri;
    private Uri taskUri;
    private Cursor cursor;
    private NotificationManager notificationManager;
    private final int NOTIFICATION_ID = 0;

    public TaskTrack(Context context, long taskID, long status, long totalTime, String taskName) {
        this.context = context;
        this.taskID = taskID;
        this.status = status;
        this.totalTime = totalTime;
        this.taskName = taskName;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void startTrack() {
        stopPreviosTrack();
        trackStartTime = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID, taskID);
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_START_TIME, trackStartTime);
        trackUri = context.getContentResolver().insert(TaskTracks.CONTENT_URI, cv);
        trackID = ContentUris.parseId(trackUri);
        setStatus(taskID, trackID);
        showNotification();
//        Log.w("SQLite", "startTrack, result Uri : " + trackUri.toString());
    }

    public void stopTrack() {
        trackStopTime = System.currentTimeMillis();
        trackUri = ContentUris.withAppendedId(TaskTracks.CONTENT_URI, status);
        ContentValues cv = new ContentValues();
        cv.put(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME, trackStopTime);
        context.getContentResolver().update(trackUri, cv, null, null);
        setStatus(taskID, 0);
        cancelNotification();
//        Log.w("SQLite", "stopTrack, result Uri : " + trackUri.toString());
    }

    private void stopPreviosTrack() {
        long trackID;
        long taskID;
        long stopTime;
        long totalTime;
        cursor = context.getContentResolver().query(TaskTracks.CONTENT_URI, null, null, null, null);
        if ((cursor.moveToFirst()) || cursor.getCount() != 0) {
            trackID = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks._ID));
            taskID = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID));
            stopTime = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME));
            cursor.close();
            if (stopTime == 0) {
                trackStopTime = System.currentTimeMillis();

//                Update stopTime
                trackUri = ContentUris.withAppendedId(TaskTracks.CONTENT_URI, trackID);
                ContentValues cv = new ContentValues();
                cv.put(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME, trackStopTime);
                context.getContentResolver().update(trackUri, cv, null, null);

//                Update status and totalTime
                taskUri = ContentUris.withAppendedId(Tasks.CONTENT_URI, taskID);
                ContentValues cvTask = new ContentValues();
                cvTask.put(Tasks.COLUMN_NAME_STATUS, status);
                cursor = context.getContentResolver().query(taskUri, null, null, null, null);
                cursor.moveToFirst();
                totalTime = cursor.getLong(cursor.getColumnIndex(Tasks.COLUMN_NAME_TOTAL_TIME));
                cursor.close();
                totalTime = totalTime + trackStopTime;
                cvTask.put(Tasks.COLUMN_NAME_TOTAL_TIME, totalTime);
                context.getContentResolver().update(taskUri, cvTask, null, null);
                cancelNotification();
//                Log.w("SQLite", "stopTrack, result Uri : " + trackUri.toString());
            }
        } else cursor.close();
    }

    private void setStatus(long taskID, long status) {
        Uri taskUri = ContentUris.withAppendedId(Tasks.CONTENT_URI, taskID);
        ContentValues cv = new ContentValues();
        cv.put(Tasks.COLUMN_NAME_STATUS, status);
        if (status == 0) {
            totalTime = totalTime + trackStopTime;
            cv.put(Tasks.COLUMN_NAME_TOTAL_TIME, totalTime);
        } else {
            totalTime = totalTime - trackStartTime;
            cv.put(Tasks.COLUMN_NAME_TOTAL_TIME, totalTime);
        }
        context.getContentResolver().update(taskUri, cv, null, null);
    }

    private void showNotification() {
        Notification.Builder builder = new Notification.Builder(context);
        Intent intent = new Intent(context, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(taskName)
                .setContentText("Runs");

        Notification notification = builder.build();

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void cancelNotification() {
        notificationManager.cancel(NOTIFICATION_ID);
    }
}
