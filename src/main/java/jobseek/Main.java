package jobseek;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.DeleteCommand;
import jobseek.command.ExitCommand;
import jobseek.command.HelpCommand;
import jobseek.command.ListCommand;
import jobseek.command.MarkCommand;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Main entry point for the JobSeek CLI application.
 */
public class Main {
    private final Ui ui;
    private final Storage storage;
    private final ApplicationList applications;
    private final DeadlineWarnings deadlineWarnings = new DeadlineWarnings();

    /**
     * Constructs the main application instance and initializes core components.
     */
    public Main() {
        this(new Ui(), new Storage());
    }

    /**
     * Constructs the main application instance with the given UI and Storage.
     *
     * @param ui User interface instance.
     * @param storage Storage instance.
     */
    public Main(Ui ui, Storage storage) {
        this(ui, storage, loadApplications(ui, storage));
    }

    /**
     * Constructs the main application instance with custom UI, storage, and applications list.
     *
     * @param ui User interface instance.
     * @param storage Storage instance.
     * @param applications In-memory list of applications.
     */
    public Main(Ui ui, Storage storage, ApplicationList applications) {
        this.ui = ui;
        this.storage = storage;
        this.applications = applications != null ? applications : new ApplicationList();
    }

    /**
     * Loads saved applications, falling back to an empty list if loading fails.
     *
     * @param ui UI used to report a loading error.
     * @param storage Storage that owns the saved application file.
     * @return Loaded applications or an empty list after an error.
     */
    private static ApplicationList loadApplications(Ui ui, Storage storage) {
        try {
            return storage.load();
        } catch (Exception e) {
            ui.showError("Failed to load applications: " + e.getMessage());
            return new ApplicationList();
        }
    }

    /**
     * Returns the in-memory application list.
     *
     * @return ApplicationList instance.
     */
    public ApplicationList getApplications() {
        return applications;
    }

    /**
     * Starts and executes the application workflow.
     */
    public void run() {
        ui.showWelcome();
        deadlineWarnings.show(applications, ui);
        boolean isExit = false;

        while (!isExit) {
            ui.showMenu(applications);
            String fullCommand = ui.readCommand();
            if (fullCommand == null) {
                break;
            }
            fullCommand = fullCommand.trim();
            if (fullCommand.isEmpty()) {
                continue;
            }

            Command command = parseCommand(fullCommand);
            if (command != null) {
                try {
                    command.execute(applications, ui, storage);
                    isExit = command.isExit();
                } catch (Exception e) {
                    ui.showError("An error occurred while executing command: " + e.getMessage());
                }
            }
        }

        ui.showGoodbye();
    }

    /**
     * Parses a user menu option string into an executable Command instance.
     * Only numeric menu options (0, 1, 2, 3, 4, 5, 6) are accepted.
     *
     * @param option Raw menu option string entered by the user.
     * @return The corresponding Command object, or null if the option is invalid.
     */
    public Command parseCommand(String option) {
        if (option == null || option.trim().isEmpty()) {
            return null;
        }

        String trimmedOption = option.trim();

        switch (trimmedOption) {
        case "1":
            return handleMenuAdd();
        case "2":
            return new ListCommand();
        case "3":
            return handleMenuDelete();
        case "4":
            return handleMenuMark(true);
        case "5":
            return handleMenuMark(false);
        case "6":
            return new HelpCommand();
        case "0":
            return new ExitCommand();
        default:
            ui.showError("Invalid option: '" + trimmedOption + "'. Enter 0, 1, 2, 3, 4, 5, or 6.");
            return null;
        }
    }

    /** Prompts for application details and creates the corresponding add command. */
    private Command handleMenuAdd() {
        String company = promptNonEmpty("Company:", "Company cannot be empty.");
        if (company == null) {
            return null;
        }
        String role = promptNonEmpty("Role:", "Role cannot be empty.");
        if (role == null) {
            return null;
        }
        LocalDate deadline = promptDeadline();
        return new AddCommand(company, role, deadline);
    }

    /** Prompts for an optional deadline and repeats until a valid date is entered. */
    private LocalDate promptDeadline() {
        while (true) {
            ui.showPrompt("Deadline (YYYY-MM-DD, leave blank if none):\n> ");
            String input = ui.readCommand();
            if (input == null || input.trim().isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(input.trim());
            } catch (DateTimeParseException e) {
                ui.showError("Invalid deadline. Use YYYY-MM-DD or leave it blank.");
            }
        }
    }

    /** Prompts until the user enters a non-blank value or reaches end of input. */
    private String promptNonEmpty(String promptLabel, String errorMessage) {
        while (true) {
            ui.showPrompt(promptLabel + "\n> ");
            String input = ui.readCommand();
            if (input == null) {
                return null;
            }
            String trimmed = input.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
            ui.showError(errorMessage);
        }
    }

    /** Prompts for an application index and creates a delete command. */
    private Command handleMenuDelete() {
        Integer index = promptApplicationIndex("Enter application index to delete:\n> ");
        return index != null ? new DeleteCommand(index) : null;
    }

    /** Prompts for an application index and creates a completion-state command. */
    private Command handleMenuMark(boolean isCompleted) {
        String action = isCompleted ? "mark as completed" : "unmark";
        Integer index = promptApplicationIndex("Enter application index to " + action + ":\n> ");
        return index != null ? new MarkCommand(index, isCompleted) : null;
    }

    /** Prompts for a one-based application index and converts it to zero-based form. */
    private Integer promptApplicationIndex(String promptMessage) {
        if (applications.isEmpty()) {
            ui.showError("Your application list is currently empty.");
            return null;
        }

        ui.showPrompt(promptMessage);
        String indexStr = ui.readCommand();
        if (indexStr == null || indexStr.trim().isEmpty()) {
            ui.showError("Please specify a valid application index.");
            return null;
        }

        try {
            int oneBasedIndex = Integer.parseInt(indexStr.trim());
            return oneBasedIndex - 1;
        } catch (NumberFormatException e) {
            ui.showError("Invalid application index. Please enter a valid number.");
            return null;
        }
    }

    /**
     * Application entry point.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        //assert false : "dummy assertion set to fail"; // Uncomment this line to test assertion failure handling
        new Main().run();
    }
}
