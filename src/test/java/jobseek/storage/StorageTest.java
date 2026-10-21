package jobseek.storage;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    public void load_fileWithHeaders_loadsApplicationRecords() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "JOBSEEK Applications\ncompany\trole\tcompleted\tdeadline\n"
                        + "Example Company\tSoftware Engineer\ttrue\t2026-12-31\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Example Company", loaded.get(0).getCompany());
        assertEquals("Software Engineer", loaded.get(0).getRole());
        assertTrue(loaded.get(0).isCompleted());
        assertEquals(LocalDate.of(2026, 12, 31), loaded.get(0).getDeadline());
    }

    @Test
    public void save_emptyList_createsLoadableFile() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Storage storage = new Storage(temporaryFile.toString());

        storage.save(new ApplicationList());

        assertTrue(Files.exists(temporaryFile));
        assertTrue(storage.load().isEmpty());
    }

    @Test
    public void saveThenLoad_preservesOptionalDeadlineAndMultipleRecords() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Storage storage = new Storage(temporaryFile.toString());
        ApplicationList list = new ApplicationList();
        list.add(new Application("Example Company", "Software Engineer", false));
        list.add(new Application("Another Company", "Product Designer", true, LocalDate.of(2026, 12, 31)));

        storage.save(list);

        ApplicationList loaded = storage.load();
        assertEquals(2, loaded.size());
        assertEquals("Example Company", loaded.get(0).getCompany());
        assertEquals("Software Engineer", loaded.get(0).getRole());
        assertNull(loaded.get(0).getDeadline());
        assertEquals("Another Company", loaded.get(1).getCompany());
        assertEquals("Product Designer", loaded.get(1).getRole());
        assertTrue(loaded.get(1).isCompleted());
        assertEquals(LocalDate.of(2026, 12, 31), loaded.get(1).getDeadline());
    }

    @Test
    public void load_ignoresBlankLinesAndComments() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "# Saved application data\n\nExample Company\tSoftware Engineer\tfalse\t\n\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Example Company", loaded.get(0).getCompany());
        assertNull(loaded.get(0).getDeadline());
    }

    @Test
    public void load_recordWithInvalidFieldCount_throwsIOException() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile, "Example Company\tSoftware Engineer\tfalse\n");
        Storage storage = new Storage(temporaryFile.toString());

        IOException exception = assertThrows(IOException.class, storage::load);

        assertTrue(exception.getMessage().contains("Invalid record on line 1"));
    }

    @Test
    public void load_recordWithInvalidCompletionStatus_throwsIOException() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile, "Example Company\tSoftware Engineer\tpending\t\n");
        Storage storage = new Storage(temporaryFile.toString());

        IOException exception = assertThrows(IOException.class, storage::load);

        assertTrue(exception.getMessage().contains("Invalid record on line 1"));
    }

    @Test
    public void load_recordWithInvalidDeadline_throwsIOException() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile, "Example Company\tSoftware Engineer\tfalse\tnot-a-date\n");
        Storage storage = new Storage(temporaryFile.toString());

        IOException exception = assertThrows(IOException.class, storage::load);

        assertTrue(exception.getMessage().contains("Invalid deadline on line 1"));
    }
}
