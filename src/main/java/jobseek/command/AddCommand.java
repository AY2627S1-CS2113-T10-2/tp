package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

import java.time.LocalDate;

/**
 * Handles adding a new job or internship application to the list.
 */
public class AddCommand extends Command {
    private final String company;
    private final String role;
    private final LocalDate deadline;

    /**
     * Constructs an AddCommand with the given company and role.
     *
     * @param company Company name for the application.
     * @param role Role name for the application.
     */
    public AddCommand(String company, String role) {
        this(company, role, null);
    }

    /**
     * Constructs an add command with an optional deadline.
     *
     * @param company Company name for the application.
     * @param role Role name for the application.
     * @param deadline Deadline for the next required action, if known.
     */
    public AddCommand(String company, String role, LocalDate deadline) {
        this.company = company != null ? company.trim() : "";
        this.role = role != null ? role.trim() : "";
        this.deadline = deadline;
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
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws Exception {
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
        Application application = new Application(company, role, false, deadline);
        applications.add(application);

        assert applications.size() == sizeBefore + 1 : "Adding one application should increase list size by one";
        assert applications.get(applications.size() - 1) == application
                : "Last element in list should be newly added application";

        String message = "Application added successfully.\n"
                + "Company: " + application.getCompany() + "\n"
                + "Role: " + application.getRole()
                + (deadline == null ? "" : "\nDeadline: " + deadline);
        ui.showMessage(message);

        storage.save(applications);
    }
}
