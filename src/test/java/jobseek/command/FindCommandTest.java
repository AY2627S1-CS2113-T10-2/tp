package jobseek.command;

import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationFilters;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.parser.CommandParser;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the individual filters, how they combine, and the find command built from typed input.
 */
public class FindCommandTest {

    @Test
    public void companyAndRoleFilters_ignoreCaseAndMatchPartialText() {
        Application app = new Application("Google Asia", "Software Engineer Intern", false);

        assertTrue(ApplicationFilters.companyContains("GOOGLE").test(app));
        assertTrue(ApplicationFilters.roleContains("engineer").test(app));
        assertFalse(ApplicationFilters.companyContains("shopee").test(app));
    }

    @Test
    public void stageFilter_matchesOnlyThatStage() {
        Application app = new Application("A", "R", false, "Interview", null);

        assertTrue(ApplicationFilters.inStage(RecruitmentStage.INTERVIEW).test(app));
        assertFalse(ApplicationFilters.inStage(RecruitmentStage.OFFER).test(app));
    }

    @Test
    public void deadlineFilters_includeBoundaryDateAndExcludeApplicationsWithoutDeadline() {
        LocalDate day = LocalDate.of(2026, 11, 30);
        Application onDay = new Application("A", "R", false, day);
        Application noDeadline = new Application("B", "R", false);

        assertTrue(ApplicationFilters.deadlineOnOrBefore(day).test(onDay));
        assertTrue(ApplicationFilters.deadlineOnOrAfter(day).test(onDay));
        assertFalse(ApplicationFilters.deadlineOnOrBefore(day.minusDays(1)).test(onDay));
        assertFalse(ApplicationFilters.deadlineOnOrAfter(day.plusDays(1)).test(onDay));
        assertFalse(ApplicationFilters.deadlineOnOrBefore(day).test(noDeadline));
        assertFalse(ApplicationFilters.deadlineOnOrAfter(day).test(noDeadline));
    }

    @Test
    public void completedFilter_matchesFlag() {
        Application done = new Application("A", "R", true);

        assertTrue(ApplicationFilters.isCompleted(true).test(done));
        assertFalse(ApplicationFilters.isCompleted(false).test(done));
    }

    @Test
    public void execute_combinedFilters_requireEveryConditionAndKeepOriginalNumbers() throws Exception {
        String output = runFind("find c/google s/interview", sampleApplications());

        assertTrue(output.contains("1 application(s) matching"));
        assertTrue(output.contains("company contains 'google', stage is Interview"));
        assertFalse(output.contains("Shopee"));
        // Google at position 1 is at the Applied stage, so only position 3 (Data Analyst) is listed.
        assertEquals(1, countRows(output));
        assertTrue(output.contains("Data Analyst"));
        assertFalse(output.contains("SWE Intern"));
    }

    @Test
    public void execute_deadlineRange_usesBothBounds() throws Exception {
        String output = runFind("find a/2026-11-01 b/2026-11-30", sampleApplications());

        assertEquals(2, countRows(output));
    }

    @Test
    public void execute_completedFilter_listsOnlyMatchingApplications() throws Exception {
        String output = runFind("find m/yes", sampleApplications());

        assertEquals(1, countRows(output));
        assertTrue(output.contains("Shopee"));
    }

    @Test
    public void execute_noMatches_reportsDescription() throws Exception {
        String output = runFind("find c/nonexistent", sampleApplications());

        assertTrue(output.contains("No applications found matching company contains 'nonexistent'."));
    }

    @Test
    public void execute_emptyList_reportsNoApplications() throws Exception {
        assertEquals("No applications found.", runFind("find c/google", new ApplicationList()));
    }

    @Test
    public void parse_findWithoutFilters_throwsWithUsage() {
        JobSeekException e = assertThrows(JobSeekException.class, () -> CommandParser.parse("find"));

        assertTrue(e.getMessage().contains(CommandParser.FIND_USAGE));
    }

    @Test
    public void parse_findInvalidValues_throw() {
        assertThrows(JobSeekException.class, () -> CommandParser.parse("find s/maybe"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("find b/tomorrow"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("find m/perhaps"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("find c/google c/shopee"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("find x/foo"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("find google c/google"));
    }

    @Test
    public void parse_find_buildsFindCommand() throws Exception {
        assertInstanceOf(FindCommand.class, CommandParser.parse("find r/intern a/2026-01-01 m/no"));
    }

    /** Parses and runs a find command, returning the message shown to the user. */
    private static String runFind(String input, ApplicationList applications) throws Exception {
        RecordingUi ui = new RecordingUi();
        CommandParser.parse(input).execute(applications, ui, new Storage());
        return ui.lastMessage;
    }

    private static ApplicationList sampleApplications() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, "Applied", LocalDate.of(2026, 11, 10)));
        applications.add(new Application("Shopee", "Backend Intern", true, "Offer", LocalDate.of(2026, 11, 30)));
        applications.add(new Application("Google", "Data Analyst", false, "Interview", LocalDate.of(2026, 12, 15)));
        applications.add(new Application("Grab", "PM Intern", false, "Applied", null));
        return applications;
    }

    /** Counts table rows by counting lines that start with a list number after the header and divider. */
    private static int countRows(String output) {
        String[] lines = output.split("\\R");
        int rows = 0;
        for (int i = 3; i < lines.length; i++) {
            if (lines[i].matches("^\\s*\\d+\\s*\\|.*")) {
                rows++;
            }
        }
        return rows;
    }

    private static class RecordingUi extends Ui {
        private String lastMessage;

        @Override
        public void showMessage(String message) {
            lastMessage = message;
        }
    }
}
