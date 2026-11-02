package jobseek;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that typing "cancel" at a numbered-menu prompt abandons the action without changing anything.
 */
public class PromptCancelTest {
    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void captureOutput() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(standardOut);
    }

    private Main createMain(String input, ApplicationList applications) throws Exception {
        Storage storage = new Storage(
                Files.createTempDirectory("jobseek-cancel-test").resolve("applications.txt").toString());
        return new Main(new Ui(new Scanner(input)), storage, applications);
    }

    private ApplicationList oneApplication() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        return applications;
    }

    @Test
    public void run_cancelAtIndexPrompt_returnsToMenu() throws Exception {
        ApplicationList applications = oneApplication();

        createMain("5\ncancel\n0\n", applications).run();

        assertTrue(outputStream.toString().contains("Cancelled. No changes were made."));
        assertFalse(applications.get(0).isCompleted());
        assertTrue(outputStream.toString().contains("Goodbye!"));
    }

    @Test
    public void run_cancelIsCaseInsensitiveAndTrimmed_atCompanyPrompt() throws Exception {
        ApplicationList applications = new ApplicationList();

        createMain("1\n  CANCEL  \n0\n", applications).run();

        assertTrue(applications.isEmpty());
        assertTrue(outputStream.toString().contains("Cancelled."));
    }

    @Test
    public void run_cancelAtLaterPrompt_discardsEarlierAnswers() throws Exception {
        ApplicationList applications = new ApplicationList();

        createMain("1\nGoogle\nSWE\ncancel\n0\n", applications).run();

        assertTrue(applications.isEmpty());
    }

    @Test
    public void run_cancelAtEditFieldMenu_changesNothing() throws Exception {
        ApplicationList applications = oneApplication();

        createMain("9\n1\ncancel\n0\n", applications).run();

        assertEquals("Google", applications.get(0).getCompany());
        assertTrue(outputStream.toString().contains("Cancelled."));
    }

    @Test
    public void run_cancelAtOptionalPrompt_abandonsTheWholeAdd() throws Exception {
        ApplicationList applications = new ApplicationList();

        createMain("1\nGoogle\nSWE\n\ncancel\n0\n", applications).run();

        assertTrue(applications.isEmpty());
    }

    @Test
    public void run_cancelAtStagePrompt_changesNothing() throws Exception {
        ApplicationList applications = oneApplication();
        Application application = applications.get(0);
        String originalStage = application.getRecruitmentStage().getLabel();

        createMain("9\n1\n3\ncancel\n0\n", applications).run();

        assertEquals(originalStage, application.getRecruitmentStage().getLabel());
    }
}
