package ua.com.sober.timetracks.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.activity.MainActivity;

public class TimeTracksService extends Service {
    private Context context;
    private String taskName;
    private static final int NOTIFICATION_ID = 1;
    private static final String LOG_TAG = "TimeTracksService";
    public static final String ACTION_STARTFOREGROUND = "ua.com.sober.timetracks.service.action.STARTFOREGROUND";
    public static final String ACTION_STOPFOREGROUND = "ua.com.sober.timetracks.service.action.STOPFOREGROUND";

    public TimeTracksService() {
    }

    @Override
    public void onCreate() {
        Log.d(LOG_TAG, "onCreate service method");
        context = getApplicationContext();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_STARTFOREGROUND)) {
            taskName = intent.getStringExtra("taskName");
            Log.d(LOG_TAG, "ACTION_STARTFOREGROUND");
            showNotification();
        } else if (intent.getAction().equals(ACTION_STOPFOREGROUND)) {
            Log.d(LOG_TAG, "ACTION_STOPFOREGROUND");
            stopForeground(true);
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

    private void showNotification() {
        //        Create notification
        Notification.Builder builder = new Notification.Builder(context);
        Intent intentMainActivity = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intentMainActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        builder
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(taskName)
                .setContentText("Runs")
                .addAction(R.mipmap.ic_launcher, "Action1", pendingIntent)
                .addAction(R.mipmap.ic_launcher, "Action2", pendingIntent);

        Notification notification = builder.build();

//        Start Foreground and show notification
        startForeground(NOTIFICATION_ID, notification);
    }
}
