package jobseek.parser;

import jobseek.command.AddCommand;
import jobseek.command.Command;
import jobseek.command.CommandHistory;
import jobseek.command.DeleteCommand;
import jobseek.command.EditCommand;
import jobseek.command.ExitCommand;
import jobseek.command.HelpCommand;
import jobseek.command.ListCommand;
import jobseek.command.MarkCommand;
import jobseek.command.SearchCommand;
import jobseek.command.SortCommand;
import jobseek.command.TodayCommand;
import jobseek.command.UndoCommand;
import jobseek.command.ViewCommand;
import jobseek.exception.JobSeekException;
import jobseek.model.RecruitmentStage;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that typed command lines are converted into the right commands, or rejected with a clear message.
 */
public class CommandParserTest {

    @Test
    public void parse_addWithAllFields_buildsAddCommand() throws Exception {
        AddCommand command = assertInstanceOf(AddCommand.class,
                CommandParser.parse("add c/Google r/SWE Intern d/2026-11-30"));

        assertEquals("Google", command.getCompany());
        assertEquals("SWE Intern", command.getRole());
        assertEquals(LocalDate.of(2026, 11, 30), command.getDeadline());
    }

    @Test
    public void parse_addWithoutDeadline_leavesDeadlineNull() throws Exception {
        AddCommand command = assertInstanceOf(AddCommand.class, CommandParser.parse("add c/Google r/SWE"));

        assertNull(command.getDeadline());
    }

    @Test
    public void parse_addFieldsInAnyOrderAndWithSpacesInValues_areParsed() throws Exception {
        AddCommand command = assertInstanceOf(AddCommand.class,
                CommandParser.parse("  ADD   d/2026-01-05  r/Data Analyst Intern   c/Grab Holdings  "));

        assertEquals("Grab Holdings", command.getCompany());
        assertEquals("Data Analyst Intern", command.getRole());
        assertEquals(LocalDate.of(2026, 1, 5), command.getDeadline());
    }

    @Test
    public void parse_addValueContainingSlashInsideWord_isNotTreatedAsPrefix() throws Exception {
        AddCommand command = assertInstanceOf(AddCommand.class,
                CommandParser.parse("add c/AT&T/Intel r/R&D/Ops"));

        assertEquals("AT&T/Intel", command.getCompany());
        assertEquals("R&D/Ops", command.getRole());
    }

    @Test
    public void parse_addMissingRole_throwsWithUsage() {
        JobSeekException e = assertThrows(JobSeekException.class, () -> CommandParser.parse("add c/Google"));

        assertTrue(e.getMessage().contains(CommandParser.ADD_USAGE));
    }

    @Test
    public void parse_addWithNoArguments_throwsWithUsage() {
        assertThrows(JobSeekException.class, () -> CommandParser.parse("add"));
    }

    @Test
    public void parse_addEmptyField_throws() {
        JobSeekException e = assertThrows(JobSeekException.class, () -> CommandParser.parse("add c/ r/SWE"));

        assertTrue(e.getMessage().contains("'c/' cannot be empty"));
    }

    @Test
    public void parse_addDuplicateField_throws() {
        JobSeekException e = assertThrows(JobSeekException.class,
                () -> CommandParser.parse("add c/Google c/Shopee r/SWE"));

        assertTrue(e.getMessage().contains("more than once"));
    }

    @Test
    public void parse_addUnknownPrefix_throws() {
        JobSeekException e = assertThrows(JobSeekException.class,
                () -> CommandParser.parse("add c/Google r/SWE x/oops"));

        assertTrue(e.getMessage().contains("Unknown field 'x/'"));
    }

    @Test
    public void parse_addStrayTextBeforeFields_throws() {
        assertThrows(JobSeekException.class, () -> CommandParser.parse("add Google c/Google r/SWE"));
    }

    @Test
    public void parse_addInvalidDeadline_throws() {
        JobSeekException e = assertThrows(JobSeekException.class,
                () -> CommandParser.parse("add c/Google r/SWE d/31-12-2026"));

        assertTrue(e.getMessage().contains("YYYY-MM-DD"));
    }

