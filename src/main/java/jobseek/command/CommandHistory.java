package jobseek.command;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Remembers the commands that changed the application list so they can be undone, most recent first.
 * This is a stack (last in, first out), which matches how undo is expected to behave.
 */
public class CommandHistory {
    private final Deque<UndoableCommand> history = new ArrayDeque<>();

    /**
     * Records a command if it is undoable and really changed the list; other commands are ignored.
     *
     * @param command The command that was just executed.
     */
    public void record(Command command) {
        if (command instanceof UndoableCommand && ((UndoableCommand) command).hasChanged()) {
            history.push((UndoableCommand) command);
        }
    }

    /** Removes and returns the most recent undoable command, or null if there is none. */
    public UndoableCommand pop() {
        return history.pollFirst();
    }

    /** Returns whether there is nothing left to undo. */
    public boolean isEmpty() {
        return history.isEmpty();
    }
}
