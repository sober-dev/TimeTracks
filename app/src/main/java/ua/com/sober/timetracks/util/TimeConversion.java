package ua.com.sober.timetracks.util;

/**
 * Created by dmitry.hmel on 12.03.2015.
 */
public class TimeConversion {
    public static final String HMS = "hms";
    public static final String HM = "hm";

    public static String getTimeStringFromMilliseconds(long milliseconds, String timeFormat) {
        String seconds = Integer.toString(getSecondsFromMilliseconds(milliseconds));
        String minutes = Integer.toString(getMinutesFromMilliseconds(milliseconds));
        String hours = Integer.toString(getHoursFromMilliseconds(milliseconds));
        for (int i = 0; i < 2; i++) {
            if (seconds.length() < 2) {
                seconds = "0" + seconds;
            }
            if (minutes.length() < 2) {
                minutes = "0" + minutes;
            }
            if (hours.length() < 2) {
                hours = "0" + hours;
            }
        }
        if (timeFormat.equals(HM)) {
            return hours + "h:" + minutes + "m";
        } else {
            return hours + "h:" + minutes + "m:" + seconds + "s";
        }
    }

    public static int getHoursFromMilliseconds(long milliseconds) {
        long time = milliseconds / 1000;
        return (int) (time / 3600);
    }

    public static int getMinutesFromMilliseconds(long milliseconds) {
        long time = milliseconds / 1000;
        return (int) ((time % 3600) / 60);
    }

    public static int getSecondsFromMilliseconds(long milliseconds) {
        long time = milliseconds / 1000;
        return (int) (time % 60);
    }
}
