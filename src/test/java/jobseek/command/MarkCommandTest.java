package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MarkCommandTest {

    @Test
    public void execute_validIndex_marksAsCompleted() throws Exception {
        Application app = new Application("Google", "SWE Intern", false);
        ApplicationList applications = new ApplicationList();
        applications.add(app);

        MarkCommand command = new MarkCommand(0, true);
        command.execute(applications, new Ui(), new Storage());

        assertTrue(app.isCompleted());
    }

    @Test
    public void execute_validIndex_unmarksAsCompleted() throws Exception {
        Application app = new Application("Google", "SWE Intern", true);
        ApplicationList applications = new ApplicationList();
        applications.add(app);

        MarkCommand command = new MarkCommand(0, false);
        command.execute(applications, new Ui(), new Storage());

        assertFalse(app.isCompleted());
    }

    @Test
    public void execute_invalidIndex_doesNotThrow() throws Exception {
        Application app = new Application("Apple", "iOS Intern", false);
        ApplicationList applications = new ApplicationList();
        applications.add(app);

        MarkCommand command = new MarkCommand(5, true);
        command.execute(applications, new Ui(), new Storage());

        assertFalse(app.isCompleted());
    }

    @Test
    public void execute_emptyList_doesNotThrow() throws Exception {
        ApplicationList applications = new ApplicationList();
        MarkCommand command = new MarkCommand(0, true);
        command.execute(applications, new Ui(), new Storage());

        assertTrue(applications.isEmpty());
    }
}
