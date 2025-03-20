package com.medialab.services;

import com.google.gson.*;
import com.medialab.models.*;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages persistence operations for task management entities including Tasks, Categories,
 * PriorityLevels, and Reminders using JSON serialization. All data is stored in a
 * predefined file location specified by {@link #FILE_PATH}.
 */
public class DataManager {
    private static final String FILE_PATH = "medialab/data.json";
    private Gson gson;
    
    /**
     * Constructs a DataManager with custom Gson configuration for LocalDate handling.
     * Registers type adapters to properly serialize/deserialize LocalDate objects
     * and enables pretty-printing for better JSON readability.
     */
    public DataManager() {
        // Register custom adapters for LocalDate serialization/deserialization
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>)
                        (src, typeOfSrc, context) -> new JsonPrimitive(src.toString()))
                .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>)
                        (json, typeOfT, context) -> LocalDate.parse(json.getAsString()))
                .setPrettyPrinting()
                .create();
    }
    
    /**
     * Serializes and saves task management data to a JSON file.
     * <p>
     * Stores tasks with their complete structure including nested reminders, while
     * categories and priorities are stored as independent entities. Reminders are
     * automatically persisted through their containing Task objects.
     *
     * @param tasks       list of tasks to save (including their associated reminders)
     * @param categories  list of available task categories
     * @param priorities  list of defined priority levels
     * @throws IOException if any I/O error occurs during file writing (caught and printed internally)
     */
    public void saveData(List<Task> tasks, List<Category> categories, List<PriorityLevel> priorities) {
        try (FileWriter writer = new FileWriter(FILE_PATH)) {
            JsonObject jsonData = new JsonObject();

            // Convert categories to JSON
            JsonArray categoryArray = new JsonArray();
            for (Category category : categories) {
                JsonObject categoryObject = new JsonObject();
                categoryObject.addProperty("title", category.getTitle());
                categoryArray.add(categoryObject);
            }

            // Convert priorities to JSON
            JsonArray priorityArray = new JsonArray();
            for (PriorityLevel priority : priorities) {
                JsonObject priorityObject = new JsonObject();
                priorityObject.addProperty("title", priority.getTitle());
                priorityArray.add(priorityObject);
            }

            // Convert tasks to JSON (with all fields)
            JsonArray taskArray = new JsonArray();
            for (Task task : tasks) {
                JsonObject taskObject = new JsonObject();
                taskObject.addProperty("title", task.getTitle());
                taskObject.addProperty("description", task.getDescription());
                taskObject.addProperty("status", task.getStatus());
                
                // Store category as a string (category title)
                taskObject.addProperty("category", task.getCategory() != null ? task.getCategory().getTitle() : null);
                
                // Store priority as a string
                taskObject.addProperty("priority", task.getPriority() != null ? task.getPriority().getTitle() : null);
                
                // Store deadline
                taskObject.addProperty("deadline", task.getDeadline() != null ? task.getDeadline().toString() : null);
                
                // Store reminders as an array
                JsonArray reminderArray = new JsonArray();
                for (Reminder reminder : task.getReminders()) {
                    JsonObject reminderObject = new JsonObject();
                    reminderObject.addProperty("date", reminder.getDate().toString());
                    reminderObject.addProperty("message", reminder.getMessage());
                    reminderArray.add(reminderObject);
                }
                taskObject.add("reminders", reminderArray);
                taskArray.add(taskObject);
            }

            // Store everything in the JSON file
            jsonData.add("tasks", taskArray);
            jsonData.add("categories", categoryArray);
            jsonData.add("priorities", priorityArray);

            gson.toJson(jsonData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Loads and deserializes task management data from the JSON file.
     * <p>
     * Reconstructs the complete object hierarchy including category/priority associations
     * and nested reminders. Loaded data is directly populated into the provided TaskManager.
     *
     * @param taskManager the TaskManager instance to populate with loaded data
     * @throws IOException            if any I/O error occurs during file reading (caught and printed internally)
     * @throws JsonParseException     if the JSON data is malformed (handled internally by Gson)
     * @throws JsonSyntaxException    if there's a type mismatch during deserialization (handled internally by Gson)
     */
    public void loadData(TaskManager taskManager) {
        try (FileReader reader = new FileReader(FILE_PATH)) {
            JsonObject jsonData = gson.fromJson(reader, JsonObject.class);
            if (jsonData == null) return;

            // Load categories
            JsonArray categoryArray = jsonData.getAsJsonArray("categories");
            if (categoryArray != null) {
                for (JsonElement categoryElement : categoryArray) {
                    String title = categoryElement.getAsJsonObject().get("title").getAsString();
                    taskManager.addCategory(new Category(title));
                }
            }

            // Load priorities
            JsonArray priorityArray = jsonData.getAsJsonArray("priorities");
            if (priorityArray != null) {
                for (JsonElement priorityElement : priorityArray) {
                    String title = priorityElement.getAsJsonObject().get("title").getAsString();
                    taskManager.addPriority(new PriorityLevel(title));
                }
            }

            // Load tasks
            JsonArray taskArray = jsonData.getAsJsonArray("tasks");
            if (taskArray != null) {
                for (JsonElement taskElement : taskArray) {
                    JsonObject taskObject = taskElement.getAsJsonObject();

                    String title = taskObject.get("title").getAsString();
                    String description = taskObject.get("description").getAsString();
                    String status = taskObject.get("status").getAsString();

                    // Retrieve category by title
                    String categoryTitle = taskObject.has("category") && !taskObject.get("category").isJsonNull()
                            ? taskObject.get("category").getAsString()
                            : null;
                    Category category = categoryTitle != null ? taskManager.findCategoryByTitle(categoryTitle) : null;

                    // Retrieve priority by title
                    String priorityName = taskObject.has("priority") && !taskObject.get("priority").isJsonNull()
                            ? taskObject.get("priority").getAsString()
                            : null;
                    PriorityLevel priority = priorityName != null ? taskManager.findPriorityByTitle(priorityName) : null;

                    // Retrieve deadline
                    LocalDate deadline = taskObject.has("deadline") && !taskObject.get("deadline").isJsonNull()
                            ? LocalDate.parse(taskObject.get("deadline").getAsString())
                            : null;

                    // Retrieve reminders
                    List<Reminder> reminders = new ArrayList<>();
                    JsonArray reminderArray = taskObject.getAsJsonArray("reminders");
                    if (reminderArray != null) {
                        for (JsonElement reminderElement : reminderArray) {
                            JsonObject reminderObject = reminderElement.getAsJsonObject();
                            LocalDate reminderDate = LocalDate.parse(reminderObject.get("date").getAsString());
                            String message = reminderObject.get("message").getAsString();
                            reminders.add(new Reminder(reminderDate, message));
                        }
                    }

                    // Create and add task
                    Task task = new Task(title, description, category, priority, deadline, status);
                    for (Reminder reminder : reminders) {
                        task.addReminder(reminder);
                    }
                    taskManager.addTask(task);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}