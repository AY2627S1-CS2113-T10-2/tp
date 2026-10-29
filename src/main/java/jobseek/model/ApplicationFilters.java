package jobseek.model;

import java.time.LocalDate;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * Ready-made filters that decide whether an {@link Application} should be shown.
 *
 * <p>Each method returns a {@link Predicate}, so filters can be combined with {@code and(...)}.
 * For example, {@code companyContains("google").and(inStage(RecruitmentStage.INTERVIEW))}
 * matches only Google applications that are at the interview stage.
 */
public final class ApplicationFilters {

    private ApplicationFilters() {
        // Utility class: all methods are static.
    }

    /** Matches applications whose company contains the text, ignoring case. */
    public static Predicate<Application> companyContains(String text) {
        String lowered = text.toLowerCase(Locale.ROOT);
        return application -> application.getCompany().toLowerCase(Locale.ROOT).contains(lowered);
    }

    /** Matches applications whose role contains the text, ignoring case. */
    public static Predicate<Application> roleContains(String text) {
        String lowered = text.toLowerCase(Locale.ROOT);
        return application -> application.getRole().toLowerCase(Locale.ROOT).contains(lowered);
    }

    /** Matches applications currently at the given recruitment stage. */
    public static Predicate<Application> inStage(RecruitmentStage stage) {
        return application -> application.getRecruitmentStage() == stage;
    }

    /** Matches applications with a deadline on or before the date; applications without a deadline never match. */
    public static Predicate<Application> deadlineOnOrBefore(LocalDate date) {
        return application -> application.getDeadline() != null && !application.getDeadline().isAfter(date);
    }

    /** Matches applications with a deadline on or after the date; applications without a deadline never match. */
    public static Predicate<Application> deadlineOnOrAfter(LocalDate date) {
        return application -> application.getDeadline() != null && !application.getDeadline().isBefore(date);
    }

    /** Matches applications whose completed flag equals the given value. */
    public static Predicate<Application> isCompleted(boolean completed) {
        return application -> application.isCompleted() == completed;
    }
}
