package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles editing specific existing fields (company, role, recruitment stage, or all) of an application.
 */
public class EditCommand extends UndoableCommand {
    private final int targetIndex;
    private final String newCompany;
    private final String newRole;
    private final RecruitmentStage newStage;
    private Application editedApplication;
    private String previousCompany;
    private String previousRole;
    private RecruitmentStage previousStage;

    /**
     * Constructs an EditCommand with the specified target index and new field values for company and role.
     *
     * @param targetIndex 0-based index of the application to edit.
     * @param newCompany New company name, or null if company is not being updated.
     * @param newRole New role title, or null if role is not being updated.
     */
    public EditCommand(int targetIndex, String newCompany, String newRole) {
        this(targetIndex, newCompany, newRole, null);
    }

    /**
     * Constructs an EditCommand with the target index and optional recruitment stage.
     *
     * @param targetIndex 0-based index of the application to edit.
     * @param newCompany New company name, or null if company is not being updated.
     * @param newRole New role title, or null if role is not being updated.
     * @param newStage New recruitment stage, or null if the stage is not being updated.
     */
    public EditCommand(int targetIndex, String newCompany, String newRole, RecruitmentStage newStage) {
        this.targetIndex = targetIndex;
        this.newCompany = (newCompany != null && !newCompany.trim().isEmpty()) ? newCompany.trim() : null;
        this.newRole = (newRole != null && !newRole.trim().isEmpty()) ? newRole.trim() : null;
        this.newStage = newStage;
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

    public RecruitmentStage getNewStage() {
        return newStage;
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

        if (newCompany == null && newRole == null && newStage == null) {
            ui.showError("At least one field (company, role, or recruitment stage) must be specified for editing.");
            return;
        }

        Application application = applications.get(targetIndex);
        assert application != null : "Application at valid index should not be null";

        // Remember all old values so undo can restore the application exactly.
        editedApplication = application;
        previousCompany = application.getCompany();
        previousRole = application.getRole();
        previousStage = application.getRecruitmentStage();
        markChanged();

        if (newCompany != null) {
            application.setCompany(newCompany);
        }
        if (newRole != null) {
            application.setRole(newRole);
        }
        if (newStage != null) {
            application.setRecruitmentStage(newStage);
        }

        assert !application.getCompany().isEmpty() : "Company should not be empty after edit";
        assert !application.getRole().isEmpty() : "Role should not be empty after edit";

        StringBuilder message = new StringBuilder("Application updated successfully.\n");
        message.append("Company: ").append(application.getCompany()).append("\n");
        message.append("Role: ").append(application.getRole()).append("\n");
        message.append("Recruitment stage: ").append(application.getRecruitmentStage().getLabel());
        ui.showMessage(message.toString());

        storage.save(applications);
    }

    /** Restores the company, role and recruitment stage from before this command ran. */
    @Override
    public String undo(ApplicationList applications) {
        editedApplication.setCompany(previousCompany);
        editedApplication.setRole(previousRole);
        editedApplication.setRecruitmentStage(previousStage);
        return "edit of " + editedApplication;
    }
}
