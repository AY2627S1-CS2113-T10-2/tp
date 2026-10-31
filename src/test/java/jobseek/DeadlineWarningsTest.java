package jobseek;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link DeadlineWarnings}.
 */
public class DeadlineWarningsTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    public void show_classifiesOverdueDueTodayAndUpcomingCorrectly() {
        LocalDate fixedToday = LocalDate.of(2026, 11, 10);
        ApplicationList list = new ApplicationList();

        list.add(new Application("Google", "SWE", false, RecruitmentStage.APPLIED,
                LocalDate.of(2026, 11, 5), "", "", "")); // Overdue
        list.add(new Application("Meta", "PE", false, RecruitmentStage.APPLIED,
                LocalDate.of(2026, 11, 10), "", "", "")); // Due today
        list.add(new Application("Apple", "QA", false, RecruitmentStage.APPLIED,
                LocalDate.of(2026, 11, 15), "", "", "")); // Upcoming (within 7 days)
        list.add(new Application("Netflix", "SRE", false, RecruitmentStage.APPLIED,
                LocalDate.of(2026, 11, 25), "", "", "")); // Outside 7-day window
        list.add(new Application("Amazon", "DevOps", true, RecruitmentStage.ACCEPTED,
                LocalDate.of(2026, 11, 5), "", "", "")); // Completed (suppressed)
        list.add(new Application("Microsoft", "PM", false, RecruitmentStage.APPLIED,
                null, "", "", "")); // Undated (suppressed)

        DeadlineWarnings warnings = new DeadlineWarnings();
        warnings.show(list, new Ui(), fixedToday);

        String output = outContent.toString();
        assertTrue(output.contains("Deadline warnings:"));
        assertTrue(output.contains("OVERDUE: Google - SWE (deadline 2026-11-05)"));
        assertTrue(output.contains("DUE TODAY: Meta - PE (deadline 2026-11-10)"));
        assertTrue(output.contains("UPCOMING: Apple - QA (deadline 2026-11-15)"));
        assertFalse(output.contains("Netflix"));
        assertFalse(output.contains("Amazon"));
        assertFalse(output.contains("Microsoft"));
    }

    @Test
    public void show_noMatchingDeadlines_producesNoOutput() {
        LocalDate fixedToday = LocalDate.of(2026, 11, 10);
        ApplicationList list = new ApplicationList();
        list.add(new Application("Stripe", "Security", true, RecruitmentStage.ACCEPTED,
                LocalDate.of(2026, 11, 1), "", "", ""));

        DeadlineWarnings warnings = new DeadlineWarnings();
        warnings.show(list, new Ui(), fixedToday);

        String output = outContent.toString().trim();
        assertTrue(output.isEmpty());
    }
}
