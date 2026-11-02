package jobseek.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;

/**
 * Handles persistence by loading and saving job application data to local storage.
 */
public class Storage {
    private static final Logger LOGGER = initLogger();
    private static final String DEFAULT_FILE_PATH = "data/applications.txt";
    private static final String BACKUP_SUFFIX = ".bak";
    /** Some editors (e.g. older Windows Notepad) put this invisible character at the start of UTF-8 files. */
    private static final char BYTE_ORDER_MARK = '\uFEFF';
    private static final String TITLE_HEADER = "JOBSEEK Applications";
    private static final String COLUMN_HEADER =
            "company\trole\tcompleted\tdeadline\tstage\tportalLink\temail\tnotes";
    private static final String LEGACY_STATUS_8_COLUMN_HEADER =
            "company\trole\tcompleted\tdeadline\tstatus\tportalLink\temail\tnotes";
    private static final String LEGACY_STAGE_5_COLUMN_HEADER =
            "company\trole\tcompleted\tdeadline\tstage";
    private static final String LEGACY_STATUS_5_COLUMN_HEADER =
            "company\trole\tcompleted\tdeadline\tstatus";
    private static final String LEGACY_4_COLUMN_HEADER =
            "company\trole\tcompleted\tdeadline";
    private static final String HEADER = TITLE_HEADER + "\n" + COLUMN_HEADER;
    private static final int LEGACY_FIELD_COUNT_4 = 4;
    private static final int LEGACY_FIELD_COUNT_5 = 5;
    private static final int FIELD_COUNT = 8;

    private String filePath;

    /** Keeps a failed load's empty fallback list from overwriting the original file. */
    private boolean saveBlocked;

    /** True after a load that skipped lines, until the original file has been copied to the backup path. */
    private boolean backupPending;

    private final List<String> loadWarnings = new ArrayList<>();

    /**
     * Constructs a Storage instance with the default file path.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a Storage instance with a specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /** Returns the path used for the application's local data file. */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Updates the path used for loading and saving application data.
     *
     * @param filePath New path to the storage file.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Returns the list of warning messages generated during the most recent load operation.
     *
     * @return An unmodifiable list of load warnings.
     */
    public List<String> getLoadWarnings() {
        return List.copyOf(loadWarnings);
    }

    /**
     * Returns where the original data file is copied before the first save that follows a load which
     * skipped corrupted lines.
     *
     * @return Path of the backup file.
     */
    public String getBackupFilePath() {
        return filePath + BACKUP_SUFFIX;
    }

    /**
     * Returns whether the data file will be backed up before the next save, because the last load
     * skipped corrupted lines that a save would otherwise erase.
     *
     * @return True if a backup is still pending.
     */
    public boolean isBackupPending() {
        return backupPending;
    }

    /** Clears the recorded load warnings. */
    public void clearLoadWarnings() {
        loadWarnings.clear();
    }

    /**
     * Rejects writes after a fatal load failure until a subsequent load succeeds.
     * Callers can also use this check before changing the in-memory list.
     *
     * @throws IOException If the existing data could not be loaded safely.
     */
    public void ensureWritable() throws IOException {
        if (saveBlocked) {
            throw new IOException("Changes are disabled because the data file could not be loaded. "
                    + "Repair or restore the file and restart JobSeek before making changes.");
        }
    }

    /**
     * Saves the application list to local storage.
     *
     * @param applications The ApplicationList to save.
     * @throws IOException If a file writing error occurs.
     */
    public void save(ApplicationList applications) throws IOException {
        ensureWritable();
        Path path = Path.of(filePath);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        backUpOriginalIfNeeded(path);

        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        for (Application application : applications.getAll()) {
            String deadline = application.getDeadline() == null ? "" : application.getDeadline().toString();
            lines.add(escape(application.getCompany()) + "\t"
                    + escape(application.getRole()) + "\t"
                    + application.isCompleted() + "\t"
                    + deadline + "\t"
                    + escape(application.getRecruitmentStage().getLabel()) + "\t"
                    + escape(application.getPortalLink()) + "\t"
                    + escape(application.getEmail()) + "\t"
                    + escape(application.getNotes()));
        }
        writeAtomically(path, lines);
        LOGGER.info("Saved " + applications.size() + " applications to " + filePath);
    }

    /**
     * Writes the lines to a temporary file next to the target and then replaces the target with it.
     * Writing straight over the target would truncate it first, so a failure part-way through
     * (a locked file, a full disk, a crash) would leave the user's saved data empty or half written.
     * A failed temporary-file write leaves the original untouched. The replacement is atomic only
     * when the file system supports atomic moves; otherwise a regular replacement is used, which
     * cannot guarantee an intact original after a move failure or a crash during replacement.
     *
     * @param target Data file to replace.
     * @param lines Complete new contents of the file.
     * @throws IOException If the new contents could not be written or moved into place.
     */
    private void writeAtomically(Path target, List<String> lines) throws IOException {
        Path parent = target.toAbsolutePath().getParent();
        // Same directory as the target, so the final move stays on one file system.
        Path temporary = Files.createTempFile(parent, "applications-", ".tmp");
        try {
            Files.write(temporary, lines, StandardCharsets.UTF_8);
            try {
                Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                // Compatibility fallback: this replacement does not provide the same atomicity guarantee.
                Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException | RuntimeException e) {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException cleanupFailure) {
                e.addSuppressed(cleanupFailure);
            }
            throw e;
        }
    }

    /**
     * Copies the original file aside before the first save after a load that skipped lines, because
     * saving rewrites the file from memory and would permanently erase the skipped lines.
     */
    private void backUpOriginalIfNeeded(Path path) throws IOException {
        if (!backupPending) {
            return;
        }
        if (Files.exists(path)) {
            Files.copy(path, Path.of(getBackupFilePath()), StandardCopyOption.REPLACE_EXISTING);
            LOGGER.info("Backed up original data file to " + getBackupFilePath());
        }
        backupPending = false;
    }

