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
import java.util.stream.Collectors;

public class MainController {
	// List Views
    @FXML private ListView<Task> tasksListView;
    @FXML private ListView<Category> categoryListView;
    @FXML private ListView<PriorityLevel> priorityListView;
    @FXML private ListView<Reminder> reminderListView;
    
    // Search Inputs
    @FXML private TextField searchTitleField;
    @FXML private ComboBox<Category> searchCategoryComboBox;
    @FXML private ComboBox<PriorityLevel> searchPriorityComboBox;
    
    // Task Inputs
    @FXML private TextField taskTitleField;
    @FXML private TextField taskDescriptionField;
    @FXML private ComboBox<Category> taskCategoryComboBox; // Use ComboBox for Category
    @FXML private ComboBox<PriorityLevel> taskPriorityComboBox; // Use ComboBox for Priority
    @FXML private DatePicker taskDeadlinePicker;
    @FXML private ComboBox<String> taskStatusComboBox;
    
    // Category Inputs
    @FXML private TextField categoryTitleField;
    
    // Priority Inputs
    @FXML private TextField priorityTitleField;
    
    // Reminder Inputs
    @FXML private TextField reminderMessageField;
    @FXML private ComboBox<String> reminderDateComboBox;
    @FXML private DatePicker reminderDatePicker;
    
    // Summary
    @FXML private Label totalTasksLabel;
    @FXML private Label completedTasksLabel;
    @FXML private Label delayedTasksLabel;
    @FXML private Label dueSoonTasksLabel;

    private TaskManager taskManager;

    public void initialize() {
        taskManager = new TaskManager();

        // Ensure the default priority exists
        PriorityLevel defaultPriority = new PriorityLevel("Default");
        if (!taskManager.getPriorities().contains(defaultPriority)) {
            taskManager.addPriority(defaultPriority);
        }

        // Populate the ComboBox with valid status options
        taskStatusComboBox.getItems().addAll("Open", "In Progress", "Postponed", "Completed", "Delayed");
        taskStatusComboBox.setValue("Open");

        // Populate the Reminder ComboBox with reminder types
        reminderDateComboBox.getItems().addAll(
            "One day before deadline",
            "One week before deadline",
            "One month before deadline",
            "Specific date"
        );

        // Update the UI to populate all ComboBoxes and ListViews
        updateUI();
    }

    public void updateUI() {
        // Update the List Views
        tasksListView.getItems().setAll(taskManager.getTasks());
        categoryListView.getItems().setAll(taskManager.getCategories());
        priorityListView.getItems().setAll(taskManager.getPriorities());
        reminderListView.getItems().setAll(taskManager.getReminders());

        // Update the summary
        updateSummary();

        // Update the Task ComboBoxes
        taskCategoryComboBox.getItems().setAll(taskManager.getCategories());
        taskPriorityComboBox.getItems().setAll(taskManager.getPriorities());

        // Update the Search ComboBoxes
        searchCategoryComboBox.getItems().setAll(taskManager.getCategories());
        searchPriorityComboBox.getItems().setAll(taskManager.getPriorities());

        // Set custom cell factories and button cells for all ComboBoxes
        // apart from Select Priority, because that should be set to "Default".
        setComboBoxPlaceholder(taskCategoryComboBox, "Select Category");
        setComboBoxPlaceholder(searchCategoryComboBox, "Search by Category");
        setComboBoxPlaceholder(searchPriorityComboBox, "Search by Priority");
    }

