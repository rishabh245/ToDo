package com.example.rishabh.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListViewAdapter.CustomChekedListener {
    Toolbar toolbar;
    Spinner spinner;
    String SpinnerCategory;
    ArrayAdapter<CharSequence> spinner_adapter;
    FloatingActionButton fab;
    ArrayList<TaskDetails> TasksList;
    ToDoHelper helper ;
    SQLiteDatabase database;
    ListViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner_adapter = ArrayAdapter.createFromResource(this, R.array.spinner_items,
                R.layout.spinner_layout);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinner_adapter);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this , AddTaskActivity.class);
                startActivityForResult(i , Constants.NEWTASK_INTENT);
            }
        });
        helper = new ToDoHelper(this);
        database = helper.getWritableDatabase();
        ListView list_view = (ListView) findViewById(R.id.list_view);
        TasksList = new ArrayList<>();
        adapter = new ListViewAdapter(this , TasksList);
        adapter.setOnCheckedBoxClickedListener(this);
        SpinnerCategory = (String) spinner.getItemAtPosition(0);
        addTasks(SpinnerCategory);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
               // Log.i("msg" , "err");
                //Toast.makeText(MainActivity.this, "List Item Clicked", Toast.LENGTH_SHORT).show();
                Intent toExistingTask = new Intent(MainActivity.this , ChangeExistingTask.class);
                toExistingTask.putExtra("id" , TasksList.get(position).id);
                toExistingTask.putExtra("title" , TasksList.get(position).title);
                toExistingTask.putExtra("date" , TasksList.get(position).date);
                toExistingTask.putExtra("time" , TasksList.get(position).time);
                toExistingTask.putExtra("category" , TasksList.get(position).category);
                toExistingTask.putExtra("priority" , TasksList.get(position).priority);
                startActivityForResult(toExistingTask , Constants.EXISTINGTASK_INTENT);
            }
        });

      spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
              SpinnerCategory = (String) spinner.getItemAtPosition(position);
              addTasks(SpinnerCategory);
          }

          @Override
          public void onNothingSelected(AdapterView<?> adapterView) {

          }
      });
    }

    private void addTasks(String SpinnerCategory) {
        TasksList.clear();
        Resources res = getResources();
        Cursor cursor;
        String allList = res.getStringArray(R.array.spinner_items)[0];
        if(SpinnerCategory.equals(allList)) {
            cursor = database.query(DataBaseContract.ToDoEntry.TABLE_NAME ,null ,
                    DataBaseContract.ToDoEntry.FINISHED + " = 0" , null , null ,null , null);
        }
        else{
            String[] selectionArgs={"0" , SpinnerCategory};
            cursor = database.query(DataBaseContract.ToDoEntry.TABLE_NAME ,null ,
                    DataBaseContract.ToDoEntry.FINISHED + " = ? AND "  + DataBaseContract.ToDoEntry.CATEGORY + " = ? " , selectionArgs , null ,null , null);
        }

        while(cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DataBaseContract.ToDoEntry.UID));
            String title = cursor.getString(cursor.getColumnIndex(DataBaseContract.ToDoEntry.TITLE));
            String date = cursor.getString(cursor.getColumnIndex(DataBaseContract.ToDoEntry.DATE));
            String time = cursor.getString(cursor.getColumnIndex(DataBaseContract.ToDoEntry.TIME));
            boolean isFinished = cursor.getInt(cursor.getColumnIndex(DataBaseContract.ToDoEntry.FINISHED)) > 0;
            String category  = cursor.getString(cursor.getColumnIndex(DataBaseContract.ToDoEntry.CATEGORY));
            int priority = cursor.getInt(cursor.getColumnIndex(DataBaseContract.ToDoEntry.PRIORITY));
            TaskDetails newtask = new TaskDetails(id , title , priority , category , date , time);
            TasksList.add(newtask);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SpinnerCategory = spinner.getSelectedItem().toString();
        if(requestCode==Constants.NEWTASK_INTENT){
            if(resultCode==RESULT_OK){
                addTasks(SpinnerCategory);
            }
            else if(resultCode==RESULT_CANCELED){

            }

        }
       else if(requestCode==Constants.EXISTINGTASK_INTENT){
            if(resultCode==RESULT_OK){
               addTasks(SpinnerCategory);
            }
            else if(resultCode==RESULT_CANCELED){


            }
        }
    }


    @Override
    protected void onDestroy() {
        helper.close();
        super.onDestroy();
    }

  @Override
    public void OnChekedButtonClicked(int position) {
        TaskDetails task = adapter.getItem(position);
        int id = task.id;
        cancelAlarm(id);
        ContentValues cv = new ContentValues();
        cv.put(DataBaseContract.ToDoEntry.FINISHED , 1);
        database.update(DataBaseContract.ToDoEntry.TABLE_NAME , cv , DataBaseContract.ToDoEntry.UID + " = " + id , null);
        String spinnerItem = spinner.getSelectedItem().toString();
        addTasks(spinnerItem);
  }

    private void cancelAlarm(int id) {
        Intent i = new Intent(this , AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this , id , i , 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        am.cancel(pi);
    }

}