    /**
     * Loads the application list from local storage, recovering valid records when encountering corrupted lines.
     * Supports modern 8-field format and backwards-compatible 4-field / 5-field records.
     *
     * @return The loaded ApplicationList containing all recoverable records.
     * @throws IOException If an unrecoverable file reading error occurs.
     */
    public ApplicationList load() throws IOException {
        loadWarnings.clear();
        backupPending = false;
        saveBlocked = true;
        Path path = Path.of(filePath);
        if (Files.notExists(path)) {
            LOGGER.info("Storage file not found at " + filePath + ". Returning empty list.");
            saveBlocked = false;
            return new ApplicationList();
        }

        ApplicationList applications = new ApplicationList();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        if (!lines.isEmpty() && lines.get(0).startsWith(String.valueOf(BYTE_ORDER_MARK))) {
            // Without this, the title line of a file saved with a BOM is not recognised as a header.
            lines.set(0, lines.get(0).substring(1));
        }
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            // Application records contain tabs, even when the company starts with '#'.
            if (line.isBlank() || (line.startsWith("#") && !line.contains("\t")) || isHeaderLine(line)) {
                continue;
            }

            int lineIndex = lineNumber + 1;
            Application application = parseRecord(lineIndex, line);
            if (application != null) {
                applications.add(application);
            }
        }
        LOGGER.info("Loaded " + applications.size() + " applications from " + filePath);
        backupPending = !loadWarnings.isEmpty();
        saveBlocked = false;
        return applications;
    }

    /** Checks whether a line matches any known active or legacy column header. */
    private boolean isHeaderLine(String line) {
        return TITLE_HEADER.equals(line) || COLUMN_HEADER.equals(line)
                || LEGACY_STATUS_8_COLUMN_HEADER.equals(line)
                || LEGACY_STAGE_5_COLUMN_HEADER.equals(line)
                || LEGACY_STATUS_5_COLUMN_HEADER.equals(line)
                || LEGACY_4_COLUMN_HEADER.equals(line);
    }

    /**
     * Parses a single line from the storage file into an Application record.
     * Records a warning in loadWarnings and returns null if the line is invalid or corrupted.
     *
     * @param lineIndex 1-based index of the line in the file.
     * @param line Raw line text.
     * @return Parsed Application instance, or null if line should be skipped.
     */
    private Application parseRecord(int lineIndex, String line) {
        List<String> fields;
        try {
            fields = splitFields(line);
        } catch (IOException e) {
            loadWarnings.add("Skipping line " + lineIndex + " in " + filePath + ": " + e.getMessage());
            return null;
        }

        boolean hasValidFieldCount = fields.size() == FIELD_COUNT
                || fields.size() == LEGACY_FIELD_COUNT_5
                || fields.size() == LEGACY_FIELD_COUNT_4;
        if (!hasValidFieldCount || (!"true".equals(fields.get(2)) && !"false".equals(fields.get(2)))) {
            loadWarnings.add("Skipping invalid record on line " + lineIndex + " in " + filePath + ".");
            return null;
        }

        String company = fields.get(0);
        String role = fields.get(1);
        if (company.isBlank() || role.isBlank()) {
            loadWarnings.add("Skipping record with empty company or role on line " + lineIndex
                    + " in " + filePath + ".");
            return null;
        }

        LocalDate deadline = null;
        if (!fields.get(3).isEmpty()) {
            try {
                deadline = LocalDate.parse(fields.get(3));
            } catch (DateTimeParseException e) {
                loadWarnings.add("Skipping record with invalid deadline on line " + lineIndex
                        + " in " + filePath + ".");
                return null;
            }
        }

        boolean isCompleted = Boolean.parseBoolean(fields.get(2));
        String stageLabel = fields.size() >= 5 ? fields.get(4) : null;
        RecruitmentStage stage;
        try {
            stage = stageLabel == null || stageLabel.isBlank()
                    ? Application.DEFAULT_STAGE : RecruitmentStage.fromLabel(stageLabel);
        } catch (IllegalArgumentException e) {
            loadWarnings.add("Skipping record with unknown recruitment stage on line " + lineIndex
                    + " in " + filePath + ".");
            return null;
        }

        String portalLink = fields.size() >= 8 ? fields.get(5) : "";
        String email = fields.size() >= 8 ? fields.get(6) : "";
        String notes = fields.size() >= 8 ? fields.get(7) : "";

        return new Application(company, role, isCompleted, stage, deadline, portalLink, email, notes);
    }

    /** Escapes characters that would otherwise conflict with the tab-separated format. */
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
    }

    /** Decodes one saved record into its tab-separated fields. */
    private List<String> splitFields(String line) throws IOException {
        List<String> fields = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean escaped = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (escaped) {
                switch (character) {
                case '\\':
                    field.append('\\');
                    break;
                case 't':
                    field.append('\t');
                    break;
                case 'n':
                    field.append('\n');
                    break;
                case 'r':
                    field.append('\r');
                    break;
                default:
                    throw new IOException("Invalid escape sequence in " + filePath + ".");
                }
                escaped = false;
            } else if (character == '\\') {
                escaped = true;
            } else if (character == '\t') {
                fields.add(field.toString());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (escaped) {
            throw new IOException("Incomplete escape sequence in " + filePath + ".");
        }
        fields.add(field.toString());
        return fields;
    }

    private static Logger initLogger() {
        Logger jobseekLogger = Logger.getLogger("jobseek");
        jobseekLogger.setUseParentHandlers(false);
        return Logger.getLogger(Storage.class.getName());
    }
}
