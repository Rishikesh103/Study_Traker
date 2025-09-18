# ☕ Java Study Tracker

A comprehensive command-line application built with Java to help students and professionals log, track, and analyze their study sessions. This tool is designed to be lightweight, fast, and run in any standard terminal, persisting all data locally.

## ✨ Features

* **Log Study Sessions:** Record study entries with a specific or current date, subject, duration, and a description.
* **Automatic Data Persistence:** All logs are automatically saved to a `studylogs.csv` file and reloaded on startup. You'll never lose your data.
* **Full CRUD Functionality:** Create, View, **Edit**, and **Delete** any study log with ease through a simple menu interface.
* **Detailed Summaries:**
    * Analyze total study time grouped by **date**.
    * Analyze total study time grouped by **subject**, including a list of all dates the subject was studied.
* **Data Export:** Export all your logs to a new, uniquely named CSV file for use in other applications like Excel or Google Sheets.

## 📸 Demo

Here is a glimpse of the application's main menu and the "Summary by Subject" feature in action:

```plaintext
-----------------------------------------------------------------------
           Welcome to the Study Tracker Application          
-----------------------------------------------------------------------

Please select an option:
  1: Add a new study log
  2: View all study logs
  3: Edit a log
  4: Delete a log
  5: View summary by date
  6: View summary by subject
  7: Export logs to a new CSV file
  8: Exit application
Enter your choice: 6

-----------------------------------------------------------------------
                    Study Summary by Subject                   
-----------------------------------------------------------------------
Subject: C++                   | Total:  5.00 hrs | Studied on: [2025-09-17]
Subject: Java                  | Total:  7.50 hrs | Studied on: [2025-09-17, 2025-09-18]
Subject: Project Management    | Total:  2.00 hrs | Studied on: [2025-09-18]
-----------------------------------------------------------------------

🛠️ How It Works: Implementation Details
The project is built with vanilla Java and follows a clear, object-oriented structure within a single file.

Class Architecture
The application is composed of four main classes: StudyTracker1, StudyTracker, StudyLog, and SubjectSummary.

Code snippet

classDiagram
    StudyTracker1 --|> StudyTracker
    StudyTracker o-- "many" StudyLog
    StudyTracker o-- "many" SubjectSummary
    class StudyTracker1 {
        +main(String[] args)
    }
    class StudyTracker {
        -ArrayList~StudyLog~ Database
        +insertLog()
        +editLog()
        +summaryBySubject()
        -saveLogsToFile()
        -loadLogsFromFile()
    }
Data Persistence Flow
The application saves data to studylogs.csv. It loads from this file on startup and saves to it after every change (add, edit, or delete).

Code snippet

graph TD
    A[Start Application] --> B{studylogs.csv exists?};
    B -- Yes --> C[Load data from CSV];
    B -- No --> D[Start with empty list];
    C & D --> E[Display Main Menu];
    E --> F{User Action};
    F -- Add/Edit/Delete --> G[Modify list in memory];
    G --> H[Overwrite studylogs.csv];
    H --> E;
    F -- View/Summarize --> E;
    F -- Exit --> I[End Program];
🚀 Getting Started
Prerequisites
Java Development Kit (JDK) 11 or higher.

Installation & Usage
Clone the repository:

Bash

git clone [https://github.com/YOUR_USERNAME/YOUR_REPOSITORY_NAME.git](https://github.com/YOUR_USERNAME/YOUR_REPOSITORY_NAME.git)
Navigate to the project directory:

Bash

cd YOUR_REPOSITORY_NAME
Compile the Java code:

Bash

javac StudyTracker1.java
Run the application:

Bash

java StudyTracker1
