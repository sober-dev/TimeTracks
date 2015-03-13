package ua.com.sober.timetracks;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ua.com.sober.timetracks.provider.ContractClass;

/**
 * Created by dmitry.hmel on 13.03.2015.
 */
public class NewTaskActivity extends ActionBarActivity {
    private EditText etxtTaskName;
    private Button btnAddTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        etxtTaskName = (EditText) findViewById(R.id.etxtTaskName);
        btnAddTask = (Button) findViewById(R.id.btnAddTask);
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask();
            }
        });

        //        Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.newTaskToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        back();
    }

    private void addTask() {
        String taskName = etxtTaskName.getText().toString();
        if (taskName.matches("")) {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.empty_name, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            ContentValues cv = new ContentValues();
            cv.put(ContractClass.Tasks.COLUMN_NAME_TASK_NAME, taskName);
            cv.put(ContractClass.Tasks.COLUMN_NAME_TOTAL_TIME, 0);
            Uri newUri = getContentResolver().insert(ContractClass.Tasks.CONTENT_URI, cv);
            back();
            Log.w("SQLite", "Task insert, result Uri : " + newUri.toString());
        }
    }

    private void back() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
        finish();
    }
}
