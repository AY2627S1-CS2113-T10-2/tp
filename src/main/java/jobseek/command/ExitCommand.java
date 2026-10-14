package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles exiting the JobSeek application.
 */
public class ExitCommand extends Command {

    /**
     * Constructs an ExitCommand setting the exit flag to true.
     */
    public ExitCommand() {
        this.isExit = true;
    }

    /**
     * Executes the exit command.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for displaying messages.
     * @param storage Storage interface for persisting data.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) {
        // Main execution loop checks isExit() to terminate gracefully
    }
}
