package ua.com.sober.timetracks.event;

/**
 * Created by dmitry.hmel on 17.12.2015.
 */
public class TrackRunEvent {
    public final long taskID;
    public final String taskName;
    public final String trackRunTime;

    public TrackRunEvent(long taskID, String taskName, String trackRunTime) {
        this.taskID = taskID;
        this.taskName = taskName;
        this.trackRunTime = trackRunTime;
    }
}
