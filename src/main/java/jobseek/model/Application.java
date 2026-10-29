package jobseek.model;

import java.time.LocalDate;

/**
 * Represents a single job or internship application record.
 */
public class Application {
    public static final RecruitmentStage DEFAULT_STAGE = RecruitmentStage.APPLIED;

    private String company;
    private String role;
    private boolean isCompleted;
    private RecruitmentStage recruitmentStage;
    private LocalDate deadline;

    /**
     * Constructs an Application with the specified company and role.
     * By default, the application is set to incomplete (false) and stage to "Applied".
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     */
    public Application(String company, String role) {
        this(company, role, false, DEFAULT_STAGE, null);
    }

    /**
     * Constructs an Application with the specified company, role, and completion status.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param isCompleted True if the application process is completed, false otherwise.
     */
    public Application(String company, String role, boolean isCompleted) {
        this(company, role, isCompleted, DEFAULT_STAGE, null);
    }

    /**
     * Constructs an Application with the specified company, role, completion state, and recruitment stage.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param isCompleted True if the application process is completed, false otherwise.
     * @param recruitmentStage Current recruitment stage.
     */
    public Application(String company, String role, boolean isCompleted, RecruitmentStage recruitmentStage) {
        this(company, role, isCompleted, recruitmentStage, null);
    }

    /** Creates an application from a user-facing recruitment stage label. */
    public Application(String company, String role, boolean isCompleted, String recruitmentStageLabel) {
        this(company, role, isCompleted, parseStageOrDefault(recruitmentStageLabel), null);
    }

    /**
     * Constructs an Application with the specified company, role, and recruitment stage.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param recruitmentStage Current recruitment stage.
     */
    public Application(String company, String role, RecruitmentStage recruitmentStage) {
        this(company, role, false, recruitmentStage, null);
    }

    /** Creates an application from a user-facing recruitment stage label. */
    public Application(String company, String role, String recruitmentStageLabel) {
        this(company, role, false, parseStageOrDefault(recruitmentStageLabel), null);
    }

    /**
     * Constructs an Application with an optional deadline.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param isCompleted Whether the application is completed.
     * @param deadline Deadline for the next required action, or null when none is known.
     */
    public Application(String company, String role, boolean isCompleted, LocalDate deadline) {
        this(company, role, isCompleted, DEFAULT_STAGE, deadline);
    }

    /**
     * Constructs an Application with company, role, completion state, recruitment stage, and deadline.
     *
     * @param company Name of the company applied to.
     * @param role Title of the job or internship role.
     * @param isCompleted Whether the application is completed.
     * @param recruitmentStage Current recruitment stage.
     * @param deadline Deadline for the next required action, or null when none is known.
     */
    public Application(String company, String role, boolean isCompleted, RecruitmentStage recruitmentStage,
            LocalDate deadline) {
        assert company != null : "Company should not be null";
        assert role != null : "Role should not be null";
        this.company = company;
        this.role = role;
        this.isCompleted = isCompleted;
        this.recruitmentStage = recruitmentStage != null ? recruitmentStage : DEFAULT_STAGE;
        this.deadline = deadline;
    }

    /** Creates an application from a saved or user-facing recruitment stage label. */
    public Application(String company, String role, boolean isCompleted, String recruitmentStageLabel,
            LocalDate deadline) {
        this(company, role, isCompleted, parseStageOrDefault(recruitmentStageLabel), deadline);
    }

    /** Converts a stage label to its enum value, using Applied for blank legacy fields. */
    private static RecruitmentStage parseStageOrDefault(String recruitmentStageLabel) {
        return recruitmentStageLabel == null || recruitmentStageLabel.isBlank()
                ? DEFAULT_STAGE : RecruitmentStage.fromLabel(recruitmentStageLabel);
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

    /** Returns the typed recruitment stage for this application. */
    public RecruitmentStage getRecruitmentStage() {
        return recruitmentStage;
    }

    /** Sets the typed recruitment stage for this application. */
    public void setRecruitmentStage(RecruitmentStage recruitmentStage) {
        assert recruitmentStage != null : "Recruitment stage cannot be null";
        this.recruitmentStage = recruitmentStage != null ? recruitmentStage : DEFAULT_STAGE;
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
     * Returns the completion icon indicating whether the application is completed.
     *
     * @return "[X]" if completed, "[ ]" otherwise.
     */
    public String getCompletionIcon() {
        return isCompleted ? "[X]" : "[ ]";
    }

    /**
     * Returns a formatted string representation of the application.
     * Format: [completion icon] Company | Role
     *
     * @return Formatted application string (e.g. "[ ] Acme | SWE Intern").
     */
    @Override
    public String toString() {
        return getCompletionIcon() + " " + company + " | " + role;
    }
}
