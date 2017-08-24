package com.example.rishabh.todo;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;

public class ChangeExistingTask extends AppCompatActivity {

    TextView titleTextView;
    TextView dateTextView;
    TextView timeTextView;
    Spinner spinner;
    RadioGroup radioGroup;
    RadioButton highradio , mediumradio , lowradio;
    ArrayAdapter<String> spinnerAdapter;
    ArrayList<String> spinnerArrayList;
    int id;
    Calendar c=null;
    ToDoHelper helper;
    SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existingtask);
        getSupportActionBar().setTitle("Task");
        Intent i = getIntent();
        Bundle b = i.getExtras();
        titleTextView = (TextView) findViewById(R.id.task_title);
        dateTextView = (TextView) findViewById(R.id.date);
        timeTextView = (TextView) findViewById(R.id.time);
        spinner = (Spinner) findViewById(R.id.spinner_task);
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);
        highradio = (RadioButton) findViewById(R.id.high_priority);
        mediumradio = (RadioButton) findViewById(R.id.medium_priority);
        lowradio = (RadioButton) findViewById(R.id.low_priority);
        spinnerArrayList = new ArrayList<>();
        spinnerArrayList.add("Default");
        spinnerArrayList.add("Birthday");
        spinnerArrayList.add("Personal");
        spinnerArrayList.add("Shopping");
        spinnerArrayList.add("Wishlist");
        spinnerArrayList.add("Work");
        spinnerAdapter = new ArrayAdapter<>(this , android.R.layout.simple_spinner_item ,
                spinnerArrayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        setCorrectCategory(spinner,b) ;
        //////////////////////////////////////////////
        id = b.getInt("id");
        titleTextView.setText(b.getString("title"));
        dateTextView.setText(b.getString("date"));
        timeTextView.setText(b.getString("time"));
        int priortiy = b.getInt("priority");
        if(priortiy==R.id.high_priority){
            highradio.setChecked(true);
        }
        else if(priortiy==R.id.medium_priority){
            mediumradio.setChecked(true);
        }
        else{
            lowradio.setChecked(true);
        }

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickTime();
            }
        });
    }

    private void setCorrectCategory(Spinner spinner , Bundle b) {
        int spinnerCount = spinner.getCount();
        int count=0;
        for(;count<spinnerCount;count++){
            String spinnerCatgeory = (String) spinner.getItemAtPosition(count);
            if(spinnerCatgeory.equals(b.getString("category"))) break;
        }
        spinner.setSelection(count);
    }

    private void pickTime() {
       if (c==null) c = Calendar.getInstance();
        TimePickerDialog TimeDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        c.set(Calendar.HOUR_OF_DAY,hour);
                        c.set(Calendar.MINUTE,minute);
                        String time="";
                        if(hour<=9) time = "0";
                        time += hour+":";
                        if(minute<=9) time += "0";
                        time += minute;
                        timeTextView.setText(time);
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
                        c.set(year , month , day);
                        month += 1;
                        String date="";
                        if(day<=9) date += "0";
                        date += day + "/";
                        if(month<=9) date += "0";
                        date += month + "/";
                        date += year;
                        dateTextView.setText(date);

                    }
                } ,
                c.get(Calendar.YEAR ), c.get(Calendar.MONTH ), c.get(Calendar.DAY_OF_MONTH));
        DateDialog.show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_existing_task , menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                //Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
                 delete();
                return true;
            case R.id.save:
               // Toast.makeText(this, "Save", Toast.LENGTH_SHORT).show();
                update();
                return true;
            default:
                return  super.onOptionsItemSelected(item);


        }
    }

    private void delete() {
       // Toast.makeText(this, "delete function", Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you Sure");
        builder.setMessage("The Task will be deleted");
        builder.setIcon(android.R.drawable.ic_delete);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                helper = new ToDoHelper(ChangeExistingTask.this);
                database = helper.getWritableDatabase();
                String[] selectionArgs = {id + ""};
                database.delete(DataBaseContract.ToDoEntry.TABLE_NAME , DataBaseContract.ToDoEntry.UID + " = ?" ,
                        selectionArgs);
                cancelAlarm();
                Intent i1 = new Intent();
                setResult(RESULT_OK , i1);
                finish();
            }
        });
        builder.setNegativeButton("Cancel" , null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cancelAlarm() {
        Intent i = new Intent(this , AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this , id ,i , 0);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.cancel(pi);
    }

    private void update(){
        String title = titleTextView.getText().toString();
        if(title.trim().isEmpty()){
            titleTextView.setError("Title is not Set");
            return ;
        }
        String date = dateTextView.getText().toString();
        if(date.trim().isEmpty()){
            dateTextView.setError("Date is not Set");
            return ;
        }
        String time = timeTextView.getText().toString();
        if(time.trim().isEmpty()){
            timeTextView.setError("Time is not Set");
            return ;
        }
        if(c.getTimeInMillis()<=System.currentTimeMillis()){
            Toast.makeText(this, "Time or Date is not set properly", Toast.LENGTH_SHORT).show();
            return ;
        }
        String category = spinner.getSelectedItem().toString();

        int radio_id = radioGroup.getCheckedRadioButtonId();
        if(radio_id==-1){
            Toast.makeText(this, "Priority is not Set", Toast.LENGTH_SHORT).show();
            return ;
        }
        //Check if there is some changes made
        //If changes made Show a dialog to confirm update
        //If user set OK then update in database and finish activity else dismiss dialog
        //IMplement on Back Pressed
        if(helper==null){
            helper = new ToDoHelper(this);
            database = helper.getWritableDatabase();
        }
        ContentValues content_values = new ContentValues();
        content_values.put(DataBaseContract.ToDoEntry.TITLE , title);
        content_values.put(DataBaseContract.ToDoEntry.DATE , date);
        content_values.put(DataBaseContract.ToDoEntry.TIME , time);
        content_values.put(DataBaseContract.ToDoEntry.CATEGORY , category);
        content_values.put(DataBaseContract.ToDoEntry.PRIORITY ,radio_id);
        String[] selectionArgs = {id + ""};
        database.update(DataBaseContract.ToDoEntry.TABLE_NAME , content_values , DataBaseContract.ToDoEntry.UID +" = ? "
        ,selectionArgs);
        Cursor cursor = database.query(DataBaseContract.ToDoEntry.TABLE_NAME , null , null , null,null,null,null);
        cursor.moveToLast();
        int id = cursor.getInt(cursor.getColumnIndex(DataBaseContract.ToDoEntry.UID));
        long AlarmTime = c.getTimeInMillis();
        setAlarm(AlarmTime , id);
        Intent updateintent = new Intent();
        setResult(RESULT_OK , updateintent);
        finish();
    }

    private void setAlarm(long alarmTime, int id) {
        Intent i = new Intent(this , AlarmReceiver.class);
        String title = titleTextView.getText().toString();
        String time = timeTextView.getText().toString();
        i.putExtra("title" , title);
        i.putExtra("time" , time);
        i.putExtra("id" , id);
        PendingIntent pi = PendingIntent.getBroadcast(this , id , i , PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP , alarmTime , pi);
    }
}


