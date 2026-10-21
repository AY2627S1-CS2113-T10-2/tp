package jobseek.storage;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

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
        assertEquals("data/applications.txt", storage.getFilePath());
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
    public void load_missingFile_returnsEmptyApplicationList() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Storage storage = new Storage(temporaryFile.toString());
        ApplicationList list = storage.load();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    @Test
    public void saveThenLoad_preservesAllApplicationFields() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Storage storage = new Storage(temporaryFile.toString());
        ApplicationList list = new ApplicationList();
        list.add(new Application("Google\tAsia", "SWE Intern", true, LocalDate.of(2026, 11, 3)));
        storage.save(list);

        ApplicationList loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals("Google\tAsia", loaded.get(0).getCompany());
        assertEquals("SWE Intern", loaded.get(0).getRole());
        assertTrue(loaded.get(0).isCompleted());
        assertEquals(LocalDate.of(2026, 11, 3), loaded.get(0).getDeadline());
    }
}
