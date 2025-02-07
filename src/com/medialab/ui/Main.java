package com.medialab.ui;

import com.medialab.services.DataManager;

import com.medialab.models.Task;
import com.medialab.models.Category;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
// import javafx.stage.WindowEvent;

import java.util.List;

public class Main extends Application {
    private DataManager dataManager = new DataManager();
    private MainController mainController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("MediaLab Assistant");
        primaryStage.setScene(scene);
        
        // Get the controller and set initial data
        mainController = loader.getController();
        dataManager.loadData(mainController.getTaskManager()); // Load both tasks and categories
        
        // Force UI Update After Loading Data
        mainController.updateUI();

        // Save tasks and categories when the application closes
        primaryStage.setOnCloseRequest(_ -> onAppClose());

        primaryStage.show();
    }

    private void onAppClose() {
        if (mainController != null) {
            List<Task> tasksToSave = mainController.getTasks();
            List<Category> categoriesToSave = mainController.getCategories();
            dataManager.saveData(tasksToSave, categoriesToSave);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}