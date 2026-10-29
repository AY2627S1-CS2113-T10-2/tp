package jobseek.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Application}.
 */
public class ApplicationTest {

    @Test
    public void constructor_twoParameters_defaultsToIncomplete() {
        Application application = new Application("Google", "Software Engineer Intern");
        assertEquals("Google", application.getCompany());
        assertEquals("Software Engineer Intern", application.getRole());
        assertFalse(application.isCompleted());
        assertEquals("[ ]", application.getStatusIcon());
    }

    @Test
    public void constructor_threeParameters_setsCompletedStatus() {
        Application completedApp = new Application("Shopee", "Backend Intern", true);
        assertEquals("Shopee", completedApp.getCompany());
        assertEquals("Backend Intern", completedApp.getRole());
        assertTrue(completedApp.isCompleted());
        assertEquals("[X]", completedApp.getStatusIcon());
    }

    @Test
    public void setCompany_updatesCompany() {
        Application application = new Application("Google", "SWE Intern");
        application.setCompany("Alphabet");
        assertEquals("Alphabet", application.getCompany());
    }

    @Test
    public void setRole_updatesRole() {
        Application application = new Application("Google", "SWE Intern");
        application.setRole("Full Stack Intern");
        assertEquals("Full Stack Intern", application.getRole());
    }

    @Test
    public void setCompleted_togglesStatus() {
        Application application = new Application("Grab", "Data Analyst Intern");
        assertFalse(application.isCompleted());

        application.setCompleted(true);
        assertTrue(application.isCompleted());
        assertEquals("[X]", application.getStatusIcon());

        application.setCompleted(false);
        assertFalse(application.isCompleted());
        assertEquals("[ ]", application.getStatusIcon());
    }

    @Test
    public void toString_formatsCorrectly() {
        Application pendingApp = new Application("Meta", "Frontend Intern", false);
        assertEquals("[ ] Meta | Frontend Intern", pendingApp.toString());

        Application doneApp = new Application("Apple", "iOS Intern", true);
        assertEquals("[X] Apple | iOS Intern", doneApp.toString());
    }

    @Test
    public void getStatus_defaultsToApplied() {
        Application application = new Application("Google", "SWE Intern");
        assertEquals("Applied", application.getStatus());
        assertEquals(RecruitmentStage.APPLIED, application.getRecruitmentStage());
    }

    @Test
    public void constructor_legacyPendingLabel_mapsToApplied() {
        Application application = new Application("Google", "SWE Intern", "Pending");

        assertEquals("Applied", application.getStatus());
        assertEquals(RecruitmentStage.APPLIED, application.getRecruitmentStage());
    }

    @Test
    public void setStatus_updatesStatusCorrectly() {
        Application application = new Application("Google", "SWE Intern");
        application.setStatus("Accepted");
        assertEquals("Accepted", application.getStatus());

        application.setStatus("Rejected");
        assertEquals("Rejected", application.getStatus());

        application.setStatus("Online Assessment");
        assertEquals(RecruitmentStage.ONLINE_ASSESSMENT, application.getRecruitmentStage());
    }

    @Test
    public void constructor_withStatus_initializesCorrectly() {
        Application application = new Application("Google", "SWE Intern", false, "Accepted");
        assertEquals("Accepted", application.getStatus());

        Application app2 = new Application("Shopee", "Backend", "Rejected");
        assertEquals("Rejected", app2.getStatus());
    }

    @Test
    public void constructor_legacyOfferAcceptedLabel_mapsToAcceptedStage() {
        Application application = new Application("Microsoft", "Security Intern", true, "Offer Accepted");
        assertEquals("Accepted", application.getStatus());
        assertEquals(RecruitmentStage.ACCEPTED, application.getRecruitmentStage());
        assertTrue(application.isCompleted());
    }
}
