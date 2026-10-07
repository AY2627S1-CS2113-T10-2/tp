package jobseek.controller;

import java.io.IOException;

import jobseek.logic.ApplicationManager;
import jobseek.model.JobApplication;
import jobseek.storage.Storage;
import jobseek.ui.CliUi;

/**
 * Main application controller managing the central navigation loop and workflows.
 */
public class JobSeekController {
    private final CliUi ui;
    private final ApplicationManager applicationManager;
    private final Storage storage;

    /**
     * Constructs a JobSeekController with the given UI, application manager, and storage.
     *
     * @param ui The user interface handler.
     * @param applicationManager The application manager handling business logic.
     * @param storage The storage handler for persistence.
     */
    public JobSeekController(CliUi ui, ApplicationManager applicationManager, Storage storage) {
        this.ui = ui;
        this.applicationManager = applicationManager;
        this.storage = storage;
    }

    /**
     * Starts the main application loop.
     */
    public void run() {
        boolean running = true;

        while (running) {
            ui.printApplicationsPage(applicationManager.getApplications());
            String option = ui.readLine();

            switch (option) {
            case "1":
                handleAddApplication();
                break;
            case "2":
                handleDeleteApplication();
                break;
            case "3":
                handleHelp();
                break;
            case "0":
                running = false;
                break;
            default:
                ui.printError("Invalid option. Enter 0, 1, 2, or 3.");
                break;
            }
        }

        ui.printGoodbye();
    }

    private void handleAddApplication() {
        System.out.println();
        ui.printAddApplicationHeader();

        String company = promptValidField(true);
        String role = promptValidField(false);

        try {
            JobApplication app = applicationManager.addApplication(company, role);
            if (storage != null) {
                storage.saveApplications(applicationManager.getApplications());
            }
            ui.printApplicationAdded(app);
        } catch (IOException e) {
            // Revert changes if saving failed
            int currentCount = applicationManager.getApplicationCount();
            if (currentCount > 0) {
                applicationManager.deleteApplication(currentCount);
            }
            ui.printError("Error saving application: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            ui.printError("Error: " + e.getMessage());
        }
    }

    private String promptValidField(boolean isCompany) {
        while (true) {
            if (isCompany) {
                ui.printCompanyPrompt();
            } else {
                ui.printRolePrompt();
            }

            String input = ui.readRawLine();
            String trimmed = input.trim();
            if (!trimmed.isEmpty()) {
                if (isCompany) {
                    System.out.println();
                }
                return trimmed;
            }

            if (isCompany) {
                System.out.println("Error: Company cannot be empty.");
            } else {
                System.out.println("Error: Role cannot be empty.");
            }
        }
    }

    private void handleDeleteApplication() {
        System.out.println();
        if (applicationManager.getApplicationCount() == 0) {
            ui.printDeleteApplicationPage(applicationManager.getApplications());
            return;
        }

        ui.printDeleteApplicationPage(applicationManager.getApplications());
        String indexInput = ui.readLine();

        int index;
        try {
            index = Integer.parseInt(indexInput);
        } catch (NumberFormatException e) {
            ui.printError("Error: Please enter a valid application index.");
            return;
        }

        if (index < 1 || index > applicationManager.getApplicationCount()) {
            ui.printError("Error: Please enter a valid application index.");
            return;
        }

        JobApplication deleted = applicationManager.deleteApplication(index);
        try {
            if (storage != null) {
                storage.saveApplications(applicationManager.getApplications());
            }
            ui.printApplicationDeleted(deleted);
        } catch (IOException e) {
            // Re-insert if saving failed
            applicationManager.addApplication(deleted.getCompany(), deleted.getRole());
            ui.printError("Error saving deletion: " + e.getMessage());
        }
    }

    private void handleHelp() {
        System.out.println();
        ui.printHelp();
    }
}
