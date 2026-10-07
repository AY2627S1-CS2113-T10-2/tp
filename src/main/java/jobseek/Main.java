package jobseek;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import jobseek.controller.JobSeekController;
import jobseek.logic.ApplicationManager;
import jobseek.model.JobApplication;
import jobseek.storage.Storage;
import jobseek.ui.CliUi;

/**
 * Main entry point for the JobSeek application.
 */
public class Main {
    /**
     * Entry method to initialize and start JobSeek.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        Storage storage = new Storage();
        ApplicationManager manager;

        try {
            List<JobApplication> loadedApplications = storage.loadApplications();
            manager = new ApplicationManager(loadedApplications);
        } catch (IOException e) {
            System.err.println("Warning: Could not load saved applications: " + e.getMessage());
            manager = new ApplicationManager();
        }

        Scanner scanner = new Scanner(System.in);
        CliUi ui = new CliUi(scanner);
        JobSeekController controller = new JobSeekController(ui, manager, storage);

        controller.run();
    }
}
