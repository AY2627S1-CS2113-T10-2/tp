package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Checks alphabetical display order without changing the source list.
 */
public class SortCommandTest {
    @Test
    public void execute_sortsByCompanyThenRole_andKeepsOriginalNumbers() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("zulu", "Analyst"));
        applications.add(new Application("Acme", "Writer"));
        applications.add(new Application("acme", "Designer", true));
        RecordingUi ui = new RecordingUi();

        new SortCommand().execute(applications, ui, new Storage());

        String[] lines = ui.lastMessage.split("\\R");
        assertEquals("Applications sorted by company, then role:", lines[0]);
        assertTrue(lines[3].startsWith("3 "));
        assertTrue(lines[4].startsWith("2 "));
        assertTrue(lines[5].startsWith("1 "));
        assertEquals("zulu", applications.get(0).getCompany());
        assertEquals("Acme", applications.get(1).getCompany());
        assertEquals("acme", applications.get(2).getCompany());
    }

    @Test
    public void execute_emptyList_showsEmptyMessage() {
        RecordingUi ui = new RecordingUi();
        new SortCommand().execute(new ApplicationList(), ui, new Storage());
        assertEquals("No applications found.", ui.lastMessage);
    }

    @Test
    public void execute_identicalCompanyAndRole_preservesOriginalAdditionOrder() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        applications.add(new Application("Google", "SWE Intern"));
        RecordingUi ui = new RecordingUi();

        new SortCommand().execute(applications, ui, new Storage());

        String[] lines = ui.lastMessage.split("\\R");
        assertTrue(lines[3].startsWith("1 "));
        assertTrue(lines[4].startsWith("2 "));
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }
}
