package com.medialab.models;

import java.time.LocalDate;

public class Reminder {
    private LocalDate date;
    private String message;

    // Constructor with date and message
    public Reminder(LocalDate date, String message) {
        this.date = date;
        this.message = message;
    }

    // Getters
    public LocalDate getDate() { return date; }
    public String getMessage() { return message; }

    // Setters
    public void setDate(LocalDate date) { this.date = date; }
    public void setMessage(String message) { this.message = message; }

    /**
     * Display of Reminders in the following form: "2025-02-07: reminder message"
     */
    @Override
    public String toString() {
        return (date + ": " + message);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Reminder reminder = (Reminder) obj;
        return date.equals(reminder.date) && message.equals(reminder.message);
    }

    @Override
    public int hashCode() {
        return 31 * date.hashCode() + message.hashCode();
    }
}