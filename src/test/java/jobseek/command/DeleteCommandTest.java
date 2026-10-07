package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        command.execute(applications, new Ui(), new Storage());

        assertTrue(applications.isEmpty());
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
}
