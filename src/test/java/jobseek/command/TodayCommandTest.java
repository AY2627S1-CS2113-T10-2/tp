package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the sections and summary shown by the today dashboard, using a fixed date.
 */
public class TodayCommandTest {
    private static final LocalDate TODAY = LocalDate.of(2026, 10, 28);

    @Test
    public void execute_emptyList_showsEmptySectionsAndZeroSummary() {
        RecordingUi ui = new RecordingUi();

        new TodayCommand(TODAY).execute(new ApplicationList(), ui, new Storage());

        assertTrue(ui.lastMessage.contains("Today: 2026-10-28"));
        assertTrue(ui.lastMessage.contains("OVERDUE (0)\n  none"));
        assertTrue(ui.lastMessage.contains("DUE TODAY (0)\n  none"));
        assertTrue(ui.lastMessage.contains("0 active, 0 completed, 0 active without a deadline"));
    }

    @Test
    public void execute_mixedDeadlines_classifiesEachApplicationWithOriginalIndex() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, LocalDate.of(2026, 10, 27)));
        applications.add(new Application("Shopee", "Backend", false, LocalDate.of(2026, 10, 28)));
        applications.add(new Application("GovTech", "Analyst", false, LocalDate.of(2026, 11, 4)));
        applications.add(new Application("Grab", "Data", false, LocalDate.of(2026, 11, 5)));
        RecordingUi ui = new RecordingUi();

        new TodayCommand(TODAY).execute(applications, ui, new Storage());

        assertTrue(ui.lastMessage.contains("OVERDUE (1)\n  1. Google | SWE Intern - 2026-10-27 (1 day overdue)"));
        assertTrue(ui.lastMessage.contains("DUE TODAY (1)\n  2. Shopee | Backend - 2026-10-28 (due today)"));
        assertTrue(ui.lastMessage.contains("3. GovTech | Analyst - 2026-11-04 (in 7 days)"));
        // Seven days is the last day inside the window, so eight days away must be left out.
        assertFalse(ui.lastMessage.contains("Grab | Data -"));
    }

    @Test
    public void execute_completedApplications_areExcludedFromSectionsButCountedInSummary() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", true, LocalDate.of(2026, 10, 1)));
        applications.add(new Application("Shopee", "Backend", false));
        RecordingUi ui = new RecordingUi();

        new TodayCommand(TODAY).execute(applications, ui, new Storage());

        assertTrue(ui.lastMessage.contains("OVERDUE (0)"));
        assertTrue(ui.lastMessage.contains("1 active, 1 completed, 1 active without a deadline"));
        assertTrue(ui.lastMessage.contains("Active by status - Pending: 1"));
    }

    @Test
    public void execute_activeApplications_groupedByStatusInSummary() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("A", "R", false, "Accepted", null));
        applications.add(new Application("B", "R", false, "Pending", null));
        applications.add(new Application("C", "R", false, "Pending", null));
        RecordingUi ui = new RecordingUi();

        new TodayCommand(TODAY).execute(applications, ui, new Storage());

        assertTrue(ui.lastMessage.contains("Active by status - Accepted: 1, Pending: 2"));
    }

    @Test
    public void isExit_isFalse() {
        assertFalse(new TodayCommand(TODAY).isExit());
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }
}
