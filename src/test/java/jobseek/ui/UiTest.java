package jobseek.ui;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Ui}.
 */
public class UiTest {

    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    public void showLine_printsDivider() {
        Ui ui = new Ui();
        ui.showLine();
        String output = outputStream.toString();
        assertTrue(output.contains("____________________________________________________________"));
    }

    @Test
    public void showEmptyLine_printsEmptyLine() {
        Ui ui = new Ui();
        ui.showEmptyLine();
        assertEquals(System.lineSeparator(), outputStream.toString());
    }

    @Test
    public void showMessage_printsMessageWithDividers() {
        Ui ui = new Ui();
        ui.showMessage("Test message");
        String output = outputStream.toString();
        assertTrue(output.contains("Test message"));
        assertTrue(output.contains("____________________________________________________________"));
    }

    @Test
    public void showError_printsErrorPrefixWithDividers() {
        Ui ui = new Ui();
        ui.showError("Something went wrong");
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Something went wrong"));
        assertTrue(output.contains("____________________________________________________________"));
    }

    @Test
    public void showError_existingErrorPrefix_doesNotDuplicateErrorPrefix() {
        Ui ui = new Ui();
        ui.showError("Error: Custom error message");
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Custom error message"));
        org.junit.jupiter.api.Assertions.assertFalse(output.contains("Error: Error:"));
    }

    @Test
    public void showWelcome_printsLogoAndWelcomeMessage() {
        Ui ui = new Ui();
        ui.showWelcome();
        String output = outputStream.toString();
        assertTrue(output.contains("Welcome to JobSeek"));
    }

    @Test
    public void showGoodbye_printsGoodbyeMessage() {
        Ui ui = new Ui();
        ui.showGoodbye();
        String output = outputStream.toString();
        assertTrue(output.contains("Goodbye! Best of luck with your job search!"));
    }

    @Test
    public void showMenu_emptyList_printsEmptyPrompt() {
        Ui ui = new Ui();
        ui.showMenu(new ApplicationList());
        String output = outputStream.toString();
        assertTrue(output.contains("APPLICATIONS"));
        assertTrue(output.contains("No applications found."));
        assertTrue(output.contains("[1] Add application       [2] List all applications"));
        assertTrue(output.contains("[3] List uncompleted      [4] Delete application"));
        assertTrue(output.contains("[5] Mark completed        [6] Unmark application"));
        assertTrue(output.contains("[7] Search applications   [8] Sort alphabetically"));
        assertTrue(output.contains("[9] Edit application      [10] Help"));
        assertTrue(output.contains("[0] Exit"));
    }

    @Test
    public void showMenu_populatedList_printsApplicationsSummary() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        Ui ui = new Ui();
        ui.showMenu(applications);
        String output = outputStream.toString();
        assertTrue(output.contains("Google"));
        assertTrue(output.contains("SWE Intern"));
        assertTrue(output.contains("1 application tracked."));
    }

    @Test
    public void showMenu_multipleApplications_pluralSummaryCount() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        applications.add(new Application("Meta", "Data Analyst"));
        Ui ui = new Ui();
        ui.showMenu(applications);
        String output = outputStream.toString();
        assertTrue(output.contains("2 applications tracked."));
    }

    @Test
    public void showMenu_nullList_doesNotThrow() {
        Ui ui = new Ui();
        ui.showMenu();
        String output = outputStream.toString();
        assertTrue(output.contains("No applications found."));
    }

    @Test
    public void readCommand_readsFromCustomScanner() {
        Scanner scanner = new Scanner("list\nexit\n");
        Ui ui = new Ui(scanner);

        assertEquals("list", ui.readCommand());
        assertEquals("exit", ui.readCommand());
        assertNull(ui.readCommand());
    }

    @Test
    public void showPrompt_printsPromptWithoutNewline() {
        Ui ui = new Ui();
        ui.showPrompt("Prompt: ");
        assertEquals("Prompt: ", outputStream.toString());
    }

    @Test
    public void showEditMenuOptions_printsAllFieldOptionsWithDividers() {
        Ui ui = new Ui();
        ui.showEditMenuOptions();
        String output = outputStream.toString();
        assertTrue(output.contains("____________________________________________________________"));
        assertTrue(output.contains("[1] Company               [2] Role"));
        assertTrue(output.contains("[3] Recruitment stage     [4] Deadline"));
        assertTrue(output.contains("[5] Portal Link           [6] Email"));
        assertTrue(output.contains("[7] Notes                 [8] All"));
    }

    @Test
    public void showEditMenu_displaysOptionsAndPrompt() {
        Ui ui = new Ui();
        ui.showEditMenu();
        String output = outputStream.toString();
        assertTrue(output.contains("[1] Company               [2] Role"));
        assertTrue(output.contains("Enter field to edit:\n> "));
    }

    @Test
    public void constructor_nullScanner_fallsBackToSystemIn() {
        Ui ui = new Ui(null);
        ui.showLine();
        assertTrue(outputStream.toString().contains("___"));
    }
}
