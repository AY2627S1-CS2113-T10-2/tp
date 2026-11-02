package jobseek.command;

import java.io.IOException;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;

/**
 * A command that changes the application list and knows how to reverse that change.
 * Only commands that extend this class can be reversed with {@code undo}.
 */
public abstract class UndoableCommand extends Command {
    private boolean hasChanged = false;

    /** Called by subclasses once {@code execute} has actually modified the list. */
    protected void markChanged() {
        hasChanged = true;
    }

    /**
     * Returns whether the last {@code execute} modified the list.
     * Commands that were rejected (e.g. invalid index) change nothing and are not worth undoing.
     */
    public boolean hasChanged() {
        return hasChanged;
    }

    /**
     * Persists a change, reversing it if saving fails so it cannot enter undo history.
     * Call this before displaying a success message.
     *
     * @param applications List changed by this command.
     * @param storage Destination for the updated records.
     * @throws IOException If the change could not be saved.
     */
    protected void saveOrRollback(ApplicationList applications, Storage storage) throws IOException {
        try {
            storage.save(applications);
        } catch (IOException e) {
            undo(applications);
            hasChanged = false;
            throw e;
        }
    }

    /**
     * Reverses the change made by {@code execute}.
     *
     * @param applications In-memory list of applications to restore.
     * @return A short description of what was undone, for display to the user.
     */
    public abstract String undo(ApplicationList applications);
}
