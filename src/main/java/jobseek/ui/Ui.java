package jobseek.ui;

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
        this.scanner = new Scanner(System.in);
    }

    /**
     * Prints a horizontal divider line.
     */
    public void showLine() {
        System.out.println(DIVIDER);
    }

    /**
     * Reads a full line of command input from the console.
     *
     * @return The user command as a string, or an empty string if no input is available.
     */
    public String readCommand() {
        return scanner.hasNextLine() ? scanner.nextLine() : "";
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
        System.out.println("Type 'help' to see available commands.");
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
