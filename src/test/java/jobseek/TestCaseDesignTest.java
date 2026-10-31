package jobseek;

import jobseek.command.AddCommand;
import jobseek.command.FindCommand;
import jobseek.command.ViewCommand;
import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.parser.CommandParser;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Systematic test suites implementing Equivalence Partitioning (EP), Boundary Value Analysis (BVA),
 * and Input Combination Strategies from the CS2113 Test Case Design curriculum.
 */
public class TestCaseDesignTest {

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

    /**
     * Equivalence Partitioning (EP) test cases.
     */
    @Nested
    public class EquivalencePartitioning {

        @Test
        public void epDate_validLeapDay_accepted() throws Exception {
            // Valid partition: 2028 is a leap year; Feb 29 is valid.
            AddCommand cmd = (AddCommand) CommandParser.parse("add c/Google r/SWE d/2028-02-29");
            assertNotNull(cmd);
        }

        @Test
        public void epDate_nonLeapYearFeb29_rejected() {
            // Invalid partition: 2027 is not a leap year; Feb 29 does not exist.
            assertThrows(JobSeekException.class, () ->
                    CommandParser.parse("add c/Google r/SWE d/2027-02-29"));
        }

        @Test
        public void epDate_invalidMonthAndDay_rejected() {
            // Invalid partitions: month > 12, day > 31, non-date string
            assertThrows(JobSeekException.class, () -> CommandParser.parse("add c/Google r/SWE d/2026-13-01"));
            assertThrows(JobSeekException.class, () -> CommandParser.parse("add c/Google r/SWE d/2026-04-31"));
            assertThrows(JobSeekException.class, () -> CommandParser.parse("add c/Google r/SWE d/tomorrow"));
        }

