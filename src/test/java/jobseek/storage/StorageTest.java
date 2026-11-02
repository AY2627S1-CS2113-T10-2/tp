package jobseek.storage;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link Storage}.
 */
public class StorageTest {

    /** Fatal decoding errors must not allow a subsequent save to overwrite the source bytes. */
    @Test
    public void load_invalidUtf8_blocksRepeatedSavesUntilSuccessfulReload() throws Exception {
        Path file = Files.createTempDirectory("jobseek-load-failure").resolve("applications.txt");
        byte[] original = {(byte) 0xFF, (byte) 0xFE};
        Files.write(file, original);
        Storage storage = new Storage(file.toString());

        assertThrows(IOException.class, storage::load);
        storage.clearLoadWarnings();
        assertThrows(IOException.class, () -> storage.save(new ApplicationList()));
        assertThrows(IOException.class, () -> storage.save(new ApplicationList()));
        assertArrayEquals(original, Files.readAllBytes(file));

        Files.writeString(file, "Acme\tIntern\tfalse\t\n");
        ApplicationList recovered = storage.load();
        storage.save(recovered);
        assertEquals("Acme", storage.load().get(0).getCompany());
    }

    /** A missing file is a normal first launch and must remain writable. */
    @Test
    public void load_missingFile_allowsFirstSave() throws Exception {
        Path file = Files.createTempDirectory("jobseek-first-save").resolve("applications.txt");
        Storage storage = new Storage(file.toString());
        ApplicationList applications = storage.load();
        applications.add(new Application("Acme", "Intern"));

        storage.save(applications);

        assertEquals("Acme", storage.load().get(0).getCompany());
    }

    /** A leading hash in a valid company name must survive saving and restarting. */
    @Test
    public void saveThenLoad_companyStartsWithHash_preservesRecord() throws Exception {
        Path file = Files.createTempDirectory("jobseek-hash-test").resolve("applications.txt");
        Storage storage = new Storage(file.toString());
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("#Acme", "Intern"));
        applications.add(new Application("#", "Developer"));
        applications.add(new Application("Other Company", "Analyst"));

        storage.save(applications);
        Storage restartedStorage = new Storage(file.toString());
        ApplicationList loaded = restartedStorage.load();

