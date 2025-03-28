package com.medialab.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Task {
    private String title;
    private String description;
    private Category category; // Category is an object of type Category
    private PriorityLevel priority; // Priority is an object of type PriorityLevel
    private LocalDate deadline;
    private String status;
    private List<Reminder> reminders; // List of reminders

    public Task(String title, String description, Category category, PriorityLevel priority, LocalDate deadline, String status) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
        this.deadline = deadline;
        this.status = status;
        this.reminders = new ArrayList<>();
    }

    // Getters and Setters

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public PriorityLevel getPriority() { return priority; }
    public void setPriority(PriorityLevel priority) { this.priority = priority; }

    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<Reminder> getReminders() { return reminders; }
    public void addReminder(Reminder reminder) { this.reminders.add(reminder); }

    /**
     * Display of Tasks with all their fields also shown.
     */
    @Override
    public String toString() {
        String categoryText = (category != null) ? category.getTitle() : "No Category";
        String priorityText = (priority != null) ? priority.getTitle() : "No Priority";
        String deadlineText = (deadline != null) ? deadline.toString() : "No Deadline";
        String descriptionText = (description != null && !description.isEmpty()) ? description : "No Description";
        
        // Convert reminders list to a readable string format
        String remindersText = reminders.isEmpty() ? "No Reminders" :
                reminders.stream()
                        .map(Reminder::toString)
                        .reduce((r1, r2) -> r1 + ", " + r2)
                        .orElse("");

        return title + "\n" +
               "Status: " + status + "\n" +
               "Category: " + categoryText + "\n" +
               "Priority: " + priorityText + "\n" +
               "Deadline: " + deadlineText + "\n" +
               "Description: " + descriptionText + "\n" +
               "Reminders: " + remindersText;
    }
}