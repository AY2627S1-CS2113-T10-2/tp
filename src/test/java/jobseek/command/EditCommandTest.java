package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link EditCommand}.
 */
public class EditCommandTest {

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
    public void getters_returnConstructedValues() {
        EditCommand command = new EditCommand(2, "Google", "SWE Intern");
        assertEquals(2, command.getTargetIndex());
        assertEquals("Google", command.getNewCompany());
        assertEquals("SWE Intern", command.getNewRole());
        assertFalse(command.isExit());
    }

    @Test
    public void getters_nullOrBlankFields_storedAsNull() {
        EditCommand command1 = new EditCommand(0, null, null);
        assertNull(command1.getNewCompany());
        assertNull(command1.getNewRole());

        EditCommand command2 = new EditCommand(0, "   ", "");
        assertNull(command2.getNewCompany());
        assertNull(command2.getNewRole());
    }

    @Test
    public void execute_updateCompanyOnly_success() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Googl", "SWE Intern", false));

        EditCommand command = new EditCommand(0, "Google", null);
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("SWE Intern", applications.get(0).getRole());
        assertFalse(applications.get(0).isCompleted());

        String output = outputStream.toString();
        assertTrue(output.contains("Application updated successfully."));
        assertTrue(output.contains("Company: Google"));
        assertTrue(output.contains("Role: SWE Intern"));
    }

    @Test
    public void execute_updateRoleOnly_success() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backnd Intern", false));

        EditCommand command = new EditCommand(0, null, "Backend Intern");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Shopee", applications.get(0).getCompany());
        assertEquals("Backend Intern", applications.get(0).getRole());

        String output = outputStream.toString();
        assertTrue(output.contains("Application updated successfully."));
        assertTrue(output.contains("Company: Shopee"));
        assertTrue(output.contains("Role: Backend Intern"));
    }

    @Test
    public void execute_updateBothFields_success() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("OldCorp", "OldRole", true));

        EditCommand command = new EditCommand(0, "NewCorp", "NewRole");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("NewCorp", applications.get(0).getCompany());
        assertEquals("NewRole", applications.get(0).getRole());
        assertTrue(applications.get(0).isCompleted());

        String output = outputStream.toString();
        assertTrue(output.contains("Application updated successfully."));
        assertTrue(output.contains("Company: NewCorp"));
        assertTrue(output.contains("Role: NewRole"));
    }

    @Test
    public void execute_whitespaceInputs_trimmedSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false));

        EditCommand command = new EditCommand(0, "   Alphabet   ", "   Senior SWE   ");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Alphabet", applications.get(0).getCompany());
        assertEquals("Senior SWE", applications.get(0).getRole());
    }

    @Test
    public void execute_emptyList_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        EditCommand command = new EditCommand(0, "Google", "SWE");
        command.execute(applications, new Ui(), new Storage());

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
    public void execute_negativeIndex_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        EditCommand command = new EditCommand(-1, "Google", "SWE Lead");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("SWE Intern", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index: 0. Please enter a number between 1 and 1."));
    }

    @Test
    public void execute_outOfBoundsIndex_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        EditCommand command = new EditCommand(5, "Meta", "SWE");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Google", applications.get(0).getCompany());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index: 6. Please enter a number between 1 and 1."));
    }

    @Test
    public void execute_bothNullFields_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        EditCommand command = new EditCommand(0, null, null);
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("SWE Intern", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: At least one field (company, role, or recruitment stage) "
                + "must be specified for editing."));
    }

    @Test
    public void execute_bothBlankFields_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        EditCommand command = new EditCommand(0, "   ", "");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("SWE Intern", applications.get(0).getRole());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: At least one field (company, role, or recruitment stage) "
                + "must be specified for editing."));
    }

    @Test
    public void execute_updateRecruitmentStageOnly_success() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backend Intern", false, "Pending"));

        EditCommand command = new EditCommand(0, null, null, RecruitmentStage.ACCEPTED);
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Shopee", applications.get(0).getCompany());
        assertEquals("Backend Intern", applications.get(0).getRole());
        assertEquals(RecruitmentStage.ACCEPTED, applications.get(0).getRecruitmentStage());

        String output = outputStream.toString();
        assertTrue(output.contains("Application updated successfully."));
        assertTrue(output.contains("Recruitment stage: Accepted"));
    }

    @Test
    public void execute_updateAllThreeFields_success() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("OldCorp", "OldRole", false, "Pending"));

        EditCommand command = new EditCommand(0, "NewCorp", "NewRole", RecruitmentStage.REJECTED);
        command.execute(applications, new Ui(), new Storage());

        assertEquals("NewCorp", applications.get(0).getCompany());
        assertEquals("NewRole", applications.get(0).getRole());
        assertEquals(RecruitmentStage.REJECTED, applications.get(0).getRecruitmentStage());

        String output = outputStream.toString();
        assertTrue(output.contains("Application updated successfully."));
        assertTrue(output.contains("Company: NewCorp"));
        assertTrue(output.contains("Role: NewRole"));
        assertTrue(output.contains("Recruitment stage: Rejected"));
    }

    @Test
    public void execute_preservesCompletionStatus_whenMarked() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", true));

        EditCommand command = new EditCommand(0, "Alphabet", null);
        command.execute(applications, new Ui(), new Storage());

        assertTrue(applications.get(0).isCompleted());
        assertEquals("[X] Alphabet | SWE Intern", applications.get(0).toString());
    }

    @Test
    public void execute_multipleApplications_updatesOnlyTarget() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        applications.add(new Application("Shopee", "Backend Intern"));
        applications.add(new Application("Grab", "Data Analyst Intern"));

        EditCommand command = new EditCommand(1, "Shopee Singapore", "Fullstack Intern");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("SWE Intern", applications.get(0).getRole());

        assertEquals("Shopee Singapore", applications.get(1).getCompany());
        assertEquals("Fullstack Intern", applications.get(1).getRole());

        assertEquals("Grab", applications.get(2).getCompany());
        assertEquals("Data Analyst Intern", applications.get(2).getRole());
    }

    @Test
    public void execute_updatePortalLink_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        EditCommand command = EditCommand.editPortalLink(0, "https://careers.google.com");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("https://careers.google.com", applications.get(0).getPortalLink());
    }

    @Test
    public void execute_updateEmail_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        EditCommand command = EditCommand.editEmail(0, "recruiter@google.com");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("recruiter@google.com", applications.get(0).getEmail());
    }

    @Test
    public void execute_updateNotes_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        EditCommand command = EditCommand.editNotes(0, "Round 1 interview scheduled");
        command.execute(applications, new Ui(), new Storage());

        assertEquals("Round 1 interview scheduled", applications.get(0).getNotes());
    }

    @Test
    public void execute_updateDeadline_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));
        java.time.LocalDate deadline = java.time.LocalDate.of(2026, 11, 20);

        EditCommand command = EditCommand.editDeadline(0, deadline);
        command.execute(applications, new Ui(), new Storage());

        assertEquals(deadline, applications.get(0).getDeadline());
    }

    @Test
    public void execute_editAllFields_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("OldCo", "OldRole"));
        java.time.LocalDate deadline = java.time.LocalDate.of(2026, 12, 10);

        EditCommand command = EditCommand.editAll(0, "NewCo", "NewRole", "Accepted",
                deadline, "https://newco.com/careers", "hiring@newco.com", "Completed screening");
        command.execute(applications, new Ui(), new Storage());

        Application app = applications.get(0);
        assertEquals("NewCo", app.getCompany());
        assertEquals("NewRole", app.getRole());
        assertEquals("Accepted", app.getStatus());
        assertEquals(deadline, app.getDeadline());
        assertEquals("https://newco.com/careers", app.getPortalLink());
        assertEquals("hiring@newco.com", app.getEmail());
        assertEquals("Completed screening", app.getNotes());
    }

    @Test
    public void getters_optionalFields_returnConstructedValues() {
        java.time.LocalDate deadline = java.time.LocalDate.of(2026, 12, 1);
        EditCommand command = new EditCommand(3, "Meta", "SWE", "Applied",
                deadline, "https://meta.com/jobs", "meta-hr@meta.com", "Follow up next week");

        assertEquals(3, command.getTargetIndex());
        assertEquals("Meta", command.getNewCompany());
        assertEquals("SWE", command.getNewRole());
        assertEquals("Applied", command.getNewStatus());
        assertEquals(deadline, command.getNewDeadline());
        assertEquals("https://meta.com/jobs", command.getNewPortalLink());
        assertEquals("meta-hr@meta.com", command.getNewEmail());
        assertEquals("Follow up next week", command.getNewNotes());
    }

    @Test
    public void execute_clearOptionalFields_clearsValuesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        java.time.LocalDate deadline = java.time.LocalDate.of(2026, 11, 15);
        Application app = new Application("Google", "SWE", false, "Pending",
                deadline, "https://careers.google.com", "recruiter@google.com", "OA passed");
        applications.add(app);

        EditCommand.editPortalLink(0, "").execute(applications, new Ui(), new Storage());
        assertEquals("", applications.get(0).getPortalLink());

        EditCommand.editEmail(0, "").execute(applications, new Ui(), new Storage());
        assertEquals("", applications.get(0).getEmail());

        EditCommand.editNotes(0, "").execute(applications, new Ui(), new Storage());
        assertEquals("", applications.get(0).getNotes());

        EditCommand.editDeadline(0, null).execute(applications, new Ui(), new Storage());
        assertEquals(null, applications.get(0).getDeadline());
    }
}
