package ua.com.sober.timetracks.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.provider.ContractClass;
import ua.com.sober.timetracks.util.TimeConversion;

/**
 * Created by dmitry.hmel on 12.03.2015.
 */
public class DataAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    public static class ViewHolder {
        public long taskID;
        public long status;
        public long totalTime;
        public String taskName;
        public TextView tvTaskName;
        public TextView tvTotalTime;
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
        holder.tvTaskName = tvTaskName;
        holder.tvTotalTime = tvTotalTime;
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String taskName = cursor.getString(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_TASK_NAME));
        long taskID = cursor.getLong(cursor.getColumnIndex(ContractClass.Tasks._ID));
        long status = cursor.getLong(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_STATUS));
        long totalTime = cursor.getLong(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME));

        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.taskID = taskID;
            holder.status = status;
            holder.totalTime = totalTime;
            holder.taskName = taskName;
            holder.tvTaskName.setText(taskName);
            if (status == 0) {
                if (totalTime < 60000) {
                    holder.tvTotalTime.setText(TimeConversion.getTimeStringFromMilliseconds(totalTime, TimeConversion.HMS));
                } else {
                    holder.tvTotalTime.setText(TimeConversion.getTimeStringFromMilliseconds(totalTime, TimeConversion.HM));
                }
            } else {
                holder.tvTotalTime.setText(R.string.item_run_status);
            }
        }
    }

}