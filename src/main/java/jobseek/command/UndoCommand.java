package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Reverses the most recent change made by an add, delete, edit, status or mark/unmark command.
 * The command itself is not undoable, so repeating {@code undo} walks further back in history.
 */
public class UndoCommand extends Command {
    private final CommandHistory history;

    /**
     * Constructs an UndoCommand that draws from the given history.
     *
     * @param history History shared with the main loop, which records each executed command.
     */
    public UndoCommand(CommandHistory history) {
        assert history != null : "History should not be null";
        this.history = history;
    }

    /**
     * Undoes the latest recorded change and saves the restored list.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for user feedback.
     * @param storage Storage instance to persist the restored list.
     * @throws Exception If an error occurs during saving.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws Exception {
        UndoableCommand last = history.pop();
        if (last == null) {
            ui.showError("Nothing to undo.");
            return;
        }
        String description = last.undo(applications);
        ui.showMessage("Undone: " + description);
        storage.save(applications);
    }
}
