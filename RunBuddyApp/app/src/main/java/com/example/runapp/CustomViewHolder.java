package com.example.runapp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CustomViewHolder extends RecyclerView.ViewHolder {
    public TextView run_distance, run_time, runner_name;
    public CustomViewHolder(@NonNull View itemView) {
        super(itemView);
        run_distance = itemView.findViewById(R.id.run_distance);
        run_time = itemView.findViewById(R.id.run_time);
        runner_name = itemView.findViewById(R.id.runner_name);

    }
}
