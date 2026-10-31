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

    /** The prompt message displaying all available numeric choices and stage names. */
    public static final String SELECTION_PROMPT =
            "Enter recruitment stage (1: Applied, 2: Online Assessment, 3: Interview, "
            + "4: Offer, 5: Accepted, 6: Rejected, 7: Withdrawn):\n> ";

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
     * Resolves a user selection—either a 1-based numeric option ("1" to "7") or a stage name/shortcut—
     * into the corresponding RecruitmentStage.
     *
     * @param choice Numeric menu option or stage name.
     * @return The resolved RecruitmentStage.
     * @throws IllegalArgumentException If the choice does not correspond to a valid stage.
     */
    public static RecruitmentStage fromChoice(String choice) {
        if (choice == null || choice.isBlank()) {
            throw new IllegalArgumentException("Recruitment stage cannot be blank.");
        }
        String trimmed = choice.trim();
        switch (trimmed) {
        case "1":
            return APPLIED;
        case "2":
            return ONLINE_ASSESSMENT;
        case "3":
            return INTERVIEW;
        case "4":
            return OFFER;
        case "5":
            return ACCEPTED;
        case "6":
            return REJECTED;
        case "7":
            return WITHDRAWN;
        default:
            return fromLabel(trimmed);
        }
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
