package jobseek.command;

import jobseek.TestStorage;
import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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

        new ListCommand().execute(new ApplicationList(), ui, TestStorage.create());

        assertEquals("No applications found.", ui.lastMessage);
    }

    @Test
    public void execute_defaultFilter_showsOnlyActiveApplicationsWithOriginalIndex() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", false));
        applications.add(new Application("Longer Co", "Engineer", true));
        applications.add(new Application("Tech Corp", "Data Analyst", false));
        RecordingUi ui = new RecordingUi();

        new ListCommand().execute(applications, ui, TestStorage.create());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals(5, lines.length);
        assertEquals("Here are your applications:", lines[0]);
        assertTrue(lines[1].contains("No."));
        assertTrue(lines[1].contains("Completed"));
        assertTrue(lines[1].contains("Company"));
        assertTrue(lines[1].contains("Role"));
        assertTrue(lines[1].contains("Stage"));
        assertTrue(lines[1].contains("Deadline"));
        assertTrue(lines[1].contains("Portal Link"));
        assertTrue(lines[1].contains("Email"));
        assertTrue(lines[1].contains("Notes"));
        assertRow(lines[3], "1", "[ ]", "Acme", "SWE Intern", "Applied", "-", "-", "-", "-");
        assertRow(lines[4], "3", "[ ]", "Tech Corp", "Data Analyst", "Applied", "-", "-", "-", "-");
    }

    @Test
    public void execute_completedFilter_showsOnlyCompletedApplicationsWithOriginalIndex() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", false));
        applications.add(new Application("Longer Co", "Engineer", true));
        applications.add(new Application("Tech Corp", "Data Analyst", false));
        RecordingUi ui = new RecordingUi();

        new ListCommand(ListCommand.FilterType.COMPLETED).execute(applications, ui, TestStorage.create());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals(4, lines.length);
        assertEquals("Here are your applications:", lines[0]);
        assertRow(lines[3], "2", "[X]", "Longer Co", "Engineer", "Applied", "-", "-", "-", "-");
    }

    @Test
    public void execute_allFilter_showsAllApplicationsWithOriginalIndex() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", false));
        applications.add(new Application("Longer Co", "Engineer", true));
        RecordingUi ui = new RecordingUi();

        new ListCommand(ListCommand.FilterType.ALL).execute(applications, ui, TestStorage.create());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals(5, lines.length);
        assertRow(lines[3], "1", "[ ]", "Acme", "SWE Intern", "Applied", "-", "-", "-", "-");
        assertRow(lines[4], "2", "[X]", "Longer Co", "Engineer", "Applied", "-", "-", "-", "-");
    }

    @Test
    public void execute_withAllFieldsPopulated_formatsAllColumnsCorrectly() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, RecruitmentStage.INTERVIEW,
                LocalDate.of(2026, 11, 20), "https://careers.google.com", "recruiter@google.com",
                "Passed OA"));
        RecordingUi ui = new RecordingUi();

        new ListCommand().execute(applications, ui, TestStorage.create());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals(4, lines.length);
        assertRow(lines[3], "1", "[ ]", "Google", "SWE Intern", "Interview", "2026-11-20",
                "https://careers.google.com", "recruiter@google.com", "Passed OA");
    }

    @Test
    public void execute_noActiveApplications_showsNoActiveMessage() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", true));
        RecordingUi ui = new RecordingUi();

        new ListCommand(ListCommand.FilterType.ACTIVE).execute(applications, ui, TestStorage.create());

        assertEquals("No active applications found.", ui.lastMessage);
    }

    @Test
    public void execute_noCompletedApplications_showsNoCompletedMessage() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "SWE Intern", false));
        RecordingUi ui = new RecordingUi();

        new ListCommand(ListCommand.FilterType.COMPLETED).execute(applications, ui, TestStorage.create());

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

    private static void assertRow(String row, String number, String status, String company, String role,
            String stage, String deadline, String portal, String email, String notes) {
        String[] columns = row.split(" \\| ");
        assertEquals(9, columns.length);
        assertEquals(number, columns[0].trim());
        assertEquals(status, columns[1].trim());
        assertEquals(company, columns[2].trim());
        assertEquals(role, columns[3].trim());
        assertEquals(stage, columns[4].trim());
        assertEquals(deadline, columns[5].trim());
        assertEquals(portal, columns[6].trim());
        assertEquals(email, columns[7].trim());
        assertEquals(notes, columns[8].trim());
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }
}
