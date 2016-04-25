package ua.com.sober.timetracks.util;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import ua.com.sober.timetracks.provider.ContractClass;

/**
 * Created by Dmitry on 17.03.2016.
 */
public class StatisticsCollector {
    private HashMap<Integer, String> tasks = new HashMap<>();
    private HashMap<Integer, Long> statistic = new HashMap<>();
    private LinkedList<Track> tracks = new LinkedList<>();

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
            return "trackID: " + trackID + "; taskID: " + taskID + "; Run: " + TimeConversion.getTimeStringFromMilliseconds(trackRunTime, TimeConversion.HMS);
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

    public HashMap<String, String> getStatistics() {
        HashMap<String, String> result = new HashMap<>();
        for (Integer key : statistic.keySet()) {
            result.put(tasks.get(key), TimeConversion.getTimeStringFromMilliseconds(statistic.get(key), TimeConversion.HMS));
        }
        return result;
    }

    private void statisticsCounting() {
        for (Integer key : tasks.keySet()) {
            statistic.put(key, 0L);
        }
        for (Track track : tracks) {
            statistic.put(track.getTaskID(), statistic.get(track.getTaskID()) + track.trackRunTime);
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

        fillTasks(context);

        Cursor cursor = context.getContentResolver().query(ContractClass.TaskTracks.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return;
        }
        try {
            while (cursor.moveToNext()) {
                trackID = cursor.getInt(cursor.getColumnIndex(ContractClass.TaskTracks._ID));
                taskID = cursor.getInt(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_TASK_ID));
                startTime = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_START_TIME));
                stopTime = cursor.getLong(cursor.getColumnIndex(ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME));

                startDate = new LocalDate(startTime);
                stopDate = new LocalDate(stopTime);

                if (startDate.isEqual(stopDate)) {
                    tracks.add(new Track(trackID, taskID, startTime, stopTime));
                } else {
                    trackDate = stopDate;
                    startOfDay = trackDate.toDateTimeAtStartOfDay().getMillis();
                    tracks.add(new Track(trackID, taskID, startOfDay, stopTime));

                    while (!(trackDate.isEqual(startDate))) {
                        trackDate = trackDate.minusDays(1);
                        if (trackDate.isEqual(startDate)) {
                            endOfDay = trackDate.toDateTime(new LocalTime(23, 59, 59, 999)).getMillis();
                            tracks.add(new Track(trackID, taskID, startTime, endOfDay));
                        } else {
                            startOfDay = trackDate.toDateTimeAtStartOfDay().getMillis();
                            endOfDay = trackDate.toDateTime(new LocalTime(23, 59, 59, 999)).getMillis();
                            tracks.add(new Track(trackID, taskID, startOfDay, endOfDay));
                        }
                    }
                }
            }
        } finally {
            cursor.close();
        }

        statisticsCounting();

        for (Track track : tracks) {
            Log.i("Test", track.toString());
        }

        for (Map.Entry<Integer, Long> pair : statistic.entrySet()) {
            String taskName = tasks.get(pair.getKey());
            Log.i("Statistics", taskName + " - " + TimeConversion.getTimeStringFromMilliseconds(pair.getValue(), TimeConversion.HMS));
        }
    }

    private void fillTasks(Context context) {
        Cursor cursor = context.getContentResolver().query(ContractClass.Tasks.CONTENT_URI, null, null, null, null);
        if (cursor == null) {
            return;
        }
        try {
            while (cursor.moveToNext()) {
                Integer taskID = cursor.getInt(cursor.getColumnIndex(ContractClass.Tasks._ID));
                String taskName = cursor.getString(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_TASK_NAME));
                tasks.put(taskID, taskName);
            }
        } finally {
            cursor.close();
        }
    }
}
