package jobseek.storage;

import jobseek.model.ApplicationList;

/**
 * Handles persistence by loading and saving job application data to local storage.
 */
public class Storage {
    private String filePath;

    /**
     * Constructs a Storage instance with the default file path.
     */
    public Storage() {
        this("data/applications.json");
    }

    /**
     * Constructs a Storage instance with a specified file path.
     *
     * @param filePath Path to the storage file.
     */
    public Storage(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

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
        // Concrete file persistence logic to be implemented by storage feature branch
    }

    /**
     * Loads the application list from local storage.
     *
     * @return The loaded ApplicationList.
     * @throws Exception If an I/O or deserialization error occurs.
     */
    public ApplicationList load() throws Exception {
        return new ApplicationList();
    }
}