    /**
     * Helper method to set a custom cell factory and button cell for a ComboBox.
     * (This is done in order to always have a placeholder, instead of an empty one when the value is null.) 
     *
     * @param comboBox The ComboBox to configure.
     * @param placeholder The placeholder text to display when no item is selected.
     */
    private <T> void setComboBoxPlaceholder(ComboBox<T> comboBox, String placeholder) {
        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(placeholder);
                } else {
                    setText(item.toString());
                }
            }
        });

        comboBox.setCellFactory(param -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(placeholder);
                } else {
                    setText(item.toString());
                }
            }
        });
    }
    
    // -------------------------- TASKS --------------------------

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

        // Update title if the text field is not blank
        if (!taskTitleField.getText().isEmpty()) {
            selectedTask.setTitle(taskTitleField.getText());
        }

        // Update description if the text field is not blank
        if (!taskDescriptionField.getText().isEmpty()) {
            selectedTask.setDescription(taskDescriptionField.getText());
        }

        // Update category if a category is selected
        Category selectedCategory = taskCategoryComboBox.getValue();
        if (selectedCategory != null) {
            selectedTask.setCategory(selectedCategory);
        }

        // Update priority if a priority is selected
        PriorityLevel selectedPriority = taskPriorityComboBox.getValue();
        if (selectedPriority != null) {
            selectedTask.setPriority(selectedPriority);
        }

        // Update deadline if a deadline is selected
        LocalDate selectedDeadline = taskDeadlinePicker.getValue();
        if (selectedDeadline != null) {
            selectedTask.setDeadline(selectedDeadline);
        }

        // Update status if a status is selected
        String selectedStatus = taskStatusComboBox.getValue();
        if (selectedStatus != null) {
            // If the new status is "Completed," show a confirmation dialog
            if (selectedStatus.equals("Completed") && !selectedTask.getReminders().isEmpty()) {
                // Create a confirmation dialog
                Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationDialog.setTitle("Delete Reminders");
                confirmationDialog.setHeaderText("This action will also delete the following reminders:");

                // List all reminders in the dialog
                StringBuilder remindersList = new StringBuilder();
                for (Reminder reminder : selectedTask.getReminders()) {
                    remindersList.append("- ").append(reminder.toString()).append("\n");
                }

                confirmationDialog.setContentText(remindersList.toString() + "\nAre you sure you want to proceed?");

                // Show the dialog and wait for user response
                ButtonType result = confirmationDialog.showAndWait().orElse(ButtonType.CANCEL);

                // If the user clicks "Cancel," do not update the status or delete reminders
                if (result != ButtonType.OK) {
                    return;
                }

                // If the user clicks "OK," delete the reminders
                selectedTask.getReminders().clear();
            }

            // Update the task's status
            selectedTask.setStatus(selectedStatus);
        }

        updateUI();
        clearTaskInputFields();
    }
    
    @FXML
    private void onSearchTasks() {
        String searchTitle = searchTitleField.getText().trim().toLowerCase();
        Category searchCategory = searchCategoryComboBox.getValue();
        PriorityLevel searchPriority = searchPriorityComboBox.getValue();

        // Filter tasks based on search criteria
        List<Task> filteredTasks = taskManager.getTasks().stream()
                .filter(task -> {
                    boolean matchesTitle = searchTitle.isEmpty() || task.getTitle().toLowerCase().contains(searchTitle);
                    boolean matchesCategory = searchCategory == null || searchCategory.equals(task.getCategory());
                    boolean matchesPriority = searchPriority == null || searchPriority.equals(task.getPriority());
                    return matchesTitle && matchesCategory && matchesPriority;
                })
                .collect(Collectors.toList());

        // Update the tasksListView with the filtered tasks
        tasksListView.getItems().setAll(filteredTasks);
    }

    @FXML
    private void onClearSearch() {
        // Clear the search fields
        searchTitleField.clear();
        searchCategoryComboBox.setValue(null);
        searchPriorityComboBox.setValue(null);

        // Reset the tasksListView to show all tasks
        tasksListView.getItems().setAll(taskManager.getTasks());
    }
    
    @FXML
    private void clearTaskInputFields() {
        taskTitleField.clear();
        taskDescriptionField.clear();
        taskCategoryComboBox.setValue(null);
        taskPriorityComboBox.setValue(taskManager.findPriorityByTitle("Default"));
        taskDeadlinePicker.setValue(null);
        taskStatusComboBox.setValue("Open");
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

        // Get all tasks associated with the selected category
        List<Task> tasksToDelete = taskManager.getTasks().stream()
                .filter(task -> selectedCategory.equals(task.getCategory()))
                .collect(Collectors.toList());

        // If there are tasks associated with the category, show a confirmation dialog
        if (!tasksToDelete.isEmpty()) {
            // Create a confirmation dialog
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Delete Category");
            confirmationDialog.setHeaderText("This action will also delete the following tasks:");

            // List all tasks in the dialog
            StringBuilder tasksList = new StringBuilder();
            for (Task task : tasksToDelete) {
                tasksList.append("- ").append(task.getTitle()).append("\n");
            }

            confirmationDialog.setContentText(tasksList.toString() + "\nAre you sure you want to proceed?");

            // Show the dialog and wait for user response
            ButtonType result = confirmationDialog.showAndWait().orElse(ButtonType.CANCEL);

            // If the user clicks "Cancel," do not delete the category or tasks
            if (result != ButtonType.OK) {
                return;
            }
        }

        // Delete the category and associated tasks
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
    private void clearCategoryInputFields() {
    	categoryTitleField.clear();
    }
    
    // -------------------------- PRIORITIES --------------------------
    
    public List<PriorityLevel> getPriorities() {
        return taskManager.getPriorities();
    }
    
    @FXML
    private void onAddPriority() {
        String priorityTitle = priorityTitleField.getText();

        if (priorityTitle.isEmpty()) {
            showAlert("Error", "Priority title cannot be empty!");
            return;
        }

        PriorityLevel newPriority = new PriorityLevel(priorityTitle);
        taskManager.addPriority(newPriority);
        updateUI();
        clearPriorityInputFields();
    }
    
    @FXML
    private void onDeletePriority() {
        PriorityLevel selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority == null) {
            showAlert("Error", "Please select a priority to delete.");
            return;
        }

        // Check if the selected priority is the default one
        if (selectedPriority.getTitle().equals("Default")) {
            showAlert("Error", "Cannot delete the default priority.");
            return;
        }

        // Get all tasks that have the selected priority
        List<Task> affectedTasks = taskManager.getTasks().stream()
                .filter(task -> selectedPriority.equals(task.getPriority()))
                .collect(Collectors.toList());

        // If there are tasks affected, show a confirmation dialog
        if (!affectedTasks.isEmpty()) {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Delete Priority");
            confirmationDialog.setHeaderText("This action will change the priority of the following tasks to 'Default':");

            // List affected tasks in the dialog
            StringBuilder taskList = new StringBuilder();
            for (Task task : affectedTasks) {
                taskList.append("- ").append(task.getTitle()).append("\n");
            }

            confirmationDialog.setContentText(taskList.toString() + "\nAre you sure you want to proceed?");

            // Show the dialog and wait for user response
            ButtonType result = confirmationDialog.showAndWait().orElse(ButtonType.CANCEL);

            // If the user clicks "Cancel," abort the operation
            if (result != ButtonType.OK) {
                return;
            }
        }

        // Proceed with deletion
        taskManager.deletePriority(selectedPriority);
        updateUI();
    }
    
    @FXML
    private void onUpdatePriority() {
        PriorityLevel selectedPriority = priorityListView.getSelectionModel().getSelectedItem();
        if (selectedPriority == null) {
            showAlert("Error", "Please select a priority to update.");
            return;
        }

        String updatedTitle = priorityTitleField.getText();

        if (updatedTitle.isEmpty()) {
            showAlert("Error", "Priority Title cannot be empty!");
            return;
        }

        // Check if the selected priority is the default one
        if (selectedPriority.getTitle().equals("Default")) {
            showAlert("Error", "Cannot update the default priority.");
            return;
        }

        PriorityLevel updatedPriority = new PriorityLevel(updatedTitle);
        taskManager.updatePriority(selectedPriority, updatedPriority);
        updateUI();
    }
    
    @FXML
    private void clearPriorityInputFields() {
    	priorityTitleField.clear();
    }
    
    // -------------------------- REMINDERS --------------------------
    
    @FXML
    private void onAddReminder() {
        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to add a reminder.");
            return;
        }

        if (selectedTask.getStatus().equals("Completed")) {
            showAlert("Error", "Cannot add reminders to completed tasks.");
            return;
        }

        String message = reminderMessageField.getText();
        if (message.isEmpty()) {
            showAlert("Error", "Reminder message cannot be empty.");
            return;
        }

        // Check if the reminder message is unique across all tasks
        for (Task task : taskManager.getTasks()) {
            for (Reminder reminder : task.getReminders()) {
                if (reminder.getMessage().equals(message)) {
                    showAlert("Error", "Reminder message must be unique.");
                    return;
                }
            }
        }

        String reminderType = reminderDateComboBox.getValue();
        if (reminderType == null) {
            showAlert("Error", "Please select a reminder type.");
            return;
        }

        LocalDate reminderDate = null;
        switch (reminderType) {
            case "One day before deadline":
                if (selectedTask.getDeadline() == null) {
                    showAlert("Error", "Task must have a deadline for this reminder type.");
                    return;
                }
                reminderDate = selectedTask.getDeadline().minusDays(1);
                break;
            case "One week before deadline":
                if (selectedTask.getDeadline() == null) {
                    showAlert("Error", "Task must have a deadline for this reminder type.");
                    return;
                }
                reminderDate = selectedTask.getDeadline().minusWeeks(1);
                break;
            case "One month before deadline":
                if (selectedTask.getDeadline() == null) {
                    showAlert("Error", "Task must have a deadline for this reminder type.");
                    return;
                }
                reminderDate = selectedTask.getDeadline().minusMonths(1);
                break;
            case "Specific date":
                reminderDate = reminderDatePicker.getValue();
                if (reminderDate == null) {
                    showAlert("Error", "Please select a specific date for the reminder.");
                    return;
                }
                break;
            default:
                showAlert("Error", "Invalid reminder type.");
                return;
        }
        
        // Check if the reminder date is valid
        if (!isReminderDateValid(selectedTask, reminderDate)) {
            return;
        }

        Reminder newReminder = new Reminder(reminderDate, message);
        selectedTask.addReminder(newReminder);
        updateUI();
        clearReminderInputFields();
    }

    @FXML
    private void onDeleteReminder() {
        // Get the selected reminder from the reminderListView
        Reminder selectedReminder = reminderListView.getSelectionModel().getSelectedItem();
        if (selectedReminder == null) {
            showAlert("Error", "Please select a reminder to delete.");
            return;
        }

        // Find the task that contains the selected reminder
        Task taskWithReminder = null;
        for (Task task : taskManager.getTasks()) {
            if (task.getReminders().contains(selectedReminder)) {
                taskWithReminder = task;
                break;
            }
        }

        if (taskWithReminder == null) {
            showAlert("Error", "No task found containing the selected reminder.");
            return;
        }

        // Remove the selected reminder from the task
        taskWithReminder.getReminders().remove(selectedReminder);
        updateUI();
        clearReminderInputFields();
    }

    @FXML
    private void onUpdateReminder() {
        Reminder selectedReminder = reminderListView.getSelectionModel().getSelectedItem();
        if (selectedReminder == null) {
            showAlert("Error", "Please select a reminder to update.");
            return;
        }

        Task selectedTask = tasksListView.getSelectionModel().getSelectedItem();
        if (selectedTask == null) {
            showAlert("Error", "Please select a task to update a reminder.");
            return;
        }

        String message = reminderMessageField.getText();
        if (message.isEmpty()) {
            showAlert("Error", "Reminder message cannot be empty.");
            return;
        }

        LocalDate reminderDate = reminderDatePicker.getValue();
        if (reminderDate == null) {
            showAlert("Error", "Please select a date for the reminder.");
            return;
        }
        
        // Check if the reminder date is valid
        if (!isReminderDateValid(selectedTask, reminderDate)) {
            return;
        }

        selectedReminder.setMessage(message);
        selectedReminder.setDate(reminderDate);
        updateUI();
    }
    
    private boolean isReminderDateValid(Task task, LocalDate reminderDate) {
        if (task.getDeadline() != null && reminderDate.isAfter(task.getDeadline())) {
            showAlert("Error", "Invalid Reminder Date!\n\n" +
                    "The selected reminder date (" + reminderDate + ") is after the task deadline (" + task.getDeadline() + ").\n" +
                    "Please choose a reminder date that is before the task deadline.");
            return false;
        }
        return true;
    }

    @FXML
    private void clearReminderInputFields() {
        reminderMessageField.clear();
        reminderDateComboBox.setValue("One day before deadline");
        reminderDatePicker.setValue(null);
    }
    
    // -------------------------- OTHERS --------------------------

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