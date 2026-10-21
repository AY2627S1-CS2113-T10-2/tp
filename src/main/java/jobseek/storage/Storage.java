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
    private static final String TITLE_LINE = "# JobSeek Applications";

    private String filePath;

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
        lines.add(TITLE_LINE);
        int companyWidth = "company".length();
        int roleWidth = "role".length();
        int completedWidth = "completed".length();
        for (Application application : applications.getAll()) {
            companyWidth = Math.max(companyWidth, escape(application.getCompany()).length());
            roleWidth = Math.max(roleWidth, escape(application.getRole()).length());
        }
        lines.add(pad("company", companyWidth) + "\t" + pad("role", roleWidth)
                + "\t" + pad("completed", completedWidth) + "\tdeadline");
        for (Application application : applications.getAll()) {
            String deadline = application.getDeadline() == null ? "" : application.getDeadline().toString();
            String completed = Boolean.toString(application.isCompleted());
            lines.add(pad(escape(application.getCompany()), companyWidth) + "\t"
                    + pad(escape(application.getRole()), roleWidth) + "\t"
                    + pad(completed, completedWidth) + "\t" + deadline);
        }
        Files.write(path, lines, StandardCharsets.UTF_8);
    }

    /**
     * Loads the application list from local storage.
     *
     * @return The loaded ApplicationList.
     * @throws Exception If an I/O or deserialization error occurs.
     */
    public ApplicationList load() throws Exception {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return new ApplicationList();
        }

        ApplicationList applications = new ApplicationList();
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);
            String trimmedLine = line.trim();
            if (line.isBlank() || line.startsWith("#") || trimmedLine.equalsIgnoreCase("JOBSEEK Applications")) {
                continue;
            }
            List<String> fields = splitFields(line);
            if (fields.stream().map(String::trim).toList()
                    .equals(List.of("company", "role", "completed", "deadline"))) {
                continue;
            }
            if (fields.size() != 4 || (!"true".equals(fields.get(2)) && !"false".equals(fields.get(2)))) {
                throw new IOException("Invalid record on line " + (lineNumber + 1) + " in " + filePath + ".");
            }
            LocalDate deadline = null;
            if (!fields.get(3).isEmpty()) {
                try {
                    deadline = LocalDate.parse(fields.get(3));
                } catch (DateTimeParseException e) {
                    throw new IOException("Invalid deadline on line " + (lineNumber + 1) + " in " + filePath + ".", e);
                }
            }
            boolean isCompleted = Boolean.parseBoolean(fields.get(2));
            applications.add(new Application(fields.get(0), fields.get(1), isCompleted, deadline));
        }
        return applications;
    }

    /** Escapes characters that would otherwise conflict with the tab-separated format. */
    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\t", "\\t").replace("\n", "\\n").replace("\r", "\\r");
    }

    /** Pads a field so the following tab separator aligns consistently in text editors. */
    private static String pad(String value, int width) {
        return value + " ".repeat(Math.max(0, width - value.length()));
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
                fields.add(field.toString().stripTrailing());
                field.setLength(0);
            } else {
                field.append(character);
            }
        }
        if (escaped) {
            throw new IOException("Incomplete escape sequence in " + filePath + ".");
        }
        fields.add(field.toString().stripTrailing());
        return fields;
    }
}
