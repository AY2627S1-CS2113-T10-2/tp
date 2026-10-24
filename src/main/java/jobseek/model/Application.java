package jobseek.model;

/**
 * Represents a single job or internship application record.
 */
public class Application {
    public static final String DEFAULT_STATUS = "Pending";

    private String company;
    private String role;
    private boolean isCompleted;
    private String status;

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

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        assert company != null && !company.trim().isEmpty() : "Company cannot be null or empty";
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        assert role != null && !role.trim().isEmpty() : "Role cannot be null or empty";
        this.role = role;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        assert status != null && !status.trim().isEmpty() : "Status cannot be null or empty";
        this.status = status.trim();
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
