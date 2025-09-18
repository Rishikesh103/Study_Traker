import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Data class to hold information about a single study session.
 */
class StudyLog {
    private LocalDate Date;
    private String Subject;
    private double Duration;
    private String Description;

    public StudyLog(LocalDate date, String subject, double duration, String description) {
        this.Date = date;
        this.Subject = subject;
        this.Duration = duration;
        this.Description = description;
    }

    @Override
    public String toString() {
        return this.Date + " | " + this.Subject + " | " + this.Duration + " hrs | " + this.Description;
    }

    // Getter methods
    public LocalDate getDate() { return this.Date; }
    public String getSubject() { return this.Subject; }
    public double getDuration() { return this.Duration; }
    public String getDescription() { return this.Description; }

    // Setter methods (for editing logs)
    public void setSubject(String subject) { this.Subject = subject; }
    public void setDuration(double duration) { this.Duration = duration; }
    public void setDescription(String description) { this.Description = description; }
}

/**
 * Helper class to store summary data for a subject, including the dates studied.
 */
class SubjectSummary {
    double totalDuration = 0.0;
    // TreeSet automatically keeps dates sorted and unique
    TreeSet<LocalDate> dates = new TreeSet<>();
}

/**
 * Main logic class that manages the study log database and file operations.
 */
class StudyTracker {
    private final ArrayList<StudyLog> Database = new ArrayList<>();
    private static final String FILENAME = "studylogs.csv";

    // Constructor loads data from the file when the application starts.
    public StudyTracker() {
        loadLogsFromFile();
    }

    // --- Core CRUD Methods ---

    public void insertLog() {
        Scanner scannerObj = new Scanner(System.in);
        System.out.println("\n-----------------------------------------------------------------------");
        System.out.println("                  Enter New Study Log Details                  ");
        System.out.println("-----------------------------------------------------------------------");

        System.out.print("Enter date (YYYY-MM-DD) or leave blank for today: ");
        String dateInput = scannerObj.nextLine();
        LocalDate dateObj;
        try {
            if (dateInput.trim().isEmpty()) {
                dateObj = LocalDate.now();
            } else {
                dateObj = LocalDate.parse(dateInput);
            }
        } catch (DateTimeParseException e) {
            System.out.println(" >> Invalid date format. Using today's date. << ");
            dateObj = LocalDate.now();
        }
        
        System.out.print("Enter the subject name: ");
        String sub = scannerObj.nextLine();

        System.out.print("Enter the study duration in hours: ");
        double dur;
        try {
            dur = scannerObj.nextDouble();
        } catch (InputMismatchException e) {
            System.out.println("\n >> Invalid input! Please enter a number for the duration. Log cancelled. <<");
            return;
        }
        scannerObj.nextLine(); // Consume the newline character

        System.out.print("Enter a brief description: ");
        String desc = scannerObj.nextLine();

        Database.add(new StudyLog(dateObj, sub, dur, desc));
        saveLogsToFile(); // Save after adding a new log
        System.out.println("\n             >> Study log saved successfully! <<             ");
    }

    public void displayLog() {
        System.out.println("\n-----------------------------------------------------------------------");
        System.out.println("                     All Study Logs Report                     ");
        System.out.println("-----------------------------------------------------------------------");
        if (Database.isEmpty()) {
            System.out.println("          Database is empty. Nothing to display.           ");
        } else {
            System.out.println("Date         | Subject    | Duration   | Description");
            System.out.println("-----------------------------------------------------------------------");
            for (StudyLog sobj : Database) {
                System.out.println(sobj);
            }
        }
        System.out.println("-----------------------------------------------------------------------");
    }

    public void editLog() {
        if (!displayLogsWithIndex()) return;

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the number of the log to edit: ");
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(" >> Invalid input. Please enter a number. <<");
            return;
        }
        scanner.nextLine();

        if (choice < 1 || choice > Database.size()) {
            System.out.println(" >> Invalid number. Please try again. <<");
            return;
        }

