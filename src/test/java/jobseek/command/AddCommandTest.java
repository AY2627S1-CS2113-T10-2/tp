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

    @Test
    public void getters_allFields_returnConstructedValues() {
        java.time.LocalDate deadline = java.time.LocalDate.of(2026, 12, 1);
        AddCommand command = new AddCommand("Google", "SWE", deadline,
                "https://portal.google.com", "jobs@google.com", "Referral link included");
        assertEquals("Google", command.getCompany());
        assertEquals("SWE", command.getRole());
        assertEquals(deadline, command.getDeadline());
        assertEquals("https://portal.google.com", command.getPortalLink());
        assertEquals("jobs@google.com", command.getEmail());
        assertEquals("Referral link included", command.getNotes());
    }

    @Test
    public void execute_withOptionalFields_addsSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        java.time.LocalDate deadline = java.time.LocalDate.of(2026, 12, 25);
        AddCommand command = new AddCommand("ByteDance", "Backend Intern", deadline,
                "https://jobs.bytedance.com", "hr@bytedance.com", "OA deadline soon");

        command.execute(applications, new Ui(), new Storage());

        assertEquals(1, applications.size());
        Application added = applications.get(0);
        assertEquals("ByteDance", added.getCompany());
        assertEquals("https://jobs.bytedance.com", added.getPortalLink());
        assertEquals("hr@bytedance.com", added.getEmail());
        assertEquals("OA deadline soon", added.getNotes());
    }

    @Test
    public void execute_omittedOptionalFields_defaultsToEmptyStrings() throws Exception {
        ApplicationList applications = new ApplicationList();
        AddCommand command = new AddCommand("GovTech", "Software Engineer", null, null, null, null);

        command.execute(applications, new Ui(), new Storage());

        assertEquals(1, applications.size());
        Application added = applications.get(0);
        assertEquals("GovTech", added.getCompany());
        assertEquals("Software Engineer", added.getRole());
        assertEquals("", added.getPortalLink());
        assertEquals("", added.getEmail());
        assertEquals("", added.getNotes());
        assertEquals(null, added.getDeadline());
    }

    @Test
    public void constructor_nullAndBlankOptionalFields_normalizedToEmpty() {
        AddCommand command = new AddCommand("Amazon", "SDE", null, "   ", null, "   ");
        assertEquals("", command.getPortalLink());
        assertEquals("", command.getEmail());
        assertEquals("", command.getNotes());
        assertEquals(null, command.getDeadline());
    }
}
