package jobseek.ui;

import java.util.List;
import java.util.Scanner;

import jobseek.model.JobApplication;

/**
 * Handles all text-based console user interface rendering and interactions for JobSeek.
 */
public class CliUi {
    private static final String DOUBLE_LINE = "============================================================";
    private static final String SINGLE_LINE = "------------------------------------------------------------";

    private final Scanner scanner;

    /**
     * Constructs a CliUi using the provided Scanner.
     *
     * @param scanner Scanner for user input.
     */
    public CliUi(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads a line of input from the user.
     *
     * @return Trimmed user input line, or empty string if input ended.
     */
    public String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "";
    }

    /**
     * Reads an un-trimmed line of input from the user (preserving spaces before specific trimming).
     *
     * @return Raw input line from the user.
     */
    public String readRawLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        return "";
    }

    /**
     * Displays the full Applications home page.
     *
     * @param applications The list of job applications to display.
     */
    public void printApplicationsPage(List<JobApplication> applications) {
        System.out.println(DOUBLE_LINE);
        System.out.println("                         JOBSEEK");
        System.out.println(DOUBLE_LINE);
        System.out.println("                     APPLICATIONS");
        System.out.println();

        if (applications.isEmpty()) {
            System.out.println("No applications found.");
            System.out.println();
            System.out.println("Add your first internship or job application.");
        } else {
            printApplicationsTable(applications);
            System.out.println();
            int count = applications.size();
            System.out.println(count + (count == 1 ? " application." : " applications."));
        }

        System.out.println();
        System.out.println(SINGLE_LINE);
        System.out.println("[1] Add application");
        System.out.println("[2] Delete application");
        System.out.println("[3] Help");
        System.out.println("[0] Exit");
        System.out.println();
        System.out.print("Enter option:\n> ");
    }

    /**
     * Prints the applications table formatted with index, company, and role columns.
     *
     * @param applications The list of job applications.
     */
    public void printApplicationsTable(List<JobApplication> applications) {
        int maxCompanyLen = "COMPANY".length();
        int maxRoleLen = "ROLE".length();

        for (JobApplication app : applications) {
            if (app.getCompany().length() > maxCompanyLen) {
                maxCompanyLen = app.getCompany().length();
            }
            if (app.getRole().length() > maxRoleLen) {
                maxRoleLen = app.getRole().length();
            }
        }

        String format = "%-5s | %-" + maxCompanyLen + "s | %-" + maxRoleLen + "s";
        System.out.println(String.format(format, "INDEX", "COMPANY", "ROLE"));

        for (int i = 0; i < applications.size(); i++) {
            JobApplication app = applications.get(i);
            System.out.println(String.format(format, (i + 1), app.getCompany(), app.getRole()));
        }
    }

    /**
     * Prints the header for the Add Application screen.
     */
    public void printAddApplicationHeader() {
        System.out.println(SINGLE_LINE);
        System.out.println("                    ADD APPLICATION");
        System.out.println(SINGLE_LINE);
        System.out.println();
    }

    /**
     * Prompts the user to enter a company name.
     */
    public void printCompanyPrompt() {
        System.out.print("Company:\n> ");
    }

    /**
     * Prompts the user to enter a role name.
     */
    public void printRolePrompt() {
        System.out.print("Role:\n> ");
    }

    /**
     * Prints confirmation that an application was added successfully.
     *
     * @param application The added job application.
     */
    public void printApplicationAdded(JobApplication application) {
        System.out.println();
        System.out.println("Application added successfully.");
        System.out.println();
        System.out.println("Company : " + application.getCompany());
        System.out.println("Role    : " + application.getRole());
        System.out.println();
        promptPressEnterToReturn();
    }

    /**
     * Prints the Delete Application screen header and table.
     *
     * @param applications The list of job applications available to delete.
     */
    public void printDeleteApplicationPage(List<JobApplication> applications) {
        System.out.println(SINGLE_LINE);
        System.out.println("                   DELETE APPLICATION");
        System.out.println(SINGLE_LINE);
        System.out.println();

        if (applications.isEmpty()) {
            System.out.println("No applications available to delete.");
            System.out.println();
            promptPressEnterToReturn();
            return;
        }

        printApplicationsTable(applications);
        System.out.println();
        System.out.print("Enter application index to delete:\n> ");
    }

    /**
     * Prints confirmation that an application was deleted successfully.
     *
     * @param application The deleted job application.
     */
    public void printApplicationDeleted(JobApplication application) {
        System.out.println();
        System.out.println("Application deleted successfully.");
        System.out.println();
        System.out.println("Company : " + application.getCompany());
        System.out.println("Role    : " + application.getRole());
        System.out.println();
    }

    /**
     * Prints the Help screen.
     */
    public void printHelp() {
        System.out.println(SINGLE_LINE);
        System.out.println("                          HELP");
        System.out.println(SINGLE_LINE);
        System.out.println();
        System.out.println("[1] Add application");
        System.out.println("    Add a new internship or job application.");
        System.out.println();
        System.out.println("[2] Delete application");
        System.out.println("    Remove an application using its current list index.");
        System.out.println();
        System.out.println("[3] Help");
        System.out.println("    Show this page.");
        System.out.println();
        System.out.println("[0] Exit");
        System.out.println("    Exit JobSeek.");
        System.out.println();
        System.out.println("Applications are saved automatically.");
        System.out.println();
        promptPressEnterToReturn();
    }

    /**
     * Prompts the user to press Enter to return to the Applications home page.
     */
    public void promptPressEnterToReturn() {
        System.out.println("Press Enter to return to Applications.");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
        System.out.println();
    }

    /**
     * Prints the goodbye message upon exiting.
     */
    public void printGoodbye() {
        System.out.println("Goodbye!");
    }

    /**
     * Prints a general error message.
     *
     * @param message The error message to print.
     */
    public void printError(String message) {
        System.out.println(message);
        System.out.println();
    }
}
