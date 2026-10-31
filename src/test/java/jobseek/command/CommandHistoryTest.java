package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link CommandHistory}.
 */
public class CommandHistoryTest {

    @Test
    public void history_initiallyEmpty() {
        CommandHistory history = new CommandHistory();
        assertTrue(history.isEmpty());
        assertNull(history.pop());
    }

    @Test
    public void record_ignoresNonUndoableCommands() {
        CommandHistory history = new CommandHistory();
        history.record(new HelpCommand());
        assertTrue(history.isEmpty());
    }

    @Test
    public void record_ignoresUnchangedUndoableCommands() {
        CommandHistory history = new CommandHistory();
        AddCommand add = new AddCommand("Google", "SWE Intern");
        // Has not executed or called markChanged() yet
        history.record(add);
        assertTrue(history.isEmpty());
    }

    @Test
    public void recordAndPop_storesAndRetrievesInLifoOrder() throws IOException {
        CommandHistory history = new CommandHistory();
        ApplicationList list = new ApplicationList();
        Ui ui = new Ui();
        Storage storage = new Storage();

        AddCommand addFirst = new AddCommand("Google", "SWE");
        addFirst.execute(list, ui, storage);
        history.record(addFirst);
        assertFalse(history.isEmpty());

        AddCommand addSecond = new AddCommand("Meta", "PE");
        addSecond.execute(list, ui, storage);
        history.record(addSecond);

        // LIFO: second command popped first
        assertEquals(addSecond, history.pop());
        assertEquals(addFirst, history.pop());
        assertTrue(history.isEmpty());
    }
}
