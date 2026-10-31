package jobseek.command;

import java.io.IOException;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles deleting an application from the application list.
 */
public class DeleteCommand extends UndoableCommand {
    private final int targetIndex;
    private Application deletedApplication;

    /**
     * Constructs a DeleteCommand with the target 0-based index.
     *
     * @param targetIndex 0-based index of the application in the application list.
     */
    public DeleteCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    /**
     * Asks the user to confirm, then deletes the target application and saves the updated list.
     * Anything other than "y" or "yes" (including end of input) cancels the deletion.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for printing user feedback.
     * @param storage Storage instance to persist the updated list.
     * @throws Exception If an error occurs during saving.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws IOException {
        if (applications.isEmpty()) {
            ui.showError("Your application list is currently empty.");
            return;
        }

        if (!applications.isValidIndex(targetIndex)) {
            ui.showError("Invalid application index: " + (targetIndex + 1)
                    + ". Please enter a number between 1 and " + applications.size() + ".");
            return;
        }

        Application target = applications.get(targetIndex);
        ui.showPrompt("Delete " + target + "? (y/n)\n> ");
        String answer = ui.readCommand();
        if (answer == null || !(answer.trim().equalsIgnoreCase("y") || answer.trim().equalsIgnoreCase("yes"))) {
            ui.showMessage("Deletion cancelled. No application was deleted.");
            return;
        }

        int sizeBefore = applications.size();
        Application application = applications.delete(targetIndex);
        deletedApplication = application;
        markChanged();

        assert applications.size() == sizeBefore - 1
                : "Deleting one application should reduce the list size by one";

        ui.showMessage("OK, I've deleted this application:\n  " + application);
        storage.save(applications);
    }

    /** Puts the deleted application back at its original position. */
    @Override
    public String undo(ApplicationList applications) {
        applications.insert(Math.min(targetIndex, applications.size()), deletedApplication);
        return "deleted " + deletedApplication;
    }
}
