package jobseek;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.DeleteCommand;
import jobseek.command.ExitCommand;
import jobseek.command.HelpCommand;
import jobseek.command.ListCommand;
import jobseek.command.MarkCommand;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Extensive tests for {@link Main} workflow execution and numbers-only menu navigation.
 */
public class MainTest {

    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    /** Captures console output before each workflow test. */
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    /** Restores the original console output stream after each workflow test. */
    public void tearDown() {
        System.setOut(standardOut);
    }

    /** Creates a Main instance using scripted input and a supplied application list. */
    private Main createMainWithInput(String input, ApplicationList applications) {
        Scanner scanner = new Scanner(input);
        Ui ui = new Ui(scanner);
        Storage storage = new Storage();
        return new Main(ui, storage, applications);
    }

    @Test
    /** Verifies that selecting Exit terminates the application cleanly. */
    public void run_menuExitOption0_terminatesCleanly() {
        Main main = createMainWithInput("0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Welcome to JobSeek"));
        assertTrue(output.contains("Goodbye! Best of luck with your job search!"));
    }

    @Test
    /** Verifies that startup displays a warning for an upcoming active deadline. */
    public void run_upcomingIncompleteDeadline_showsStartupWarning() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, LocalDate.now().plusDays(1)));
        Main main = createMainWithInput("0\n", applications);

        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Deadline warnings:"));
        assertTrue(output.contains("UPCOMING: Google - SWE Intern"));
    }

    @Test
    /** Verifies that unsupported word commands show errors without ending the workflow. */
    public void run_typedWordsOnMenu_rejectedWithErrorMessage() {
        Main main = createMainWithInput("exit\nbye\nhelp\nlist\nadd\n0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid option: 'exit'. Enter 0, 1, 2, 3, 4, 5, or 6."));
        assertTrue(output.contains("Error: Invalid option: 'bye'. Enter 0, 1, 2, 3, 4, 5, or 6."));
        assertTrue(output.contains("Error: Invalid option: 'help'. Enter 0, 1, 2, 3, 4, 5, or 6."));
        assertTrue(output.contains("Error: Invalid option: 'list'. Enter 0, 1, 2, 3, 4, 5, or 6."));
        assertTrue(output.contains("Error: Invalid option: 'add'. Enter 0, 1, 2, 3, 4, 5, or 6."));
        assertTrue(output.contains("Goodbye! Best of luck with your job search!"));
    }

    @Test
    /** Verifies that the Help menu displays all supported options. */
    public void run_menuHelpOption6_showsHelpMessage() {
        Main main = createMainWithInput("6\n0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Available menu options:"));
        assertTrue(output.contains("[1] Add application"));
        assertTrue(output.contains("[2] List applications"));
        assertTrue(output.contains("[3] Delete application"));
        assertTrue(output.contains("[4] Mark application completed"));
        assertTrue(output.contains("[5] Unmark application"));
        assertTrue(output.contains("[6] Help"));
        assertTrue(output.contains("[0] Exit"));
    }

    @Test
    /** Verifies that adding an application records its company and role. */
    public void run_menuAddOption1_promptsAndAddsApplication() {
        ApplicationList applications = new ApplicationList();
        String input = "1\nGoogle\nSoftware Engineer Intern\n\n2\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals(1, applications.size());
        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("Software Engineer Intern", applications.get(0).getRole());

        String output = outputStream.toString();
        assertTrue(output.contains("Company:\n> "));
        assertTrue(output.contains("Role:\n> "));
        assertTrue(output.contains("Application added successfully."));
        assertTrue(output.contains("Here are your applications:"));
        assertTrue(output.contains("Google"));
        assertTrue(output.contains("Software Engineer Intern"));
    }

    @Test
    /** Verifies that blank company and role inputs are rejected until valid values are entered. */
    public void run_menuAddWithRetryOnEmptyField_promptsUntilValid() {
        ApplicationList applications = new ApplicationList();
        String input = "1\n\n   \nShopee\n\n   \nBackend Intern\n\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals(1, applications.size());
        assertEquals("Shopee", applications.get(0).getCompany());
        assertEquals("Backend Intern", applications.get(0).getRole());

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Company cannot be empty."));
        assertTrue(output.contains("Error: Role cannot be empty."));
    }

    @Test
    /** Verifies that selecting List displays the current applications. */
    public void run_menuListOption2_showsTable() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backend Intern"));
        String input = "2\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Here are your applications:"));
        assertTrue(output.contains("Shopee"));
        assertTrue(output.contains("Backend Intern"));
    }

    @Test
    /** Verifies that selecting Delete removes the chosen application. */
    public void run_menuDeleteOption3_promptsAndDeletes() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false));
        applications.add(new Application("Shopee", "Backend Intern", false));

        String input = "3\n1\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals(1, applications.size());
        assertEquals("Shopee", applications.get(0).getCompany());

        String output = outputStream.toString();
        assertTrue(output.contains("Enter application index to delete:\n> "));
        assertTrue(output.contains("I've deleted this application:"));
    }

    @Test
    /** Verifies that Delete reports an error when there are no applications. */
    public void run_menuDeleteWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "3\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
    /** Verifies that Delete rejects a non-numeric application index. */
    public void run_menuDeleteWithInvalidIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "3\ninvalidNumber\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    @Test
    /** Verifies that Delete rejects a blank application index. */
    public void run_menuDeleteWithBlankIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "3\n   \n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please specify a valid application index."));
    }

    @Test
    /** Verifies that selecting Mark Complete updates the chosen application. */
    public void run_menuMarkOption4_promptsAndMarksCompleted() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false));

        String input = "4\n1\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertTrue(applications.get(0).isCompleted());

        String output = outputStream.toString();
        assertTrue(output.contains("Enter application index to mark as completed:\n> "));
        assertTrue(output.contains("I've marked this application as completed:"));
    }

    @Test
    /** Verifies that Mark Complete reports an error when there are no applications. */
    public void run_menuMarkWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "4\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
    /** Verifies that Mark Complete rejects a non-numeric application index. */
    public void run_menuMarkWithInvalidIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "4\nabc\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    @Test
    /** Verifies that Mark Complete rejects a blank application index. */
    public void run_menuMarkWithBlankIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "4\n   \n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please specify a valid application index."));
    }

    @Test
    /** Verifies that selecting Unmark clears the completion flag. */
    public void run_menuUnmarkOption5_promptsAndUnmarks() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", true));

        String input = "5\n1\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertFalse(applications.get(0).isCompleted());

        String output = outputStream.toString();
        assertTrue(output.contains("Enter application index to unmark:\n> "));
        assertTrue(output.contains("I've marked this application as not completed yet:"));
    }

    @Test
    /** Verifies that Unmark reports an error when there are no applications. */
    public void run_menuUnmarkWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "5\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
    /** Verifies that Unmark rejects a non-numeric application index. */
    public void run_menuUnmarkWithInvalidIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "5\nabc\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    @Test
    /** Verifies that Unmark rejects a blank application index. */
    public void run_menuUnmarkWithBlankIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "5\n   \n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please specify a valid application index."));
    }

    @Test
    /** Verifies that blank menu lines are ignored and Help remains accessible. */
    public void run_blankLinesAndEmptyInputs_ignoredGracefully() {
        String input = "\n   \n\t\n6\n0\n";
        Main main = createMainWithInput(input, new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Available menu options:"));
    }

    @Test
    /** Verifies that an unsupported numeric menu option displays an error. */
    public void run_invalidMenuOption_showsError() {
        String input = "99\n0\n";
        Main main = createMainWithInput(input, new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid option: '99'. Enter 0, 1, 2, 3, 4, 5, or 6."));
    }

    @Test
    /** Verifies that null and blank menu input do not produce commands. */
    public void parseCommand_nullOrEmpty_returnsNull() {
        Main main = new Main();
        assertNull(main.parseCommand(null));
        assertNull(main.parseCommand(""));
        assertNull(main.parseCommand("   "));
    }

    @Test
    /** Verifies that valid menu options create the expected command objects. */
    public void parseCommand_validNumbers_returnsCorrectCommandInstances() {
        Main main = new Main();

        assertInstanceOf(ListCommand.class, main.parseCommand("2"));
        assertInstanceOf(HelpCommand.class, main.parseCommand("6"));
        assertInstanceOf(ExitCommand.class, main.parseCommand("0"));

        Scanner addScanner = new Scanner("Google\nSWE Intern\n");
        Main addMain = new Main(new Ui(addScanner), new Storage(), new ApplicationList());
        Command addCmd = addMain.parseCommand("1");
        assertInstanceOf(AddCommand.class, addCmd);
        assertEquals("Google", ((AddCommand) addCmd).getCompany());
        assertEquals("SWE Intern", ((AddCommand) addCmd).getRole());

        ApplicationList list = new ApplicationList();
        list.add(new Application("Google", "SWE Intern"));

        Scanner delScanner = new Scanner("1\n");
        Main delMain = new Main(new Ui(delScanner), new Storage(), list);
        Command deleteCmd = delMain.parseCommand("3");
        assertInstanceOf(DeleteCommand.class, deleteCmd);
        assertEquals(0, ((DeleteCommand) deleteCmd).getTargetIndex());

        Scanner markScanner = new Scanner("1\n");
        Main markMain = new Main(new Ui(markScanner), new Storage(), list);
        Command markCmd = markMain.parseCommand("4");
        assertInstanceOf(MarkCommand.class, markCmd);
        assertEquals(0, ((MarkCommand) markCmd).getTargetIndex());
        assertTrue(((MarkCommand) markCmd).isCompleted());

        Scanner unmarkScanner = new Scanner("1\n");
        Main unmarkMain = new Main(new Ui(unmarkScanner), new Storage(), list);
        Command unmarkCmd = unmarkMain.parseCommand("5");
        assertInstanceOf(MarkCommand.class, unmarkCmd);
        assertEquals(0, ((MarkCommand) unmarkCmd).getTargetIndex());
        assertFalse(((MarkCommand) unmarkCmd).isCompleted());
    }

    @Test
    /** Verifies that command words are not accepted as numeric menu options. */
    public void parseCommand_nonNumericInput_returnsNull() {
        Main main = new Main();
        assertNull(main.parseCommand("add"));
        assertNull(main.parseCommand("list"));
        assertNull(main.parseCommand("delete"));
        assertNull(main.parseCommand("mark"));
        assertNull(main.parseCommand("unmark"));
        assertNull(main.parseCommand("help"));
        assertNull(main.parseCommand("exit"));
        assertNull(main.parseCommand("bye"));
        assertNull(main.parseCommand("foobar"));
    }

    @Test
    /** Verifies that constructors initialize the application list in both cases. */
    public void constructors_initializeCorrectly() {
        Main mainDefault = new Main();
        assertNotNull(mainDefault.getApplications());

        Main mainCustom = new Main(new Ui(), new Storage(), null);
        assertNotNull(mainCustom.getApplications());
    }
}
