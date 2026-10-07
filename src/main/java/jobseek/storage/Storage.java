package jobseek.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jobseek.model.JobApplication;

/**
 * Handles saving and loading applications from a local human-editable text file.
 */
public class Storage {
    private static final String DEFAULT_FILE_PATH = "data/jobseek.txt";
    private static final String DELIMITER = " \\| ";
    private static final String SEPARATOR = " | ";

    private final String filePath;

    /**
     * Constructs a Storage instance with the default file path.
     */
    public Storage() {
        this(DEFAULT_FILE_PATH);
    }

    /**
     * Constructs a Storage instance with a custom file path.
     *
     * @param filePath Path to the data file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads applications from the storage file.
     * If the file does not exist, returns an empty list.
     *
     * @return List of loaded {@link JobApplication} instances.
     * @throws IOException If an I/O error occurs while reading.
     */
    public List<JobApplication> loadApplications() throws IOException {
        List<JobApplication> applications = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            return applications;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] parts = line.split(DELIMITER, 2);
                if (parts.length == 2) {
                    String company = parts[0].trim();
                    String role = parts[1].trim();
                    if (!company.isEmpty() && !role.isEmpty()) {
                        applications.add(new JobApplication(company, role));
                    }
                }
            }
        }

        return applications;
    }

    /**
     * Saves the provided list of applications to the storage file.
     *
     * @param applications List of {@link JobApplication} to save.
     * @throws IOException If an I/O error occurs while writing.
     */
    public void saveApplications(List<JobApplication> applications) throws IOException {
        File file = new File(filePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (JobApplication app : applications) {
                writer.write(app.getCompany() + SEPARATOR + app.getRole());
                writer.newLine();
            }
        }
    }
}
