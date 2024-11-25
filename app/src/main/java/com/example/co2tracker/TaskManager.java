package com.example.co2tracker;

import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private final List<Task> tasks;

    public TaskManager() {
        this.tasks = createDefaultTasks();
    }

    private List<Task> createDefaultTasks() {
        List<Task> defaultTasks = new ArrayList<>();
        
        defaultTasks.add(new Task(
            "Use Public Transport",
            "Take a bus or train instead of driving",
            1500,
            2.5
        ));
        
        defaultTasks.add(new Task(
            "Plant a Tree",
            "Plant a tree in your garden or local park",
            1500,
            0.5
        ));
        
        defaultTasks.add(new Task(
            "Zero Waste Day",
            "Try to produce no waste for a day",
            1500,
            1.0
        ));
        
        defaultTasks.add(new Task(
            "Ride a Bike",
            "Use a bike instead of driving",
            1500,
            2.0
        ));
        
        defaultTasks.add(new Task(
            "Buy Local Food",
            "Purchase food from local producers",
            1500,
            1.5
        ));
        
        defaultTasks.add(new Task(
            "Save Energy",
            "Turn off unused lights and appliances",
            1500,
            3.0
        ));
        
        defaultTasks.add(new Task(
            "Start Composting",
            "Begin composting your food waste",
            1500,
            0.8
        ));
        
        defaultTasks.add(new Task(
            "Use Reusable Bags",
            "Bring your own bags when shopping",
            1500,
            0.7
        ));
        
        defaultTasks.add(new Task(
            "Save Water",
            "Take shorter showers and fix leaks",
            1500,
            0.6
        ));
        
        defaultTasks.add(new Task(
            "Meatless Monday",
            "Eat vegetarian for a day",
            1500,
            4.0
        ));
        
        return defaultTasks;
    }

    public List<Task> getTasks() {
        return new ArrayList<>(tasks);
    }

    public void completeTask(Task task) {
        int index = tasks.indexOf(task);
        if (index != -1) {
            tasks.get(index).setCompleted(true);
        }
    }

    public void resetTasks() {
        for (Task task : tasks) {
            task.setCompleted(false);
        }
    }
}
