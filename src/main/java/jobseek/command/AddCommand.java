package jobseek.command;

import java.io.IOException;
import java.time.LocalDate;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles adding a new job or internship application to the list.
 */
public class AddCommand extends UndoableCommand {
    private final String company;
    private final String role;
    private final LocalDate deadline;
    private final String portalLink;
    private final String email;
    private final String notes;
    private Application addedApplication;

    /**
     * Constructs an AddCommand with the given company and role.
     *
     * @param company Company name for the application.
     * @param role Role name for the application.
     */
    public AddCommand(String company, String role) {
        this(company, role, null, "", "", "");
    }

    /**
     * Constructs an add command with an optional deadline.
     *
     * @param company Company name for the application.
     * @param role Role name for the application.
     * @param deadline Deadline for the next required action, if known.
     */
    public AddCommand(String company, String role, LocalDate deadline) {
        this(company, role, deadline, "", "", "");
    }

    /**
     * Constructs an add command with all optional fields including portal link, email, and notes.
     *
     * @param company Company name for the application.
     * @param role Role name for the application.
     * @param deadline Deadline for the next required action, if known.
     * @param portalLink Web link to the company's application portal.
     * @param email Contact email for the recruiter or application.
     * @param notes Additional freeform notes about the application.
     */
    public AddCommand(String company, String role, LocalDate deadline,
                      String portalLink, String email, String notes) {
        this.company = company != null ? company.trim() : "";
        this.role = role != null ? role.trim() : "";
        this.deadline = deadline;
        this.portalLink = portalLink != null ? portalLink.trim() : "";
        this.email = email != null ? email.trim() : "";
        this.notes = notes != null ? notes.trim() : "";
    }

    /** Returns the company name entered for this command. */
    public String getCompany() {
        return company;
    }

    /** Returns the role title entered for this command. */
    public String getRole() {
        return role;
    }

    /** Returns the optional deadline entered for this command. */
    public LocalDate getDeadline() {
        return deadline;
    }

    /** Returns the optional portal link entered for this command. */
    public String getPortalLink() {
        return portalLink;
    }

    /** Returns the optional email entered for this command. */
    public String getEmail() {
        return email;
    }

    /** Returns the optional notes entered for this command. */
    public String getNotes() {
        return notes;
    }

    /**
     * Executes the add command by validating inputs, creating a new application,
     * adding it to the application list, and saving the updated list to storage.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for user feedback.
     * @param storage Storage instance to persist the updated list.
     * @throws Exception If an error occurs during saving.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws IOException {
        assert applications != null : "Applications list cannot be null";
        assert ui != null : "Ui instance cannot be null";
        assert storage != null : "Storage instance cannot be null";

        if (company.isEmpty()) {
            ui.showError("Company cannot be empty.");
            return;
        }
        if (role.isEmpty()) {
            ui.showError("Role cannot be empty.");
            return;
        }

        int sizeBefore = applications.size();
        Application application = new Application(company, role, false, Application.DEFAULT_STAGE,
                deadline, portalLink, email, notes);
        applications.add(application);
        addedApplication = application;
        markChanged();

        assert applications.size() == sizeBefore + 1 : "Adding one application should increase list size by one";
        assert applications.get(applications.size() - 1) == application
                : "Last element in list should be newly added application";

        StringBuilder message = new StringBuilder("Application added successfully.\n");
        message.append("Company: ").append(application.getCompany()).append("\n");
        message.append("Role: ").append(application.getRole());
        if (deadline != null) {
            message.append("\nDeadline: ").append(deadline);
        }
        if (!portalLink.isEmpty()) {
            message.append("\nPortal Link: ").append(portalLink);
        }
        if (!email.isEmpty()) {
            message.append("\nEmail: ").append(email);
        }
        if (!notes.isEmpty()) {
            message.append("\nNotes: ").append(notes);
        }
        ui.showMessage(message.toString());

        storage.save(applications);
    }

    /** Removes the application this command added. */
    @Override
    public String undo(ApplicationList applications) {
        // Compare by identity: two applications can have identical text but only one was added here.
        for (int i = applications.size() - 1; i >= 0; i--) {
            if (applications.get(i) == addedApplication) {
                applications.delete(i);
                break;
            }
        }
        return "added " + addedApplication;
    }
}
