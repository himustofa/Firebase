package com.apps.firebase.student;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.apps.firebase.R;

import java.util.List;

public class StudentsList extends ArrayAdapter<Student> {

    private Activity activity;
    private List<Student> list;

    public StudentsList(Activity activity, List<Student> list) {
        super(activity, R.layout.list_layout, list);
        this.activity = activity;
        this.list = list;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View row = inflater.inflate(R.layout.list_layout, null, true);

        TextView n = (TextView) row.findViewById(R.id.layout_name);
        TextView e = (TextView) row.findViewById(R.id.layout_email);
        TextView a = (TextView) row.findViewById(R.id.layout_age);

        Student obj = list.get(position);

        n.setText(obj.getStudentName());
        e.setText(obj.getStudentEmail());
        a.setText(String.valueOf(obj.getStudentAge()));

        return row;
    }
}
