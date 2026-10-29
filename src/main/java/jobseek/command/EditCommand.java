package jobseek.command;

import java.time.LocalDate;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.model.RecruitmentStage;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles editing one or more fields (company, role, recruitment stage, deadline, portal link, email, notes)
 * of an existing application.
 */
public class EditCommand extends UndoableCommand {
    private final int targetIndex;
    private final String newCompany;
    private final String newRole;
    private final RecruitmentStage newStage;
    private final LocalDate newDeadline;
    private final String newPortalLink;
    private final String newEmail;
    private final String newNotes;
    private boolean isDeadlineUpdated;
    private Application editedApplication;
    private String previousCompany;
    private String previousRole;
    private RecruitmentStage previousStage;
    private LocalDate previousDeadline;
    private String previousPortalLink;
    private String previousEmail;
    private String previousNotes;

    /**
     * Constructs an EditCommand with the specified target index and new field values for company and role.
     *
     * @param targetIndex 0-based index of the application to edit.
     * @param newCompany New company name, or null if company is not being updated.
     * @param newRole New role title, or null if role is not being updated.
     */
    public EditCommand(int targetIndex, String newCompany, String newRole) {
        this(targetIndex, newCompany, newRole, (RecruitmentStage) null, null, null, null, null);
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
        this(targetIndex, newCompany, newRole, newStage, null, null, null, null);
    }

    /**
     * Constructs an EditCommand with the target index and recruitment stage string label.
     *
     * @param targetIndex 0-based index of the application to edit.
     * @param newCompany New company name, or null if company is not being updated.
     * @param newRole New role title, or null if role is not being updated.
     * @param newStatus New recruitment status string label, or null if status is not being updated.
     */
    public EditCommand(int targetIndex, String newCompany, String newRole, String newStatus) {
        this(targetIndex, newCompany, newRole,
                (newStatus != null && !newStatus.trim().isEmpty())
                        ? RecruitmentStage.fromLabel(newStatus.trim()) : null,
                null, null, null, null);
    }

    /**
     * Constructs an EditCommand with all editable fields using RecruitmentStage enum.
     *
     * @param targetIndex 0-based index of the application to edit.
     * @param newCompany New company name, or null if company is not being updated.
     * @param newRole New role title, or null if role is not being updated.
     * @param newStage New recruitment stage, or null if stage is not being updated.
     * @param newDeadline New deadline, or null if deadline is not being updated.
     * @param newPortalLink New portal link, or null if portal link is not being updated.
     * @param newEmail New contact email, or null if email is not being updated.
     * @param newNotes New freeform notes, or null if notes are not being updated.
     */
    public EditCommand(int targetIndex, String newCompany, String newRole, RecruitmentStage newStage,
                       LocalDate newDeadline, String newPortalLink, String newEmail, String newNotes) {
        this.targetIndex = targetIndex;
        this.newCompany = (newCompany != null && !newCompany.trim().isEmpty()) ? newCompany.trim() : null;
        this.newRole = (newRole != null && !newRole.trim().isEmpty()) ? newRole.trim() : null;
        this.newStage = newStage;
        this.newDeadline = newDeadline;
        this.newPortalLink = newPortalLink != null ? newPortalLink.trim() : null;
        this.newEmail = newEmail != null ? newEmail.trim() : null;
        this.newNotes = newNotes != null ? newNotes.trim() : null;
        this.isDeadlineUpdated = newDeadline != null;
    }

    /**
     * Constructs an EditCommand with all editable fields including portal link, email, and notes.
     *
     * @param targetIndex 0-based index of the application to edit.
     * @param newCompany New company name, or null if company is not being updated.
     * @param newRole New role title, or null if role is not being updated.
     * @param newStatus New recruitment status, or null if status is not being updated.
     * @param newDeadline New deadline, or null if deadline is not being updated.
     * @param newPortalLink New portal link, or null if portal link is not being updated.
     * @param newEmail New contact email, or null if email is not being updated.
     * @param newNotes New freeform notes, or null if notes are not being updated.
     */
    public EditCommand(int targetIndex, String newCompany, String newRole, String newStatus,
                       LocalDate newDeadline, String newPortalLink, String newEmail, String newNotes) {
        this(targetIndex, newCompany, newRole,
                (newStatus != null && !newStatus.trim().isEmpty())
                        ? RecruitmentStage.fromLabel(newStatus.trim()) : null,
                newDeadline, newPortalLink, newEmail, newNotes);
    }

