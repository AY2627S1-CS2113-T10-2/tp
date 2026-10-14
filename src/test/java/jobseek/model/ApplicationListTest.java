package jobseek.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link ApplicationList}.
 */
public class ApplicationListTest {

    private ApplicationList list;
    private Application app1;
    private Application app2;

    @BeforeEach
    public void setUp() {
        list = new ApplicationList();
        app1 = new Application("Google", "SWE Intern");
        app2 = new Application("Shopee", "Backend Intern");
    }

    @Test
    public void defaultConstructor_createsEmptyList() {
        assertTrue(list.isEmpty());
        assertEquals(0, list.size());
    }

    @Test
    public void parameterizedConstructor_initializesWithGivenList() {
        ArrayList<Application> initialList = new ArrayList<>();
        initialList.add(app1);
        initialList.add(app2);

        ApplicationList populatedList = new ApplicationList(initialList);
        assertEquals(2, populatedList.size());
        assertEquals(app1, populatedList.get(0));
        assertEquals(app2, populatedList.get(1));
    }

    @Test
    public void parameterizedConstructor_nullList_createsEmptyList() {
        ApplicationList populatedList = new ApplicationList(null);
        assertTrue(populatedList.isEmpty());
        assertEquals(0, populatedList.size());
    }

    @Test
    public void add_validApplication_increasesSize() {
        list.add(app1);
        assertEquals(1, list.size());
        assertFalse(list.isEmpty());
        assertEquals(app1, list.get(0));

        list.add(app2);
        assertEquals(2, list.size());
        assertEquals(app2, list.get(1));
    }

    @Test
    public void add_nullApplication_ignored() {
        list.add(null);
        assertEquals(0, list.size());
        assertTrue(list.isEmpty());
    }

    @Test
    public void delete_validIndex_removesAndReturnsApplication() {
        list.add(app1);
        list.add(app2);

        Application deleted = list.delete(0);
        assertEquals(app1, deleted);
        assertEquals(1, list.size());
        assertEquals(app2, list.get(0));
    }

    @Test
    public void delete_invalidIndex_throwsIndexOutOfBoundsException() {
        list.add(app1);

        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.delete(5));
    }

    @Test
    public void get_invalidIndex_throwsIndexOutOfBoundsException() {
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0));

        list.add(app1);
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1));
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1));
    }

    @Test
    public void isValidIndex_checksBoundsCorrectly() {
        assertFalse(list.isValidIndex(0));
        assertFalse(list.isValidIndex(-1));

        list.add(app1);
        list.add(app2);

        assertTrue(list.isValidIndex(0));
        assertTrue(list.isValidIndex(1));
        assertFalse(list.isValidIndex(-1));
        assertFalse(list.isValidIndex(2));
    }

    @Test
    public void getAll_returnsUnderlyingList() {
        list.add(app1);
        list.add(app2);

        ArrayList<Application> all = list.getAll();
        assertEquals(2, all.size());
        assertEquals(app1, all.get(0));
        assertEquals(app2, all.get(1));
    }
}
