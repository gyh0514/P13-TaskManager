package demoandroidwear.android.myapplicationdev.com.p13_taskmanager;

import android.content.Intent;
import android.support.v4.app.RemoteInput;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView lv;
    ArrayList<Task> tasks;
    ArrayAdapter<Task> adapter;
    Button btnAdd;
    int actReqCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv);
        btnAdd = (Button) findViewById(R.id.btnAdd);

        DBHelper dbh = new DBHelper(this);
        tasks = dbh.getAllTasks();
        adapter = new ArrayAdapter<Task>(this, android.R.layout.simple_list_item_1, tasks);
        lv.setAdapter(adapter);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, AddActivity.class);
                startActivityForResult(i, actReqCode);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, EditActivity.class);
                Task data = tasks.get(position);
                i.putExtra("data", data);
                startActivityForResult(i, actReqCode);
            }
        });

        CharSequence reply = null;
        Intent intent = getIntent();
        Task task = (Task) intent.getSerializableExtra("data");
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null){
            reply = remoteInput.getCharSequence("status");
        }

        if(reply != null){

            if (reply.equals("Completed")){
                dbh = new DBHelper(MainActivity.this);
                dbh.deleteTask(task.getId());
                dbh.close();
            }
            Toast.makeText(MainActivity.this, "You have indicated: " + reply, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == actReqCode) {
            if (resultCode == RESULT_OK) {
                DBHelper dbh = new DBHelper(MainActivity.this);
                tasks.clear();
                tasks.addAll(dbh.getAllTasks());
                dbh.close();
                adapter.notifyDataSetChanged();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
