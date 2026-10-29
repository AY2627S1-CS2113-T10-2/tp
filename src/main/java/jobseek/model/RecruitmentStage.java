package jobseek.model;

import java.util.Locale;

/**
 * The named stages an application can move through during recruitment.
 */
public enum RecruitmentStage {
    APPLIED("Applied", false),
    ONLINE_ASSESSMENT("Online Assessment", false),
    INTERVIEW("Interview", false),
    OFFER("Offer", false),
    ACCEPTED("Accepted", true),
    REJECTED("Rejected", true),
    WITHDRAWN("Withdrawn", true);

    private final String label;
    private final boolean terminal;

    RecruitmentStage(String label, boolean terminal) {
        this.label = label;
        this.terminal = terminal;
    }

    /** Returns the human-readable label shown in the CLI and data file. */
    public String getLabel() {
        return label;
    }

    /** Returns whether the hiring process has reached an ending stage. */
    public boolean isTerminal() {
        return terminal;
    }

    /**
     * Parses a stage name while accepting labels saved by older JobSeek versions.
     *
     * @param value Human-readable stage label or supported shortcut.
     * @return The matching recruitment stage.
     * @throws IllegalArgumentException If the value is not a known stage.
     */
    public static RecruitmentStage fromLabel(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Recruitment stage cannot be blank.");
        }

        String normalized = value.trim().toLowerCase(Locale.ROOT).replaceAll("[\\s_-]+", "");
        switch (normalized) {
        case "applied":
        case "pending": // Migrate the previous default status to the more specific stage.
            return APPLIED;
        case "onlineassessment":
        case "oa":
            return ONLINE_ASSESSMENT;
        case "interview":
            return INTERVIEW;
        case "offer":
            return OFFER;
        case "accepted":
        case "offeraccepted":
            return ACCEPTED;
        case "rejected":
            return REJECTED;
        case "withdrawn":
            return WITHDRAWN;
        default:
            throw new IllegalArgumentException("Unknown recruitment stage: " + value);
        }
    }
}
