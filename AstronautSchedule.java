package com.example.astronaut;

import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AstronautSchedule {
    private static final Logger LOGGER = Logger.getLogger(AstronautSchedule.class.getName());
    private final ScheduleManager manager;

    public AstronautSchedule() {
        Logger root = Logger.getLogger("");
        root.setLevel(Level.INFO);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO);
        root.addHandler(handler);

        manager = ScheduleManager.getInstance();
        manager.addObserver(new ConflictNotifier());
    }

    private void printHelp() {
        System.out.println("Commands:");
        System.out.println(" add \"desc\" HH:mm HH:mm [priority]");
        System.out.println(" remove <task-id-prefix>");
        System.out.println(" view");
        System.out.println(" view-priority [LOW|MEDIUM|HIGH]");
        System.out.println(" edit <id-prefix> \"desc\" HH:mm HH:mm [priority]");
        System.out.println(" complete <id-prefix>");
        System.out.println(" help | exit");
    }

    private String expandIdPrefix(String prefix) {
        for (Task t : manager.getAllTasksSorted()) {
            if (t.getId().startsWith(prefix)) return t.getId();
        }
        return prefix;
    }

    private void runInteractive() {
        Scanner sc = new Scanner(System.in);
        printHelp();
        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;
            try {
                if (line.equalsIgnoreCase("exit")) break;
                if (line.equalsIgnoreCase("help")) { printHelp(); continue; }
                if (line.startsWith("add ")) {
                    String[] parts = parseQuoted(line.substring(4));
                    if (parts == null) { System.out.println("Invalid add syntax."); continue; }
                    String desc = parts[0];
                    String[] tokens = parts[1].trim().split("\s+");
                    if (tokens.length < 2) { System.out.println("Need start and end time."); continue; }
                    String start = tokens[0], end = tokens[1];
                    String pr = tokens.length >= 3 ? tokens[2] : "MEDIUM";
                    Task t = TaskFactory.createTask(desc, start, end, pr);
                    manager.addTask(t);
                    System.out.println("Task added successfully. id=" + t.getId().substring(0,8));
                } else if (line.startsWith("remove ")) {
                    String pref = line.substring(7).trim();
                    String id = expandIdPrefix(pref);
                    manager.removeTask(id);
                    System.out.println("Task removed successfully.");
                } else if (line.equals("view")) {
                    List<Task> all = manager.getAllTasksSorted();
                    if (all.isEmpty()) { System.out.println("No tasks scheduled for the day."); continue; }
                    all.forEach(t -> System.out.println(t));
                } else if (line.startsWith("view-priority")) {
                    String[] tok = line.split("\s+");
                    if (tok.length < 2) { System.out.println("Specify priority."); continue; }
                    TaskPriority p = TaskPriority.fromString(tok[1]);
                    List<Task> byPr = manager.getTasksByPriority(p);
                    if (byPr.isEmpty()) System.out.println("No tasks for priority " + p);
                    byPr.forEach(System.out::println);
                } else if (line.startsWith("edit ")) {
                    String rest = line.substring(5).trim();
                    String[] firstSplit = rest.split("\s+", 2);
                    if (firstSplit.length < 2) { System.out.println("Invalid edit syntax."); continue; }
                    String idpref = firstSplit[0];
                    String[] parts = parseQuoted(firstSplit[1]);
                    if (parts == null) { System.out.println("Invalid edit syntax."); continue; }
                    String desc = parts[0];
                    String[] tokens = parts[1].trim().split("\s+");
                    if (tokens.length < 2) { System.out.println("Need start and end time."); continue; }
                    String start = tokens[0], end = tokens[1];
                    String pr = tokens.length >= 3 ? tokens[2] : "MEDIUM";
                    String id = expandIdPrefix(idpref);
                    manager.editTask(id, desc, LocalTime.parse(start), LocalTime.parse(end), TaskPriority.fromString(pr));
                    System.out.println("Task edited successfully.");
                } else if (line.startsWith("complete ")) {
                    String pref = line.substring(9).trim();
                    String id = expandIdPrefix(pref);
                    manager.getTaskById(id).orElseThrow(() -> new NoSuchElementException("Task not found")).setCompleted(true);
                    System.out.println("Task marked completed.");
                } else {
                    System.out.println("Unknown command. Type help.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                LOGGER.log(Level.WARNING, "Exception in command processing", e);
            }
        }
        sc.close();
        System.out.println("Bye.");
    }

    private String[] parseQuoted(String s) {
        s = s.trim();
        if (!s.startsWith("\"")) return null;
        int endQuote = s.indexOf("\", 1);
        if (endQuote <= 0) return null;
        String desc = s.substring(1, endQuote);
        String rest = s.substring(endQuote + 1).trim();
        return new String[]{desc, rest};
    }

    public static void main(String[] args) {
        new AstronautSchedule().runInteractive();
    }
}