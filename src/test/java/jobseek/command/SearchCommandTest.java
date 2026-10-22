package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks company/role matching and preservation of original list numbers.
 */
public class SearchCommandTest {
    @Test
    public void execute_matchesCompanyAndRoleIgnoringCase_andKeepsOriginalNumbers() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Zulu", "Designer"));
        applications.add(new Application("Acme", "Senior Engineer"));
        applications.add(new Application("Engineering Co", "Intern", true));
        RecordingUi ui = new RecordingUi();

        new SearchCommand("  ENGin  ").execute(applications, ui, new Storage());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals("Applications matching 'ENGin':", lines[0]);
        assertEquals(5, lines.length);
        assertTrue(lines[3].startsWith("2 "));
        assertTrue(lines[3].contains("Senior Engineer"));
        assertTrue(lines[4].startsWith("3 "));
        assertTrue(lines[4].contains("Engineering Co"));
        assertFalse(ui.lastMessage.contains("Zulu"));
        assertEquals("Zulu", applications.get(0).getCompany());
    }

    @Test
    public void execute_noMatches_showsClearMessage() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Acme", "Designer"));
        RecordingUi ui = new RecordingUi();

        new SearchCommand("Google").execute(applications, ui, new Storage());

        assertEquals("No applications found matching 'Google'.", ui.lastMessage);
    }

    @Test
    public void execute_emptyList_showsEmptyMessage() {
        RecordingUi ui = new RecordingUi();
        new SearchCommand("Acme").execute(new ApplicationList(), ui, new Storage());
        assertEquals("No applications found.", ui.lastMessage);
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }
}
