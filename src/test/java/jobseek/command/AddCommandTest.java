package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link AddCommand}.
 */
public class AddCommandTest {

    @Test
    public void getters_returnConstructedValues() {
        AddCommand command = new AddCommand("Google", "SWE Intern");
        assertEquals("Google", command.getCompany());
        assertEquals("SWE Intern", command.getRole());
        assertFalse(command.isExit());
    }

    @Test
    public void execute_validInputs_addsApplicationSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        AddCommand command = new AddCommand("Google", "Software Engineer Intern");
        command.execute(applications, new Ui(), new Storage());

        assertEquals(1, applications.size());
        Application added = applications.get(0);
        assertEquals("Google", added.getCompany());
        assertEquals("Software Engineer Intern", added.getRole());
    }

    @Test
    public void execute_whitespaceAroundInputs_trimmedSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        AddCommand command = new AddCommand("   Shopee   ", "   Backend Intern   ");
        command.execute(applications, new Ui(), new Storage());

        assertEquals(1, applications.size());
        assertEquals("Shopee", applications.get(0).getCompany());
        assertEquals("Backend Intern", applications.get(0).getRole());
    }

    @Test
    public void execute_multipleApplications_preservesInsertionOrder() throws Exception {
        ApplicationList applications = new ApplicationList();
        new AddCommand("Google", "SWE").execute(applications, new Ui(), new Storage());
        new AddCommand("Shopee", "Backend").execute(applications, new Ui(), new Storage());
        new AddCommand("Grab", "Data").execute(applications, new Ui(), new Storage());

        assertEquals(3, applications.size());
        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("Shopee", applications.get(1).getCompany());
        assertEquals("Grab", applications.get(2).getCompany());
    }

    @Test
    public void execute_emptyOrBlankCompany_doesNotAdd() throws Exception {
        ApplicationList applications = new ApplicationList();
        new AddCommand("", "SWE Intern").execute(applications, new Ui(), new Storage());
        new AddCommand("   ", "SWE Intern").execute(applications, new Ui(), new Storage());
        new AddCommand(null, "SWE Intern").execute(applications, new Ui(), new Storage());

        assertTrue(applications.isEmpty());
    }

    @Test
    public void execute_emptyOrBlankRole_doesNotAdd() throws Exception {
        ApplicationList applications = new ApplicationList();
        new AddCommand("Google", "").execute(applications, new Ui(), new Storage());
        new AddCommand("Google", "   ").execute(applications, new Ui(), new Storage());
        new AddCommand("Google", null).execute(applications, new Ui(), new Storage());

        assertTrue(applications.isEmpty());
    }
}
