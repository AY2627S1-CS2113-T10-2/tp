package jobseek.command;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.TreeMap;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Shows a "what needs my attention today?" dashboard: overdue, due-today and upcoming deadlines
 * of active applications, followed by a short summary of the whole pipeline.
 * Unlike the startup warnings, the dashboard can be requested at any time with {@code today}.
 */
public class TodayCommand extends Command {
    private static final int UPCOMING_WINDOW_DAYS = 7;

    private final LocalDate today;

    /** Constructs a TodayCommand that uses the system date. */
    public TodayCommand() {
        this(LocalDate.now());
    }

    /**
     * Constructs a TodayCommand for a fixed date, which makes the output deterministic in tests.
     *
     * @param today Date treated as "today" when classifying deadlines.
     */
    public TodayCommand(LocalDate today) {
        assert today != null : "Today's date should not be null";
        this.today = today;
    }

    /**
     * Builds and displays the dashboard. This command only reads data, so nothing is saved.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface used to display the dashboard.
     * @param storage Storage instance required by the command interface but not used here.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) {
        assert applications != null : "Applications list cannot be null";
        assert ui != null : "Ui instance cannot be null";

        StringBuilder overdue = new StringBuilder();
        StringBuilder dueToday = new StringBuilder();
        StringBuilder upcoming = new StringBuilder();
        int overdueCount = 0;
        int dueTodayCount = 0;
        int upcomingCount = 0;
        int activeCount = 0;
        int noDeadlineCount = 0;
        Map<String, Integer> activeByStage = new TreeMap<>();

        for (int i = 0; i < applications.size(); i++) {
            Application application = applications.get(i);
            if (application.isCompleted()) {
                continue;
            }
            activeCount++;
            activeByStage.merge(application.getRecruitmentStage().getLabel(), 1, Integer::sum);

            LocalDate deadline = application.getDeadline();
            if (deadline == null) {
                noDeadlineCount++;
                continue;
            }
            long daysUntil = ChronoUnit.DAYS.between(today, deadline);
            if (daysUntil < 0) {
                overdueCount++;
                appendEntry(overdue, i, application, deadline, -daysUntil + (-daysUntil == 1 ? " day" : " days")
                        + " overdue");
            } else if (daysUntil == 0) {
                dueTodayCount++;
                appendEntry(dueToday, i, application, deadline, "due today");
            } else if (daysUntil <= UPCOMING_WINDOW_DAYS) {
                upcomingCount++;
                appendEntry(upcoming, i, application, deadline, "in " + daysUntil
                        + (daysUntil == 1 ? " day" : " days"));
            }
        }

        StringBuilder dashboard = new StringBuilder("Today: " + today + "\n");
        appendSection(dashboard, "OVERDUE", overdueCount, overdue);
        appendSection(dashboard, "DUE TODAY", dueTodayCount, dueToday);
        appendSection(dashboard, "UPCOMING (next " + UPCOMING_WINDOW_DAYS + " days)", upcomingCount, upcoming);
        appendSummary(dashboard, applications.size(), activeCount, noDeadlineCount, activeByStage);
        ui.showMessage(dashboard.toString().trim());
    }

    /** Appends one application line, keeping the one-based index that other commands use. */
    private void appendEntry(StringBuilder section, int zeroBasedIndex, Application application,
            LocalDate deadline, String timing) {
        section.append("  ").append(zeroBasedIndex + 1).append(". ")
                .append(application.getCompany()).append(" | ").append(application.getRole())
                .append(" - ").append(deadline).append(" (").append(timing).append(")\n");
    }

    /** Appends a titled section, or "none" when the section has no entries. */
    private void appendSection(StringBuilder dashboard, String title, int count, StringBuilder entries) {
        dashboard.append('\n').append(title).append(" (").append(count).append(")\n");
        dashboard.append(count == 0 ? "  none\n" : entries.toString());
    }

    /** Appends pipeline totals so the user also sees applications that have no deadline pressure. */
    private void appendSummary(StringBuilder dashboard, int total, int activeCount, int noDeadlineCount,
            Map<String, Integer> activeByStage) {
        dashboard.append("\nSUMMARY\n");
        dashboard.append("  ").append(activeCount).append(" active, ")
                .append(total - activeCount).append(" completed, ")
                .append(noDeadlineCount).append(" active without a deadline\n");
        if (!activeByStage.isEmpty()) {
            StringBuilder statuses = new StringBuilder();
            for (Map.Entry<String, Integer> entry : activeByStage.entrySet()) {
                if (statuses.length() > 0) {
                    statuses.append(", ");
                }
                statuses.append(entry.getKey()).append(": ").append(entry.getValue());
            }
            dashboard.append("  Active by recruitment stage - ").append(statuses).append('\n');
        }
    }
}
