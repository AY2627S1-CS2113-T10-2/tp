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
    }

    @Test
    public void setStatus_updatesStatusCorrectly() {
        Application application = new Application("Google", "SWE Intern");
        application.setStatus("Accepted");
        assertEquals("Accepted", application.getStatus());

        application.setStatus("Rejected");
        assertEquals("Rejected", application.getStatus());
    }

    @Test
    public void constructor_withStatus_initializesCorrectly() {
        Application application = new Application("Google", "SWE Intern", false, "Accepted");
        assertEquals("Accepted", application.getStatus());

        Application app2 = new Application("Shopee", "Backend", "Rejected");
        assertEquals("Rejected", app2.getStatus());
    }

    @Test
    public void constructor_fourParameters_setsStatusCorrectly() {
        Application application = new Application("Microsoft", "Security Intern", true, "Offer Accepted");
        assertEquals("Accepted", application.getStatus());
        assertTrue(application.isCompleted());
    }

    @Test
    public void constructor_allParameters_initializesCorrectly() {
        java.time.LocalDate deadline = java.time.LocalDate.of(2026, 12, 1);
        Application application = new Application("Google", "SWE", true, "Accepted",
                deadline, "https://careers.google.com", "recruiter@google.com", "Referred by Alice");
        assertEquals("Google", application.getCompany());
        assertEquals("SWE", application.getRole());
        assertTrue(application.isCompleted());
        assertEquals("Accepted", application.getStatus());
        assertEquals(deadline, application.getDeadline());
        assertEquals("https://careers.google.com", application.getPortalLink());
        assertEquals("recruiter@google.com", application.getEmail());
        assertEquals("Referred by Alice", application.getNotes());
    }

    @Test
    public void setPortalLink_validAndNull_updatesCorrectly() {
        Application application = new Application("Google", "SWE");
        assertEquals("", application.getPortalLink());

        application.setPortalLink("  https://jobs.apple.com  ");
        assertEquals("https://jobs.apple.com", application.getPortalLink());

        application.setPortalLink(null);
        assertEquals("", application.getPortalLink());
    }

    @Test
    public void setEmail_validAndNull_updatesCorrectly() {
        Application application = new Application("Google", "SWE");
        assertEquals("", application.getEmail());

        application.setEmail("  hr@shopee.com  ");
        assertEquals("hr@shopee.com", application.getEmail());

        application.setEmail(null);
        assertEquals("", application.getEmail());
    }

    @Test
    public void setNotes_validAndNull_updatesCorrectly() {
        Application application = new Application("Google", "SWE");
        assertEquals("", application.getNotes());

        application.setNotes("  Passed screening round.  ");
        assertEquals("Passed screening round.", application.getNotes());

        application.setNotes(null);
        assertEquals("", application.getNotes());
    }
}
