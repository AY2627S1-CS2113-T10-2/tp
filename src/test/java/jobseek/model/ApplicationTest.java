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
}
