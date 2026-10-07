package jobseek.model;

/**
 * Represents a single job or internship application record.
 */
public class Application {
    private String company;
    private String role;
    private boolean isCompleted;

    /**
     * Constructs an Application with the specified company and role.
     * By default, the application status is set to incomplete (false).
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     */
    public Application(String company, String role) {
        this(company, role, false);
    }

    /**
     * Constructs an Application with the specified company, role, and completion status.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param isCompleted True if the application process is completed, false otherwise.
     */
    public Application(String company, String role, boolean isCompleted) {
        this.company = company;
        this.role = role;
        this.isCompleted = isCompleted;
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
