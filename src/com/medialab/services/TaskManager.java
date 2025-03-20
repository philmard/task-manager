package com.medialab.services;

import java.util.ArrayList;
import java.util.List;

import com.medialab.models.Category;
import com.medialab.models.PriorityLevel;
import com.medialab.models.Task;
import com.medialab.models.Reminder;

/*
 * Task Manager stores Tasks, Categories and Priorities in separate lists.
 * This is done in order to allow for more flexibility in the creation and managing of Categories and Priorities.
 * 
 * (!) Note that neither Categories nor Priorities are tied to any specific Task necessarily.
 * This means that they can exist independently of Tasks.
 * 
 * (!) Reminders, on the other hand, are always tied to a specific Task.
 */
public class TaskManager {
    private List<Task> tasks;
    private List<Category> categories;
    private List<PriorityLevel> priorities;

    public TaskManager() {
        tasks = new ArrayList<>();
        categories = new ArrayList<>();
        priorities = new ArrayList<>();
    }

    /*
     * Task Management Methods
     */
    
    public List<Task> getTasks() {
        return tasks;
    }
    
    public void addTask(Task task) {
        tasks.add(task);
    }

    public void deleteTask(Task task) {
        tasks.remove(task);
    }
    
    /*
     * Category Management Methods
     */
    
    public List<Category> getCategories() {
        return categories;
    }
    
    public void addCategory(Category category) {
    	// Check if the category already exists
        if (!getCategories().contains(category)) {
            categories.add(category);
        }
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
        
        // Update the priority in the priorities list
        int index = categories.indexOf(oldCategory);
        if (index != -1) {
            categories.set(index, updatedCategory);
        }
    }
    
    public Category findCategoryByTitle(String title) {
        for (Category category : categories) {
            if (category.getTitle().equals(title)) {
                return category;
            }
        }
        return null;
    }
    
    /*
     * Priority Management Methods
     */

    public List<PriorityLevel> getPriorities() {
        return priorities;
    }
    
    public void addPriority(PriorityLevel priority) {
        // Check if the priority already exists
        if (!getPriorities().contains(priority)) {
            priorities.add(priority);
        }
    }
    
    public void deletePriority(PriorityLevel priority) {
        // Find the default priority
        PriorityLevel defaultPriority = findPriorityByTitle("Default");

        // Update all tasks with the deleted priority to the default priority
        for (Task task : tasks) {
            if (task.getPriority() != null && task.getPriority().equals(priority)) {
                task.setPriority(defaultPriority);
            }
        }

        // Remove the priority from the list
        getPriorities().remove(priority);
    }
    
    public void updatePriority(PriorityLevel oldPriority, PriorityLevel updatedPriority) {
        // Update the priority in all tasks that use the old priority
        for (Task task : tasks) {
            if (task.getPriority() != null && task.getPriority().equals(oldPriority)) {
                task.setPriority(updatedPriority);
            }
        }
        
        // Update the priority in the priorities list
        int index = priorities.indexOf(oldPriority);
        if (index != -1) {
            priorities.set(index, updatedPriority);
        }
    }
    
    public PriorityLevel findPriorityByTitle(String title) {
        for (PriorityLevel priority : getPriorities()) {
            if (priority.getTitle().equals(title)) {
                return priority;
            }
        }
        return null;
    }
    
    /*
     * Reminder Management Methods
     */
    
    /* 
     * Slightly different from getCategories and getPriorities
     */
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