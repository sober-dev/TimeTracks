package ua.com.sober.timetracks.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by dmitry.hmel on 12.03.2015.
 */
public final class ContractClass {
    public static final String AUTHORITY = "ua.com.sober.timetracks.provider.ContractClass";

    private ContractClass() {
    }

    public static final class Tasks implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        private static final String SCHEME = "content://";
        private static final String PATH_TASKS = "/tasks";
        private static final String PATH_TASKS_ID = "/tasks/";
        public static final int TASKS_ID_PATH_POSITION = 1;
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_TASKS);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_TASKS_ID);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ua.com.sober.timetracks.provider.ContractClass.tasks";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ua.com.sober.timetracks.provider.ContractClass.tasks";
        public static final String DEFAULT_SORT_ORDER = "_id DESC";
        public static final String COLUMN_NAME_TASK_NAME = "task_name";
        public static final String[] DEFAULT_PROJECTION = new String[]{
                ContractClass.Tasks._ID,
                ContractClass.Tasks.COLUMN_NAME_TASK_NAME,
        };

        private Tasks() {
        }
    }

    public static final class TaskTracks implements BaseColumns {
        public static final String TABLE_NAME = "task_tracks";
        private static final String SCHEME = "content://";
        private static final String PATH_TASK_TRACKS = "/task_tracks";
        private static final String PATH_TASK_TRACKS_ID = "/task_tracks/";
        public static final int TASK_TRACKS_ID_PATH_POSITION = 1;
        public static final Uri CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_TRACKS);
        public static final Uri CONTENT_ID_URI_BASE = Uri.parse(SCHEME + AUTHORITY + PATH_TASK_TRACKS_ID);
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.ua.com.sober.timetracks.provider.ContractClass.task_tracks";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.ua.com.sober.timetracks.provider.ContractClass.task_tracks";
        public static final String DEFAULT_SORT_ORDER = "_id DESC";
        public static final String COLUMN_NAME_TASK_ID = "task_id";
        public static final String COLUMN_NAME_START_TIME = "start_time";
        public static final String COLUMN_NAME_STOP_TIME = "stop_time";
        public static final String[] DEFAULT_PROJECTION = new String[]{
                ContractClass.TaskTracks._ID,
                ContractClass.TaskTracks.COLUMN_NAME_TASK_ID,
                ContractClass.TaskTracks.COLUMN_NAME_START_TIME,
                ContractClass.TaskTracks.COLUMN_NAME_STOP_TIME,
        };

        private TaskTracks() {
        }
    }

}
