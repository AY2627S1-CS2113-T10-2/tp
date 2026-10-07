package jobseek.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JobApplication}.
 */
public class JobApplicationTest {

    @Test
    public void constructor_validInputs_initializesFields() {
        JobApplication application = new JobApplication("Google", "Software Engineer Intern");
        assertEquals("Google", application.getCompany());
        assertEquals("Software Engineer Intern", application.getRole());
    }

    @Test
    public void getCompany_returnsCompany() {
        JobApplication application = new JobApplication("Shopee", "Backend Intern");
        assertEquals("Shopee", application.getCompany());
    }

    @Test
    public void getRole_returnsRole() {
        JobApplication application = new JobApplication("Shopee", "Backend Intern");
        assertEquals("Backend Intern", application.getRole());
    }
}
