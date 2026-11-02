package jobseek;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.CommandHistory;
import jobseek.command.DeleteCommand;
import jobseek.command.EditCommand;
import jobseek.command.ExitCommand;
import jobseek.command.HelpCommand;
import jobseek.command.ListCommand;
import jobseek.command.MarkCommand;
import jobseek.command.RecruitmentStageCommand;
import jobseek.command.SearchCommand;
import jobseek.command.SortCommand;
import jobseek.command.UndoCommand;
import jobseek.command.UndoableCommand;
import jobseek.exception.JobSeekException;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.parser.CommandParser;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Main entry point for the JobSeek CLI application.
 */
public class Main {
    private static final Logger LOGGER = initLogger();

    /** Typing this word at any menu prompt abandons the action. It cannot clash with a valid answer. */
    private static final String CANCEL_WORD = "cancel";

    private final Ui ui;
    private final Storage storage;
    private final ApplicationList applications;
    private final DeadlineWarnings deadlineWarnings = new DeadlineWarnings();
    private final CommandHistory history = new CommandHistory();

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
            LOGGER.info("Attempting to load applications from storage.");
            ApplicationList loaded = storage.load();
            for (String warning : storage.getLoadWarnings()) {
                ui.showError(warning);
            }
            if (storage.isBackupPending()) {
                ui.showMessage("Skipped lines will be erased when your next change is saved. Your original file "
                        + "will first be copied to " + storage.getBackupFilePath() + ".");
            }
            return loaded;
        } catch (IOException e) {
            LOGGER.warning("IOException encountered during application load: " + e.getMessage());
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
        LOGGER.info("Starting JobSeek application loop.");
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
                    // A failed startup load must not allow edits to the empty fallback list.
                    if (command instanceof UndoableCommand || command instanceof UndoCommand) {
                        storage.ensureWritable();
                    }
                    command.execute(applications, ui, storage);
                    if (!(command instanceof UndoCommand)) {
                        history.record(command);
                    }
                    isExit = command.isExit();
                } catch (JobSeekException e) {
                    ui.showError(e.getMessage());
                } catch (IOException e) {
                    LOGGER.warning("Storage operation failed: " + e.getMessage());
                    ui.showError("Storage operation failed: " + e.getMessage());
                }
            }
            if (!isExit) {
                ui.showEmptyLine();
                ui.showMenuOptions();
                ui.showPrompt("Enter option:\n> ");
            }
        }

        LOGGER.info("Exiting JobSeek application cleanly.");
        ui.showGoodbye();
    }

    /**
     * Parses a user menu option string into an executable Command instance.
     * Accepts numeric menu options (0 through 10), plus typed commands such as list, view, today, add and edit.
     *
     * @param option Raw menu option string entered by the user.
     * @return The corresponding Command object, or null if the option is invalid.
     */
    public Command parseCommand(String option) {
        if (option == null || option.trim().isEmpty()) {
            return null;
        }

        String trimmedOption = option.trim();

        // Typed commands (e.g. "add c/Google", "view 1", "list all", "today", "undo") are parsed first.
        try {
            Command typedCommand = CommandParser.parse(trimmedOption, history);
            if (typedCommand != null) {
                return typedCommand;
            }
        } catch (JobSeekException e) {
            ui.showError(e.getMessage());
            return null;
        }

        try {
            return parseMenuOption(trimmedOption);
        } catch (PromptCancelledException e) {
            ui.showMessage("Cancelled. No changes were made.");
            return null;
        }
    }

    /**
     * Runs the prompts for a numbered menu option and builds its command.
     *
     * @param trimmedOption Menu option entered by the user, without surrounding spaces.
     * @return The command to run, or null if the option is invalid or the user ran out of input.
     * @throws PromptCancelledException If the user types "cancel" at any prompt.
     */
    private Command parseMenuOption(String trimmedOption) {
        switch (trimmedOption) {
        case "1":
            return handleMenuAdd();
        case "2":
            return new ListCommand(ListCommand.FilterType.ALL);
        case "3":
            return new ListCommand(ListCommand.FilterType.ACTIVE);
        case "4":
            return handleMenuDelete();
        case "5":
            return handleMenuMark(true);
        case "6":
            return handleMenuMark(false);
        case "7":
            return handleMenuSearch();
        case "8":
            return new SortCommand();
        case "9":
            return handleMenuEdit();
        case "10":
            return new HelpCommand();
        case "0":
            return new ExitCommand();
        default:
            ui.showError("Invalid option: '" + trimmedOption + "'. Enter a number from 0 to 10.");
            return null;
        }
    }

    /** Thrown by {@link #checkCancel} so one catch in {@code parseCommand} can abandon a half-finished prompt. */
    private static class PromptCancelledException extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /**
     * Returns the input unchanged, or abandons the current menu action if the user typed "cancel".
     *
     * @param input Line read from the user, or null at end of input.
     * @return The same input.
     */
    private static String checkCancel(String input) {
        if (input != null && input.trim().equalsIgnoreCase(CANCEL_WORD)) {
            throw new PromptCancelledException();
        }
        return input;
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
        String portalLink = promptOptional("Company Portal Link (leave blank if none):");
        String email = promptOptional("Contact Email (leave blank if none):");
        String notes = promptOptional("Notes (leave blank if none):");
        return new AddCommand(company, role, deadline, portalLink, email, notes);
    }

    /**
     * Prompts for an optional deadline and repeats until a valid date is entered.
     * Pressing Enter means "no deadline"; end of input abandons the action instead, so a half-finished
     * edit cannot silently clear a saved deadline.
     */
    private LocalDate promptDeadline() {
        while (true) {
            ui.showPrompt("Deadline (YYYY-MM-DD, leave blank if none):\n> ");
            String input = checkCancel(ui.readCommand());
            if (input == null) {
                throw new PromptCancelledException();
            }
            if (input.trim().isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(input.trim());
            } catch (DateTimeParseException e) {
                ui.showError("Invalid deadline. Use YYYY-MM-DD or leave it blank.");
            }
        }
    }

    /**
     * Prompts for an optional text value. Pressing Enter gives an empty string (which clears the field when
     * editing); end of input abandons the action instead, so it cannot be mistaken for pressing Enter.
     */
    private String promptOptional(String promptLabel) {
        ui.showPrompt(promptLabel + "\n> ");
        String input = checkCancel(ui.readCommand());
        if (input == null) {
            throw new PromptCancelledException();
        }
        return input.trim().replace('\t', ' ');
    }

    /** Prompts until the user enters a non-blank value or reaches end of input. */
    private String promptNonEmpty(String promptLabel, String errorMessage) {
        while (true) {
            ui.showPrompt(promptLabel + "\n> ");
            String input = checkCancel(ui.readCommand());
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

        ui.showEditMenu();
        String fieldChoice = checkCancel(ui.readCommand());
        if (fieldChoice == null) {
            return null;
        }
        String trimmedChoice = fieldChoice.trim();

        switch (trimmedChoice) {
        case "1":
            String company = promptNonEmpty("Enter new company:", "Company cannot be empty.");
            return company != null ? EditCommand.editCompany(index, company) : null;
        case "2":
            String role = promptNonEmpty("Enter new role:", "Role cannot be empty.");
            return role != null ? EditCommand.editRole(index, role) : null;
        case "3":
            RecruitmentStage stage = promptRecruitmentStage();
            return stage != null ? new RecruitmentStageCommand(index, stage) : null;
        case "4":
            LocalDate deadline = promptDeadline();
            return EditCommand.editDeadline(index, deadline);
        case "5":
            String portalLink = promptOptional("Enter new company portal link (leave blank to clear):");
            return EditCommand.editPortalLink(index, portalLink);
        case "6":
            String email = promptOptional("Enter new contact email (leave blank to clear):");
            return EditCommand.editEmail(index, email);
        case "7":
            String notes = promptOptional("Enter new notes (leave blank to clear):");
            return EditCommand.editNotes(index, notes);
        case "8":
            return handleEditAllFields(index);
        default:
            ui.showError("Invalid field choice: '" + trimmedChoice + "'. Enter a number from 1 to 8.");
            return null;
        }
    }

    /** Prompts sequentially for all application fields to update an application record completely. */
    private Command handleEditAllFields(int index) {
        String allCompany = promptNonEmpty("Enter new company:", "Company cannot be empty.");
        if (allCompany == null) {
            return null;
        }
        String allRole = promptNonEmpty("Enter new role:", "Role cannot be empty.");
        if (allRole == null) {
            return null;
        }
        RecruitmentStage allStage = promptRecruitmentStage();
        if (allStage == null) {
            return null;
        }
        LocalDate allDeadline = promptDeadline();
        String allPortalLink = promptOptional("Company Portal Link (leave blank to clear):");
        String allEmail = promptOptional("Contact Email (leave blank to clear):");
        String allNotes = promptOptional("Notes (leave blank to clear):");
        return EditCommand.editAll(index, allCompany, allRole, allStage,
                allDeadline, allPortalLink, allEmail, allNotes);
    }

    private RecruitmentStage promptRecruitmentStage() {
        while (true) {
            ui.showPrompt(RecruitmentStage.SELECTION_PROMPT);
            String input = checkCancel(ui.readCommand());
            if (input == null) {
                return null;
            }
            try {
                return RecruitmentStage.fromChoice(input);
            } catch (IllegalArgumentException e) {
                ui.showError("Invalid recruitment stage: '" + input.trim()
                        + "'. Enter a stage name or a number from 1 to 7.");
            }
        }
    }

    private Integer promptApplicationIndex(String promptMessage) {
        if (applications.isEmpty()) {
            ui.showError("Your application list is currently empty.");
            return null;
        }

        while (true) {
            ui.showPrompt(promptMessage);
            String indexStr = checkCancel(ui.readCommand());
            if (indexStr == null) {
                return null;
            }
            if (indexStr.trim().isEmpty()) {
                ui.showError("Please specify a valid application index.");
                continue;
            }

            try {
                int oneBasedIndex = Integer.parseInt(indexStr.trim());
                if (!applications.isValidIndex(oneBasedIndex - 1)) {
                    ui.showError("Invalid application index: " + oneBasedIndex
                            + ". Please enter a number between 1 and " + applications.size() + ".");
                    continue;
                }
                return oneBasedIndex - 1;
            } catch (NumberFormatException e) {
                ui.showError("Invalid application index. Please enter a valid number.");
            }
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

    private static Logger initLogger() {
        Logger jobseekLogger = Logger.getLogger("jobseek");
        jobseekLogger.setUseParentHandlers(false);
        return Logger.getLogger(Main.class.getName());
    }
}
