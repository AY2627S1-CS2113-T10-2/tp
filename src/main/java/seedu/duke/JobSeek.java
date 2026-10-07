package seedu.duke;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the JobSeek application.
 */
public class JobSeek {
    private static final List<JobApplication> applications = new ArrayList<>();

    /**
     * Main entry-point for the JobSeek application.
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        run(in);
    }

    /**
     * Runs the main interactive loop of the application.
     *
     * @param in The scanner to read user input from.
     */
    public static void run(Scanner in) {
        boolean isRunning = true;
        while (isRunning) {
            printApplicationsPage();
            String option = in.nextLine().trim();

            switch (option) {
            case "1":
                handleAddApplication(in);
                break;
            case "0":
                isRunning = false;
                break;
            default:
                System.out.println("Invalid option. Enter 0 or 1.");
                break;
            }
        }
    }

    /**
     * Displays the Applications home page.
     */
    public static void printApplicationsPage() {
        System.out.println("APPLICATIONS");
        System.out.println();

        if (applications.isEmpty()) {
            System.out.println("No applications found.");
        } else {
            for (int i = 0; i < applications.size(); i++) {
                JobApplication app = applications.get(i);
                System.out.println((i + 1) + ". " + app.getCompany() + " | " + app.getRole());
            }
        }

        System.out.println();
        System.out.println("[1] Add application");
        System.out.println("[0] Exit");
        System.out.println();
        System.out.print("Enter option:\n> ");
    }

    private static void handleAddApplication(Scanner in) {
        String company = promptNonEmpty(in, "Company:", "Error: Company cannot be empty.");
        String role = promptNonEmpty(in, "Role:", "Error: Role cannot be empty.");

        JobApplication app = new JobApplication(company, role);
        applications.add(app);

        System.out.println();
        System.out.println("Application added successfully.");
        System.out.println("Company: " + app.getCompany());
        System.out.println("Role: " + app.getRole());
        System.out.println();
    }

    private static String promptNonEmpty(Scanner in, String promptLabel, String errorMessage) {
        while (true) {
            System.out.print(promptLabel + "\n> ");
            String input = in.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println(errorMessage);
        }
    }

    /**
     * Returns the current list of applications (for testing).
     */
    public static List<JobApplication> getApplications() {
        return applications;
    }

    /**
     * Clears all applications from the list (for testing).
     */
    public static void clearApplications() {
        applications.clear();
    }
}
