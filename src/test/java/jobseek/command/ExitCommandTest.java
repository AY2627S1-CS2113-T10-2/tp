package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ExitCommand}.
 */
public class ExitCommandTest {

    @Test
    public void constructor_setsExitFlagToTrue() {
        ExitCommand command = new ExitCommand();
        assertTrue(command.isExit());
    }

    @Test
    public void execute_doesNotModifyApplicationsOrThrow() {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        ExitCommand command = new ExitCommand();

        command.execute(applications, new Ui(), new Storage());

        assertEquals(1, applications.size());
        assertTrue(command.isExit());
    }
}
