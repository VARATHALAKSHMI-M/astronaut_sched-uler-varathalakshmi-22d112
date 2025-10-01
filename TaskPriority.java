package com.example.astronaut;

public enum TaskPriority {
    LOW, MEDIUM, HIGH;

    public static TaskPriority fromString(String s) {
        if (s == null) return MEDIUM;
        switch (s.trim().toUpperCase()) {
            case "HIGH": return HIGH;
            case "LOW": return LOW;
            default: return MEDIUM;
        }
    }
}