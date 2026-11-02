package jobseek.command;

import java.io.IOException;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles setting or updating the recruitment stage (e.g., Applied, Interview, Accepted)
 * for an existing application.
 */
public class RecruitmentStageCommand extends UndoableCommand {
    private final int targetIndex;
    private final RecruitmentStage stage;
    private Application updatedApplication;
    private RecruitmentStage previousStage;

    /**
     * Constructs a command with the target index and desired recruitment stage.
     *
     * @param targetIndex 0-based index of the application in the application list.
     * @param stage Recruitment stage to assign.
     */
    public RecruitmentStageCommand(int targetIndex, RecruitmentStage stage) {
        this.targetIndex = targetIndex;
        this.stage = stage;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    public RecruitmentStage getStage() {
        return stage;
    }

    /**
     * Executes the command by validating the target index, setting the recruitment stage,
     * printing a confirmation message, and saving the updated list.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for user feedback.
     * @param storage Storage instance to persist the updated list.
     * @throws IOException If the updated list could not be saved.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws IOException {
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

        if (stage == null) {
            ui.showError("Recruitment stage cannot be empty.");
            return;
        }

        Application application = applications.get(targetIndex);
        assert application != null : "Application at valid index should not be null";

        if (application.getRecruitmentStage() == stage) {
            ui.showMessage("No changes made: the application is already at stage " + stage.getLabel() + ".\n  "
                    + application);
            return;
        }

        updatedApplication = application;
        previousStage = application.getRecruitmentStage();
        application.setRecruitmentStage(stage);
        assert application.getRecruitmentStage() == stage
                : "Application stage should match the updated stage";
        markChanged();

        StringBuilder message = new StringBuilder("Application recruitment stage updated successfully.\n");
        message.append("Company: ").append(application.getCompany()).append("\n");
        message.append("Role: ").append(application.getRole()).append("\n");
        message.append("Recruitment stage: ").append(application.getRecruitmentStage().getLabel());
        if (application.getDeadline() != null) {
            message.append("\nDeadline: ").append(application.getDeadline());
        }
        if (!application.getPortalLink().isEmpty()) {
            message.append("\nPortal Link: ").append(application.getPortalLink());
        }
        if (!application.getEmail().isEmpty()) {
            message.append("\nEmail: ").append(application.getEmail());
        }
        if (!application.getNotes().isEmpty()) {
            message.append("\nNotes: ").append(application.getNotes());
        }
        saveOrRollback(applications, storage);
        ui.showMessage(message.toString());
    }

    /** Restores the recruitment stage the application had before this command ran. */
    @Override
    public String undo(ApplicationList applications) {
        updatedApplication.setRecruitmentStage(previousStage);
        return "recruitment stage change of " + updatedApplication;
    }
}