        StudyLog logToEdit = Database.get(choice - 1);
        System.out.println("\nEditing log: " + logToEdit);
        System.out.println("(Press Enter to keep the current value)");

        System.out.print("Enter new subject (current: " + logToEdit.getSubject() + "): ");
        String newSubject = scanner.nextLine();
        if (!newSubject.trim().isEmpty()) {
            logToEdit.setSubject(newSubject);
        }

        System.out.print("Enter new duration (current: " + logToEdit.getDuration() + "): ");
        String newDurationStr = scanner.nextLine();
        if (!newDurationStr.trim().isEmpty()) {
            try {
                double newDuration = Double.parseDouble(newDurationStr);
                logToEdit.setDuration(newDuration);
            } catch (NumberFormatException e) {
                System.out.println(" >> Invalid number format. Keeping original duration. <<");
            }
        }

        System.out.print("Enter new description (current: " + logToEdit.getDescription() + "): ");
        String newDescription = scanner.nextLine();
        if (!newDescription.trim().isEmpty()) {
            logToEdit.setDescription(newDescription);
        }

        saveLogsToFile();
        System.out.println("\n           >> Log updated successfully! <<           ");
    }

    public void deleteLog() {
        if (!displayLogsWithIndex()) return;

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the number of the log to delete: ");
        int choice;
        try {
            choice = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.out.println(" >> Invalid input. Please enter a number. <<");
            return;
        }
        scanner.nextLine();

        if (choice < 1 || choice > Database.size()) {
            System.out.println(" >> Invalid number. Please try again. <<");
            return;
        }

        Database.remove(choice - 1);
        saveLogsToFile();
        System.out.println("\n           >> Log deleted successfully! <<           ");
    }
    
    // --- Summary & Export Methods ---

    public void summaryByDate() {
        System.out.println("\n-----------------------------------------------------------------------");
        System.out.println("                      Study Summary by Date                      ");
        System.out.println("-----------------------------------------------------------------------");

        if (Database.isEmpty()) {
            System.out.println("          Database is empty. Nothing to display.           ");
        } else {
            TreeMap<LocalDate, Double> summaryMap = new TreeMap<>();
            for (StudyLog sobj : Database) {
                summaryMap.put(sobj.getDate(), summaryMap.getOrDefault(sobj.getDate(), 0.0) + sobj.getDuration());
            }
            for (Map.Entry<LocalDate, Double> entry : summaryMap.entrySet()) {
                System.out.printf("Date: %s | Total Duration: %.2f hours\n", entry.getKey(), entry.getValue());
            }
        }
        System.out.println("-----------------------------------------------------------------------");
    }

    public void summaryBySubject() {
        System.out.println("\n-----------------------------------------------------------------------");
        System.out.println("                    Study Summary by Subject                   ");
        System.out.println("-----------------------------------------------------------------------");

        if (Database.isEmpty()) {
            System.out.println("          Database is empty. Nothing to display.           ");
        } else {
            TreeMap<String, SubjectSummary> summaryMap = new TreeMap<>();
            
            for (StudyLog log : Database) {
                String subject = log.getSubject();
                SubjectSummary summary = summaryMap.computeIfAbsent(subject, k -> new SubjectSummary());
                summary.totalDuration += log.getDuration();
                summary.dates.add(log.getDate());
            }

            for (Map.Entry<String, SubjectSummary> entry : summaryMap.entrySet()) {
                String subject = entry.getKey();
                SubjectSummary summary = entry.getValue();
                System.out.printf("Subject: %-20s | Total: %5.2f hrs | Studied on: %s\n", 
                    subject, 
                    summary.totalDuration, 
                    summary.dates.toString());
            }
        }
        System.out.println("-----------------------------------------------------------------------");
    }

    public void exportCSV() {
        String exportFilename = "StudyExport_" + LocalDate.now() + ".csv";
        try (FileWriter fw = new FileWriter(exportFilename)) {
            fw.write("Date,Subject,Duration,Description\n");
            for (StudyLog log : Database) {
                fw.write(log.getDate() + "," + log.getSubject().replace(",", ";") + "," + log.getDuration() + "," + log.getDescription().replace(",", ";") + "\n");
            }
            System.out.println("\n        >> Successfully exported data to " + exportFilename + " <<        ");
        } catch (IOException e) {
            System.out.println(" >> Error: Could not export logs to CSV file. <<");
        }
    }

    // --- Private Helper & File I/O Methods ---

    private boolean displayLogsWithIndex() {
        System.out.println("\n---------------------- All Study Logs ----------------------");
        if (Database.isEmpty()) {
            System.out.println("          Database is empty. Nothing to select.          ");
            return false;
        }
        for (int i = 0; i < Database.size(); i++) {
            System.out.println((i + 1) + ": " + Database.get(i));
        }
        System.out.println("------------------------------------------------------------");
        return true;
    }
    
    private void saveLogsToFile() {
        try (FileWriter fw = new FileWriter(FILENAME)) {
            fw.write("Date,Subject,Duration,Description\n");
            for (StudyLog log : Database) {
                fw.write(log.getDate() + "," + log.getSubject().replace(",", ";") + "," + log.getDuration() + "," + log.getDescription().replace(",", ";") + "\n");
            }
        } catch (IOException e) {
            System.out.println(" >> Error: Could not save logs to file. <<");
        }
    }

    private void loadLogsFromFile() {
        File file = new File(FILENAME);
        if (!file.exists()) {
            return; // No file to load, start fresh.
        }
        try (BufferedReader br = new BufferedReader(new FileReader(FILENAME))) {
            br.readLine(); // Skip header line
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",", 4);
                if (values.length == 4) {
                    Database.add(new StudyLog(
                        LocalDate.parse(values[0]),
                        values[1].replace(";", ","),
                        Double.parseDouble(values[2]),
                        values[3].replace(";", ",")
                    ));
                }
            }
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            System.out.println(" >> Error: Could not load logs from file. The file might be corrupted. <<");
        }
    }
}

