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

/**
 * Handles persistence by loading and saving job application data to local storage.
 */
public class Storage {
    private static final String DEFAULT_FILE_PATH = "data/applications.txt";
    private static final String TITLE_HEADER = "JOBSEEK Applications";
    private static final String COLUMN_HEADER = "company\trole\tcompleted\tdeadline\tstatus";
    /** Column header written by earlier versions, which did not save the recruitment status. */
    private static final String LEGACY_COLUMN_HEADER = "company\trole\tcompleted\tdeadline";
    private static final String HEADER = TITLE_HEADER + "\n" + COLUMN_HEADER;
    private static final int LEGACY_FIELD_COUNT = 4;
    private static final int FIELD_COUNT = 5;

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
            lines.add(escape(application.getCompany()) + "\t" + escape(application.getRole()) + "\t"
                    + application.isCompleted() + "\t" + deadline + "\t" + escape(application.getStatus()));
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /**
     * Loads the application list from local storage, recovering valid records when encountering corrupted lines.
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
            if (line.isBlank() || line.startsWith("#")) {
                continue;
            }
            if (TITLE_HEADER.equals(line) || COLUMN_HEADER.equals(line) || LEGACY_COLUMN_HEADER.equals(line)) {
                continue;
            }

            int lineIndex = lineNumber + 1;
            List<String> fields;
            try {
                fields = splitFields(line);
            } catch (IOException e) {
                loadWarnings.add("Skipping line " + lineIndex + " in " + filePath + ": " + e.getMessage());
                continue;
            }

            // Files saved before status was persisted have only four fields; they load as "Pending".
            boolean hasValidFieldCount = fields.size() == FIELD_COUNT || fields.size() == LEGACY_FIELD_COUNT;
            if (!hasValidFieldCount || (!"true".equals(fields.get(2)) && !"false".equals(fields.get(2)))) {
                loadWarnings.add("Skipping invalid record on line " + lineIndex + " in " + filePath + ".");
                continue;
            }

            String company = fields.get(0);
            String role = fields.get(1);
            if (company.isBlank() || role.isBlank()) {
                loadWarnings.add("Skipping record with empty company or role on line " + lineIndex
                        + " in " + filePath + ".");
                continue;
            }

            LocalDate deadline = null;
            if (!fields.get(3).isEmpty()) {
                try {
                    deadline = LocalDate.parse(fields.get(3));
                } catch (DateTimeParseException e) {
                    loadWarnings.add("Skipping record with invalid deadline on line " + lineIndex
                            + " in " + filePath + ".");
                    continue;
                }
            }

            boolean isCompleted = Boolean.parseBoolean(fields.get(2));
            // A missing or blank status makes the Application constructor fall back to "Pending".
            String status = fields.size() == FIELD_COUNT ? fields.get(4) : null;
            applications.add(new Application(company, role, isCompleted, status, deadline));
        }
        return applications;
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
