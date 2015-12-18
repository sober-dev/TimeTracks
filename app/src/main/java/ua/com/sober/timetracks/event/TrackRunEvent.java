package ua.com.sober.timetracks.event;

/**
 * Created by dmitry.hmel on 17.12.2015.
 */
public class TrackRunEvent {
    public final String taskName;
    public final String trackRunTime;

    public TrackRunEvent(String taskName, String trackRunTime) {
        this.taskName = taskName;
        this.trackRunTime = trackRunTime;
    }
}
