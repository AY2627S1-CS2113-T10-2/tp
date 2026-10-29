package jobseek.command;

import jobseek.model.ApplicationList;

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
     * Reverses the change made by {@code execute}.
     *
     * @param applications In-memory list of applications to restore.
     * @return A short description of what was undone, for display to the user.
     */
    public abstract String undo(ApplicationList applications);
}
