package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link JobApplication}.
 */
public class JobApplicationTest {

    @Test
    public void constructorAndGetters_validInputs_success() {
        JobApplication app = new JobApplication("Google", "Software Engineer Intern");
        assertEquals("Google", app.getCompany());
        assertEquals("Software Engineer Intern", app.getRole());
    }
}
