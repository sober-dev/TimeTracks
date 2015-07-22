package ua.com.sober.timetracks;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TimeTracksService extends Service {
    public TimeTracksService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