        assertEquals(3, loaded.size());
        assertEquals("#Acme", loaded.get(0).getCompany());
        assertEquals("Intern", loaded.get(0).getRole());
        assertEquals("#", loaded.get(1).getCompany());
        assertEquals("Other Company", loaded.get(2).getCompany());
        assertTrue(restartedStorage.getLoadWarnings().isEmpty());
    }

    /** Existing legacy records with hash-prefixed names remain readable alongside comments. */
    @Test
    public void load_hashCompanyInLegacyRecords_ignoresOnlyCommentLines() throws Exception {
        Path file = Files.createTempDirectory("jobseek-hash-test").resolve("applications.txt");
        Files.writeString(file, "# Saved application data\n"
                + "#Acme\tIntern\tfalse\t\n"
                + "#OtherCo\tDeveloper\ttrue\t2026-11-03\tAccepted\n");
        Storage storage = new Storage(file.toString());

        ApplicationList loaded = storage.load();

        assertEquals(2, loaded.size());
        assertEquals("#Acme", loaded.get(0).getCompany());
        assertNull(loaded.get(0).getDeadline());
        assertEquals("#OtherCo", loaded.get(1).getCompany());
        assertTrue(loaded.get(1).isCompleted());
        assertEquals(RecruitmentStage.ACCEPTED, loaded.get(1).getRecruitmentStage());
        assertTrue(storage.getLoadWarnings().isEmpty());
    }

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
    public void saveThenLoad_preservesRecruitmentStages() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Storage storage = new Storage(temporaryFile.toString());
        ApplicationList list = new ApplicationList();
        list.add(new Application("Google", "SWE", false, "Accepted", null));
        list.add(new Application("Shopee", "Backend", true, "Online Assessment", LocalDate.of(2026, 11, 3)));

        storage.save(list);

        ApplicationList loaded = storage.load();
        assertEquals(RecruitmentStage.ACCEPTED, loaded.get(0).getRecruitmentStage());
        assertEquals(RecruitmentStage.ONLINE_ASSESSMENT, loaded.get(1).getRecruitmentStage());
        assertEquals(LocalDate.of(2026, 11, 3), loaded.get(1).getDeadline());
    }

    @Test
    public void load_legacyFourFieldFile_defaultsStageToApplied() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "JOBSEEK Applications\ncompany\trole\tcompleted\tdeadline\n"
                        + "Example Company\tSoftware Engineer\tfalse\t2026-12-31\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(RecruitmentStage.APPLIED, loaded.get(0).getRecruitmentStage());
        assertEquals(LocalDate.of(2026, 12, 31), loaded.get(0).getDeadline());
    }

    @Test
    public void load_legacyStatusColumnHeader_readsRecruitmentStage() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "JOBSEEK Applications\ncompany\trole\tcompleted\tdeadline\tstatus\n"
                        + "Example Company\tSoftware Engineer\tfalse\t\tInterview\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(RecruitmentStage.INTERVIEW, loaded.get(0).getRecruitmentStage());
    }

    @Test
    public void load_blankStageField_defaultsStageToApplied() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile, "Example Company\tSoftware Engineer\tfalse\t\t\n");
        Storage storage = new Storage(temporaryFile.toString());

        assertEquals(RecruitmentStage.APPLIED, storage.load().get(0).getRecruitmentStage());
    }

    @Test
    public void load_legacyPendingStage_migratesToAppliedStage() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile, "Example Company\tSoftware Engineer\tfalse\t\tPending\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals(RecruitmentStage.APPLIED, loaded.get(0).getRecruitmentStage());
    }

    @Test
    public void load_unknownRecruitmentStage_skipsRecordAndReportsWarning() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "Unknown Stage Co\tEngineer\tfalse\t\tScreening\n"
                        + "Valid Company\tEngineer\tfalse\t\tInterview\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Valid Company", loaded.get(0).getCompany());
        assertEquals(RecruitmentStage.INTERVIEW, loaded.get(0).getRecruitmentStage());
        assertTrue(storage.getLoadWarnings().get(0).contains("unknown recruitment stage on line 1"));
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
    public void load_recordWithInvalidFieldCount_skipsCorruptedLineAndLoadsValidRecords() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "Bad Company\tInvalid Only Three\tfalse\n"
                        + "Valid Company\tSoftware Engineer\tfalse\t2026-12-31\tPending\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Valid Company", loaded.get(0).getCompany());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("line 1"));
    }

    @Test
    public void load_recordWithInvalidCompletionStatus_skipsCorruptedLine() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "Bad Status\tSoftware Engineer\tnot-a-boolean\t\tPending\n"
                        + "Valid Company\tSWE\ttrue\t\tAccepted\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Valid Company", loaded.get(0).getCompany());
        assertTrue(loaded.get(0).isCompleted());
        assertEquals(1, storage.getLoadWarnings().size());
    }

    @Test
    public void load_recordWithInvalidDeadline_skipsCorruptedLine() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "Bad Date\tSoftware Engineer\tfalse\tnot-a-date\tPending\n"
                        + "Valid Company\tSoftware Engineer\tfalse\t2026-12-31\tPending\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Valid Company", loaded.get(0).getCompany());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("invalid deadline on line 1"));
    }

    @Test
    public void load_recordWithEmptyCompanyOrRole_skipsCorruptedLine() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "\tSoftware Engineer\tfalse\t\tPending\n"
                        + "Valid Company\t\tfalse\t\tPending\n"
                        + "Google\tSWE Intern\tfalse\t\tPending\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Google", loaded.get(0).getCompany());
        assertEquals(2, storage.getLoadWarnings().size());
    }

    @Test
    public void load_corruptedEscapeSequence_skipsCorruptedLine() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "Company\\z\tSoftware Engineer\tfalse\t\tPending\n"
                        + "Google\tSWE Intern\tfalse\t\tPending\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Google", loaded.get(0).getCompany());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.getLoadWarnings().get(0).contains("Invalid escape sequence"));
    }

    @Test
    public void saveThenLoad_allEightFields_preservesAllData() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Storage storage = new Storage(temporaryFile.toString());
        ApplicationList list = new ApplicationList();
        LocalDate deadline = LocalDate.of(2026, 11, 28);
        list.add(new Application("Apple", "iOS SWE", false, "Accepted",
                deadline, "https://apple.com/careers", "hiring@apple.com", "Completed all rounds"));
        storage.save(list);

        ApplicationList loaded = storage.load();
        assertEquals(1, loaded.size());
        Application app = loaded.get(0);
        assertEquals("Apple", app.getCompany());
        assertEquals("iOS SWE", app.getRole());
        assertFalse(app.isCompleted());
        assertEquals("Accepted", app.getStatus());
        assertEquals(deadline, app.getDeadline());
        assertEquals("https://apple.com/careers", app.getPortalLink());
        assertEquals("hiring@apple.com", app.getEmail());
        assertEquals("Completed all rounds", app.getNotes());
    }

    @Test
    public void load_legacyFourFieldFormat_loadsWithDefaults() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Files.writeString(temporaryFile,
                "JOBSEEK Applications\ncompany\trole\tcompleted\tdeadline\n"
                        + "LegacyCo\tIntern\ttrue\t2026-12-31\n");
        Storage storage = new Storage(temporaryFile.toString());

        ApplicationList loaded = storage.load();
        assertEquals(1, loaded.size());
        Application app = loaded.get(0);
        assertEquals("LegacyCo", app.getCompany());
        assertEquals("Intern", app.getRole());
        assertTrue(app.isCompleted());
        assertEquals("Applied", app.getStatus());
        assertEquals(LocalDate.of(2026, 12, 31), app.getDeadline());
        assertEquals("", app.getPortalLink());
        assertEquals("", app.getEmail());
        assertEquals("", app.getNotes());
    }

    @Test
    public void saveThenLoad_specialCharactersInNewFields_escapedAndDecodedCorrectly() throws Exception {
        Path temporaryFile = Files.createTempDirectory("jobseek-storage-test").resolve("applications.txt");
        Storage storage = new Storage(temporaryFile.toString());
        ApplicationList list = new ApplicationList();
        list.add(new Application("ByteDance", "SWE", false, "Pending",
                null, "https://jobs.bytedance.com/line1\\line2", "hr\tcontact@bytedance.com",
                "Note line 1\nNote line 2"));
        storage.save(list);

        ApplicationList loaded = storage.load();
        assertEquals(1, loaded.size());
        Application app = loaded.get(0);
        assertEquals("https://jobs.bytedance.com/line1\\line2", app.getPortalLink());
        assertEquals("hr\tcontact@bytedance.com", app.getEmail());
        assertEquals("Note line 1\nNote line 2", app.getNotes());
    }
}
