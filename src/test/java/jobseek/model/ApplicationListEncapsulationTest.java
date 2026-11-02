package jobseek.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that {@link ApplicationList} does not let outside code change its internal list.
 */
public class ApplicationListEncapsulationTest {

    private static ApplicationList listOfTwo() {
        ApplicationList list = new ApplicationList();
        list.add(new Application("Google", "SWE", false));
        list.add(new Application("Shopee", "Backend", false));
        return list;
    }

    @Test
    public void getAll_cannotBeUsedToAddRemoveOrClear() {
        ApplicationList list = listOfTwo();
        List<Application> view = list.getAll();

        assertThrows(UnsupportedOperationException.class, view::clear);
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Application("A", "B", false)));
        assertThrows(UnsupportedOperationException.class, () -> view.remove(0));
        assertEquals(2, list.size());
    }

    @Test
    public void getAll_isALiveViewOfLaterChanges() {
        ApplicationList list = listOfTwo();
        List<Application> view = list.getAll();

        list.add(new Application("Grab", "Data", false));

        assertEquals(3, view.size());
    }

    @Test
    public void constructor_copiesTheGivenCollection() {
        ArrayList<Application> source = new ArrayList<>();
        source.add(new Application("Google", "SWE", false));
        ApplicationList list = new ApplicationList(source);

        source.add(new Application("Shopee", "Backend", false));
        source.clear();

        assertEquals(1, list.size());
        assertEquals("Google", list.get(0).getCompany());
    }

    @Test
    public void replaceAll_restoresContentAndOrderWithoutKeepingTheGivenList() {
        ApplicationList list = listOfTwo();
        Application first = list.get(0);
        Application second = list.get(1);
        List<Application> saved = new ArrayList<>(list.getAll());
        list.delete(0);
        list.add(new Application("Grab", "Data", false));

        list.replaceAll(saved);
        saved.clear();

        assertEquals(2, list.size());
        assertSame(first, list.get(0));
        assertSame(second, list.get(1));
    }

    @Test
    public void replaceAll_withItsOwnView_keepsTheContent() {
        ApplicationList list = listOfTwo();

        list.replaceAll(list.getAll());

        assertTrue(list.size() == 2 && "Google".equals(list.get(0).getCompany()));
    }
}
