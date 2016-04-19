package ua.com.sober.timetracks.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import ua.com.sober.timetracks.provider.ContractClass;

/**
 * Created by Dmitry on 17.03.2016.
 */
public class StatisticsCollector {
    private LinkedList<Track> dateTracks = new LinkedList<>();

    class Track {
        private int trackID;
        private int taskID;
        private long trackStartTime;
        private long trackStopTime;
        private long trackRunTime;

        public Track(int trackID, int taskID, long trackStartTime, long trackStopTime) {
            this.trackID = trackID;
            this.taskID = taskID;
            this.trackStartTime = trackStartTime;
            this.trackStopTime = trackStopTime;
            this.trackRunTime = trackStopTime - trackStartTime;
        }

        @Override
        public String toString() {
            return "trackID: " + trackID + "; taskID: " + taskID + "; trackRunTime: " + trackRunTime;
        }

        public int getTrackID() {
            return trackID;
        }

        public int getTaskID() {
            return taskID;
        }

        public long getTrackStartTime() {
            return trackStartTime;
        }

        public long getTrackStopTime() {
            return trackStopTime;
        }

        public long getTrackRunTime() {
            return trackRunTime;
        }
    }

    public void collect(Context context) {
        int trackID;
        int taskID;
        long startTime;
        long stopTime;

        LocalDate startDate;
        LocalDate stopDate;
        LocalDate trackDate;
        long startOfDay;
        long endOfDay;

        Cursor cursor = context.getContentResolver().query(ContractClass.TaskTracks.CONTENT_URI, null, null, null, null);
        if ((cursor.moveToFirst()) || cursor.getCount() != 0) {
            while (cursor.moveToNext()) {

                trackID = cursor.getInt(cursor.getColumnIndex(ContractClass.TaskTracks._ID));
                taskID = cursor.getInt(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID));
                startTime = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_START_TIME));
                stopTime = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME));

                startDate = new LocalDate(startTime);
                stopDate = new LocalDate(stopTime);

                if (startDate.isEqual(stopDate)) {
                    dateTracks.add(new Track(trackID, taskID, startTime, stopTime));
                } else {
                    trackDate = stopDate;
                    startOfDay = trackDate.toDateTimeAtStartOfDay().getMillis();
                    dateTracks.add(new Track(trackID, taskID, startOfDay, stopTime));

                    while (!(trackDate.isEqual(startDate))) {
                        trackDate.minusDays(1);
                        if (trackDate.isEqual(startDate)) {
                            endOfDay = trackDate.toDateTime(new LocalTime(23, 59, 59, 999)).getMillis();
                            dateTracks.add(new Track(trackID, taskID, startTime, endOfDay));
                        } else {
                            startOfDay = trackDate.toDateTimeAtStartOfDay().getMillis();
                            endOfDay = trackDate.toDateTime(new LocalTime(23, 59, 59, 999)).getMillis();
                            dateTracks.add(new Track(trackID, taskID, startOfDay, endOfDay));
                        }
                    }
                }
            }
            cursor.close();
        } else cursor.close();

        for (Track track : dateTracks) {
            Log.i("Test", track.toString());
        }
    }
}
