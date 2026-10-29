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
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link StatusCommand}.
 */
public class StatusCommandTest {

    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream;

    /** Gives each command execution an isolated save path. */
    private Storage createTestStorage() {
        return new Storage(Path.of(System.getProperty("java.io.tmpdir"), "jobseek-status-tests",
                UUID.randomUUID().toString(), "applications.txt").toString());
    }

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
        StatusCommand command = new StatusCommand(1, "Accepted");
        assertEquals(1, command.getTargetIndex());
        assertEquals("Accepted", command.getStatus());
        assertFalse(command.isExit());
    }

    @Test
    public void execute_appliedStage_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, "Accepted"));

        StatusCommand command = new StatusCommand(0, "Applied");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Applied", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Application status updated successfully."));
        assertTrue(output.contains("Status: Applied"));
    }

    @Test
    public void execute_acceptedStatus_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, "Applied"));

        StatusCommand command = new StatusCommand(0, "Accepted");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Accepted", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Application status updated successfully."));
        assertTrue(output.contains("Status: Accepted"));
    }

    @Test
    public void execute_onlineAssessmentStage_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        StatusCommand command = new StatusCommand(0, "Online Assessment");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals(RecruitmentStage.ONLINE_ASSESSMENT, applications.get(0).getRecruitmentStage());
        assertTrue(outputStream.toString().contains("Status: Online Assessment"));
    }

    @Test
    public void execute_legacyPendingLabel_updatesToAppliedStage() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        new StatusCommand(0, "Pending").execute(applications, new Ui(), createTestStorage());

        assertEquals(RecruitmentStage.APPLIED, applications.get(0).getRecruitmentStage());
    }

    @Test
    public void execute_rejectedStatus_updatesSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Shopee", "Backend Intern", false, "Applied"));

        StatusCommand command = new StatusCommand(0, "Rejected");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Rejected", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Application status updated successfully."));
        assertTrue(output.contains("Status: Rejected"));
    }

    @Test
    public void execute_whitespaceStatus_trimmedSuccessfully() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Grab", "Data Analyst", false, "Applied"));

        StatusCommand command = new StatusCommand(0, "   Accepted   ");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Accepted", applications.get(0).getStatus());
    }

    @Test
    public void execute_emptyList_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        StatusCommand command = new StatusCommand(0, "Accepted");
        command.execute(applications, new Ui(), createTestStorage());

        String output = outputStream.toString();
        assertTrue(output.contains("Error: Your application list is currently empty."));
    }

    @Test
    public void execute_negativeIndex_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        StatusCommand command = new StatusCommand(-1, "Accepted");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Applied", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index: 0. Please enter a number between 1 and 1."));
    }

    @Test
    public void execute_outOfBoundsIndex_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        StatusCommand command = new StatusCommand(5, "Accepted");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Applied", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Invalid application index: 6. Please enter a number between 1 and 1."));
    }

    @Test
    public void execute_emptyStatus_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, "Applied"));

        StatusCommand command = new StatusCommand(0, "");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Applied", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Status cannot be empty."));
    }

    @Test
    public void execute_nullStatus_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern", false, "Applied"));

        StatusCommand command = new StatusCommand(0, null);
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Applied", applications.get(0).getStatus());
        String output = outputStream.toString();
        assertTrue(output.contains("Error: Status cannot be empty."));
    }

    @Test
    public void execute_preservesOtherFields_unchanged() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Meta", "Production Engineer", true, "Applied"));

        StatusCommand command = new StatusCommand(0, "Accepted");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Meta", applications.get(0).getCompany());
        assertEquals("Production Engineer", applications.get(0).getRole());
        assertTrue(applications.get(0).isCompleted());
        assertEquals("Accepted", applications.get(0).getStatus());
    }

    @Test
    public void execute_multipleApplications_updatesOnlyTarget() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false, "Applied"));
        applications.add(new Application("Shopee", "Backend", false, "Applied"));
        applications.add(new Application("Grab", "Data", false, "Applied"));

        StatusCommand command = new StatusCommand(1, "Accepted");
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals("Applied", applications.get(0).getStatus());
        assertEquals("Accepted", applications.get(1).getStatus());
        assertEquals("Applied", applications.get(2).getStatus());
    }
}
