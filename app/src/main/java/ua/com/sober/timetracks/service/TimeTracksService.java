package ua.com.sober.timetracks.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.activity.MainActivity;
import ua.com.sober.timetracks.util.TimeConversion;

public class TimeTracksService extends Service {
    private Context context;
    private String taskName;
    private long runTime = 0;
    private NotificationManager notificationManager;
    private Notification.Builder builder;
    private Timer timer;
    private static final int NOTIFICATION_ID = 1;
    private static final String LOG_TAG = "TimeTracksService";
    public static final String ACTION_STARTFOREGROUND = "ua.com.sober.timetracks.service.action.STARTFOREGROUND";
    public static final String ACTION_STOPFOREGROUND = "ua.com.sober.timetracks.service.action.STOPFOREGROUND";

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate service method");
        context = getApplicationContext();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_STARTFOREGROUND)) {
            taskName = intent.getStringExtra("taskName");
            Log.d(LOG_TAG, "ACTION_STARTFOREGROUND");
//            Start Foreground and show notification
            builder = new Notification.Builder(context);
            startForeground(NOTIFICATION_ID, getNotifacation(runTime));
            updateNotification();
        } else if (intent.getAction().equals(ACTION_STOPFOREGROUND)) {
            Log.d(LOG_TAG, "ACTION_STOPFOREGROUND");
//            Stop Foreground and clear notification
            timer.cancel();
            stopForeground(true);
            runTime = 0;
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy service method");
        super.onDestroy();
    }

    private Notification getNotifacation(long runTime) {
//        Create notification
        Intent intentMainActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(taskName)
                .setContentText(TimeConversion.getTimeStringFromMilliseconds(runTime, TimeConversion.HMS))
                .addAction(R.mipmap.ic_launcher, "Action1", pendingIntent)
                .addAction(R.mipmap.ic_launcher, "Action2", pendingIntent);
        return builder.build();
    }

    private void updateNotification() {
        if (runTime < 60000) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (runTime < 60000) {
                        Log.d(LOG_TAG, "runTime: " + runTime);
                        builder.setContentText(TimeConversion.getTimeStringFromMilliseconds(runTime, TimeConversion.HMS));
                        notificationManager.notify(NOTIFICATION_ID, builder.build());
                        runTime = runTime + 1000;
                    } else {
                        timer.cancel();
                        updateNotification();
                    }
                }
            }, 0, 1000);
        } else {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "runTime: " + runTime);
                    builder.setContentText(TimeConversion.getTimeStringFromMilliseconds(runTime, TimeConversion.HM));
                    notificationManager.notify(NOTIFICATION_ID, builder.build());
                    runTime = runTime + 60000;
                }
            }, 0, 60000);
        }
    }
}
