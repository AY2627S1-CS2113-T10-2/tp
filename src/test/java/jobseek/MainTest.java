package jobseek;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.DeleteCommand;
import jobseek.command.EditCommand;
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
    public void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(standardOut);
    }

    private Main createMainWithInput(String input, ApplicationList applications) {
        Scanner scanner = new Scanner(input);
        Ui ui = new Ui(scanner);
        Storage storage = new Storage();
        return new Main(ui, storage, applications);
    }

    @Test
    public void run_menuExitOption0_terminatesCleanly() {
        Main main = createMainWithInput("0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Welcome to JobSeek"));
        assertTrue(output.contains("Goodbye! Best of luck with your job search!"));
    }

    @Test
    public void run_typedWordsOnMenu_rejectedWithErrorMessage() {
        Main main = createMainWithInput("exit\nbye\nhelp\nlist\nadd\n0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid option: 'exit'. Enter 0, 1, 2, 3, 4, 5, 6, or 7."));
        assertTrue(output.contains("Error: Invalid option: 'bye'. Enter 0, 1, 2, 3, 4, 5, 6, or 7."));
        assertTrue(output.contains("Error: Invalid option: 'help'. Enter 0, 1, 2, 3, 4, 5, 6, or 7."));
        assertTrue(output.contains("Error: Invalid option: 'list'. Enter 0, 1, 2, 3, 4, 5, 6, or 7."));
        assertTrue(output.contains("Error: Invalid option: 'add'. Enter 0, 1, 2, 3, 4, 5, 6, or 7."));
        assertTrue(output.contains("Goodbye! Best of luck with your job search!"));
    }

    @Test
    public void run_menuHelpOption7_showsHelpMessage() {
        Main main = createMainWithInput("7\n0\n", new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Available menu options:"));
        assertTrue(output.contains("[1] Add application"));
        assertTrue(output.contains("[2] List applications"));
        assertTrue(output.contains("[3] Delete application"));
        assertTrue(output.contains("[4] Mark application completed"));
        assertTrue(output.contains("[5] Unmark application"));
        assertTrue(output.contains("[6] Edit application"));
        assertTrue(output.contains("[7] Help"));
        assertTrue(output.contains("[0] Exit"));
    }

    @Test
    public void run_menuAddOption1_promptsAndAddsApplication() {
        ApplicationList applications = new ApplicationList();
        String input = "1\nGoogle\nSoftware Engineer Intern\n2\n0\n";
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
    public void run_menuAddWithRetryOnEmptyField_promptsUntilValid() {
        ApplicationList applications = new ApplicationList();
        String input = "1\n\n   \nShopee\n\n   \nBackend Intern\n0\n";
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
    public void run_menuDeleteWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "3\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
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
    public void run_menuMarkWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "4\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
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
    public void run_menuUnmarkWhenEmpty_showsEmptyError() {
        ApplicationList applications = new ApplicationList();
        String input = "5\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
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
    public void run_blankLinesAndEmptyInputs_ignoredGracefully() {
        String input = "\n   \n\t\n7\n0\n";
        Main main = createMainWithInput(input, new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Available menu options:"));
    }

    @Test
    public void run_invalidMenuOption_showsError() {
        String input = "99\n0\n";
        Main main = createMainWithInput(input, new ApplicationList());
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid option: '99'. Enter 0, 1, 2, 3, 4, 5, 6, or 7."));
    }

    @Test
    public void run_menuEditOption6_updatesCompanyOnly() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Googl", "SWE Intern", false));

        String input = "6\n1\n1\nGoogle\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("SWE Intern", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Enter application index to edit:\n> "));
        assertTrue(output.contains("Enter field to edit (1: Company, 2: Role, 3: Both):\n> "));
        assertTrue(output.contains("Enter new company:\n> "));
        assertTrue(output.contains("Application updated successfully."));
    }

    @Test
    public void run_menuEditOption6_updatesRoleOnly() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backnd Intern", false));

        String input = "6\n1\n2\nBackend Intern\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Shopee", applications.get(0).getCompany());
        assertEquals("Backend Intern", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Enter new role:\n> "));
        assertTrue(output.contains("Application updated successfully."));
    }

    @Test
    public void run_menuEditOption6_updatesBothFields() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("OldCorp", "OldRole", false));

        String input = "6\n1\n3\nNewCorp\nNewRole\n0\n";
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
        String input = "6\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
    public void run_menuEditInvalidIndexFormat_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "6\nabc\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index. Please enter a valid number."));
    }

    @Test
    public void run_menuEditBlankIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "6\n   \n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Please specify a valid application index."));
    }

    @Test
    public void run_menuEditOutOfBoundsIndex_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "6\n99\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index: 99. Please enter a number between 1 and 1."));
    }

    @Test
    public void run_menuEditInvalidFieldChoice_showsError() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        String input = "6\n1\n99\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid field choice: '99'. Enter 1, 2, or 3."));
    }

    @Test
    public void run_menuEditEmptyCompany_promptsUntilValid() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Googl", "SWE Intern"));
        String input = "6\n1\n1\n\n   \nGoogle\n0\n";
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
        String input = "6\n1\n2\n\n   \nSenior SWE\n0\n";
        Main main = createMainWithInput(input, applications);
        main.run();

        assertEquals("Senior SWE", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Role cannot be empty."));
    }

    @Test
    public void parseCommand_nullOrEmpty_returnsNull() {
        Main main = new Main();
        assertNull(main.parseCommand(null));
        assertNull(main.parseCommand(""));
        assertNull(main.parseCommand("   "));
    }

    @Test
    public void parseCommand_validNumbers_returnsCorrectCommandInstances() {
        Main main = new Main();

        assertInstanceOf(ListCommand.class, main.parseCommand("2"));
        assertInstanceOf(HelpCommand.class, main.parseCommand("7"));
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

        Scanner editScanner = new Scanner("1\n1\nGoogle LLC\n");
        Main editMain = new Main(new Ui(editScanner), new Storage(), list);
        Command editCmd = editMain.parseCommand("6");
        assertInstanceOf(EditCommand.class, editCmd);
        assertEquals(0, ((EditCommand) editCmd).getTargetIndex());
        assertEquals("Google LLC", ((EditCommand) editCmd).getNewCompany());
        assertNull(((EditCommand) editCmd).getNewRole());
    }

    @Test
    public void parseCommand_nonNumericInput_returnsNull() {
        Main main = new Main();
        assertNull(main.parseCommand("add"));
        assertNull(main.parseCommand("list"));
        assertNull(main.parseCommand("delete"));
        assertNull(main.parseCommand("mark"));
        assertNull(main.parseCommand("unmark"));
        assertNull(main.parseCommand("edit"));
        assertNull(main.parseCommand("help"));
        assertNull(main.parseCommand("exit"));
        assertNull(main.parseCommand("bye"));
        assertNull(main.parseCommand("foobar"));
    }

    @Test
    public void constructors_initializeCorrectly() {
        Main mainDefault = new Main();
        assertNotNull(mainDefault.getApplications());

        Main mainCustom = new Main(new Ui(), new Storage(), null);
        assertNotNull(mainCustom.getApplications());
    }
}