    @Test
    public void parse_editWithAllFields_buildsEditCommandWithZeroBasedIndex() throws Exception {
        EditCommand command = assertInstanceOf(EditCommand.class,
                CommandParser.parse("edit 3 c/Shopee r/Backend s/interview"));

        assertEquals(2, command.getTargetIndex());
        assertEquals("Shopee", command.getNewCompany());
        assertEquals("Backend", command.getNewRole());
        assertEquals(RecruitmentStage.INTERVIEW, command.getNewStage());
    }

    @Test
    public void parse_editStageShortcut_isNormalisedToLabel() throws Exception {
        EditCommand command = assertInstanceOf(EditCommand.class, CommandParser.parse("edit 1 s/oa"));

        assertEquals(RecruitmentStage.ONLINE_ASSESSMENT, command.getNewStage());
        assertNull(command.getNewCompany());
    }

    @Test
    public void parse_editInvalidStage_throwsListingValidStages() {
        JobSeekException e = assertThrows(JobSeekException.class, () -> CommandParser.parse("edit 1 s/maybe"));

        assertTrue(e.getMessage().contains("Valid stages"));
    }

    @Test
    public void parse_editWithoutFields_throws() {
        JobSeekException e = assertThrows(JobSeekException.class, () -> CommandParser.parse("edit 2"));

        assertTrue(e.getMessage().contains("Nothing to edit"));
    }

    @Test
    public void parse_editWithoutIndex_throws() {
        assertThrows(JobSeekException.class, () -> CommandParser.parse("edit c/Google"));
    }

    @Test
    public void parse_deleteMarkAndUnmark_useZeroBasedIndex() throws Exception {
        DeleteCommand delete = assertInstanceOf(DeleteCommand.class, CommandParser.parse("delete 2"));
        MarkCommand mark = assertInstanceOf(MarkCommand.class, CommandParser.parse("mark 1"));
        MarkCommand unmark = assertInstanceOf(MarkCommand.class, CommandParser.parse("unmark 4"));

        assertEquals(1, delete.getTargetIndex());
        assertEquals(0, mark.getTargetIndex());
        assertTrue(mark.isCompleted());
        assertEquals(3, unmark.getTargetIndex());
        assertFalse(unmark.isCompleted());
    }

    @Test
    public void parse_invalidIndexes_throw() {
        assertThrows(JobSeekException.class, () -> CommandParser.parse("delete"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("delete abc"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("delete 0"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("mark -2"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("unmark 99999999999"));
    }

    @Test
    public void parse_search_buildsSearchCommand() throws Exception {
        assertInstanceOf(SearchCommand.class, CommandParser.parse("search google"));
        assertThrows(JobSeekException.class, () -> CommandParser.parse("search"));
    }

    @Test
    public void parse_simpleWords_buildMatchingCommands() throws Exception {
        assertInstanceOf(SortCommand.class, CommandParser.parse("sort"));
        assertInstanceOf(HelpCommand.class, CommandParser.parse("HELP"));
        assertInstanceOf(TodayCommand.class, CommandParser.parse("today"));
        assertInstanceOf(ListCommand.class, CommandParser.parse("list"));
        assertInstanceOf(ListCommand.class, CommandParser.parse("list completed"));
        assertInstanceOf(ViewCommand.class, CommandParser.parse("view 1"));
        CommandHistory history = new CommandHistory();
        assertInstanceOf(UndoCommand.class, CommandParser.parse("undo", history));
        assertNull(CommandParser.parse("undo")); // Null when history is omitted
        Command exit = CommandParser.parse("exit");
        assertInstanceOf(ExitCommand.class, exit);
        assertTrue(exit.isExit());
    }

    @Test
    public void parse_menuNumbersAndUnknownWords_returnNull() throws Exception {
        assertNull(CommandParser.parse("1"));
        assertNull(CommandParser.parse("10"));
        assertNull(CommandParser.parse("foobar"));
        assertNull(CommandParser.parse("unknown"));
        assertNull(CommandParser.parse(""));
        assertNull(CommandParser.parse(null));
    }
}
