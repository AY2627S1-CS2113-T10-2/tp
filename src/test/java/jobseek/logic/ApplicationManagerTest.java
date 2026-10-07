package jobseek.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jobseek.model.JobApplication;

/**
 * Unit tests for {@link ApplicationManager}.
 */
public class ApplicationManagerTest {

    private ApplicationManager manager;

    @BeforeEach
    public void setUp() {
        manager = new ApplicationManager();
    }

    @Test
    public void addApplication_normalApplication_success() {
        JobApplication app = manager.addApplication("Google", "Software Engineer Intern");
        assertEquals("Google", app.getCompany());
        assertEquals("Software Engineer Intern", app.getRole());
        assertEquals(1, manager.getApplicationCount());
    }

    @Test
    public void addApplication_companyContainingSpaces_success() {
        JobApplication app = manager.addApplication("Meta Platforms Inc", "Software Engineer Intern");
        assertEquals("Meta Platforms Inc", app.getCompany());
        assertEquals(1, manager.getApplicationCount());
    }

    @Test
    public void addApplication_roleContainingSpaces_success() {
        JobApplication app = manager.addApplication("Grab", "Junior Product Operations Intern");
        assertEquals("Junior Product Operations Intern", app.getRole());
        assertEquals(1, manager.getApplicationCount());
    }

    @Test
    public void addApplication_leadingTrailingSpaces_trimmed() {
        JobApplication app = manager.addApplication("   Shopee   ", "   Backend Intern   ");
        assertEquals("Shopee", app.getCompany());
        assertEquals("Backend Intern", app.getRole());
    }

    @Test
    public void addApplication_emptyCompany_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> manager.addApplication("", "Engineer"));
        assertThrows(IllegalArgumentException.class, () -> manager.addApplication("   ", "Engineer"));
        assertThrows(IllegalArgumentException.class, () -> manager.addApplication(null, "Engineer"));
        assertEquals(0, manager.getApplicationCount());
    }

    @Test
    public void addApplication_emptyRole_throwsException() {
        assertThrows(IllegalArgumentException.class, () -> manager.addApplication("Google", ""));
        assertThrows(IllegalArgumentException.class, () -> manager.addApplication("Google", "   "));
        assertThrows(IllegalArgumentException.class, () -> manager.addApplication("Google", null));
        assertEquals(0, manager.getApplicationCount());
    }

    @Test
    public void addApplication_multipleApplications_retainsInsertionOrder() {
        manager.addApplication("Google", "SWE Intern");
        manager.addApplication("Shopee", "Backend Intern");
        manager.addApplication("Grab", "Product Intern");

        List<JobApplication> list = manager.getApplications();
        assertEquals(3, list.size());
        assertEquals("Google", list.get(0).getCompany());
        assertEquals("Shopee", list.get(1).getCompany());
        assertEquals("Grab", list.get(2).getCompany());
    }

    @Test
    public void getApplicationCount_returnsCorrectCount() {
        assertEquals(0, manager.getApplicationCount());
        manager.addApplication("Google", "SWE Intern");
        assertEquals(1, manager.getApplicationCount());
        manager.addApplication("Shopee", "Backend Intern");
        assertEquals(2, manager.getApplicationCount());
    }

    @Test
    public void deleteApplication_validIndex_removesApplication() {
        manager.addApplication("Google", "SWE Intern");
        manager.addApplication("Shopee", "Backend Intern");

        JobApplication deleted = manager.deleteApplication(1);
        assertEquals("Google", deleted.getCompany());
        assertEquals(1, manager.getApplicationCount());
        assertEquals("Shopee", manager.getApplications().get(0).getCompany());
    }

    @Test
    public void deleteApplication_invalidIndex_doesNotModifyList() {
        manager.addApplication("Google", "SWE Intern");

        assertThrows(IndexOutOfBoundsException.class, () -> manager.deleteApplication(0));
        assertThrows(IndexOutOfBoundsException.class, () -> manager.deleteApplication(2));
        assertThrows(IndexOutOfBoundsException.class, () -> manager.deleteApplication(-1));

        assertEquals(1, manager.getApplicationCount());
        assertEquals("Google", manager.getApplications().get(0).getCompany());
    }

    @Test
    public void emptyListBehavior_startsEmpty() {
        assertEquals(0, manager.getApplicationCount());
        assertEquals(0, manager.getApplications().size());
        assertThrows(IndexOutOfBoundsException.class, () -> manager.deleteApplication(1));
    }
}
