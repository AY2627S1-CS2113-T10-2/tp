package jobseek.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jobseek.model.JobApplication;

/**
 * Manages the collection of job applications and application operations.
 */
public class ApplicationManager {
    private final List<JobApplication> applications;

    /**
     * Constructs an ApplicationManager with an empty application list.
     */
    public ApplicationManager() {
        this.applications = new ArrayList<>();
    }

    /**
     * Constructs an ApplicationManager initialized with existing applications.
     *
     * @param initialApplications Initial list of applications.
     */
    public ApplicationManager(List<JobApplication> initialApplications) {
        this.applications = new ArrayList<>();
        if (initialApplications != null) {
            this.applications.addAll(initialApplications);
        }
    }

    /**
     * Adds a new application to the list after validating inputs.
     *
     * @param company The company name (must not be empty/blank).
     * @param role The role name (must not be empty/blank).
     * @return The newly created and added {@link JobApplication}.
     * @throws IllegalArgumentException If company or role is empty or blank.
     */
    public JobApplication addApplication(String company, String role) {
        if (company == null || company.trim().isEmpty()) {
            throw new IllegalArgumentException("Company cannot be empty.");
        }
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role cannot be empty.");
        }

        JobApplication application = new JobApplication(company.trim(), role.trim());
        applications.add(application);
        return application;
    }

    /**
     * Deletes an application at the specified 1-based index.
     *
     * @param index The 1-based index of the application to delete.
     * @return The deleted {@link JobApplication}.
     * @throws IndexOutOfBoundsException If the index is less than 1 or exceeds list size.
     */
    public JobApplication deleteApplication(int index) {
        if (index < 1 || index > applications.size()) {
            throw new IndexOutOfBoundsException("Invalid application index.");
        }
        return applications.remove(index - 1);
    }

    /**
     * Returns an unmodifiable view of the application list in insertion order.
     *
     * @return List of job applications.
     */
    public List<JobApplication> getApplications() {
        return Collections.unmodifiableList(applications);
    }

    /**
     * Returns the total count of applications.
     *
     * @return Total application count.
     */
    public int getApplicationCount() {
        return applications.size();
    }
}
