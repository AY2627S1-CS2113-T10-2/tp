package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link HelpCommand}.
 */
public class HelpCommandTest {

    @Test
    public void execute_displaysHelpMessage() {
        RecordingUi ui = new RecordingUi();
        HelpCommand command = new HelpCommand();

        command.execute(new ApplicationList(), ui, new Storage());

        assertFalse(command.isExit());
        assertTrue(ui.lastMessage.contains("Available menu options:"));
        assertTrue(ui.lastMessage.contains("list [completed|all]"));
        assertTrue(ui.lastMessage.contains("view INDEX"));
        assertTrue(ui.lastMessage.contains("[1] Add application"));
        assertTrue(ui.lastMessage.contains("[2] List applications"));
        assertTrue(ui.lastMessage.contains("[3] List uncompleted apps"));
        assertTrue(ui.lastMessage.contains("[4] Delete application"));
        assertTrue(ui.lastMessage.contains("[5] Mark application completed"));
        assertTrue(ui.lastMessage.contains("[6] Unmark application"));
        assertTrue(ui.lastMessage.contains("[7] Help"));
        assertTrue(ui.lastMessage.contains("[0] Exit"));
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }
}
