package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles setting or updating the recruitment status indicator (e.g., Pending, Accepted, Rejected)
 * for an existing application.
 */
public class StatusCommand extends Command {
    private final int targetIndex;
    private final String status;

    /**
     * Constructs a StatusCommand with the target index and desired recruitment status.
     *
     * @param targetIndex 0-based index of the application in the application list.
     * @param status Recruitment status string to assign (e.g. "Pending", "Accepted", "Rejected").
     */
    public StatusCommand(int targetIndex, String status) {
        this.targetIndex = targetIndex;
        this.status = status != null ? status.trim() : "";
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Executes the status command by validating the target index, setting the new recruitment status,
     * printing a confirmation message, and saving the updated list.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for user feedback.
     * @param storage Storage instance to persist the updated list.
     * @throws Exception If an error occurs during saving.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws Exception {
        assert applications != null : "Applications list cannot be null";
        assert ui != null : "Ui instance cannot be null";
        assert storage != null : "Storage instance cannot be null";

        if (applications.isEmpty()) {
            ui.showError("Your application list is currently empty.");
            return;
        }

        if (!applications.isValidIndex(targetIndex)) {
            ui.showError("Invalid application index: " + (targetIndex + 1)
                    + ". Please enter a number between 1 and " + applications.size() + ".");
            return;
        }

        if (status.isEmpty()) {
            ui.showError("Status cannot be empty.");
            return;
        }

        Application application = applications.get(targetIndex);
        assert application != null : "Application at valid index should not be null";

        application.setStatus(status);
        assert application.getStatus().equals(status) : "Application status should match the updated status";

        String message = "Application status updated successfully.\n"
                + "Company: " + application.getCompany() + "\n"
                + "Role: " + application.getRole() + "\n"
                + "Status: " + application.getStatus();
        ui.showMessage(message);

        storage.save(applications);
    }
}
