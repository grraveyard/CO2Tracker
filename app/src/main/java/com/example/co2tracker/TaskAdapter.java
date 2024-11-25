package com.example.co2tracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private List<Task> tasks;
    private final Context context;
    private final TaskClickListener listener;

    public interface TaskClickListener {
        void onTaskClicked(Task task);
    }

    public TaskAdapter(Context context, List<Task> tasks, TaskClickListener listener) {
        this.context = context;
        this.tasks = tasks;
        this.listener = listener;
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Task task = tasks.get(position);
        
        holder.titleText.setText(task.getTitle());
        holder.descriptionText.setText(task.getDescription());
        holder.pointsText.setText(String.format("%d points", task.getPoints()));
        
        if (task.isCompleted()) {
            holder.proofButton.setEnabled(false);
            holder.proofButton.setText("Completed!");
        } else {
            holder.proofButton.setEnabled(true);
            holder.proofButton.setText("Take Photo");
            holder.proofButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTaskClicked(task);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tasks != null ? tasks.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView descriptionText;
        TextView pointsText;
        Button proofButton;

        public ViewHolder(View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.taskTitle);
            descriptionText = itemView.findViewById(R.id.taskDescription);
            pointsText = itemView.findViewById(R.id.taskPoints);
            proofButton = itemView.findViewById(R.id.proofButton);
        }
    }
}