/**
 * Entry point of the application. Contains the main method and user menu.
 */
public class StudyTracker1 {
    public static void main(String[] args) {
        StudyTracker stobj = new StudyTracker();
        Scanner scannerObj = new Scanner(System.in);
        int choice = 0;

        System.out.println("-----------------------------------------------------------------------");
        System.out.println("           Welcome to the Study Tracker Application          ");
        System.out.println("-----------------------------------------------------------------------");

        do {
            System.out.println("\nPlease select an option:");
            System.out.println("  1: Add a new study log");
            System.out.println("  2: View all study logs");
            System.out.println("  3: Edit a log");
            System.out.println("  4: Delete a log");
            System.out.println("  5: View summary by date");
            System.out.println("  6: View summary by subject");
            System.out.println("  7: Export logs to a new CSV file");
            System.out.println("  8: Exit application");
            System.out.print("Enter your choice: ");

            try {
                choice = scannerObj.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("\n >> Invalid input. Please enter a number. <<");
                scannerObj.next(); // Clear the bad input
                choice = 0; // Reset choice to loop again
                continue;
            }

            switch (choice) {
                case 1: stobj.insertLog(); break;
                case 2: stobj.displayLog(); break;
                case 3: stobj.editLog(); break;
                case 4: stobj.deleteLog(); break;
                case 5: stobj.summaryByDate(); break;
                case 6: stobj.summaryBySubject(); break;
                case 7: stobj.exportCSV(); break;
                case 8:
                    System.out.println("\n-----------------------------------------------------------------------");
                    System.out.println("          Thank you for using the Study Tracker!           ");
                    System.out.println("-----------------------------------------------------------------------");
                    break;
                default:
                    System.out.println("\n >> Invalid option. Please choose a number from the menu. <<");
            }
        } while (choice != 8);
        
        scannerObj.close();
    }
}