    /** Creates an EditCommand updating only the company name. */
    public static EditCommand editCompany(int targetIndex, String company) {
        return new EditCommand(targetIndex, company, null, (RecruitmentStage) null, null, null, null, null);
    }

    /** Creates an EditCommand updating only the role title. */
    public static EditCommand editRole(int targetIndex, String role) {
        return new EditCommand(targetIndex, null, role, (RecruitmentStage) null, null, null, null, null);
    }

    /** Creates an EditCommand updating only the company portal link. */
    public static EditCommand editPortalLink(int targetIndex, String portalLink) {
        return new EditCommand(targetIndex, null, null, (RecruitmentStage) null, null, portalLink, null, null);
    }

    /** Creates an EditCommand updating only the contact email. */
    public static EditCommand editEmail(int targetIndex, String email) {
        return new EditCommand(targetIndex, null, null, (RecruitmentStage) null, null, null, email, null);
    }

    /** Creates an EditCommand updating only the freeform notes. */
    public static EditCommand editNotes(int targetIndex, String notes) {
        return new EditCommand(targetIndex, null, null, (RecruitmentStage) null, null, null, null, notes);
    }

    /** Creates an EditCommand updating only the deadline. */
    public static EditCommand editDeadline(int targetIndex, LocalDate deadline) {
        EditCommand command = new EditCommand(targetIndex, null, null, (RecruitmentStage) null,
                deadline, null, null, null);
        command.isDeadlineUpdated = true;
        return command;
    }

    /** Creates an EditCommand updating all fields of an application with RecruitmentStage. */
    public static EditCommand editAll(int targetIndex, String company, String role, RecruitmentStage stage,
                                      LocalDate deadline, String portalLink, String email, String notes) {
        EditCommand command = new EditCommand(targetIndex, company, role, stage, deadline, portalLink, email, notes);
        command.isDeadlineUpdated = true;
        return command;
    }

    /** Creates an EditCommand updating all fields of an application with status string. */
    public static EditCommand editAll(int targetIndex, String company, String role, String status,
                                      LocalDate deadline, String portalLink, String email, String notes) {
        EditCommand command = new EditCommand(targetIndex, company, role, status, deadline, portalLink, email, notes);
        command.isDeadlineUpdated = true;
        return command;
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

    public String getNewStatus() {
        return newStage != null ? newStage.getLabel() : null;
    }

    public LocalDate getNewDeadline() {
        return newDeadline;
    }

    public String getNewPortalLink() {
        return newPortalLink;
    }

    public String getNewEmail() {
        return newEmail;
    }

    public String getNewNotes() {
        return newNotes;
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

        if (newCompany == null && newRole == null && newStage == null
                && !isDeadlineUpdated && newPortalLink == null && newEmail == null && newNotes == null) {
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
        previousDeadline = application.getDeadline();
        previousPortalLink = application.getPortalLink();
        previousEmail = application.getEmail();
        previousNotes = application.getNotes();
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
        if (isDeadlineUpdated) {
            application.setDeadline(newDeadline);
        }
        if (newPortalLink != null) {
            application.setPortalLink(newPortalLink);
        }
        if (newEmail != null) {
            application.setEmail(newEmail);
        }
        if (newNotes != null) {
            application.setNotes(newNotes);
        }

        assert !application.getCompany().isEmpty() : "Company should not be empty after edit";
        assert !application.getRole().isEmpty() : "Role should not be empty after edit";

        StringBuilder message = new StringBuilder("Application updated successfully.\n");
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
        ui.showMessage(message.toString());

        storage.save(applications);
    }

    /** Restores all fields the application had before this command ran. */
    @Override
    public String undo(ApplicationList applications) {
        editedApplication.setCompany(previousCompany);
        editedApplication.setRole(previousRole);
        editedApplication.setRecruitmentStage(previousStage);
        editedApplication.setDeadline(previousDeadline);
        editedApplication.setPortalLink(previousPortalLink);
        editedApplication.setEmail(previousEmail);
        editedApplication.setNotes(previousNotes);
        return "edit of " + editedApplication;
    }
}
