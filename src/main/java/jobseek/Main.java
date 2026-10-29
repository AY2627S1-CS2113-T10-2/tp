package jobseek;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.DeleteCommand;
import jobseek.command.EditCommand;
import jobseek.command.ExitCommand;
import jobseek.command.HelpCommand;
import jobseek.command.ListCommand;
import jobseek.command.MarkCommand;
import jobseek.command.SearchCommand;
import jobseek.command.SortCommand;
import jobseek.command.StatusCommand;
import jobseek.command.TodayCommand;
import jobseek.command.ViewCommand;
import jobseek.exception.JobSeekException;
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
        assert ui != null : "Ui instance should not be null";
        assert storage != null : "Storage instance should not be null";
        this.ui = ui;
        this.storage = storage;
        this.applications = applications != null ? applications : new ApplicationList();
        assert this.applications != null : "Applications list should not be null";
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
            ApplicationList loaded = storage.load();
            for (String warning : storage.getLoadWarnings()) {
                ui.showError(warning);
            }
            return loaded;
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
        ui.showMenu(applications);
        boolean isExit = false;

        while (!isExit) {
            String fullCommand = ui.readCommand();
            if (fullCommand == null) {
                break;
            }
            fullCommand = fullCommand.trim();
            if (fullCommand.isEmpty()) {
                ui.showPrompt("Enter option:\n> ");
                continue;
            }

            Command command = parseCommand(fullCommand);
            if (command != null) {
                try {
                    command.execute(applications, ui, storage);
                    isExit = command.isExit();
                } catch (JobSeekException e) {
                    ui.showError(e.getMessage());
                } catch (Exception e) {
                    ui.showError("An error occurred while executing command: " + e.getMessage());
                }
            }
            if (!isExit) {
                ui.showPrompt("Enter option:\n> ");
            }
        }

        ui.showGoodbye();
    }

    /**
     * Parses a user menu option string into an executable Command instance.
     * Accepts numeric menu options (0 through 10), plus supported list and view commands.
     *
     * @param option Raw menu option string entered by the user.
     * @return The corresponding Command object, or null if the option is invalid.
     */
    public Command parseCommand(String option) {
        if (option == null || option.trim().isEmpty()) {
            return null;
        }

        String trimmedOption = option.trim();

        if (trimmedOption.equalsIgnoreCase("view") || trimmedOption.matches("(?i)^view\\s+.*")) {
            try {
                String args = trimmedOption.replaceFirst("(?i)^view", "").trim();
                return ViewCommand.parse(args);
            } catch (JobSeekException e) {
                ui.showError(e.getMessage());
                return null;
            }
        }

        if (trimmedOption.equalsIgnoreCase("today")) {
            return new TodayCommand();
        }

        if (trimmedOption.equalsIgnoreCase("list") || trimmedOption.matches("(?i)^list\\s+.*")) {
            try {
                String args = trimmedOption.replaceFirst("(?i)^list", "").trim();
                return ListCommand.parse(args);
            } catch (JobSeekException e) {
                ui.showError(e.getMessage());
                return null;
            }
        }

        switch (trimmedOption) {
        case "1":
            return handleMenuAdd();
        case "2":
            return new ListCommand();
        case "3":
            return new ListCommand(ListCommand.FilterType.ACTIVE);
        case "4":
            return handleMenuDelete();
        case "5":
            return handleMenuMark(true);
        case "6":
            return handleMenuMark(false);
        case "7":
            return new HelpCommand();
        case "8":
            return handleMenuSearch();
        case "9":
            return new SortCommand();
        case "10":
            return handleMenuEdit();
        case "0":
            return new ExitCommand();
        default:
            ui.showError("Invalid option: '" + trimmedOption + "'. Enter a number from 0 to 10.");
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
            String trimmed = input.trim().replace('\t', ' ');
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

    private Command handleMenuSearch() {
        String keyword = promptNonEmpty("Search company or role:", "Search term cannot be empty.");
        return keyword != null ? new SearchCommand(keyword) : null;
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

        ui.showPrompt("Enter field to edit (1: Company, 2: Role, 3: Status, 4: All):\n> ");
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
            String status = promptStatus();
            return status != null ? new StatusCommand(index, status) : null;
        case "4":
            String allCompany = promptNonEmpty("Enter new company:", "Company cannot be empty.");
            if (allCompany == null) {
                return null;
            }
            String allRole = promptNonEmpty("Enter new role:", "Role cannot be empty.");
            if (allRole == null) {
                return null;
            }
            String allStatus = promptStatus();
            return allStatus != null ? new EditCommand(index, allCompany, allRole, allStatus) : null;
        default:
            ui.showError("Invalid field choice: '" + trimmedChoice + "'. Enter 1, 2, 3, or 4.");
            return null;
        }
    }

    private String promptStatus() {
        while (true) {
            ui.showPrompt("Enter recruitment status (1: Pending, 2: Accepted, 3: Rejected):\n> ");
            String input = ui.readCommand();
            if (input == null) {
                return null;
            }
            String trimmed = input.trim();
            switch (trimmed) {
            case "1":
                return "Pending";
            case "2":
                return "Accepted";
            case "3":
                return "Rejected";
            default:
                if (trimmed.equalsIgnoreCase("Pending")) {
                    return "Pending";
                } else if (trimmed.equalsIgnoreCase("Accepted")) {
                    return "Accepted";
                } else if (trimmed.equalsIgnoreCase("Rejected")) {
                    return "Rejected";
                }
                ui.showError("Invalid status choice: '" + trimmed
                        + "'. Enter 1 (Pending), 2 (Accepted), or 3 (Rejected).");
            }
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