        @Test
        public void epStage_allSevenStagesAndShortcuts_resolved() {
            // Valid partition: all 7 standard enum stages
            assertEquals(RecruitmentStage.APPLIED, RecruitmentStage.fromChoice("1"));
            assertEquals(RecruitmentStage.ONLINE_ASSESSMENT, RecruitmentStage.fromChoice("oa"));
            assertEquals(RecruitmentStage.INTERVIEW, RecruitmentStage.fromChoice("interview"));
            assertEquals(RecruitmentStage.OFFER, RecruitmentStage.fromChoice("offer"));
            assertEquals(RecruitmentStage.ACCEPTED, RecruitmentStage.fromChoice("accepted"));
            assertEquals(RecruitmentStage.REJECTED, RecruitmentStage.fromChoice("rejected"));
            assertEquals(RecruitmentStage.WITHDRAWN, RecruitmentStage.fromChoice("withdrawn"));

            // Invalid partition: numbers out of range and arbitrary text
            assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromChoice("0"));
            assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromChoice("8"));
            assertThrows(IllegalArgumentException.class, () -> RecruitmentStage.fromChoice("ghosted"));
        }

        @Test
        public void epFindBooleanFilter_yesNoPartitions() throws Exception {
            // Valid partition: m/yes, m/no, m/true, m/false
            assertInstanceOf(FindCommand.class, CommandParser.parse("find m/yes"));
            assertInstanceOf(FindCommand.class, CommandParser.parse("find m/no"));

            // Invalid partition: unrecognized status
            assertThrows(JobSeekException.class, () -> CommandParser.parse("find m/maybe"));
        }
    }

    /**
     * Boundary Value Analysis (BVA) test cases.
     */
    @Nested
    public class BoundaryValueAnalysis {

        @Test
        public void bvaIndex_zeroMinMaxOutOfBoundsAndOverflow() throws Exception {
            ApplicationList list = new ApplicationList();
            list.add(new Application("Google", "SWE", false, RecruitmentStage.APPLIED, null, "", "", ""));
            list.add(new Application("Meta", "PE", false, RecruitmentStage.APPLIED, null, "", "", ""));
            Ui ui = new Ui();
            Storage storage = new Storage();

            // Boundary: 0 (Min - 1, invalid)
            assertThrows(JobSeekException.class, () -> CommandParser.parse("view 0"));
            assertThrows(JobSeekException.class, () -> CommandParser.parse("delete 0"));

            // Boundary: 1 (Min, valid)
            ViewCommand viewMin = (ViewCommand) CommandParser.parse("view 1");
            assertNotNull(viewMin);
            viewMin.execute(list, ui, storage);
            assertTrue(outContent.toString().contains("Google"));

            // Boundary: N = 2 (Max, valid)
            outContent.reset();
            ViewCommand viewMax = (ViewCommand) CommandParser.parse("view 2");
            assertNotNull(viewMax);
            viewMax.execute(list, ui, storage);
            assertTrue(outContent.toString().contains("Meta"));

            // Boundary: N + 1 = 3 (Max + 1, invalid)
            ViewCommand viewAbove = (ViewCommand) CommandParser.parse("view 3");
            assertNotNull(viewAbove);
            assertThrows(JobSeekException.class, () -> viewAbove.execute(list, ui, storage));

            // Extreme boundaries: negative and integer overflow
            assertThrows(JobSeekException.class, () -> CommandParser.parse("view -1"));
            assertThrows(JobSeekException.class, () -> CommandParser.parse("view 999999999999999999"));
        }

        @Test
        public void bvaDeadlineWarnings_exactBoundaryOffsets() {
            LocalDate referenceToday = LocalDate.of(2026, 11, 10);
            ApplicationList list = new ApplicationList();

            // D - 1: Overdue
            list.add(new Application("A", "R1", false, RecruitmentStage.APPLIED,
                    referenceToday.minusDays(1), "", "", ""));
            // D: Due Today
            list.add(new Application("B", "R2", false, RecruitmentStage.APPLIED,
                    referenceToday, "", "", ""));
            // D + 1: Upcoming (1 day away)
            list.add(new Application("C", "R3", false, RecruitmentStage.APPLIED,
                    referenceToday.plusDays(1), "", "", ""));
            // D + 7: Upcoming (exact 7-day boundary)
            list.add(new Application("D", "R4", false, RecruitmentStage.APPLIED,
                    referenceToday.plusDays(7), "", "", ""));
            // D + 8: Outside boundary (should NOT warn)
            list.add(new Application("E", "R5", false, RecruitmentStage.APPLIED,
                    referenceToday.plusDays(8), "", "", ""));

            DeadlineWarnings warnings = new DeadlineWarnings();
            warnings.show(list, new Ui(), referenceToday);

            String out = outContent.toString();
            assertTrue(out.contains("OVERDUE: A - R1"));
            assertTrue(out.contains("DUE TODAY: B - R2"));
            assertTrue(out.contains("UPCOMING: C - R3"));
            assertTrue(out.contains("UPCOMING: D - R4"));
            assertFalse(out.contains("E - R5"));
        }

        @Test
        public void bvaStorage_fieldCountBoundaries() throws Exception {
            Path tempFile = Files.createTempFile("bva_storage", ".txt");
            try {
                // Boundary: 3 fields (too few - invalid)
                // Boundary: 4 fields (legacy valid)
                // Boundary: 5 fields (legacy valid)
                // Boundary: 8 fields (modern valid)
                // Boundary: 9 fields (too many - invalid)
                String content = "JOBSEEK Applications\n"
                        + "company\trole\tcompleted\tdeadline\tstage\tportalLink\temail\tnotes\n"
                        + "Too\tFew\tfalse\n"
                        + "FourFields\tSWE\tfalse\t2026-12-01\n"
                        + "FiveFields\tQA\ttrue\t2026-12-02\tInterview\n"
                        + "EightFields\tDev\tfalse\t2026-12-03\tOffer\thttps://portal\tme@co.com\tnote\n"
                        + "Too\tMany\tFields\tHere\tExtra\tMore\tAnd\tEven\tMore\n";
                Files.writeString(tempFile, content);

                Storage storage = new Storage(tempFile.toString());
                ApplicationList loaded = storage.load();

                // Exactly 3 valid records loaded; 2 invalid skipped with warnings
                assertEquals(3, loaded.size());
                assertEquals("FourFields", loaded.get(0).getCompany());
                assertEquals("FiveFields", loaded.get(1).getCompany());
                assertEquals("EightFields", loaded.get(2).getCompany());
                assertEquals(2, storage.getLoadWarnings().size());
            } finally {
                Files.deleteIfExists(tempFile);
            }
        }
    }

    /**
     * Input Combination Strategies (Heuristics from textbook).
     */
    @Nested
    public class CombiningTestInputs {

        @Test
        public void heuristic_eachValidFilterAtLeastOnceInPositiveTest() throws Exception {
            // Heuristic 1: Each valid filter at least once
            assertInstanceOf(FindCommand.class, CommandParser.parse("find c/Google"));
            assertInstanceOf(FindCommand.class, CommandParser.parse("find r/Engineer"));
            assertInstanceOf(FindCommand.class, CommandParser.parse("find s/interview"));
            assertInstanceOf(FindCommand.class, CommandParser.parse("find b/2026-12-01"));
            assertInstanceOf(FindCommand.class, CommandParser.parse("find a/2026-10-01"));
            assertInstanceOf(FindCommand.class, CommandParser.parse("find m/yes"));
        }

        @Test
        public void heuristic_testInvalidInputsIndividuallyBeforeCombining() {
            // Heuristic 2: Test invalid inputs individually first
            assertThrows(JobSeekException.class, () -> CommandParser.parse("find s/ghosted"));
            assertThrows(JobSeekException.class, () -> CommandParser.parse("find b/invalid-date"));
            assertThrows(JobSeekException.class, () -> CommandParser.parse("find a/invalid-date"));
            assertThrows(JobSeekException.class, () -> CommandParser.parse("find m/invalid-bool"));
            assertThrows(JobSeekException.class, () -> CommandParser.parse("find x/unknown-prefix"));
        }

        @Test
        public void combinatorial_multipleValidFiltersSimultaneously() throws Exception {
            ApplicationList list = new ApplicationList();
            list.add(new Application("Google Asia", "SWE Intern", false,
                    RecruitmentStage.INTERVIEW, LocalDate.of(2026, 11, 15), "", "", ""));
            list.add(new Application("Google US", "PM Intern", false,
                    RecruitmentStage.APPLIED, LocalDate.of(2026, 12, 1), "", "", ""));
            list.add(new Application("Shopee", "SWE Intern", false,
                    RecruitmentStage.INTERVIEW, LocalDate.of(2026, 11, 20), "", "", ""));

            Ui ui = new Ui();
            Storage storage = new Storage();

            // Combined filter: company=google AND stage=interview AND deadline before 2026-11-30 AND incomplete
            FindCommand cmd = (FindCommand) CommandParser.parse("find c/Google s/interview b/2026-11-30 m/no");
            assertNotNull(cmd);
            cmd.execute(list, ui, storage);

            String output = outContent.toString();
            // Should match only Google Asia SWE Intern
            assertTrue(output.contains("Google Asia"));
            assertTrue(output.contains("SWE Intern"));
            assertFalse(output.contains("Google US"));
            assertFalse(output.contains("Shopee"));
        }
    }
}
