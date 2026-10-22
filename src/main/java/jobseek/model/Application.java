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
     * By default, the application status is set to incomplete (false) and status to "Pending".
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
     * Constructs an Application with the specified company, role, completion status, and status string.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param isCompleted True if the application process is completed, false otherwise.
     * @param status Current application recruitment stage (e.g., Pending, Accepted, Rejected).
     */
    public Application(String company, String role, boolean isCompleted, String status) {
        this.company = company;
        this.role = role;
        this.isCompleted = isCompleted;
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : DEFAULT_STATUS;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    public String getStatus() {
        return status != null ? status : DEFAULT_STATUS;
    }

    public void setStatus(String status) {
        this.status = (status != null && !status.trim().isEmpty()) ? status.trim() : DEFAULT_STATUS;
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
