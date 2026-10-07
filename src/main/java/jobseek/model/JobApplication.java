package jobseek.model;

/**
 * Represents an internship or job application.
 */
public class JobApplication {
    private String company;
    private String role;

    /**
     * Constructs a new JobApplication with the specified company and role.
     *
     * @param company The company applied to.
     * @param role The role or job title applied for.
     */
    public JobApplication(String company, String role) {
        this.company = company;
        this.role = role;
    }

    public String getCompany() {
        return company;
    }

    public String getRole() {
        return role;
    }
}
