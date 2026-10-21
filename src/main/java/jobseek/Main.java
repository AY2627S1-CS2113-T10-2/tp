package jobseek;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.DeleteCommand;
import jobseek.command.EditCommand;
import jobseek.command.ExitCommand;
import jobseek.command.HelpCommand;
import jobseek.command.ListCommand;
import jobseek.command.MarkCommand;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Main entry point for the JobSeek CLI application.
 */
public class Main {
    private final Ui ui;
    private final Storage storage;
    private final ApplicationList applications;

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
     * Only numeric menu options (0, 1, 2, 3, 4, 5, 6, 7) are accepted.
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
            return handleMenuEdit();
        case "7":
            return new HelpCommand();
        case "0":
            return new ExitCommand();
        default:
            ui.showError("Invalid option: '" + trimmedOption + "'. Enter 0, 1, 2, 3, 4, 5, 6, or 7.");
            return null;
        }
    }

    private Command handleMenuAdd() {
        String company = promptNonEmpty("Company:", "Company cannot be empty.");
        if (company == null) {
            return null;
        }
        String role = promptNonEmpty("Role:", "Role cannot be empty.");
        if (role == null) {
            return null;
        }
        return new AddCommand(company, role);
    }

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

    private Command handleMenuDelete() {
        Integer index = promptApplicationIndex("Enter application index to delete:\n> ");
        return index != null ? new DeleteCommand(index) : null;
    }

    private Command handleMenuMark(boolean isCompleted) {
        String action = isCompleted ? "mark as completed" : "unmark";
        Integer index = promptApplicationIndex("Enter application index to " + action + ":\n> ");
        return index != null ? new MarkCommand(index, isCompleted) : null;
    }

    private Command handleMenuEdit() {
        Integer index = promptApplicationIndex("Enter application index to edit:\n> ");
        if (index == null) {
            return null;
        }
        if (!applications.isValidIndex(index)) {
            ui.showError("Invalid application index: " + (index + 1)
                    + ". Please enter a number between 1 and " + applications.size() + ".");
            return null;
        }

        ui.showPrompt("Enter field to edit (1: Company, 2: Role, 3: Both):\n> ");
        String fieldChoice = ui.readCommand();
        if (fieldChoice == null) {
            return null;
        }
        String trimmedChoice = fieldChoice.trim();

        switch (trimmedChoice) {
        case "1":
            String company = promptNonEmpty("Enter new company:", "Company cannot be empty.");
            return company != null ? new EditCommand(index, company, null) : null;
        case "2":
            String role = promptNonEmpty("Enter new role:", "Role cannot be empty.");
            return role != null ? new EditCommand(index, null, role) : null;
        case "3":
            String bothCompany = promptNonEmpty("Enter new company:", "Company cannot be empty.");
            if (bothCompany == null) {
                return null;
            }
            String bothRole = promptNonEmpty("Enter new role:", "Role cannot be empty.");
            return bothRole != null ? new EditCommand(index, bothCompany, bothRole) : null;
        default:
            ui.showError("Invalid field choice: '" + trimmedChoice + "'. Enter 1, 2, or 3.");
            return null;
        }
    }

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
