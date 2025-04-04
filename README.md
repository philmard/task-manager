# Task Management System - Medialab Project  

## Overview  
This project was developed as part of the **7th semester course "Multimedia Technology"** at the **National Technical University of Athens (NTUA)**. It is a **Task Management System**, allowing users to **create, edit, and monitor tasks** while managing **priorities, deadlines, and reminders** through a structured GUI.  

## Technologies Used  

**Programming Language: Java**  
**Graphical User Interface (GUI): JavaFX**  
**Data Storage: JSON**

## Project Structure  

### Folder Organization  
- **src/** → Contains the main source code, organized into packages:  
  - `com.medialab.models` → Defines the core application entities.  
  - `com.medialab.services` → Handles application logic and data management.  
  - `com.medialab.ui` → Manages the user interface and interaction logic.  
- **resources/** → Contains the FXML file (`main.fxml`) for the UI layout.  
- **medialab/** → Stores the **data.json** file, which holds all task-related data.  

## Data Storage (JSON)  
All task-related information is stored in a **single JSON file (`data.json`)** within the `medialab/` folder. The structure includes:  

1. **Tasks** (List of stored tasks):  
   - Each task has:  
     - `title` (Task name)  
     - `status` (e.g., Open, Completed, Delayed)  
     - `priority` (Assigned priority level)  
     - **Optional fields:**  
       - `category` (Task grouping)  
       - `deadline` (Task due date)  
       - `reminders` (List of associated reminders)  

2. **Categories** (Independent task categories)  
   - Stored separately as a list (e.g., `"category1"`, `"category2"`).  

3. **Priorities** (Predefined priority levels)  
   - Includes a default `"Default"` priority and custom levels (e.g., `"priority1"`, `"priority2"`).  

> **Note:** Categories and Priorities are stored independently from tasks, as they can exist without being directly associated with any task. However, **Reminders are stored within Tasks**, since each reminder is always linked to a specific task.  

### JSON Example  
```json
{
  "tasks": [
    {
      "title": "task1",
      "status": "Open",
      "priority": "priority3",
      "reminders": []
    },
    {
      "title": "task3",
      "status": "Open",
      "priority": "category1",
      "category": "priority2",
      "deadline": "2025-02-08",
      "reminders": [
        {
          "date": "2025-02-07",
          "message": "reminder1"
        }
      ]
    }
  ],
  "categories": [
    { "title": "category1" },
    { "title": "category2" }
  ],
  "priorities": [
    { "title": "Default" },
    { "title": "priority2" },
    { "title": "priority3" }
  ]
}
```

### User Interface
How it should look like:
![image](https://github.com/user-attachments/assets/34fc47a9-4292-4595-8db9-1f2ee4dcb739)

