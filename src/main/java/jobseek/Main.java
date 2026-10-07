package jobseek;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Main entry point for the JobSeek CLI application.
 */
public class Main {
    private final Ui ui;
    private final Storage storage;
    private final ApplicationList applications;

    /**
     * Constructs the main application instance and initializes core components.
     */
    public Main() {
        this.ui = new Ui();
        this.storage = new Storage();
        this.applications = new ApplicationList();
    }

    /**
     * Starts and executes the application workflow.
     */
    public void run() {
        ui.showWelcome();
        // Integration point: team members can plug in Parser and Command execution loop here
        ui.showGoodbye();
    }

    /**
     * Application entry point.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        new Main().run();
    }
}
