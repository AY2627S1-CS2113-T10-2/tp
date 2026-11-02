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
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that input ending in the middle of a menu action abandons it, instead of acting like blank answers.
 */
public class EndOfInputTest {
    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream output;

    @BeforeEach
    public void captureOutput() {
        output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(standardOut);
    }

    private static ApplicationList filledList() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false, RecruitmentStage.APPLIED,
                LocalDate.of(2026, 12, 1), "http://portal", "hr@google.com", "important note"));
        return applications;
    }

    private void run(String input, ApplicationList applications) {
        new Main(new Ui(new Scanner(input)), TestStorage.create(), applications).run();
    }

    @Test
    public void run_inputEndsAtEditDeadlinePrompt_keepsDeadline() {
        ApplicationList applications = filledList();

        run("9\n1\n4\n", applications);

        assertEquals(LocalDate.of(2026, 12, 1), applications.get(0).getDeadline());
    }

    @Test
    public void run_inputEndsAtEditPortalEmailOrNotesPrompt_keepsThem() {
        for (String choice : new String[] {"5", "6", "7"}) {
            ApplicationList applications = filledList();

            run("9\n1\n" + choice + "\n", applications);

            assertEquals("http://portal", applications.get(0).getPortalLink());
            assertEquals("hr@google.com", applications.get(0).getEmail());
            assertEquals("important note", applications.get(0).getNotes());
        }
    }

    @Test
    public void run_enterOnEditNotesPrompt_stillClearsNotes() {
        ApplicationList applications = filledList();

        run("9\n1\n7\n\n0\n", applications);

        assertEquals("", applications.get(0).getNotes());
    }

    @Test
    public void run_enterOnEditDeadlinePrompt_stillClearsDeadline() {
        ApplicationList applications = filledList();

        run("9\n1\n4\n\n0\n", applications);

        assertNull(applications.get(0).getDeadline());
    }

    @Test
    public void run_inputEndsPartWayThroughAdd_addsNothing() {
        ApplicationList applications = new ApplicationList();

        run("1\nGoogle\nSWE\n", applications);

        assertTrue(applications.isEmpty());
    }

    @Test
    public void run_inputEndsAtLastAddPrompt_addsNothing() {
        ApplicationList applications = new ApplicationList();

        run("1\nGoogle\nSWE\n\n\n", applications);

        assertTrue(applications.isEmpty());
    }

    @Test
    public void run_inputEndsPartWayThroughEditAll_changesNothing() {
        ApplicationList applications = filledList();

        run("9\n1\n8\nNewCo\nNewRole\n1\n", applications);

        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("important note", applications.get(0).getNotes());
    }
}
