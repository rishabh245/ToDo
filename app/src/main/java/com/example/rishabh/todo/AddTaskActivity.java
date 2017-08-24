package com.example.rishabh.todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

public class AddTaskActivity extends AppCompatActivity  {

    Spinner spinner;
    ArrayAdapter<String> spinnerAdapter;
    EditText DateText;
    EditText TimeText;
    Calendar c=null;
    Toolbar toolbar;
    EditText TaskTitle;
    RadioGroup radioGroup;
    ArrayList<String> spinnerArrayList;
    RadioButton high_button;
    ToDoHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        spinnerArrayList = new ArrayList<>();
        spinnerArrayList.add("Default");
        spinnerArrayList.add("Birthday");
        spinnerArrayList.add("Personal");
        spinnerArrayList.add("Shopping");
        spinnerArrayList.add("Wishlist");
        spinnerArrayList.add("Work");
        spinner = (Spinner) findViewById(R.id.spinner_task);
        spinnerAdapter = new ArrayAdapter<>(this  ,
                android.R.layout.simple_spinner_item , spinnerArrayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        toolbar = (Toolbar)findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Task");
        DateText = (EditText) findViewById(R.id.date);
        TimeText = (EditText) findViewById(R.id.time);
        TaskTitle = (EditText)findViewById(R.id.task_title);
        high_button = (RadioButton) findViewById(R.id.high_priority);
        high_button.setChecked(true);

        DateText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });

        TimeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });

        radioGroup = (RadioGroup) findViewById(R.id.radio_group);


    }

    private void pickTime() {
       if (c==null)
            c = Calendar.getInstance();
       TimePickerDialog TimeDialog = new TimePickerDialog(this,
               new TimePickerDialog.OnTimeSetListener() {
                   @Override
                   public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                       String time="";
                       if(hour<=9) time = "0";
                       time += hour+":";
                       if(minute<=9) time += "0";
                       time += minute;
                       TimeText.setText(time);
                       c.set(Calendar.HOUR_OF_DAY , hour);
                       c.set(Calendar.MINUTE,minute);
                       c.set(Calendar.SECOND,0);

                   }
               },
       c.get(Calendar.HOUR_OF_DAY) , c.get(Calendar.MINUTE) ,false);
        TimeDialog.show();
    }

    public void pickDate(){
        if(c==null)
            c = Calendar.getInstance();
        DatePickerDialog DateDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        c.set(year , month ,day);
                        month += 1;
                        String date="";
                        if(day<=9) date += "0";
                        date += day + "/";
                        if(month<=9) date += "0";
                        date += month + "/";
                        date += year;
                        DateText.setText(date);
                    }
                } ,
                c.get(Calendar.YEAR ), c.get(Calendar.MONTH ), c.get(Calendar.DAY_OF_MONTH));
        DateDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_task , menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = TaskTitle.getText().toString();
        if(title.trim().isEmpty()){
            TaskTitle.setError("Title is not Set");
            return false;
        }
        String date = DateText.getText().toString();
        if(date.trim().isEmpty()){
            DateText.setError("Date is not Set");
            return  false;
        }
        String time = TimeText.getText().toString();
        if(time.trim().isEmpty()){
            TimeText.setError("Time is not Set");
            return false;
        }
       long AlarmTime = c.getTime().getTime();
      /* Toast.makeText(this, c.get(Calendar.MONTH)+" "+c.get(Calendar.DAY_OF_MONTH)+" "+c.get(Calendar.HOUR_OF_DAY)+" "+
                c.get(Calendar.MINUTE)+" "+c.get(Calendar.SECOND), Toast.LENGTH_LONG).show();*/
      //long curTime = System.currentTimeMillis();
      //  Log.i("Time" , AlarmTime+" " +curTime);
        if(AlarmTime <= System.currentTimeMillis()){
            Toast.makeText(this, "Time or Date is not set properly", Toast.LENGTH_SHORT).show();
            return false;
        }
        String category = spinner.getSelectedItem().toString();

        int radio_id = radioGroup.getCheckedRadioButtonId();
        if(radio_id==-1){
            Toast.makeText(this, "Priority is not Set", Toast.LENGTH_SHORT).show();
            return false;
        }
         helper = new ToDoHelper(AddTaskActivity.this);
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues content_values = new ContentValues();
        content_values.put(DataBaseContract.ToDoEntry.TITLE , title);
        content_values.put(DataBaseContract.ToDoEntry.DATE , date);
        content_values.put(DataBaseContract.ToDoEntry.TIME , time);
        content_values.put(DataBaseContract.ToDoEntry.CATEGORY , category);
        content_values.put(DataBaseContract.ToDoEntry.PRIORITY ,radio_id);
        content_values.put(DataBaseContract.ToDoEntry.FINISHED ,0);
        long row = db.insert(DataBaseContract.ToDoEntry.TABLE_NAME , null , content_values);
        if(row==-1){
            Toast.makeText(this, "Report a bug", Toast.LENGTH_SHORT).show();
        }
        Cursor cursor = db.query(DataBaseContract.ToDoEntry.TABLE_NAME,null,null,null,null,null,null);
        cursor.moveToLast();
        int id = cursor.getInt(cursor.getColumnIndex(DataBaseContract.ToDoEntry.UID));
        setAlarm(AlarmTime,id);
        Toast.makeText(this , "New Task Added " , Toast.LENGTH_SHORT).show();
        Intent i = new Intent();
        setResult(RESULT_OK , i);
        finish();
        return  true ;
    }

    private void setAlarm(long alarmTime ,int id) {
        Intent alarmintent = new Intent(this , AlarmReceiver.class);
        String title = TaskTitle.getText().toString();
        String time = TimeText.getText().toString();
        alarmintent.putExtra("title" , title);
        alarmintent.putExtra("time" , time);
        alarmintent.putExtra("id" , id);
        PendingIntent pi =  PendingIntent.getBroadcast(this , id, alarmintent , PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP , alarmTime , pi);
    }

    @Override
    protected void onDestroy() {
        if(helper!=null){
            helper.close();
        }

        super.onDestroy();
    }
}
