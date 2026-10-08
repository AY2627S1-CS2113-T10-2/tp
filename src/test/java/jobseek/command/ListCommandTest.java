package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the user-visible output of the list command.
 */
public class ListCommandTest {
    @Test
    public void execute_emptyList_showsClearMessage() {
        RecordingUi ui = new RecordingUi();

        new ListCommand().execute(new ApplicationList(), ui, new Storage());

        assertEquals("No applications found.", ui.lastMessage);
    }

    @Test
    public void execute_multipleApplications_showsNumberedTableInListOrder() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern"));
        applications.add(new Application("Longer Co", "Engineer", true));
        RecordingUi ui = new RecordingUi();

        new ListCommand().execute(applications, ui, new Storage());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals(5, lines.length);
        assertEquals("Here are your applications:", lines[0]);
        assertTrue(lines[1].contains("No."));
        assertTrue(lines[1].contains("Status"));
        assertTrue(lines[1].contains("Company"));
        assertTrue(lines[1].contains("Role"));
        assertRow(lines[3], "1", "[ ]", "Acme", "SWE Intern");
        assertRow(lines[4], "2", "[X]", "Longer Co", "Engineer");
        assertEquals(2, applications.size());
    }

    private static void assertRow(String row, String number, String status, String company, String role) {
        String[] columns = row.split(" \\| ");
        assertEquals(4, columns.length);
        assertEquals(number, columns[0].trim());
        assertEquals(status, columns[1].trim());
        assertEquals(company, columns[2].trim());
        assertEquals(role, columns[3].trim());
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }
}
