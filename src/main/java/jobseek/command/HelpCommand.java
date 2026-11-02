package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Displays help information listing available menu options and usage formats.
 */
public class HelpCommand extends Command {
    /** Typed commands as {format, description} pairs. */
    private static final String[][] TYPED_COMMANDS = {
        {"list [completed|all]", "List active, completed, or all applications"},
        {"view INDEX", "View details for application at INDEX"},
        {"add c/COMPANY r/ROLE [d/DATE]", "Add an application (optional: d/ p/ e/ n/ fields)"},
        {"edit INDEX [c/..] [r/..] [s/..]", "Edit fields (also d/ p/ e/ n/; an empty value clears)"},
        {"delete|mark|unmark INDEX", "Delete, complete or reopen an application"},
        {"find c/.. r/.. s/.. b/.. a/.. m/..", "Combine filters (company, role, stage, deadlines, done)"},
        {"search KEYWORD", "Find applications by company or role"},
        {"sort", "View applications alphabetically"},
        {"today", "Show overdue, due-today and upcoming deadlines"},
        {"undo", "Undo the last add, delete, edit, stage or mark change"},
        {"help", "Show this help message"},
        {"exit", "Exit JobSeek"},
    };

    /** Numbered menu options as {label, description} pairs. */
    private static final String[][] MENU_OPTIONS = {
        {"[1] Add application", "Add a new internship or job application"},
        {"[2] List all applications", "View all applications in a numbered table"},
        {"[3] List uncompleted applications", "View active / uncompleted applications"},
        {"[4] Delete application", "Delete an application by index"},
        {"[5] Mark application as completed", "Mark an application as completed"},
        {"[6] Unmark application", "Mark an application as not completed"},
        {"[7] Search applications", "Find applications by company or role"},
        {"[8] Sort applications alphabetically", "View applications alphabetically"},
        {"[9] Edit application", "Edit an existing application's details"},
        {"[10] Help", "Show this help message"},
        {"[0] Exit", "Exit JobSeek"},
    };

    /**
     * Executes the help command by showing supported menu options and formats.
     * Each of the two lists is padded to its own longest entry so that the descriptions line up.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for displaying messages.
     * @param storage Storage interface for persisting data.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) {
        StringBuilder help = new StringBuilder("Available menu options:\n");
        help.append("Typed commands (you can also use the numbers below):\n");
        appendAligned(help, TYPED_COMMANDS);
        help.append("Numbered menu options:\n");
        appendAligned(help, MENU_OPTIONS);
        help.setLength(help.length() - 1);
        ui.showMessage(help.toString());
    }

    /** Appends one "  left - right" line per entry, padding the left side to the longest one. */
    private static void appendAligned(StringBuilder help, String[][] entries) {
        int width = 0;
        for (String[] entry : entries) {
            width = Math.max(width, entry[0].length());
        }
        for (String[] entry : entries) {
            help.append("  ").append(String.format("%-" + width + "s", entry[0]))
                    .append(" - ").append(entry[1]).append('\n');
        }
    }
}
