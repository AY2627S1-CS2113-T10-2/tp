package jobseek.command;

import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ViewCommand}.
 */
public class ViewCommandTest {
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
    public void parse_validIndex_returnsViewCommandWithZeroBasedIndex() throws Exception {
        ViewCommand command = ViewCommand.parse("1");
        assertEquals(0, command.getTargetIndex());

        ViewCommand commandTwo = ViewCommand.parse(" 42 ");
        assertEquals(41, commandTwo.getTargetIndex());
    }

    @Test
    public void parse_nullOrEmptyArgument_throwsJobSeekException() {
        JobSeekException nullException = assertThrows(JobSeekException.class, () -> ViewCommand.parse(null));
        assertEquals("Please provide a valid positive application index. Usage: view INDEX",
                nullException.getMessage());

        JobSeekException emptyException = assertThrows(JobSeekException.class, () -> ViewCommand.parse("   "));
        assertEquals("Please provide a valid positive application index. Usage: view INDEX",
                emptyException.getMessage());
    }

    @Test
    public void parse_nonNumericArgument_throwsJobSeekException() {
        JobSeekException exception = assertThrows(JobSeekException.class, () -> ViewCommand.parse("abc"));
        assertEquals("Please provide a valid positive application index. Usage: view INDEX",
                exception.getMessage());
    }

    @Test
    public void parse_zeroOrNegativeArgument_throwsJobSeekException() {
        JobSeekException zeroException = assertThrows(JobSeekException.class, () -> ViewCommand.parse("0"));
        assertEquals("Please provide a valid positive application index. Usage: view INDEX",
                zeroException.getMessage());

        JobSeekException negativeException = assertThrows(JobSeekException.class, () -> ViewCommand.parse("-5"));
        assertEquals("Please provide a valid positive application index. Usage: view INDEX",
                negativeException.getMessage());
    }

    @Test
    public void parse_integerOverflow_throwsJobSeekException() {
        JobSeekException exception = assertThrows(JobSeekException.class, () -> ViewCommand.parse("2147483648"));
        assertEquals("Please provide a valid positive application index. Usage: view INDEX",
                exception.getMessage());
    }

    @Test
    public void parse_extraTokens_throwsJobSeekException() {
        JobSeekException exception = assertThrows(JobSeekException.class, () -> ViewCommand.parse("1 2"));
        assertEquals("Please provide a valid positive application index. Usage: view INDEX",
                exception.getMessage());
    }

    @Test
    public void parse_zeroPaddedIndex_parsesCorrectly() throws Exception {
        ViewCommand command = ViewCommand.parse("007");
        assertEquals(6, command.getTargetIndex());
    }

    @Test
    public void execute_validIndexDefaultStage_displaysCardCorrectly() throws Exception {
        ApplicationList list = new ApplicationList();
        list.add(new Application("Google", "SWE Intern", false));

        ViewCommand command = new ViewCommand(0);
        command.execute(list, new Ui(), new Storage());

        String output = outputStream.toString();
        assertTrue(output.contains("=================================================="));
        assertTrue(output.contains("Application Details [Index: 1]"));
        assertTrue(output.contains("--------------------------------------------------"));
        assertTrue(output.contains("Company:   Google"));
        assertTrue(output.contains("Role:      SWE Intern"));
        assertTrue(output.contains("Status:    Applied"));
        assertTrue(output.contains("Completed: No"));
    }

    @Test
    public void execute_validIndexCustomStatusAndCompleted_displaysCardCorrectly() throws Exception {
        ApplicationList list = new ApplicationList();
        Application app = new Application("Shopee", "Backend Intern", true, "Accepted");
        list.add(app);

        ViewCommand command = new ViewCommand(0);
        command.execute(list, new Ui(), new Storage());

        String output = outputStream.toString();
        assertTrue(output.contains("Application Details [Index: 1]"));
        assertTrue(output.contains("--------------------------------------------------"));
        assertTrue(output.contains("Company:   Shopee"));
        assertTrue(output.contains("Role:      Backend Intern"));
        assertTrue(output.contains("Status:    Accepted"));
        assertTrue(output.contains("Completed: Yes"));
    }

    @Test
    public void execute_onlineAssessmentStage_displaysStageLabel() throws Exception {
        ApplicationList list = new ApplicationList();
        list.add(new Application("Google", "SWE Intern", false, "Online Assessment"));

        new ViewCommand(0).execute(list, new Ui(), new Storage());

        assertTrue(outputStream.toString().contains("Status:    Online Assessment"));
    }

    @Test
    public void execute_indexGreaterThanListSize_throwsJobSeekException() {
        ApplicationList list = new ApplicationList();
        list.add(new Application("Meta", "Data Scientist"));

        ViewCommand command = new ViewCommand(3); // target index 3 for list size 1
        JobSeekException exception = assertThrows(JobSeekException.class, () ->
                command.execute(list, new Ui(), new Storage()));

        assertTrue(exception.getMessage().contains("Application index 4 does not exist"));
    }

    @Test
    public void execute_emptyList_throwsJobSeekException() {
        ApplicationList list = new ApplicationList();
        ViewCommand command = new ViewCommand(0);

        JobSeekException exception = assertThrows(JobSeekException.class, () ->
                command.execute(list, new Ui(), new Storage()));

        assertTrue(exception.getMessage().contains("Application index 1 does not exist"));
    }

    @Test
    public void execute_negativeIndex_throwsJobSeekException() {
        ApplicationList list = new ApplicationList();
        list.add(new Application("Google", "SWE Intern"));

        ViewCommand command = new ViewCommand(-1);
        JobSeekException exception = assertThrows(JobSeekException.class, () ->
                command.execute(list, new Ui(), new Storage()));

        assertTrue(exception.getMessage().contains("Application index 0 does not exist"));
    }
}
