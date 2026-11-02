package jobseek;

import jobseek.storage.Storage;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Creates {@link Storage} objects that point at a throwaway temporary file.
 *
 * <p>Commands save after every change. A test that uses {@code new Storage()} writes to the real
 * {@code data/applications.txt}, which overwrites the data of whoever runs the tests.
 * Tests should use {@link #create()} instead.
 */
public final class TestStorage {

    private TestStorage() {
        // Utility class: all methods are static.
    }

    /**
     * Returns a Storage whose file lives in a new temporary directory that is deleted when the JVM exits.
     *
     * @return Storage for one test; its file does not exist until the first save.
     */
    public static Storage create() {
        try {
            Path directory = Files.createTempDirectory("jobseek-test");
            Path file = directory.resolve("applications.txt");
            // Deleted in this order on exit: the file first (if a save created it), then its directory.
            directory.toFile().deleteOnExit();
            new File(file.toString()).deleteOnExit();
            return new Storage(file.toString());
        } catch (IOException e) {
            throw new UncheckedIOException("Could not create temporary storage for a test", e);
        }
    }
}
