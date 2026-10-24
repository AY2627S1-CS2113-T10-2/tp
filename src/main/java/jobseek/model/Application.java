package jobseek.model;

import java.time.LocalDate;

/**
 * Represents a single job or internship application record.
 */
public class Application {
    public static final String DEFAULT_STATUS = "Pending";

    private String company;
    private String role;
    private boolean isCompleted;
    private LocalDate deadline;

    /**
     * Constructs an Application with the specified company and role.
     * By default, the application is set to incomplete (false) and status to "Pending".
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     */
    public Application(String company, String role) {
        this(company, role, false, DEFAULT_STATUS);
    }

    /**
     * Constructs an Application with the specified company, role, and completion status.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param isCompleted True if the application process is completed, false otherwise.
     */
    public Application(String company, String role, boolean isCompleted) {
        this(company, role, isCompleted, DEFAULT_STATUS);
    }

    /**
     * Constructs an Application with the specified company, role, completion status, and recruitment status.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param isCompleted True if the application process is completed, false otherwise.
     * @param status Current recruitment status indicator (e.g. "Pending", "Accepted", "Rejected").
     */
    public Application(String company, String role, boolean isCompleted, String status) {
        assert company != null : "Company should not be null";
        assert role != null : "Role should not be null";
        this.company = company;
        this.role = role;
        this.isCompleted = isCompleted;
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : DEFAULT_STATUS;
    }

    /**
     * Constructs an Application with the specified company, role, and recruitment status.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param status Current recruitment status indicator.
     */
    public Application(String company, String role, String status) {
        this(company, role, false, status);
    }

    /** Returns the company name for this application. */
    public String getCompany() {
        return company;
    }

    /**
     * Updates the company name for this application.
     *
     * @param company New company name.
     */
    public void setCompany(String company) {
        assert company != null && !company.trim().isEmpty() : "Company cannot be null or empty";
        this.company = company;
    }

    /** Returns the role title for this application. */
    public String getRole() {
        return role;
    }

    /**
     * Updates the role title for this application.
     *
     * @param role New role title.
     */
    public void setRole(String role) {
        assert role != null && !role.trim().isEmpty() : "Role cannot be null or empty";
        this.role = role;
    }

    /** Returns whether the application process is marked complete. */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * Updates whether the application process is complete.
     *
     * @param isCompleted True to mark complete, or false to reactivate it.
     */
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getStatus() {
        return status != null ? status : DEFAULT_STATUS;
    }

    public void setStatus(String status) {
        assert status != null && !status.trim().isEmpty() : "Status cannot be null or empty";
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : DEFAULT_STATUS;
    }

    /**
     * Returns the application's optional deadline.
     *
     * @return The deadline, or null if no deadline was recorded.
     */
    public LocalDate getDeadline() {
        return deadline;
    }

    /**
     * Updates the deadline for the application's next action.
     *
     * @param deadline The new deadline, or null to remove it.
     */
    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    /**
     * Returns the status icon indicating whether the application is completed.
     *
     * @return "[X]" if completed, "[ ]" otherwise.
     */
    public String getStatusIcon() {
        return isCompleted ? "[X]" : "[ ]";
    }

    /**
     * Returns a formatted string representation of the application.
     * Format: [status] Company | Role
     *
     * @return Formatted application string (e.g. "[ ] Acme | SWE Intern").
     */
    @Override
    public String toString() {
        return getStatusIcon() + " " + company + " | " + role;
    }
}
