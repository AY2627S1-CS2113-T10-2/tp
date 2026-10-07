package jobseek.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jobseek.model.JobApplication;

/**
 * Unit tests for {@link Storage}.
 */
public class StorageTest {

    private Path tempFile;
    private Storage storage;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = Files.createTempFile("jobseek_test", ".txt");
        storage = new Storage(tempFile.toString());
    }

    @AfterEach
    public void tearDown() throws IOException {
        Files.deleteIfExists(tempFile);
    }

    @Test
    public void loadApplications_emptyFile_returnsEmptyList() throws IOException {
        List<JobApplication> loaded = storage.loadApplications();
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void loadApplications_nonExistentFile_returnsEmptyList() throws IOException {
        Storage missingStorage = new Storage("non_existent_dir/missing_file.txt");
        List<JobApplication> loaded = missingStorage.loadApplications();
        assertTrue(loaded.isEmpty());
    }

    @Test
    public void saveAndLoadApplications_roundTrip_success() throws IOException {
        List<JobApplication> applications = List.of(
            new JobApplication("Google", "SWE Intern"),
            new JobApplication("Shopee", "Backend Intern")
        );

        storage.saveApplications(applications);
        List<JobApplication> loaded = storage.loadApplications();

        assertEquals(2, loaded.size());
        assertEquals("Google", loaded.get(0).getCompany());
        assertEquals("SWE Intern", loaded.get(0).getRole());
        assertEquals("Shopee", loaded.get(1).getCompany());
        assertEquals("Backend Intern", loaded.get(1).getRole());
    }

    @Test
    public void saveApplications_createsDirectoryIfMissing() throws IOException {
        Path tempDir = Files.createTempDirectory("jobseek_dir");
        File nestedFile = new File(tempDir.toFile(), "sub/dir/test.txt");
        Storage nestedStorage = new Storage(nestedFile.getAbsolutePath());

        nestedStorage.saveApplications(List.of(new JobApplication("Grab", "Product Intern")));
        assertTrue(nestedFile.exists());

        List<JobApplication> loaded = nestedStorage.loadApplications();
        assertEquals(1, loaded.size());
        assertEquals("Grab", loaded.get(0).getCompany());

        nestedFile.delete();
        nestedFile.getParentFile().delete();
        nestedFile.getParentFile().getParentFile().delete();
        Files.deleteIfExists(tempDir);
    }
}
