package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Represents an abstract executable command within the JobSeek application.
 * All concrete user commands extend this base class.
 */
public abstract class Command {
    protected boolean isExit = false;

    /**
     * Executes the specific command action.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for displaying messages.
     * @param storage Storage interface for persisting data.
     * @throws Exception If an error occurs during command execution.
     */
    public abstract void execute(ApplicationList applications, Ui ui, Storage storage) throws Exception;

    /**
     * Checks if this command requests termination of the application loop.
     *
     * @return True if the application should terminate, false otherwise.
     */
    public boolean isExit() {
        return isExit;
    }
}
