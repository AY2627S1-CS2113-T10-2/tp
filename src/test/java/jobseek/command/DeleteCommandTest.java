package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests deleting applications from the application list.
 */
public class DeleteCommandTest {

    @Test
    public void execute_validIndex_deletesApplication() throws Exception {
        Application application = new Application("Google", "SWE Intern", false);
        ApplicationList applications = new ApplicationList();
        applications.add(application);

        DeleteCommand command = new DeleteCommand(0);
        command.execute(applications, uiAnswering("y"), tempStorage());

        assertTrue(applications.isEmpty());
    }

    @Test
    public void execute_confirmedWithYes_deletesApplication() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false));

        new DeleteCommand(0).execute(applications, uiAnswering("  YES "), tempStorage());

        assertTrue(applications.isEmpty());
    }

    @Test
    public void execute_answerIsNo_keepsApplication() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false));
        DeleteCommand command = new DeleteCommand(0);

        command.execute(applications, uiAnswering("n"), tempStorage());

        assertEquals(1, applications.size());
        assertFalse(command.hasChanged());
    }

    @Test
    public void execute_noAnswerAtEndOfInput_keepsApplication() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false));

        new DeleteCommand(0).execute(applications, uiAnswering(""), tempStorage());

        assertEquals(1, applications.size());
    }

    @Test
    public void undo_afterDelete_restoresApplicationAtOriginalPosition() throws Exception {
        ApplicationList applications = new ApplicationList();
        Application first = new Application("Apple", "iOS", false);
        Application second = new Application("Google", "SWE", false);
        Application third = new Application("Shopee", "Backend", false);
        applications.add(first);
        applications.add(second);
        applications.add(third);
        DeleteCommand command = new DeleteCommand(1);
        command.execute(applications, uiAnswering("y"), tempStorage());

        command.undo(applications);

        assertEquals(3, applications.size());
        assertSame(second, applications.get(1));
        assertSame(third, applications.get(2));
    }

    @Test
    public void execute_invalidIndex_doesNotDeleteApplication() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Apple", "iOS Intern", false));

        DeleteCommand command = new DeleteCommand(5);
        command.execute(applications, new Ui(), new Storage());

        assertEquals(1, applications.size());
    }

    @Test
    public void execute_emptyList_doesNotThrow() throws Exception {
        ApplicationList applications = new ApplicationList();

        DeleteCommand command = new DeleteCommand(0);
        command.execute(applications, new Ui(), new Storage());

        assertTrue(applications.isEmpty());
    }

    private static Ui uiAnswering(String answer) {
        return new Ui(new Scanner(answer));
    }

    /** Uses a temporary file so tests never touch the real data/applications.txt. */
    private static Storage tempStorage() throws Exception {
        return new Storage(Files.createTempDirectory("jobseek-delete-test").resolve("applications.txt").toString());
    }
}
