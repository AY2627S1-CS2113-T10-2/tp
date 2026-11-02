package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that marking or unmarking an application that already has that state changes nothing
 * and is not recorded in the undo history.
 */
public class MarkAlreadyMarkedTest {

    @Test
    public void execute_alreadyCompleted_reportsAndDoesNotRecordChange() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", true));
        MarkCommand command = new MarkCommand(0, true);
        RecordingUi ui = new RecordingUi();

        command.execute(applications, ui, tempStorage());

        assertTrue(ui.lastMessage.startsWith("This application is already marked as completed"));
        assertFalse(command.hasChanged());
        assertTrue(applications.get(0).isCompleted());
    }

    @Test
    public void execute_alreadyNotCompleted_reportsAndDoesNotRecordChange() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        MarkCommand command = new MarkCommand(0, false);
        RecordingUi ui = new RecordingUi();

        command.execute(applications, ui, tempStorage());

        assertTrue(ui.lastMessage.startsWith("This application is already not completed"));
        assertFalse(command.hasChanged());
    }

    @Test
    public void history_markingTwice_recordsOnlyTheRealChange() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        CommandHistory history = new CommandHistory();
        Storage storage = tempStorage();

        MarkCommand first = new MarkCommand(0, true);
        first.execute(applications, new RecordingUi(), storage);
        history.record(first);
        MarkCommand second = new MarkCommand(0, true);
        second.execute(applications, new RecordingUi(), storage);
        history.record(second);
        new UndoCommand(history).execute(applications, new RecordingUi(), storage);

        // The first undo reverses the real change, instead of silently undoing a no-op.
        assertFalse(applications.get(0).isCompleted());
        assertTrue(history.isEmpty());
        assertEquals(1, applications.size());
    }

    private static Storage tempStorage() throws Exception {
        return new Storage(Files.createTempDirectory("jobseek-mark-test").resolve("applications.txt").toString());
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }
}
