package jobseek.storage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;

/**
 * Handles persistence by loading and saving job application data to local storage.
 */
public class Storage {
    private static final String DEFAULT_FILE_PATH = "data/applications.txt";
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
     * Saves the application list to local storage.
     *
     * @param applications The ApplicationList to save.
     * @throws Exception If an I/O or serialization error occurs.
     */
    public void save(ApplicationList applications) throws Exception {
        Path path = Path.of(filePath);
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }

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
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /**
     * Loads the application list from local storage, recovering valid records when encountering corrupted lines.
     * Supports modern 8-field format and backwards-compatible 4-field / 5-field records.
     *
     * @return The loaded ApplicationList containing all recoverable records.
     * @throws Exception If an unrecoverable file reading error occurs.
     */
    public ApplicationList load() throws Exception {
        loadWarnings.clear();
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return new ApplicationList();
        }

        ApplicationList applications = new ApplicationList();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            if (line.isBlank() || line.startsWith("#") || isHeaderLine(line)) {
                continue;
            }

            int lineIndex = lineNumber + 1;
            Application application = parseRecord(lineIndex, line);
            if (application != null) {
                applications.add(application);
            }
        }
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
}
