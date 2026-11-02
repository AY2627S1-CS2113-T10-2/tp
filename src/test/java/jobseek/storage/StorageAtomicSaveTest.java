package jobseek.storage;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that saving replaces the data file as a whole and never leaves it truncated or littered with temp files.
 */
public class StorageAtomicSaveTest {

    private static List<String> namesIn(Path directory) throws IOException {
        try (Stream<Path> files = Files.list(directory)) {
            return files.map(path -> path.getFileName().toString()).sorted().toList();
        }
    }

    @Test
    public void save_replacesContentAndLeavesNoTemporaryFile() throws Exception {
        Path directory = Files.createTempDirectory("jobseek-atomic-test");
        Path file = directory.resolve("applications.txt");
        Storage storage = new Storage(file.toString());
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        storage.save(applications);

        applications.add(new Application("Shopee", "Backend", false));
        storage.save(applications);

        assertEquals(List.of("applications.txt"), namesIn(directory));
        String content = Files.readString(file, StandardCharsets.UTF_8);
        assertTrue(content.contains("Google") && content.contains("Shopee"));
        assertEquals(2, storage.load().size());
    }

    @Test
    public void save_intoMissingFolder_createsFolderAndFile() throws Exception {
        Path directory = Files.createTempDirectory("jobseek-atomic-test").resolve("data");
        Storage storage = new Storage(directory.resolve("applications.txt").toString());

        storage.save(new ApplicationList());

        assertEquals(List.of("applications.txt"), namesIn(directory));
    }

    @Test
    public void save_whenOriginalCannotBeReplaced_keepsOriginalContentAndCleansUp() throws Exception {
        Path directory = Files.createTempDirectory("jobseek-atomic-test");
        Path file = directory.resolve("applications.txt");
        Storage storage = new Storage(file.toString());
        ApplicationList applications = new ApplicationList();
        applications.add(new Application("Google", "SWE", false));
        storage.save(applications);
        String original = Files.readString(file, StandardCharsets.UTF_8);
        applications.add(new Application("Shopee", "Backend", false));

        boolean threw = false;
        // Another process holding the file open is a real cause of failed saves (e.g. sync tools on Windows).
        // Where the operating system lets the replacement succeed anyway, the assertions below still hold.
        try (RandomAccessFile open = new RandomAccessFile(file.toFile(), "rw");
             FileChannel channel = open.getChannel();
             FileLock ignored = channel.lock(0, Long.MAX_VALUE, false)) {
            try {
                storage.save(applications);
            } catch (IOException e) {
                threw = true;
            }
        }

        String after = Files.readString(file, StandardCharsets.UTF_8);
        if (threw) {
            assertEquals(original, after, "a failed save must leave the original file untouched");
        } else {
            assertTrue(after.contains("Shopee"));
        }
        assertEquals(List.of("applications.txt"), namesIn(directory), "no temporary file may be left behind");
    }
}
