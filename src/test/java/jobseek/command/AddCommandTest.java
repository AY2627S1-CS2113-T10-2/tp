package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AddCommand}.
 */
public class AddCommandTest {

    @Test
    public void execute_validInputs_addsApplicationSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage();

        AddCommand command = new AddCommand("Google", "Software Engineer Intern");
        command.execute(applications, ui, storage);

        assertEquals(1, applications.size());
        Application added = applications.get(0);
        assertEquals("Google", added.getCompany());
        assertEquals("Software Engineer Intern", added.getRole());

        String expectedMessage = "Application added successfully.\n"
                + "Company: Google\n"
                + "Role: Software Engineer Intern";
        assertEquals(expectedMessage, ui.lastMessage);
    }

    @Test
    public void execute_whitespaceAroundInputs_trimmedSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage();

        AddCommand command = new AddCommand("   Shopee   ", "   Backend Intern   ");
        command.execute(applications, ui, storage);

        assertEquals(1, applications.size());
        Application added = applications.get(0);
        assertEquals("Shopee", added.getCompany());
        assertEquals("Backend Intern", added.getRole());
    }

    @Test
    public void execute_emptyCompany_showsErrorMessageAndDoesNotAdd() throws Exception {
        ApplicationList applications = new ApplicationList();
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage();

        AddCommand command = new AddCommand("", "SWE Intern");
        command.execute(applications, ui, storage);

        assertTrue(applications.isEmpty());
        assertEquals("Error: Company cannot be empty.", ui.lastError);
    }

    @Test
    public void execute_whitespaceOnlyCompany_showsErrorMessageAndDoesNotAdd() throws Exception {
        ApplicationList applications = new ApplicationList();
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage();

        AddCommand command = new AddCommand("   ", "SWE Intern");
        command.execute(applications, ui, storage);

        assertTrue(applications.isEmpty());
        assertEquals("Error: Company cannot be empty.", ui.lastError);
    }

    @Test
    public void execute_emptyRole_showsErrorMessageAndDoesNotAdd() throws Exception {
        ApplicationList applications = new ApplicationList();
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage();

        AddCommand command = new AddCommand("Google", "");
        command.execute(applications, ui, storage);

        assertTrue(applications.isEmpty());
        assertEquals("Error: Role cannot be empty.", ui.lastError);
    }

    @Test
    public void execute_whitespaceOnlyRole_showsErrorMessageAndDoesNotAdd() throws Exception {
        ApplicationList applications = new ApplicationList();
        RecordingUi ui = new RecordingUi();
        Storage storage = new Storage();

        AddCommand command = new AddCommand("Google", "   ");
        command.execute(applications, ui, storage);

        assertTrue(applications.isEmpty());
        assertEquals("Error: Role cannot be empty.", ui.lastError);
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;
        private String lastError;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }

        @Override
        public void showError(String errorMessage) {
            lastError = "Error: " + errorMessage;
        }
    }
}
