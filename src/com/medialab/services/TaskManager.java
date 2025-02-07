package com.medialab.services;

import java.util.ArrayList;
import java.util.List;

import com.medialab.models.Category;
import com.medialab.models.PriorityLevel;
import com.medialab.models.Task;
import com.medialab.models.Reminder;

public class TaskManager {
    private List<Task> tasks;
    private List<Category> categories;

    public TaskManager() {
        tasks = new ArrayList<>();
        categories = new ArrayList<>();
    }

    // ------------------------------ Task Management Methods ------------------------------
    public void addTask(Task task) {
        tasks.add(task);
    }

    public void updateTask(Task oldTask, Task newTask) {
        // Update logic (you can implement logic to update the fields of tasks)
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
    }

    public List<Task> getTasks() {
        return tasks;
    }

    // Load tasks when the app starts
    public void setTasks(List<Task> loadedTasks) {
        if (loadedTasks != null) {
            this.tasks = loadedTasks;
        }
    }
    
    // ------------------------------ Category Management Methods ------------------------------
    public void addCategory(Category category) {
        categories.add(category);
    }

    public void deleteCategory(Category category) {
        // Remove the category from the categories list
        categories.remove(category);

        // Remove all tasks that are associated with this category
        tasks.removeIf(task -> category.equals(task.getCategory()));
    }

    public void updateCategory(Category oldCategory, Category updatedCategory) {
        // Update the category title in all tasks that use the old category
        for (Task task : tasks) {
            if (task.getCategory() != null && task.getCategory().equals(oldCategory)) {
                task.getCategory().setTitle(updatedCategory.getTitle());  // Update the category title in task
            }
        }
    }

    public List<Category> getCategories() {
        return categories;
    }
    
    public Category findCategoryByTitle(String title) {
        for (Category category : categories) {
            if (category.getTitle().equals(title)) {
                return category;
            }
        }
        return null;
    }
    
    // ------------------------------ Priority Management Methods ------------------------------

    public List<PriorityLevel> getPriorities() {
        List<PriorityLevel> priorities = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPriority() != null && !priorities.contains(task.getPriority())) {
                priorities.add(task.getPriority());
            }
        }
        return priorities;
    }
    
    // ------------------------------ Reminder Management Methods ------------------------------

    public List<Reminder> getReminders() {
        List<Reminder> reminders = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getReminders() != null) {
                reminders.addAll(task.getReminders());
            }
        }
        return reminders;
    }
}