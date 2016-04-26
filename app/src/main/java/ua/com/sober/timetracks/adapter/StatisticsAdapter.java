package ua.com.sober.timetracks.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import ua.com.sober.timetracks.R;

public class StatisticsAdapter extends BaseAdapter {
    private final ArrayList mData;

    public StatisticsAdapter(Map<String, String> map) {
        mData = new ArrayList();
        mData.addAll(map.entrySet());
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Map.Entry<String, String> getItem(int position) {
        return (Map.Entry) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO implement you own logic with ID
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistics_adapter_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.tvStatTaskName = (TextView) convertView.findViewById(R.id.tvStatTaskName);
            viewHolder.tvStatTaskTime = (TextView) convertView.findViewById(R.id.tvStatTaskTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Map.Entry<String, String> item = getItem(position);

        viewHolder.tvStatTaskName.setText(item.getKey());
        viewHolder.tvStatTaskTime.setText(item.getValue());

        return convertView;
    }

    private static class ViewHolder {
        public TextView tvStatTaskName;
        public TextView tvStatTaskTime;
    }
}