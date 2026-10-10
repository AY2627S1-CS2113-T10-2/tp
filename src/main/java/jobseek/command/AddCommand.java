package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles adding a new job or internship application to the list.
 */
public class AddCommand extends Command {
    private final String company;
    private final String role;

    /**
     * Constructs an AddCommand with the given company and role.
     *
     * @param company Company name for the application.
     * @param role Role name for the application.
     */
    public AddCommand(String company, String role) {
        this.company = company != null ? company.trim() : "";
        this.role = role != null ? role.trim() : "";
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
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
        if (company.isEmpty()) {
            ui.showError("Company cannot be empty.");
            return;
        }
        if (role.isEmpty()) {
            ui.showError("Role cannot be empty.");
            return;
        }

        Application application = new Application(company, role);
        applications.add(application);

        String message = "Application added successfully.\n"
                + "Company: " + application.getCompany() + "\n"
                + "Role: " + application.getRole();
        ui.showMessage(message);

        storage.save(applications);
    }
}
