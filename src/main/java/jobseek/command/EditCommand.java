package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles editing specific existing fields (company, role, or both) of an application.
 */
public class EditCommand extends Command {
    private final int targetIndex;
    private final String newCompany;
    private final String newRole;

    /**
     * Constructs an EditCommand with the specified target index and new field values.
     *
     * @param targetIndex 0-based index of the application to edit.
     * @param newCompany New company name, or null if company is not being updated.
     * @param newRole New role title, or null if role is not being updated.
     */
    public EditCommand(int targetIndex, String newCompany, String newRole) {
        this.targetIndex = targetIndex;
        this.newCompany = (newCompany != null && !newCompany.trim().isEmpty()) ? newCompany.trim() : null;
        this.newRole = (newRole != null && !newRole.trim().isEmpty()) ? newRole.trim() : null;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public String getNewCompany() {
        return newCompany;
    }

    public String getNewRole() {
        return newRole;
    }

    /**
     * Executes the edit command by validating the target index, updating provided fields,
     * displaying confirmation, and persisting changes to storage.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for user feedback.
     * @param storage Storage instance to persist the updated list.
     * @throws Exception If an error occurs during saving.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws Exception {
        if (applications.isEmpty()) {
            ui.showError("Your application list is currently empty.");
            return;
        }

        if (!applications.isValidIndex(targetIndex)) {
            ui.showError("Invalid application index: " + (targetIndex + 1)
                    + ". Please enter a number between 1 and " + applications.size() + ".");
            return;
        }

        if (newCompany == null && newRole == null) {
            ui.showError("At least one field (company or role) must be specified for editing.");
            return;
        }

        Application application = applications.get(targetIndex);
        if (newCompany != null) {
            application.setCompany(newCompany);
        }
        if (newRole != null) {
            application.setRole(newRole);
        }

        String message = "Application updated successfully.\n"
                + "Company: " + application.getCompany() + "\n"
                + "Role: " + application.getRole();
        ui.showMessage(message);

        storage.save(applications);
    }
}
