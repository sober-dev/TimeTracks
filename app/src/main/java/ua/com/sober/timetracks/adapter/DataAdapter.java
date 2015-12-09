package ua.com.sober.timetracks.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.provider.ContractClass;

/**
 * Created by dmitry.hmel on 12.03.2015.
 */
public class DataAdapter extends CursorAdapter {
    private LayoutInflater inflater;

    public static class ViewHolder {
        public long taskID;
        public String taskName;
        public TextView tvTaskName;
    }

    public DataAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = inflater.inflate(R.layout.list_item, parent, false);

        ViewHolder holder = new ViewHolder();
        holder.tvTaskName = (TextView) view.findViewById(R.id.tvTaskName);
        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String taskName = cursor.getString(cursor.getColumnIndex(ContractClass.Tasks.COLUMN_NAME_TASK_NAME));
        long taskID = cursor.getLong(cursor.getColumnIndex(ContractClass.Tasks._ID));

        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            holder.taskID = taskID;
            holder.taskName = taskName;
            holder.tvTaskName.setText(taskName);
        }
    }

}