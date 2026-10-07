package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles marking an application as completed or uncompleted.
 */
public class MarkCommand extends Command {
    private final int targetIndex;
    private final boolean isCompleted;

    /**
     * Constructs a MarkCommand with the target 0-based index and desired completion status.
     *
     * @param targetIndex 0-based index of the application in the application list.
     * @param isCompleted True to mark as completed, false to unmark.
     */
    public MarkCommand(int targetIndex, boolean isCompleted) {
        this.targetIndex = targetIndex;
        this.isCompleted = isCompleted;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Executes the mark or unmark operation on the target application.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for printing user feedback.
     * @param storage Storage instance to persist the updated status.
     * @throws Exception If an error occurs during saving.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws Exception {
        if (applications.isEmpty()) {
            ui.showError("Your application list is currently empty.");
            return;
        }

        if (!applications.isValidIndex(targetIndex)) {
            ui.showError("Invalid application index: " + (targetIndex + 1)
                    + ". Please enter a number between 1 and " + applications.size() + ".");
            return;
        }

        Application application = applications.get(targetIndex);
        application.setCompleted(isCompleted);

        String message = isCompleted
                ? "Nice! I've marked this application as completed:\n  " + application
                : "OK, I've marked this application as not completed yet:\n  " + application;

        ui.showMessage(message);
        storage.save(applications);
    }
}
