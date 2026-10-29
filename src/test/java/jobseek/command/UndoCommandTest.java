package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that each undoable command is reversed by {@link UndoCommand} and that history behaves as a stack.
 */
public class UndoCommandTest {

    @Test
    public void execute_emptyHistory_showsNothingToUndo() throws Exception {
        RecordingUi ui = new RecordingUi();

        new UndoCommand(new CommandHistory()).execute(new ApplicationList(), ui, tempStorage());

        assertEquals("Nothing to undo.", ui.lastError);
    }

    @Test
    public void undo_afterAdd_removesAddedApplication() throws Exception {
        ApplicationList applications = new ApplicationList();
        CommandHistory history = new CommandHistory();

        run(new AddCommand("Google", "SWE", LocalDate.of(2026, 12, 1)), applications, history);
        run(new UndoCommand(history), applications, history);

        assertTrue(applications.isEmpty());
    }

    @Test
    public void undo_afterAddingIdenticalApplications_removesOnlyTheLastOne() throws Exception {
        ApplicationList applications = new ApplicationList();
        CommandHistory history = new CommandHistory();
        run(new AddCommand("Google", "SWE"), applications, history);
        Application first = applications.get(0);
        run(new AddCommand("Google", "SWE"), applications, history);

        run(new UndoCommand(history), applications, history);

        assertEquals(1, applications.size());
        assertSame(first, applications.get(0));
    }

    @Test
    public void undo_afterMark_restoresPreviousCompletion() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        CommandHistory history = new CommandHistory();

        run(new MarkCommand(0, true), applications, history);
        assertTrue(applications.get(0).isCompleted());
        run(new UndoCommand(history), applications, history);

        assertFalse(applications.get(0).isCompleted());
    }

    @Test
    public void undo_afterStatusChange_restoresPreviousStatus() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        String original = applications.get(0).getStatus();
        CommandHistory history = new CommandHistory();

        run(new StatusCommand(0, "Interview"), applications, history);
        assertEquals("Interview", applications.get(0).getStatus());
        run(new UndoCommand(history), applications, history);

        assertEquals(original, applications.get(0).getStatus());
    }

    @Test
    public void undo_afterEdit_restoresCompanyRoleAndStatus() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        String originalStatus = applications.get(0).getStatus();
        CommandHistory history = new CommandHistory();

        run(new EditCommand(0, "Shopee", "Backend", "Interview"), applications, history);
        run(new UndoCommand(history), applications, history);

        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("SWE", applications.get(0).getRole());
        assertEquals(originalStatus, applications.get(0).getStatus());
    }

    @Test
    public void undo_afterConfirmedDelete_restoresApplication() throws Exception {
        ApplicationList applications = new ApplicationList();
        Application google = new Application("Google", "SWE", false);
        applications.add(google);
        CommandHistory history = new CommandHistory();
        DeleteCommand delete = new DeleteCommand(0);

        delete.execute(applications, new Ui(new Scanner("y")), tempStorage());
        history.record(delete);
        assertTrue(applications.isEmpty());
        run(new UndoCommand(history), applications, history);

        assertEquals(1, applications.size());
        assertSame(google, applications.get(0));
    }

    @Test
    public void undo_declinedDelete_isNotRecorded() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        CommandHistory history = new CommandHistory();
        DeleteCommand delete = new DeleteCommand(0);

        delete.execute(applications, new Ui(new Scanner("n")), tempStorage());
        history.record(delete);

        assertTrue(history.isEmpty());
    }

    @Test
    public void undo_repeated_walksBackThroughHistoryMostRecentFirst() throws Exception {
        ApplicationList applications = new ApplicationList();
        CommandHistory history = new CommandHistory();
        run(new AddCommand("Google", "SWE"), applications, history);
        run(new AddCommand("Shopee", "Backend"), applications, history);

        run(new UndoCommand(history), applications, history);
        assertEquals(1, applications.size());
        assertEquals("Google", applications.get(0).getCompany());
        run(new UndoCommand(history), applications, history);

        assertTrue(applications.isEmpty());
        assertTrue(history.isEmpty());
    }

    @Test
    public void history_rejectedCommand_isNotRecorded() throws Exception {
        ApplicationList applications = new ApplicationList();
        CommandHistory history = new CommandHistory();

        run(new MarkCommand(5, true), applications, history);
        run(new AddCommand("", "SWE"), applications, history);

        assertTrue(history.isEmpty());
    }

    @Test
    public void history_nonUndoableCommand_isNotRecorded() throws Exception {
        ApplicationList applications = new ApplicationList();
        CommandHistory history = new CommandHistory();

        run(new ListCommand(), applications, history);
        run(new HelpCommand(), applications, history);

        assertTrue(history.isEmpty());
    }

    /** Executes a command and records it in history the same way the main loop does. */
    private static void run(Command command, ApplicationList applications, CommandHistory history)
            throws Exception {
        command.execute(applications, new RecordingUi(), tempStorage());
        history.record(command);
    }

    /** Uses a temporary file so tests never touch the real data/applications.txt. */
    private static Storage tempStorage() throws Exception {
        return new Storage(Files.createTempDirectory("jobseek-undo-test").resolve("applications.txt").toString());
    }

    private static class RecordingUi extends Ui {
        private String lastError;

        @Override
        public void showMessage(String message) {
            // Output is not needed by these tests.
        }

        @Override
        public void showError(String errorMessage) {
            lastError = errorMessage;
        }
    }
}
