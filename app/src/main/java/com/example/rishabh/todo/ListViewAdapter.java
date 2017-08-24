package com.example.rishabh.todo;

import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by rishabh on 7/23/17.
 */

public class ListViewAdapter extends ArrayAdapter<TaskDetails> {
    private Context  context;
    private  ArrayList<TaskDetails> taskslist ;
    CustomChekedListener CheckBoxlistener;
    ListViewAdapter(Context context ,ArrayList<TaskDetails> taskslist){
        super(context ,0,  taskslist);
        this.context = context ;
        this.taskslist = taskslist;
    }


    static class ViewHolder{
        TextView titleTextView;
        TextView DateTextView;
        TextView categoryTextView;
        TextView priorityTextView;
        CheckBox checkbox;


        ViewHolder( TextView titleTextView,TextView DateTextView, TextView categoryTextView,
                TextView priorityTextView, CheckBox checkbox){
            this.titleTextView = titleTextView;
            this.DateTextView = DateTextView;
            this.categoryTextView = categoryTextView;
            this.priorityTextView = priorityTextView;
            this.checkbox = checkbox;


        }

    }


    public void setOnCheckedBoxClickedListener(CustomChekedListener listener){
        this.CheckBoxlistener = listener;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         View row = convertView;
        if(row==null){
            LayoutInflater inflater = LayoutInflater.from(context);
            row = inflater.inflate(R.layout.listview_item , null);
            TextView titleTextView = (TextView) row.findViewById(R.id.task_title);
            TextView DateTextView = (TextView)row.findViewById(R.id.date);
            TextView categoryTextView = (TextView)row.findViewById(R.id.category);
            TextView priorityTextView = (TextView) row.findViewById(R.id.priority);
            CheckBox checkbox = (CheckBox)row.findViewById(R.id.checkbox) ;
            ViewHolder holder = new ViewHolder(titleTextView , DateTextView , categoryTextView , priorityTextView ,
                    checkbox);
            row.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) row.getTag();
        TaskDetails task = taskslist.get(position);
        holder.checkbox.setChecked(task.isFinished);
        holder.titleTextView.setText(task.title);
        holder.DateTextView.setText(task.date + " , " +  task.time);
        if (task.priority==R.id.high_priority) {
            holder.priorityTextView.setBackgroundResource(R.color.high_priority);
        }
        else if(task.priority==R.id.medium_priority){
                holder.priorityTextView.setBackgroundResource(R.color.medium_priority);
            }
        else{
            holder.priorityTextView.setBackgroundResource(R.color.low_priority);
        }
        holder.categoryTextView.setText(task.category);
        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBoxlistener.OnChekedButtonClicked(position);
            }
        });
        return row;
    }
    interface  CustomChekedListener{
        void OnChekedButtonClicked(int position);
    }

}
