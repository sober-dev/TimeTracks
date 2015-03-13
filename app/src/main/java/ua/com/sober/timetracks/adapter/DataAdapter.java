package ua.com.sober.timetracks.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.*;

import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.provider.ContractClass;
import ua.com.sober.timetracks.util.TaskTrack;
import ua.com.sober.timetracks.util.TimeConversion;

/**
 * Created by dmitry.hmel on 12.03.2015.
 */
public class DataAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    public static class ViewHolder {
        public long taskID;
        public TextView tvTaskName;
        public TextView tvTotalTime;
        public Button btnStartOrStop;
    }

    public DataAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item, parent, false);

        ViewHolder holder = new ViewHolder();
        TextView tvTaskName = (TextView) view.findViewById(R.id.tvTaskName);
        TextView tvTotalTime = (TextView) view.findViewById(R.id.tvTotalTime);
        Button btnStartOrStop = (Button) view.findViewById(R.id.btnStartOrStop);
        holder.tvTaskName = tvTaskName;
        holder.tvTotalTime = tvTotalTime;
        holder.btnStartOrStop = btnStartOrStop;
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        long taskID = cursor.getLong(cursor.getColumnIndex(ContractClass.Tasks._ID));
        String taskName = cursor.getString(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_TASK_NAME));
        long status = cursor.getLong(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_STATUS));
        long totalTime = cursor.getLong(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME));

        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.taskID = taskID;
            holder.tvTaskName.setText(taskName);
            if (status == 0) {
                holder.btnStartOrStop.setText(R.string.item_btn_start);
                holder.tvTotalTime.setText(TimeConversion.getTimeStringFromMilliseconds(totalTime));
            } else {
                holder.btnStartOrStop.setText(R.string.item_btn_stop);
                holder.tvTotalTime.setText(R.string.item_run_status);
            }
            holder.btnStartOrStop.setOnClickListener(new OnBtnClickListener(context, taskID, status, totalTime));
        }
    }

    private class OnBtnClickListener implements OnClickListener {
        private Context context;
        private long taskID;
        private long status;
        private long totalTime;

        public OnBtnClickListener(Context context, long taskID, long status, long totalTime) {
            super();
            this.context = context;
            this.taskID = taskID;
            this.status = status;
            this.totalTime = totalTime;
        }

        @Override
        public void onClick(View v) {
            TaskTrack track = new TaskTrack(context, taskID, status, totalTime);
            if (status == 0) {
                track.StartTrack();
            } else {
                track.StopTrack();
            }
        }

    }

}