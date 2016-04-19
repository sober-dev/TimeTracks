package ua.com.sober.timetracks.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.activity.MainActivity;
import ua.com.sober.timetracks.event.TrackRunEvent;
import ua.com.sober.timetracks.event.TrackStopEvent;
import ua.com.sober.timetracks.provider.ContractClass;
import ua.com.sober.timetracks.util.TimeConversion;

public class TimeTracksService extends Service {
    public static final String ACTION_START_OR_STOP_TRACK = "ua.com.sober.timetracks.service.action.START_OR_STOP_TRACK";

    private static final int NOTIFICATION_ID = 1;
    private static final long MIN_RUN_TIME = 0;
//    private static final long MIN_RUN_TIME = 60000;

    private Context context;
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private Timer timer;

    private String taskName;
    private long taskID = -1;
    private long trackStartTime;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(context);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_START_OR_STOP_TRACK)) {
            long taskID = intent.getLongExtra("taskID", -2);

            if (this.taskID == -1) {
                this.taskID = taskID;
                startTrack();
            } else if (this.taskID == taskID) {
                stopTrack();
                this.taskID = -1;
            } else if (taskID != -2) {
                stopTrack();
                this.taskID = taskID;
                startTrack();
            }

        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    private void startTrack() {
        trackStartTime = System.currentTimeMillis();

//        Get taskName from DB
        Uri taskUri = ContentUris.withAppendedId(ContractClass.Tasks.CONTENT_URI, taskID);
        Cursor cursor = context.getContentResolver().query(taskUri, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            taskName = cursor.getString(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_TASK_NAME));
            cursor.close();
        }

//        Start Foreground and show notification        
        startForeground(NOTIFICATION_ID, getNotifacation());
        updateNotification();
    }

    private void stopTrack() {
//        Send TrackStopEvent to MainActivity
        EventBus.getDefault().post(new TrackStopEvent());

//        Save track in DB
        if (getTrackRunTime() > MIN_RUN_TIME) {
            long trackStopTime = System.currentTimeMillis();
            ContentValues cv = new ContentValues();
            cv.put(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID, taskID);
            cv.put(ContractClass.TaskTracks.COLUMN_NAME_START_TIME, trackStartTime);
            cv.put(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME, trackStopTime);
            context.getContentResolver().insert(ContractClass.TaskTracks.CONTENT_URI, cv);
        }

//            Stop Foreground and clear notification
        timer.cancel();
        stopForeground(true);
    }

    private Notification getNotifacation() {
//        Create notification
        Intent intentMainActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(taskName)
                .setContentText(TimeConversion.getTimeStringFromMilliseconds(getTrackRunTime(), TimeConversion.HMS))
//                .addAction(R.mipmap.ic_launcher, "Action1", pendingIntent)
//                .addAction(R.mipmap.ic_launcher, "Action2", pendingIntent)
        ;
        return builder.build();
    }

    private void updateNotification() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                String runTime;
                if (getTrackRunTime() < 60000) {
                    runTime = TimeConversion.getTimeStringFromMilliseconds(getTrackRunTime(), TimeConversion.HMS);

                } else {
                    runTime = TimeConversion.getTimeStringFromMilliseconds(getTrackRunTime(), TimeConversion.HM);
                }

//                Send TrackRunEvent to MainActivity
                EventBus.getDefault().post(new TrackRunEvent(taskID, taskName, runTime));

                builder.setContentText(runTime);
                notificationManager.notify(NOTIFICATION_ID, builder.build());
            }
        }, 0, 1000);
    }

    private long getTrackRunTime() {
        return System.currentTimeMillis() - trackStartTime;
    }

}
