package com.example.astronaut;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

public class TaskFactory {
    private static final Logger LOGGER = Logger.getLogger(TaskFactory.class.getName());

    public static Task createTask(String description, String startStr, String endStr, String priorityStr) {
        try {
            LocalTime start = LocalTime.parse(startStr);
            LocalTime end = LocalTime.parse(endStr);
            if (!start.isBefore(end)) {
                throw new IllegalArgumentException("Start time must be before end time.");
            }
            TaskPriority priority = TaskPriority.fromString(priorityStr);
            return new Task(description, start, end, priority);
        } catch (DateTimeParseException e) {
            LOGGER.severe("Invalid time format. Use HH:mm. " + e.getMessage());
            throw new IllegalArgumentException("Invalid time format. Use HH:mm.");
        }
    }
}