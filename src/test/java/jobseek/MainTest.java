package jobseek;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.DeleteCommand;
import jobseek.command.EditCommand;
import jobseek.command.ExitCommand;
import jobseek.command.HelpCommand;
import jobseek.command.ListCommand;
import jobseek.command.MarkCommand;
import jobseek.command.SearchCommand;
import jobseek.command.SortCommand;
import jobseek.command.ViewCommand;
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

    /** Captures console output before each workflow test. */
    @BeforeEach
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    /** Restores the original console output stream after each workflow test. */
    @AfterEach
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

    /** Verifies that selecting Exit terminates the application cleanly. */
    @Test
    public void run_menuExitOption0_terminatesCleanly() {
        Main main = createMainWithInput("0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Welcome to JobSeek"));
        assertTrue(output.contains("Goodbye! Best of luck with your job search!"));
    }

    /** Verifies that startup displays a warning for an upcoming active deadline. */
    @Test
    public void run_upcomingIncompleteDeadline_showsStartupWarning() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, LocalDate.now().plusDays(1)));
        Main main = createMainWithInput("0\n", applications);

        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Deadline warnings:"));
        assertTrue(output.contains("UPCOMING: Google - SWE Intern"));
    }

    /** Verifies that overdue and due-today active deadlines use the correct warning labels. */
    @Test
    public void run_overdueAndDueTodayDeadlines_showsCorrectWarnings() {
        LocalDate today = LocalDate.now();
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Past Company", "Past Role", false, today.minusDays(1)));
        applications.add(new Application("Today Company", "Current Role", false, today));
        Main main = createMainWithInput("0\n", applications);

        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("OVERDUE: Past Company - Past Role"));
        assertTrue(output.contains("DUE TODAY: Today Company - Current Role"));
    }

    /** Verifies that the seven-day warning window includes its last day, but not the next day. */
    @Test
    public void run_deadlineAtWarningWindowBoundary_showsOnlyWithinWindow() {
        LocalDate today = LocalDate.now();
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Within Window", "Intern", false, today.plusDays(7)));
        applications.add(new Application("Outside Window", "Intern", false, today.plusDays(8)));
        Main main = createMainWithInput("0\n", applications);

        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("UPCOMING: Within Window - Intern"));
        assertFalse(output.contains("UPCOMING: Outside Window - Intern"));
    }

    /** Verifies that completed applications and applications without deadlines have no warnings. */
    @Test
    public void run_completedAndUndatedApplications_doNotShowDeadlineWarnings() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Completed Company", "Past Role", true, LocalDate.now().minusDays(1)));
        applications.add(new Application("Undated Company", "Open Role", false));
        Main main = createMainWithInput("0\n", applications);

        main.run();

        assertFalse(outputStream.toString().contains("Deadline warnings:"));
    }

    /** Verifies that unsupported word commands show errors without ending the workflow. */
    @Test
    public void run_typedWordsOnMenu_rejectedWithErrorMessage() {
        Main main = createMainWithInput("exit\nbye\nhelp\nremove\nadd\n0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid option: 'exit'. Enter a number from 0 to 10."));
        assertTrue(output.contains("Error: Invalid option: 'bye'. Enter a number from 0 to 10."));
        assertTrue(output.contains("Error: Invalid option: 'help'. Enter a number from 0 to 10."));
        assertTrue(output.contains("Error: Invalid option: 'remove'. Enter a number from 0 to 10."));
        assertTrue(output.contains("Error: Invalid option: 'add'. Enter a number from 0 to 10."));
        assertTrue(output.contains("Goodbye! Best of luck with your job search!"));
    }

    /** Verifies that the Help menu displays all supported options. */
    @Test
    public void run_menuHelpOption7_showsHelpMessage() {
        Main main = createMainWithInput("7\n0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Available menu options:"));
        assertTrue(output.contains("[1] Add application"));
        assertTrue(output.contains("[2] List applications"));
        assertTrue(output.contains("[3] List uncompleted apps"));
        assertTrue(output.contains("[4] Delete application"));
        assertTrue(output.contains("[5] Mark application completed"));
        assertTrue(output.contains("[6] Unmark application"));
        assertTrue(output.contains("[7] Help"));
        assertTrue(output.contains("[8] Search applications"));
        assertTrue(output.contains("[9] Sort applications"));
        assertTrue(output.contains("[10] Edit application"));
        assertTrue(output.contains("[0] Exit"));
    }

    /** Verifies that adding an application records its company and role. */
    @Test
    public void run_menuAddOption1_promptsAndAddsApplication() {
        ApplicationList applications = new ApplicationList();
        String input = "1\nGoogle\nSoftware Engineer Intern\n\n\n\n\n2\n0\n";
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

    /** Verifies that blank company and role inputs are rejected until valid values are entered. */
    @Test
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

    /** Verifies that selecting List displays the current applications. */
    @Test
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

    /** Verifies that selecting Delete removes the chosen application. */
    @Test
    public void run_commandListFilters_showsFilteredApplications() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Active Co", "SWE Intern", false));
        applications.add(new Application("Done Co", "Data Analyst", true));

        String input = "list\nlist completed\nlist all\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Active Co"));
        assertTrue(output.contains("Done Co"));
    }

    @Test
    public void run_commandViewValid_displaysCard() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        String input = "view 1\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Application Details [Index: 1]"));
        assertTrue(output.contains("Company:   Google"));
        assertTrue(output.contains("Role:      SWE Intern"));
    }

    @Test
    public void run_commandViewInvalid_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        String input = "view\nview abc\nview 0\nview 5\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please provide a valid positive application index. Usage: view INDEX"));
        assertTrue(output.contains("Error: Application index 5 does not exist. (Total applications: 1)"));
    }

    @Test
    public void run_menuOption3_showsOnlyActive() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Active Co", "SWE Intern", false));
        applications.add(new Application("Done Co", "Data Analyst", true));

        String input = "3\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        int tableStart = output.indexOf("Here are your applications:\n");
        assertTrue(tableStart >= 0);
        int tableEnd = output.indexOf("JOBSEEK", tableStart);
        String tableOutput = tableEnd >= 0 ? output.substring(tableStart, tableEnd) : output.substring(tableStart);
        assertTrue(tableOutput.contains("Active Co"));
        assertFalse(tableOutput.contains("Done Co"));
    }

    @Test
    public void run_menuOption3NoActive_showsFallbackMessage() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Done Co", "Data Analyst", true));

        String input = "3\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("No active applications found."));
    }

    @Test
    public void run_multipleCommands_printsMenuTitleOnlyOnce() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backend Intern"));
        String input = "2\n2\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        int firstIndex = output.indexOf("JOBSEEK");
        assertTrue(firstIndex >= 0);
        int secondIndex = output.indexOf("JOBSEEK", firstIndex + 1);
        assertEquals(-1, secondIndex);
    }

    @Test
    public void run_menuDeleteOption3_promptsAndDeletes() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false));
        applications.add(new Application("Shopee", "Backend Intern", false));

        String input = "4\n1\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals(1, applications.size());
        assertEquals("Shopee", applications.get(0).getCompany());

        String output = outputStream.toString();
        assertTrue(output.contains("Enter application index to delete:\n> "));
        assertTrue(output.contains("I've deleted this application:"));
    }

    /** Verifies that Delete reports an error when there are no applications. */
    @Test
    public void run_menuDeleteWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "4\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    /** Verifies that Delete rejects a non-numeric application index. */
    @Test
    public void run_menuDeleteWithInvalidIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "4\ninvalidNumber\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    /** Verifies that Delete rejects a blank application index. */
    @Test
    public void run_menuDeleteWithBlankIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "4\n   \n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please specify a valid application index."));
    }

    /** Verifies that selecting Mark Complete updates the chosen application. */
    @Test
    public void run_menuMarkOption5_promptsAndMarksCompleted() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false));

        String input = "5\n1\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertTrue(applications.get(0).isCompleted());

        String output = outputStream.toString();
        assertTrue(output.contains("Enter application index to mark as completed:\n> "));
        assertTrue(output.contains("I've marked this application as completed:"));
    }

    /** Verifies that Mark Complete reports an error when there are no applications. */
    @Test
    public void run_menuMarkWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "5\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    /** Verifies that Mark Complete rejects a non-numeric application index. */
    @Test
    public void run_menuMarkWithInvalidIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "5\nabc\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    /** Verifies that Mark Complete rejects a blank application index. */
    @Test
    public void run_menuMarkWithBlankIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "5\n   \n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please specify a valid application index."));
    }

    /** Verifies that selecting Unmark clears the completion flag. */
    @Test
    public void run_menuUnmarkOption6_promptsAndUnmarks() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", true));

        String input = "6\n1\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertFalse(applications.get(0).isCompleted());

        String output = outputStream.toString();
        assertTrue(output.contains("Enter application index to unmark:\n> "));
        assertTrue(output.contains("I've marked this application as not completed yet:"));
    }

    /** Verifies that Unmark reports an error when there are no applications. */
    @Test
    public void run_menuUnmarkWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "6\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    /** Verifies that Unmark rejects a non-numeric application index. */
    @Test
    public void run_menuUnmarkWithInvalidIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "6\nabc\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    /** Verifies that Unmark rejects a blank application index. */
    @Test
    public void run_menuUnmarkWithBlankIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "6\n   \n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please specify a valid application index."));
    }

    /** Verifies that blank menu lines are ignored and Help remains accessible. */
    @Test
    public void run_blankLinesAndEmptyInputs_ignoredGracefully() {
        String input = "\n   \n\t\n7\n0\n";
        Main main = createMainWithInput(input, new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Available menu options:"));
    }

    /** Verifies that an unsupported numeric menu option displays an error. */
    @Test
    public void run_invalidMenuOption_showsError() {
        String input = "99\n0\n";
        Main main = createMainWithInput(input, new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid option: '99'. Enter a number from 0 to 10."));
    }

    @Test
    public void run_menuEditOption10_updatesCompanyOnly() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Googl", "SWE Intern", false));

        String input = "10\n1\n1\nGoogle\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("SWE Intern", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Enter application index to edit:\n> "));
        assertTrue(output.contains("Enter field to edit (1: Company, 2: Role, 3: Status, 4: Deadline, "
                + "5: Portal Link, 6: Email, 7: Notes, 8: All):\n> "));
        assertTrue(output.contains("Enter new company:\n> "));
        assertTrue(output.contains("Application updated successfully."));
    }

    @Test
    public void run_menuEditOption10_updatesRoleOnly() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backnd Intern", false));

        String input = "10\n1\n2\nBackend Intern\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Shopee", applications.get(0).getCompany());
        assertEquals("Backend Intern", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Enter new role:\n> "));
        assertTrue(output.contains("Application updated successfully."));
    }

    @Test
    public void run_menuEditOption10_updatesBothFields() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("OldCorp", "OldRole", false));

        String input = "10\n1\n8\nNewCorp\nNewRole\n1\n\n\n\n\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("NewCorp", applications.get(0).getCompany());
        assertEquals("NewRole", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Application updated successfully."));
    }

    @Test
    public void run_menuEditEmptyList_showsError() {
        ApplicationList applications = new ApplicationList();
        String input = "10\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
    public void run_menuEditInvalidIndexFormat_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "10\nabc\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    @Test
    public void run_menuEditBlankIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "10\n   \n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please specify a valid application index."));
    }

    @Test
    public void run_menuEditOutOfBoundsIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "10\n99\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index: 99. Please enter a number between 1 and 1."));
    }

    @Test
    public void run_menuEditInvalidFieldChoice_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "10\n1\n99\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid field choice: '99'. Enter a number from 1 to 8."));
    }

    @Test
    public void run_menuEditStatusOnly_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backend Intern", false, "Pending"));

        String input = "10\n1\n3\n2\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Accepted", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Enter recruitment status (1: Pending, 2: Accepted, 3: Rejected):\n> "));
        assertTrue(output.contains("Application status updated successfully."));
        assertTrue(output.contains("Status: Accepted"));
    }

    @Test
    public void run_menuEditStatusRejected_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Grab", "Data Analyst", false, "Pending"));

        String input = "10\n1\n3\n3\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Rejected", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Application status updated successfully."));
        assertTrue(output.contains("Status: Rejected"));
    }

    @Test
    public void run_menuEditStatusText_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, "Pending"));

        String input = "10\n1\n3\nAccepted\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Accepted", applications.get(0).getStatus());
    }

    @Test
    public void run_menuEditStatusInvalid_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        String input = "10\n1\n3\n99\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid status choice: '99'."));
    }

    @Test
    public void run_menuEditStatusReprompts_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        String input = "10\n1\n3\n99\n2\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Accepted", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid status choice: '99'."));
        assertTrue(output.contains("Application status updated successfully."));
        assertTrue(output.contains("Status: Accepted"));
    }

    @Test
    public void run_menuEditAllFields_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("OldCorp", "OldRole", false, "Pending"));

        String input = "10\n1\n8\nNewCorp\nNewRole\n2\n\n\n\n\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("NewCorp", applications.get(0).getCompany());
        assertEquals("NewRole", applications.get(0).getRole());
        assertEquals("Accepted", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Application updated successfully."));
        assertTrue(output.contains("Company: NewCorp"));
        assertTrue(output.contains("Role: NewRole"));
        assertTrue(output.contains("Status: Accepted"));
    }

    @Test
    public void run_menuEditEmptyCompany_promptsUntilValid() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Googl", "SWE Intern"));
        String input = "10\n1\n1\n\n   \nGoogle\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Google", applications.get(0).getCompany());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Company cannot be empty."));
    }

    @Test
    public void run_menuEditEmptyRole_promptsUntilValid() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE"));
        String input = "10\n1\n2\n\n   \nSenior SWE\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Senior SWE", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Role cannot be empty."));
    }

    /** Verifies that null and blank menu input do not produce commands. */
    @Test
    public void parseCommand_nullOrEmpty_returnsNull() {
        Main main = new Main();
        assertNull(main.parseCommand(null));
        assertNull(main.parseCommand(""));
        assertNull(main.parseCommand("   "));
    }

    /** Verifies that valid menu options create the expected command objects. */
    @Test
    public void parseCommand_validNumbers_returnsCorrectCommandInstances() {
        Main main = new Main();

        assertInstanceOf(ListCommand.class, main.parseCommand("2"));
        assertInstanceOf(ListCommand.class, main.parseCommand("3"));
        assertEquals(ListCommand.FilterType.ACTIVE,
                ((ListCommand) main.parseCommand("3")).getFilterType());
        assertInstanceOf(HelpCommand.class, main.parseCommand("7"));
        assertInstanceOf(SortCommand.class, main.parseCommand("9"));
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
        Command deleteCmd = delMain.parseCommand("4");
        assertInstanceOf(DeleteCommand.class, deleteCmd);
        assertEquals(0, ((DeleteCommand) deleteCmd).getTargetIndex());

        Scanner markScanner = new Scanner("1\n");
        Main markMain = new Main(new Ui(markScanner), new Storage(), list);
        Command markCmd = markMain.parseCommand("5");
        assertInstanceOf(MarkCommand.class, markCmd);
        assertEquals(0, ((MarkCommand) markCmd).getTargetIndex());
        assertTrue(((MarkCommand) markCmd).isCompleted());

        Scanner unmarkScanner = new Scanner("1\n");
        Main unmarkMain = new Main(new Ui(unmarkScanner), new Storage(), list);
        Command unmarkCmd = unmarkMain.parseCommand("6");
        assertInstanceOf(MarkCommand.class, unmarkCmd);
        assertEquals(0, ((MarkCommand) unmarkCmd).getTargetIndex());
        assertFalse(((MarkCommand) unmarkCmd).isCompleted());

        Scanner editScanner = new Scanner("1\n1\nGoogle LLC\n");
        Main editMain = new Main(new Ui(editScanner), new Storage(), list);
        Command editCmd = editMain.parseCommand("10");
        assertInstanceOf(EditCommand.class, editCmd);
        assertEquals(0, ((EditCommand) editCmd).getTargetIndex());
        assertEquals("Google LLC", ((EditCommand) editCmd).getNewCompany());
        assertNull(((EditCommand) editCmd).getNewRole());

        Scanner searchScanner = new Scanner("goo\n");
        Main searchMain = new Main(new Ui(searchScanner), new Storage(), list);
        assertInstanceOf(SearchCommand.class, searchMain.parseCommand("8"));

        Command viewCmd = main.parseCommand("view 1");
        assertInstanceOf(ViewCommand.class, viewCmd);
        assertEquals(0, ((ViewCommand) viewCmd).getTargetIndex());

        Command listActiveCmd = main.parseCommand("list");
        assertInstanceOf(ListCommand.class, listActiveCmd);
        assertEquals(ListCommand.FilterType.ACTIVE, ((ListCommand) listActiveCmd).getFilterType());

        Command listCompletedCmd = main.parseCommand("list completed");
        assertInstanceOf(ListCommand.class, listCompletedCmd);
        assertEquals(ListCommand.FilterType.COMPLETED, ((ListCommand) listCompletedCmd).getFilterType());

        Command listAllCmd = main.parseCommand("list all");
        assertInstanceOf(ListCommand.class, listAllCmd);
        assertEquals(ListCommand.FilterType.ALL, ((ListCommand) listAllCmd).getFilterType());
    }

    @Test
    public void run_searchAndSortOptions_keepOriginalApplicationOrder() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Zulu", "Engineer"));
        applications.add(new Application("Acme", "Designer"));
        Main main = createMainWithInput("8\nacm\n9\n0\n", applications);

        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Applications matching 'acm':"));
        assertTrue(output.contains("Applications sorted by company, then role:"));
        assertEquals("Zulu", applications.get(0).getCompany());
        assertEquals("Acme", applications.get(1).getCompany());
    }

    @Test
    public void run_searchBlankTerm_reprompts() {
        Main main = createMainWithInput("8\n  \nacme\n0\n", new ApplicationList());
        main.run();

        assertTrue(outputStream.toString().contains("Error: Search term cannot be empty."));
    }

    /** Verifies that command words are not accepted as numeric menu options. */
    @Test
    public void parseCommand_nonNumericInput_returnsNull() {
        Main main = new Main();
        assertNull(main.parseCommand("add"));
        assertNull(main.parseCommand("delete"));
        assertNull(main.parseCommand("mark"));
        assertNull(main.parseCommand("unmark"));
        assertNull(main.parseCommand("edit"));
        assertNull(main.parseCommand("help"));
        assertNull(main.parseCommand("exit"));
        assertNull(main.parseCommand("bye"));
        assertNull(main.parseCommand("foobar"));
        assertNull(main.parseCommand("remove"));
    }

    /** Verifies that constructors initialize the application list in both cases. */
    @Test
    public void constructors_initializeCorrectly() {
        Main mainDefault = new Main();
        assertNotNull(mainDefault.getApplications());

        Main mainCustom = new Main(new Ui(), new Storage(), null);
        assertNotNull(mainCustom.getApplications());
    }

    @Test
    public void run_immediateEofAtPrompts_handlesGracefully() {
        Main addMain = createMainWithInput("1\n", new ApplicationList());
        addMain.run();

        Main addRoleMain = createMainWithInput("1\nGoogle\n", new ApplicationList());
        addRoleMain.run();

        Main searchMain = createMainWithInput("8\n", new ApplicationList());
        searchMain.run();

        ApplicationList list = new ApplicationList();
        list.add(new Application("Google", "SWE Intern"));
        Main editFieldMain = createMainWithInput("10\n1\n", list);
        editFieldMain.run();

        Main editAllMidMain = createMainWithInput("10\n1\n4\nNewCo\n", list);
        editAllMidMain.run();

        assertEquals("Google", list.get(0).getCompany());
    }

    @Test
    public void run_menuAddUnicodeEmojis_storesAndDisplays() {
        ApplicationList applications = new ApplicationList();
        String input = "1\n🚀 Tech & Co.\n🎉 Lead SWE 💻✨\n2\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("🚀 Tech & Co.", applications.get(0).getCompany());
        assertEquals("🎉 Lead SWE 💻✨", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("🚀 Tech & Co."));
        assertTrue(output.contains("🎉 Lead SWE 💻✨"));
    }

    @Test
    public void run_menuAddFormatTokens_handledAsLiterals() {
        ApplicationList applications = new ApplicationList();
        String input = "1\n%s%d%x%n\n%s Developer\n\n\n\n\n8\n%s\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("%s%d%x%n", applications.get(0).getCompany());
        String output = outputStream.toString();
        assertTrue(output.contains("%s%d%x%n"));
        assertTrue(output.contains("Applications matching '%s':"));
    }

    @Test
    public void run_menuEditStatusRepeatedInvalid_repromptsAndUpdates() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        String input = "10\n1\n3\ninvalid\n99\n???\nfoo\nbar\n-1\n4\n0\nAccepted\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Accepted", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid status choice: 'invalid'."));
        assertTrue(output.contains("Error: Invalid status choice: '99'."));
        assertTrue(output.contains("Application status updated successfully."));
    }

    @Test
    public void run_indexIntegerOverflow_showsInvalidNumberError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        String input = "4\n2147483648\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    @Test
    public void run_listInvalidFilter_showsCleanError() {
        Main main = createMainWithInput("list invalid\n0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid list filter: 'invalid'. Usage: list [completed|all]"));
        org.junit.jupiter.api.Assertions.assertFalse(output.contains("Error: Error:"));
    }

    @Test
    public void run_viewOutOfBounds_showsCleanError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        Main main = createMainWithInput("view 2\n0\n", applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Application index 2 does not exist. (Total applications: 1)"));
        org.junit.jupiter.api.Assertions.assertFalse(output.contains("An error occurred while executing command:"));
    }

    @Test
    public void run_commandViewWithTab_displaysCard() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        Main main = createMainWithInput("view\t1\n0\n", applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Application Details [Index: 1]"));
        assertTrue(output.contains("Company:   Google"));
    }

    @Test
    public void run_commandListWithTab_displaysFilteredList() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backend Intern"));

        Main main = createMainWithInput("list\tall\n0\n", applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Here are your applications:"));
        assertTrue(output.contains("Shopee"));
    }

    @Test
    public void run_menuAddTabsInFields_normalizedToSpaces() {
        ApplicationList applications = new ApplicationList();
        String input = "1\nGoogle\tLLC\nSWE\tIntern\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Google LLC", applications.get(0).getCompany());
        assertEquals("SWE Intern", applications.get(0).getRole());
    }

    @Test
    public void run_menuAddWithAllFields_addsSuccessfully() {
        ApplicationList applications = new ApplicationList();
        String input = "1\nNetflix\nPlatform SWE\n2026-12-05\nhttps://jobs.netflix.com\n"
                + "hr@netflix.com\nTop priority\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals(1, applications.size());
        Application app = applications.get(0);
        assertEquals("Netflix", app.getCompany());
        assertEquals("Platform SWE", app.getRole());
        assertEquals(java.time.LocalDate.of(2026, 12, 5), app.getDeadline());
        assertEquals("https://jobs.netflix.com", app.getPortalLink());
        assertEquals("hr@netflix.com", app.getEmail());
        assertEquals("Top priority", app.getNotes());

        String output = outputStream.toString();
        assertTrue(output.contains("Portal Link: https://jobs.netflix.com"));
        assertTrue(output.contains("Email: hr@netflix.com"));
        assertTrue(output.contains("Notes: Top priority"));
    }

    @Test
    public void run_menuEditDeadlineOption4_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "10\n1\n4\n2026-12-15\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals(java.time.LocalDate.of(2026, 12, 15), applications.get(0).getDeadline());
        assertTrue(outputStream.toString().contains("Deadline: 2026-12-15"));
    }

    @Test
    public void run_menuEditPortalLinkOption5_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backend"));
        String input = "10\n1\n5\nhttps://careers.shopee.sg\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("https://careers.shopee.sg", applications.get(0).getPortalLink());
        assertTrue(outputStream.toString().contains("Portal Link: https://careers.shopee.sg"));
    }

    @Test
    public void run_menuEditEmailOption6_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Grab", "Data Analyst"));
        String input = "10\n1\n6\nrecruitment@grab.com\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("recruitment@grab.com", applications.get(0).getEmail());
        assertTrue(outputStream.toString().contains("Email: recruitment@grab.com"));
    }

    @Test
    public void run_menuEditNotesOption7_updatesSuccessfully() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("ByteDance", "Fullstack"));
        String input = "10\n1\n7\nPassed technical interview\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Passed technical interview", applications.get(0).getNotes());
        assertTrue(outputStream.toString().contains("Notes: Passed technical interview"));
    }

    @Test
    public void run_menuAddOmitOptionalFields_savesEmptyStrings() {
        ApplicationList applications = new ApplicationList();
        String input = "1\nMeta\nSoftware Engineer\n\n\n\n\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals(1, applications.size());
        Application app = applications.get(0);
        assertEquals("Meta", app.getCompany());
        assertEquals("Software Engineer", app.getRole());
        assertEquals("", app.getPortalLink());
        assertEquals("", app.getEmail());
        assertEquals("", app.getNotes());
        assertEquals(null, app.getDeadline());

        String output = outputStream.toString();
        assertTrue(output.contains("Application added successfully."));
        assertTrue(output.contains("Company: Meta"));
        assertTrue(output.contains("Role: Software Engineer"));
        assertFalse(output.contains("Deadline:"));
        assertFalse(output.contains("Portal Link:"));
        assertFalse(output.contains("Email:"));
        assertFalse(output.contains("Notes:"));
    }

    @Test
    public void run_menuEditClearOptionalFields_clearsSuccessfully() {
        ApplicationList applications = new ApplicationList();
        java.time.LocalDate deadline = java.time.LocalDate.of(2026, 12, 1);
        applications.add(new Application("Apple", "iOS Intern", false, "Pending",
                deadline, "https://jobs.apple.com", "jobs@apple.com", "Interview scheduled"));

        // Edit deadline (4) to blank, portal link (5) to blank, email (6) to blank, notes (7) to blank
        String input = "10\n1\n4\n\n10\n1\n5\n\n10\n1\n6\n\n10\n1\n7\n\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        Application app = applications.get(0);
        assertEquals(null, app.getDeadline());
        assertEquals("", app.getPortalLink());
        assertEquals("", app.getEmail());
        assertEquals("", app.getNotes());
    }
}
