package com.example.rishabh.todo;


/**
 * Created by rishabh on 7/21/17.
 */

public class DataBaseContract {

    private DataBaseContract(){

    }

    class  ToDoEntry {
        public static  final  String TABLE_NAME = "TaskEntries";
        public static  final  String UID = "_id";
        public static  final  String TITLE = "title";
        public static  final  String DATE = "date";
        public static  final  String TIME = "time";
        public static  final  String CATEGORY ="category";
        public static  final  String PRIORITY = "priority";
        public static  final  String FINISHED = "finished";
    }


}
