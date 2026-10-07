package jobseek.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jobseek.logic.ApplicationManager;
import jobseek.storage.Storage;
import jobseek.ui.CliUi;

/**
 * Unit and workflow tests for {@link JobSeekController}.
 */
public class JobSeekControllerTest {

    private Path tempFile;
    private Storage storage;
    private ApplicationManager manager;
    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("jobseek_ctrl_test", ".txt");
        storage = new Storage(tempFile.toString());
        manager = new ApplicationManager();
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() throws IOException {
        System.setOut(standardOut);
        Files.deleteIfExists(tempFile);
    }

    private JobSeekController createController(String simulatedInput) {
        Scanner scanner = new Scanner(new ByteArrayInputStream(simulatedInput.getBytes()));
        CliUi ui = new CliUi(scanner);
        return new JobSeekController(ui, manager, storage);
    }

    @Test
    public void run_exitOption_terminatesCleanly() {
        JobSeekController controller = createController("0\n");
        controller.run();

        String output = outputStream.toString();
        assertEquals(true, output.contains("JOBSEEK"));
        assertEquals(true, output.contains("Goodbye!"));
    }

    @Test
    public void run_invalidMenuOption_displaysErrorAndDoesNotCrash() {
        JobSeekController controller = createController("abc\n0\n");
        controller.run();

        String output = outputStream.toString();
        assertEquals(true, output.contains("Invalid option. Enter 0, 1, 2, or 3."));
        assertEquals(0, manager.getApplicationCount());
    }

    @Test
    public void run_addApplicationFlow_savesAndDisplaysSuccess() {
        // Option 1 -> Company -> Role -> Enter to return -> Exit (0)
        String input = "1\nGoogle\nSoftware Engineer Intern\n\n0\n";
        JobSeekController controller = createController(input);
        controller.run();

        assertEquals(1, manager.getApplicationCount());
        assertEquals("Google", manager.getApplications().get(0).getCompany());
        assertEquals("Software Engineer Intern", manager.getApplications().get(0).getRole());

        String output = outputStream.toString();
        assertEquals(true, output.contains("Application added successfully."));
        assertEquals(true, output.contains("Company : Google"));
        assertEquals(true, output.contains("Role    : Software Engineer Intern"));
    }

    @Test
    public void run_addApplicationWithEmptyInputs_promptsAgainUntilValid() {
        // Option 1 -> empty company -> spaces -> valid company -> empty role -> valid role -> Enter -> 0
        String input = "1\n\n   \nGoogle\n\n   \nSWE Intern\n\n0\n";
        JobSeekController controller = createController(input);
        controller.run();

        assertEquals(1, manager.getApplicationCount());
        String output = outputStream.toString();
        assertEquals(true, output.contains("Error: Company cannot be empty."));
        assertEquals(true, output.contains("Error: Role cannot be empty."));
    }

    @Test
    public void run_deleteApplicationFlow_success() {
        manager.addApplication("Google", "SWE Intern");
        manager.addApplication("Shopee", "Backend Intern");

        // Option 2 -> Delete index 1 -> 0
        String input = "2\n1\n0\n";
        JobSeekController controller = createController(input);
        controller.run();

        assertEquals(1, manager.getApplicationCount());
        assertEquals("Shopee", manager.getApplications().get(0).getCompany());

        String output = outputStream.toString();
        assertEquals(true, output.contains("Application deleted successfully."));
        assertEquals(true, output.contains("Company : Google"));
    }

    @Test
    public void run_deleteApplicationWithInvalidInputs_displaysError() {
        manager.addApplication("Google", "SWE Intern");

        // Option 2 -> not a number -> 0
        String input = "2\ninvalid\n0\n";
        JobSeekController controller = createController(input);
        controller.run();

        assertEquals(1, manager.getApplicationCount());
        String output = outputStream.toString();
        assertEquals(true, output.contains("Error: Please enter a valid application index."));
    }

    @Test
    public void run_deleteWhenEmpty_displaysNoApplicationsMessage() {
        // Option 2 -> Enter -> 0
        String input = "2\n\n0\n";
        JobSeekController controller = createController(input);
        controller.run();

        String output = outputStream.toString();
        assertEquals(true, output.contains("No applications available to delete."));
    }

    @Test
    public void run_helpFlow_returnsToApplications() {
        // Option 3 -> Enter -> 0
        String input = "3\n\n0\n";
        JobSeekController controller = createController(input);
        controller.run();

        String output = outputStream.toString();
        assertEquals(true, output.contains("HELP"));
        assertEquals(true, output.contains("[1] Add application"));
        assertEquals(true, output.contains("Applications are saved automatically."));
    }
}
