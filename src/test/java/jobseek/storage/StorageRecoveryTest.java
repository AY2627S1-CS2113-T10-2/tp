package jobseek.storage;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests byte-order-mark handling and the backup made before skipped lines would be erased by a save.
 */
public class StorageRecoveryTest {
    private static final String HEADERS = "JOBSEEK Applications\n"
            + "company\trole\tcompleted\tdeadline\tstage\tportalLink\temail\tnotes\n";
    private static final String GOOD_LINE = "Google\tSWE\tfalse\t\tApplied\t\t\t\n";
    private static final String BAD_LINE = "Shopee\tBackend\tyes\t\tApplied\t\t\t\n";

    private static Path newFile(String content) throws Exception {
        Path file = Files.createTempDirectory("jobseek-recovery-test").resolve("applications.txt");
        Files.writeString(file, content, StandardCharsets.UTF_8);
        return file;
    }

    @Test
    public void load_fileWithByteOrderMark_readsHeaderAndRecordsWithoutWarnings() throws Exception {
        Path file = newFile("﻿" + HEADERS + GOOD_LINE);
        Storage storage = new Storage(file.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Google", loaded.get(0).getCompany());
        assertTrue(storage.getLoadWarnings().isEmpty());
        assertFalse(storage.isBackupPending());
    }

    @Test
    public void load_byteOrderMarkOnFirstRecordWithoutHeader_stillLoadsRecord() throws Exception {
        Path file = newFile("﻿" + GOOD_LINE);
        Storage storage = new Storage(file.toString());

        ApplicationList loaded = storage.load();

        assertEquals(1, loaded.size());
        assertEquals("Google", loaded.get(0).getCompany());
    }

    @Test
    public void load_cleanFile_needsNoBackupAndSaveCreatesNone() throws Exception {
        Path file = newFile(HEADERS + GOOD_LINE);
        Storage storage = new Storage(file.toString());

        ApplicationList loaded = storage.load();
        storage.save(loaded);

        assertFalse(storage.isBackupPending());
        assertFalse(Files.exists(Path.of(storage.getBackupFilePath())));
    }

    @Test
    public void save_afterSkippingCorruptedLine_copiesOriginalFileFirst() throws Exception {
        String original = HEADERS + GOOD_LINE + BAD_LINE;
        Path file = newFile(original);
        Storage storage = new Storage(file.toString());

        ApplicationList loaded = storage.load();
        assertEquals(1, loaded.size());
        assertEquals(1, storage.getLoadWarnings().size());
        assertTrue(storage.isBackupPending());
        storage.save(loaded);

        Path backup = Path.of(storage.getBackupFilePath());
        assertEquals(original, Files.readString(backup, StandardCharsets.UTF_8));
        assertFalse(Files.readString(file, StandardCharsets.UTF_8).contains("Shopee"));
        assertFalse(storage.isBackupPending());
    }

    @Test
    public void save_secondSaveInSameSession_doesNotOverwriteBackupWithCleanedFile() throws Exception {
        String original = HEADERS + GOOD_LINE + BAD_LINE;
        Path file = newFile(original);
        Storage storage = new Storage(file.toString());
        ApplicationList loaded = storage.load();
        storage.save(loaded);

        loaded.add(new Application("Grab", "Data", false));
        storage.save(loaded);

        assertEquals(original, Files.readString(Path.of(storage.getBackupFilePath()), StandardCharsets.UTF_8));
    }

    @Test
    public void load_afterCleanReload_clearsPendingBackup() throws Exception {
        Path file = newFile(HEADERS + GOOD_LINE + BAD_LINE);
        Storage storage = new Storage(file.toString());
        storage.load();
        assertTrue(storage.isBackupPending());

        Files.writeString(file, HEADERS + GOOD_LINE, StandardCharsets.UTF_8);
        storage.load();

        assertFalse(storage.isBackupPending());
    }
}
