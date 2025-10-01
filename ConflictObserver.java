package com.example.astronaut;

public interface ConflictObserver {
    void onConflict(Task newTask, Task conflictingTask);
    void onTaskAdded(Task task);
    void onTaskRemoved(Task task);
}