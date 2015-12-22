package ua.com.sober.timetracks.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;

import com.melnykov.fab.FloatingActionButton;

import de.greenrobot.event.EventBus;
import ua.com.sober.timetracks.R;
import ua.com.sober.timetracks.adapter.DataAdapter;
import ua.com.sober.timetracks.adapter.DataAdapter.ViewHolder;
import ua.com.sober.timetracks.event.TrackRunEvent;
import ua.com.sober.timetracks.event.TrackStopEvent;
import ua.com.sober.timetracks.provider.ContractClass;
import ua.com.sober.timetracks.service.TimeTracksService;

/**
 * Created by dmitry.hmel on 13.03.2015.
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private DataAdapter dataAdapter;

    private LinearLayout activeTaskLinearLayout;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.statistic:
                Intent intent = new Intent(getApplicationContext(), StatisticActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                ContractClass.Tasks.CONTENT_URI,
                ContractClass.Tasks.DEFAULT_PROJECTION,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dataAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dataAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long taskID;
        ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            taskID = holder.taskID;
        } else {
            return;
        }
//        Start service
        Intent intent = new Intent(getApplicationContext(), TimeTracksService.class);
        intent.setAction(TimeTracksService.ACTION_START_OR_STOP_TRACK);
        intent.putExtra("taskID", taskID);
        getApplicationContext().startService(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        String taskName;
        final ViewHolder holder = (ViewHolder) view.getTag();
        if (holder != null) {
            taskName = holder.tvTaskName.getText().toString();
        } else {
            return true;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(taskName);
        builder.setCancelable(true);
        builder.setItems(R.array.dialog_items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: {
                        renameTask(holder.taskID);
                        break;
                    }
                    case 1: {
                        deleteTask(holder.taskID);
                        break;
                    }
                }
            }
        });
        builder.show();

        return true;
    }

    public void onEventMainThread(final TrackRunEvent event) {
        TextView taskName = (TextView) findViewById(R.id.tvActiveTaskName);
        TextView trackRunTime = (TextView) findViewById(R.id.tvActiveTrackRunTime);
        taskName.setText(event.taskName);
        trackRunTime.setText(event.trackRunTime);
        activeTaskLinearLayout.setVisibility(View.VISIBLE);
        activeTaskLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                long taskID = event.taskID;

//                Stop active service
                Intent intent = new Intent(getApplicationContext(), TimeTracksService.class);
                intent.setAction(TimeTracksService.ACTION_START_OR_STOP_TRACK);
                intent.putExtra("taskID", taskID);
                getApplicationContext().startService(intent);
            }
        });
    }

    public void onEventMainThread(TrackStopEvent event) {
        activeTaskLinearLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lvItems = (ListView) findViewById(R.id.lvItems);
        activeTaskLinearLayout = (LinearLayout) findViewById(R.id.activeTaskLinearLayout);

//        Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

//        Add FloatingActionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.attachToListView(lvItems);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NewTaskActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
                finish();
            }
        });

        dataAdapter = new DataAdapter(this, null, 0);
        lvItems.setAdapter(dataAdapter);
        lvItems.setOnItemClickListener(this);
        lvItems.setOnItemLongClickListener(this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void renameTask(long taskID) {
        final Uri uri = ContentUris.withAppendedId(ContractClass.Tasks.CONTENT_URI, taskID);
        final EditText input = new EditText(this);
        input.setHint(R.string.rename_dialog_name_hint);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.rename_dialog_title);
        builder.setCancelable(true);
        builder.setView(input);
        builder.setPositiveButton(R.string.rename_dialog_positive_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String inputName = input.getText().toString();
                if (inputName.matches("")) {
                    Toast toast = Toast.makeText(getApplicationContext(), R.string.empty_name, Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    ContentValues cv = new ContentValues();
                    cv.put(ContractClass.Tasks.COLUMN_NAME_TASK_NAME, inputName);
                    getContentResolver().update(uri, cv, null, null);
//                    Log.w("SQLite", "Task renamed, result Uri : " + uri.toString());
                }
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
        builder.setNegativeButton(R.string.rename_dialog_negative_btn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });
        builder.show();

    }

    private void deleteTask(long taskID) {
        Uri uri = ContentUris.withAppendedId(ContractClass.Tasks.CONTENT_URI, taskID);
        getContentResolver().delete(uri, null, null);
//        Log.w("SQLite", "Task delete, result Uri: " + uri.toString());
    }

}