package jobseek.command;

import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the user-visible output and filtering of the list command.
 */
public class ListCommandTest {

    @Test
    public void execute_emptyList_showsNoApplicationsFoundMessage() {
        RecordingUi ui = new RecordingUi();

        new ListCommand().execute(new ApplicationList(), ui, new Storage());

        assertEquals("No applications found.", ui.lastMessage);
    }

    @Test
    public void execute_defaultFilter_showsOnlyActiveApplicationsWithOriginalIndex() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", false));
        applications.add(new Application("Longer Co", "Engineer", true));
        applications.add(new Application("Tech Corp", "Data Analyst", false));
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
        assertRow(lines[4], "3", "[ ]", "Tech Corp", "Data Analyst");
    }

    @Test
    public void execute_completedFilter_showsOnlyCompletedApplicationsWithOriginalIndex() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", false));
        applications.add(new Application("Longer Co", "Engineer", true));
        applications.add(new Application("Tech Corp", "Data Analyst", false));
        RecordingUi ui = new RecordingUi();

        new ListCommand(ListCommand.FilterType.COMPLETED).execute(applications, ui, new Storage());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals(4, lines.length);
        assertEquals("Here are your applications:", lines[0]);
        assertRow(lines[3], "2", "[X]", "Longer Co", "Engineer");
    }

    @Test
    public void execute_allFilter_showsAllApplicationsWithOriginalIndex() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", false));
        applications.add(new Application("Longer Co", "Engineer", true));
        RecordingUi ui = new RecordingUi();

        new ListCommand(ListCommand.FilterType.ALL).execute(applications, ui, new Storage());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals(5, lines.length);
        assertRow(lines[3], "1", "[ ]", "Acme", "SWE Intern");
        assertRow(lines[4], "2", "[X]", "Longer Co", "Engineer");
    }

    @Test
    public void execute_noActiveApplications_showsNoActiveMessage() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", true));
        RecordingUi ui = new RecordingUi();

        new ListCommand(ListCommand.FilterType.ACTIVE).execute(applications, ui, new Storage());

        assertEquals("No active applications found.", ui.lastMessage);
    }

    @Test
    public void execute_noCompletedApplications_showsNoCompletedMessage() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", false));
        RecordingUi ui = new RecordingUi();

        new ListCommand(ListCommand.FilterType.COMPLETED).execute(applications, ui, new Storage());

        assertEquals("No completed applications found.", ui.lastMessage);
    }

    @Test
    public void parse_validArguments_returnsCorrectFilter() throws Exception {
        assertEquals(ListCommand.FilterType.ACTIVE, ListCommand.parse(null).getFilterType());
        assertEquals(ListCommand.FilterType.ACTIVE, ListCommand.parse("").getFilterType());
        assertEquals(ListCommand.FilterType.ACTIVE, ListCommand.parse("  ").getFilterType());
        assertEquals(ListCommand.FilterType.COMPLETED, ListCommand.parse("completed").getFilterType());
        assertEquals(ListCommand.FilterType.COMPLETED, ListCommand.parse(" COMPLETED ").getFilterType());
        assertEquals(ListCommand.FilterType.ALL, ListCommand.parse("all").getFilterType());
        assertEquals(ListCommand.FilterType.ALL, ListCommand.parse(" ALL ").getFilterType());
    }

    @Test
    public void parse_invalidArgument_throwsJobSeekException() {
        JobSeekException exception = assertThrows(JobSeekException.class, () -> ListCommand.parse("invalid"));
        assertEquals("Invalid list filter: 'invalid'. Usage: list [completed|all]", exception.getMessage());
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
