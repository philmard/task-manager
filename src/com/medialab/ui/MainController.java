package com.medialab.ui;

import com.medialab.models.Task;
import com.medialab.models.Category;
import com.medialab.models.PriorityLevel;
import com.medialab.models.Reminder;

import com.medialab.services.TaskManager;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class MainController {
    @FXML private ListView<Task> tasksListView;
    @FXML private ListView<Category> categoryListView;
    @FXML private ListView<PriorityLevel> priorityListView;
    @FXML private ListView<Reminder> reminderListView;
    
    @FXML private TextField taskTitleField;
    @FXML private TextField taskDescriptionField;
    @FXML private ComboBox<Category> taskCategoryComboBox; // Use ComboBox for Category
    @FXML private ComboBox<PriorityLevel> taskPriorityComboBox; // Use ComboBox for Priority
    @FXML private DatePicker taskDeadlinePicker;
    @FXML private ComboBox<String> taskStatusComboBox;
    
    @FXML private TextField categoryTitleField; // For new category
    
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label delayedTasksLabel;
    @FXML private Label dueSoonTasksLabel;

    private TaskManager taskManager;

    public void initialize() {
        taskManager = new TaskManager();
        
        // Populate ComboBoxes with Category and Priority
        taskCategoryComboBox.getItems().setAll(taskManager.getCategories()); // taskManager for now (!)
//        taskPriorityComboBox.getItems().setAll(taskManager.getPriorities());
        
        // Populate the ComboBox with valid status options
        taskStatusComboBox.getItems().addAll("Open", "In Progress", "Postponed", "Completed", "Delayed");
        
        // Set the default value
        taskStatusComboBox.setValue("Open");
        
        updateUI();
    }

    public void updateUI() {
        tasksListView.getItems().setAll(taskManager.getTasks());
        categoryListView.getItems().setAll(taskManager.getCategories()); // also show the category list
        updateSummary();
        // also update the combobox for categories:
        taskCategoryComboBox.getItems().setAll(taskManager.getCategories());
    }

    public void setTasks(List<Task> tasks) {
        taskManager.setTasks(tasks);
        updateUI();
    }

    public List<Task> getTasks() {
        return taskManager.getTasks();
    }

    @FXML
    private void onAddTask() {
        String title = taskTitleField.getText();
        String description = taskDescriptionField.getText();
        Category category = taskCategoryComboBox.getValue(); // Use Category object
        PriorityLevel priority = taskPriorityComboBox.getValue(); // Use PriorityLevel object
        LocalDate deadline = taskDeadlinePicker.getValue();
        String status = taskStatusComboBox.getValue();

        if (title.isEmpty()) {
            showAlert("Error", "Title cannot be empty!");
            return;
        }

        Task newTask = new Task(title, description, category, priority, deadline, status);
        taskManager.addTask(newTask);
        updateUI();
        clearTaskInputFields();
    }

    @FXML
    private void onDeleteTask() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to delete.");
            return;
        }

        taskManager.deleteTask(selectedTask);
        updateUI();
    }

    @FXML
    private void onUpdateTask() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to update.");
            return;
        }

        selectedTask.setTitle(taskTitleField.getText());
        selectedTask.setDescription(taskDescriptionField.getText());
        selectedTask.setCategory(taskCategoryComboBox.getValue()); // Update Category
        selectedTask.setPriority(taskPriorityComboBox.getValue()); // Update Priority
        selectedTask.setDeadline(taskDeadlinePicker.getValue());
        selectedTask.setStatus(taskStatusComboBox.getValue());

        updateUI();
    }
    
    // -------------------------- CATEGORIES --------------------------
    public List<Category> getCategories() {
        return taskManager.getCategories();
    }
    
    @FXML
    private void onAddCategory() {
        String title = categoryTitleField.getText();

        if (title.isEmpty()) {
            showAlert("Error", "Title of Category cannot be empty!");
            return;
        }

        Category newCategory = new Category(title);
        taskManager.addCategory(newCategory);
        updateUI();
        clearCategoryInputFields();
    }
    
    @FXML
    private void onDeleteCategory() {
        Category selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert("Error", "Please select a category to delete.");
            return;
        }

        taskManager.deleteCategory(selectedCategory);
        updateUI();
    }

    @FXML
    private void onUpdateCategory() {
        Category selectedCategory = categoryListView.getSelectionModel().getSelectedItem();
        if (selectedCategory == null) {
            showAlert("Error", "Please select a category to update.");
            return;
        }

        String updatedTitle = categoryTitleField.getText();
        if (updatedTitle.isEmpty()) {
            showAlert("Error", "Category title cannot be empty!");
            return;
        }

        // Create a new category with the updated title
        Category updatedCategory = new Category(updatedTitle);

        // Update tasks with the old category title to the new category title
        taskManager.updateCategory(selectedCategory, updatedCategory);
        updateUI();
    }

    @FXML
    private void clearTaskInputFields() {
        taskTitleField.clear();
        taskDescriptionField.clear();
        taskCategoryComboBox.setValue(null);
        taskPriorityComboBox.setValue(null);
        taskDeadlinePicker.setValue(null);
        taskStatusComboBox.setValue("Open");
    }
    
    @FXML
    private void clearCategoryInputFields() {
    	categoryTitleField.clear();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public TaskManager getTaskManager() {
        return taskManager;
    }
    
    private void updateSummary() {
        List<Task> tasks = taskManager.getTasks();
        
        int totalTasks = tasks.size();
        int completedTasks = (int) tasks.stream().filter(t -> "Completed".equals(t.getStatus())).count();
        int delayedTasks = (int) tasks.stream().filter(t -> "Delayed".equals(t.getStatus())).count();
        
        LocalDate today = LocalDate.now();
        int dueSoonTasks = (int) tasks.stream()
            .filter(t -> t.getDeadline() != null && !t.getDeadline().isBefore(today) && t.getDeadline().isBefore(today.plusDays(7)))
            .count();

        // Update of Labels
        totalTasksLabel.setText(String.valueOf(totalTasks));
        completedTasksLabel.setText(String.valueOf(completedTasks));
        delayedTasksLabel.setText(String.valueOf(delayedTasks));
        dueSoonTasksLabel.setText(String.valueOf(dueSoonTasks));
    }
}