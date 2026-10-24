package jobseek.ui;

import jobseek.model.ApplicationList;

import java.util.Scanner;

/**
 * Handles text-based console user interface formatting and interaction.
 */
public class Ui {
    private static final String DIVIDER = "____________________________________________________________";
    private static final String LOGO = "     _       _    ____            _\n"
            + "    | | ___ | |__/ ___|  ___  ___| | __\n"
            + " _  | |/ _ \\| '_ \\___ \\ / _ \\/ _ \\ |/ /\n"
            + "| |_| | (_) | |_) |__) |  __/  __/   <\n"
            + " \\___/ \\___/|_.__/____/ \\___|\\___|_|\\_\\\n";

    private final Scanner scanner;

    /**
     * Constructs a Ui object reading input from standard input.
     */
    public Ui() {
        this(new Scanner(System.in));
    }

    /**
     * Constructs a Ui object reading input from the specified Scanner.
     *
     * @param scanner Scanner to read input from.
     */
    public Ui(Scanner scanner) {
        this.scanner = scanner != null ? scanner : new Scanner(System.in);
    }

    /**
     * Prints a horizontal divider line.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Displays a prompt message to the user without a trailing newline.
     *
     * @param prompt The prompt message to display.
     */
    public void showPrompt(String prompt) {
        System.out.print(prompt);
    }

    /**
     * Reads a full line of command input from the console.
     *
     * @return The user command as a string, or null if no further input is available (EOF).
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : null;
    }

    /**
     * Displays a message surrounded by divider lines.
     *
     * @param message Message text to display.
     */
    public void showMessage(String message) {
        showLine();
        System.out.println(message);
        showLine();
    }

    /**
     * Displays an error message prefixed with an error label and divider lines.
     *
     * @param errorMessage Error message text to display.
     */
    public void showError(String errorMessage) {
        showLine();
        System.out.println("Error: " + errorMessage);
        showLine();
    }

    /**
     * Displays the welcome banner and initial prompt upon application startup.
     */
    public void showWelcome() {
        showLine();
        System.out.println(LOGO);
        System.out.println("Welcome to JobSeek - Personal Job & Internship Application Tracker!");
        showLine();
    }

    /**
     * Displays the Applications home page and menu options.
     *
     * @param applications The current list of applications to display.
     */
    public void showMenu(ApplicationList applications) {
        showLine();
        System.out.println("                         JOBSEEK");
        showLine();
        System.out.println("                     APPLICATIONS");
        System.out.println();
        showApplicationsSummary(applications);
        System.out.println();
        showMenuOptions();
        showPrompt("Enter option:\n> ");
    }

    /**
     * Displays the menu options without an application list.
     */
    public void showMenu() {
        showMenu(null);
    }

    private void showApplicationsSummary(ApplicationList applications) {
        if (applications == null || applications.isEmpty()) {
            System.out.println("No applications found.");
            System.out.println();
            System.out.println("Add your first internship or job application.");
            return;
        }

        for (int i = 0; i < applications.size(); i++) {
            System.out.println((i + 1) + ". " + applications.get(i));
        }
        System.out.println();
        int count = applications.size();
        System.out.println(count + (count == 1 ? " application tracked." : " applications tracked."));
    }

    private void showMenuOptions() {
        showLine();
        System.out.println("[1] Add application");
        System.out.println("[2] List applications");
        System.out.println("[3] List uncompleted applications");
        System.out.println("[4] Delete application");
        System.out.println("[5] Mark application as completed");
        System.out.println("[6] Unmark application");
        System.out.println("[7] Help");
        System.out.println("[8] Search applications");
        System.out.println("[9] Sort applications alphabetically");
        System.out.println("[10] Edit application");
        System.out.println("[0] Exit");
        showLine();
    }

    /**
     * Displays the goodbye message when exiting the application.
     */
    public void showGoodbye() {
        showLine();
        System.out.println("Goodbye! Best of luck with your job search!");
        showLine();
    }
}
