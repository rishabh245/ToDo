package com.example.rishabh.todo;

/**
 * Created by rishabh on 7/21/17.
 */

public class TaskDetails {


    int id;
    String title;
    int priority;
    String category;
    String date;
    String time;
    boolean isFinished;



    TaskDetails(int id , String title , int priority , String category , String date , String time ){
        this.id = id;
        this.title = title;
        this.priority = priority;
        this.category = category;
        this.date = date;
        this.time = time;
        this.isFinished = false;
    }
}
