package com.example.MAPF.service;

import java.sql.Timestamp;

public class Task {
    int[] targetLocation;
    TaskState state = TaskState.ONHOLD;
    TaskPriority priority;

    public Task(int[] targetLocation, TaskPriority priority) {
        this.targetLocation = targetLocation;
        this.priority = priority;
    }
}



