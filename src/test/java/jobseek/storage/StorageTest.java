package jobseek.storage;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Storage}.
 */
public class StorageTest {

    @Test
    public void defaultConstructor_usesDefaultFilePath() {
        Storage storage = new Storage();
        assertEquals("data/applications.json", storage.getFilePath());
    }

    @Test
    public void customConstructor_setsFilePath() {
        Storage storage = new Storage("custom/path.json");
        assertEquals("custom/path.json", storage.getFilePath());
    }

    @Test
    public void setFilePath_updatesFilePath() {
        Storage storage = new Storage();
        storage.setFilePath("new/path.json");
        assertEquals("new/path.json", storage.getFilePath());
    }

    @Test
    public void load_returnsEmptyApplicationList() throws Exception {
        Storage storage = new Storage();
        ApplicationList list = storage.load();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void save_doesNotThrow() throws Exception {
        Storage storage = new Storage();
        ApplicationList list = new ApplicationList();
        list.add(new Application("Google", "SWE Intern"));
        storage.save(list);
    }
}
