package com.example.rishabh.todo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by rishabh on 7/21/17.
 */

public class ToDoHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "TaskDB.db";
    public static final int DATABASE_VERSION = 1;
    public ToDoHelper(Context context){
        super(context , DATABASE_NAME , null , DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        try {
            sqLiteDatabase.execSQL("CREATE TABLE " + DataBaseContract.ToDoEntry.TABLE_NAME +
                    " ( " + DataBaseContract.ToDoEntry.UID + " INTEGER PRIMARY KEY AUTOINCREMENT , " +
                    DataBaseContract.ToDoEntry.TITLE + " TEXT , " + DataBaseContract.ToDoEntry.CATEGORY + " TEXT , "
                    + DataBaseContract.ToDoEntry.PRIORITY + " INTEGER , " + DataBaseContract.ToDoEntry.FINISHED + " INTEGER , " +
                    DataBaseContract.ToDoEntry.DATE + " TEXT , " + DataBaseContract.ToDoEntry.TIME + " TEXT  " +
                    " );"
            );
        }catch (SQLiteException e){

        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
