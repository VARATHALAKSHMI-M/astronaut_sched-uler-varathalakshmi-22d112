package com.example.astronaut;

import java.util.logging.Logger;

public class ConflictNotifier implements ConflictObserver {
    private static final Logger LOGGER = Logger.getLogger(ConflictNotifier.class.getName());

    @Override
    public void onConflict(Task newTask, Task conflictingTask) {
        LOGGER.warning(String.format("Conflict detected: New task '%s' conflicts with existing '%s'",
                newTask.getDescription(), conflictingTask.getDescription()));
        System.out.println("ERROR: Task conflicts with existing task: " + conflictingTask.getDescription());
    }

    @Override
    public void onTaskAdded(Task task) {
        LOGGER.info("Task added: " + task.getDescription());
    }

    @Override
    public void onTaskRemoved(Task task) {
        LOGGER.info("Task removed: " + task.getDescription());
    }
}