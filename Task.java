package com.example.astronaut;

import java.time.LocalTime;
import java.util.UUID;

public class Task implements Comparable<Task> {
    private final String id;
    private String description;
    private LocalTime start;
    private LocalTime end;
    private TaskPriority priority;
    private boolean completed;

    public Task(String description, LocalTime start, LocalTime end, TaskPriority priority) {
        this.id = UUID.randomUUID().toString();
        this.description = description;
        this.start = start;
        this.end = end;
        this.priority = priority;
        this.completed = false;
    }

    public String getId() { return id; }
    public String getDescription() { return description; }
    public LocalTime getStart() { return start; }
    public LocalTime getEnd() { return end; }
    public TaskPriority getPriority() { return priority; }
    public boolean isCompleted() { return completed; }

    public void setDescription(String description) { this.description = description; }
    public void setStart(LocalTime start) { this.start = start; }
    public void setEnd(LocalTime end) { this.end = end; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public boolean overlaps(Task other) {
        return this.start.isBefore(other.end) && other.start.isBefore(this.end);
    }

    @Override
    public int compareTo(Task o) {
        return this.start.compareTo(o.start);
    }

    @Override
    public String toString() {
        String done = completed ? "[Completed]" : "";
        return String.format("%s - %s: %s [%s] %s (id=%s)",
                start, end, description, priority, done, id.substring(0,8));
    }
}