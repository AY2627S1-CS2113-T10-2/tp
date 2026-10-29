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

/** Unit tests for {@link RecruitmentStageCommand}. */
public class RecruitmentStageCommandTest {

    private final PrintStream standardOut = System.out;
    private ByteArrayOutputStream outputStream;

    /** Gives each command execution an isolated save path. */
    private Storage createTestStorage() {
        return new Storage(Path.of(System.getProperty("java.io.tmpdir"), "jobseek-stage-tests",
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
        RecruitmentStageCommand command = new RecruitmentStageCommand(1, RecruitmentStage.ACCEPTED);

        assertEquals(1, command.getTargetIndex());
        assertEquals(RecruitmentStage.ACCEPTED, command.getStage());
        assertFalse(command.isExit());
    }

    @Test
    public void execute_updatesRecruitmentStage() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        RecruitmentStageCommand command = new RecruitmentStageCommand(0, RecruitmentStage.ONLINE_ASSESSMENT);
        command.execute(applications, new Ui(), createTestStorage());

        assertEquals(RecruitmentStage.ONLINE_ASSESSMENT, applications.get(0).getRecruitmentStage());
        assertTrue(outputStream.toString().contains("Application recruitment stage updated successfully."));
        assertTrue(outputStream.toString().contains("Recruitment stage: Online Assessment"));
    }

    @Test
    public void execute_nullStage_showsError() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        new RecruitmentStageCommand(0, null).execute(applications, new Ui(), createTestStorage());

        assertEquals(RecruitmentStage.APPLIED, applications.get(0).getRecruitmentStage());
        assertTrue(outputStream.toString().contains("Error: Recruitment stage cannot be empty."));
    }

    @Test
    public void execute_emptyList_showsError() throws Exception {
        new RecruitmentStageCommand(0, RecruitmentStage.ACCEPTED)
                .execute(new ApplicationList(), new Ui(), createTestStorage());

        assertTrue(outputStream.toString().contains("Error: Your application list is currently empty."));
    }

    @Test
    public void execute_invalidIndex_leavesApplicationUnchanged() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE Intern"));

        new RecruitmentStageCommand(-1, RecruitmentStage.ACCEPTED)
                .execute(applications, new Ui(), createTestStorage());

        assertEquals(RecruitmentStage.APPLIED, applications.get(0).getRecruitmentStage());
        assertTrue(outputStream.toString().contains("Invalid application index: 0"));
    }

    @Test
    public void execute_preservesOtherApplicationFields() throws Exception {
        Application application = new Application("Google", "SWE Intern", true,
                RecruitmentStage.INTERVIEW);
        ApplicationList applications = new ApplicationList();
        applications.add(application);

        new RecruitmentStageCommand(0, RecruitmentStage.OFFER)
                .execute(applications, new Ui(), createTestStorage());

        assertEquals("Google", application.getCompany());
        assertEquals("SWE Intern", application.getRole());
        assertTrue(application.isCompleted());
        assertEquals(RecruitmentStage.OFFER, application.getRecruitmentStage());
    }

    @Test
    public void execute_multipleApplications_updatesOnlyTarget() throws Exception {
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE"));
        applications.add(new Application("Shopee", "Backend"));
        applications.add(new Application("Grab", "Data"));

        new RecruitmentStageCommand(1, RecruitmentStage.ACCEPTED)
                .execute(applications, new Ui(), createTestStorage());

        assertEquals(RecruitmentStage.APPLIED, applications.get(0).getRecruitmentStage());
        assertEquals(RecruitmentStage.ACCEPTED, applications.get(1).getRecruitmentStage());
        assertEquals(RecruitmentStage.APPLIED, applications.get(2).getRecruitmentStage());
    }
}
