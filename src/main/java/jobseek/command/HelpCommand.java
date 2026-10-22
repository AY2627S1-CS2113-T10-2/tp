package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Displays help information listing available menu options and usage formats.
 */
public class HelpCommand extends Command {

    /**
     * Executes the help command by showing supported menu options and formats.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for displaying messages.
     * @param storage Storage interface for persisting data.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) {
        String helpMessage = "Available menu options:\n"
                + "  list [completed|all]            - List active, completed, or all applications\n"
                + "  view INDEX                      - View details for application at INDEX\n"
                + "  [1] Add application             - Add a new internship or job application\n"
                + "  [2] List applications           - View all applications in a numbered table\n"
                + "  [3] List uncompleted apps       - View active / uncompleted applications\n"
                + "  [4] Delete application          - Delete an application by index\n"
                + "  [5] Mark application completed  - Mark an application as completed\n"
                + "  [6] Unmark application          - Mark an application as not completed\n"
                + "  [7] Help                        - Show this help message\n"
                + "  [0] Exit                        - Exit JobSeek";
        ui.showMessage(helpMessage);
    }
}
