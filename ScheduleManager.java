package com.example.astronaut;

import java.time.LocalTime;
import java.util.*;
import java.util.logging.Logger;

public class ScheduleManager {
    private static final Logger LOGGER = Logger.getLogger(ScheduleManager.class.getName());
    private static ScheduleManager instance = null;

    private final Map<String, Task> tasksById;
    private final List<ConflictObserver> observers;

    private ScheduleManager() {
        tasksById = new HashMap<>();
        observers = new ArrayList<>();
    }

    public static synchronized ScheduleManager getInstance() {
        if (instance == null) {
            instance = new ScheduleManager();
        }
        return instance;
    }

    public void addObserver(ConflictObserver obs) {
        observers.add(obs);
    }

    public void removeObserver(ConflictObserver obs) {
        observers.remove(obs);
    }

    private void notifyConflict(Task newTask, Task existing) {
        for (ConflictObserver obs : observers) obs.onConflict(newTask, existing);
    }

    private void notifyAdded(Task t) {
        for (ConflictObserver obs : observers) obs.onTaskAdded(t);
    }

    private void notifyRemoved(Task t) {
        for (ConflictObserver obs : observers) obs.onTaskRemoved(t);
    }

    public synchronized void addTask(Task t) {
        for (Task existing : tasksById.values()) {
            if (t.overlaps(existing)) {
                notifyConflict(t, existing);
                throw new IllegalStateException("Task conflicts with existing task: " + existing.getDescription());
            }
        }
        tasksById.put(t.getId(), t);
        LOGGER.info("Task added: " + t);
        notifyAdded(t);
    }

    public synchronized void removeTask(String id) {
        Task removed = tasksById.remove(id);
        if (removed == null) {
            throw new NoSuchElementException("Task not found with id " + id);
        }
        notifyRemoved(removed);
    }

    public synchronized List<Task> getAllTasksSorted() {
        List<Task> list = new ArrayList<>(tasksById.values());
        Collections.sort(list);
        return list;
    }

    public synchronized Optional<Task> getTaskById(String id) {
        return Optional.ofNullable(tasksById.get(id));
    }

    public synchronized List<Task> getTasksByPriority(TaskPriority pr) {
        List<Task> out = new ArrayList<>();
        for (Task t : tasksById.values()) {
            if (t.getPriority() == pr) out.add(t);
        }
        out.sort(Comparator.naturalOrder());
        return out;
    }

    public synchronized void editTask(String id, String newDesc, LocalTime newStart, LocalTime newEnd, TaskPriority newPr) {
        Task t = tasksById.get(id);
        if (t == null) throw new NoSuchElementException("Task not found");
        Task temp = new Task(newDesc, newStart, newEnd, newPr);
        for (Task other : tasksById.values()) {
            if (other.getId().equals(id)) continue;
            if (temp.overlaps(other)) {
                notifyConflict(temp, other);
                throw new IllegalStateException("Edited task would conflict with existing task: " + other.getDescription());
            }
        }
        t.setDescription(newDesc);
        t.setStart(newStart);
        t.setEnd(newEnd);
        t.setPriority(newPr);
        LOGGER.info("Task edited: " + t);
    }
}