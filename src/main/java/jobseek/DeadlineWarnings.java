package jobseek;

import java.time.LocalDate;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.ui.Ui;

/**
 * Finds and displays application deadlines that need the user's attention.
 */
public class DeadlineWarnings {
    private static final int UPCOMING_WINDOW_DAYS = 7;

    /**
     * Displays overdue, due-today, and near-term deadlines for active applications based on today's date.
     *
     * @param applications Applications to inspect.
     * @param ui User interface used to display any warnings.
     */
    public void show(ApplicationList applications, Ui ui) {
        show(applications, ui, LocalDate.now());
    }

    /**
     * Displays overdue, due-today, and near-term deadlines using an explicit reference date.
     *
     * @param applications Applications to inspect.
     * @param ui User interface used to display any warnings.
     * @param today Reference date for deadline calculations.
     */
    public void show(ApplicationList applications, Ui ui, LocalDate today) {
        assert today != null : "Reference date cannot be null";
        StringBuilder warnings = new StringBuilder();
        for (int i = 0; i < applications.size(); i++) {
            Application application = applications.get(i);
            LocalDate deadline = application.getDeadline();
            if (application.isCompleted() || deadline == null
                    || deadline.isAfter(today.plusDays(UPCOMING_WINDOW_DAYS))) {
                continue;
            }
            appendWarning(warnings, application, deadline, today);
        }
        if (!warnings.isEmpty()) {
            ui.showMessage("Deadline warnings:\n" + warnings.toString().trim());
        }
    }

    /**
     * Adds one formatted deadline warning to the startup summary.
     *
     * @param warnings Summary being built.
     * @param application Application associated with the deadline.
     * @param deadline Deadline to describe.
     * @param today Current date used to classify the deadline.
     */
    private void appendWarning(StringBuilder warnings, Application application, LocalDate deadline, LocalDate today) {
        if (deadline.isBefore(today)) {
            warnings.append("OVERDUE: ");
        } else if (deadline.isEqual(today)) {
            warnings.append("DUE TODAY: ");
        } else {
            warnings.append("UPCOMING: ");
        }
        warnings.append(application.getCompany()).append(" - ").append(application.getRole())
                .append(" (deadline ").append(deadline).append(")\n");
    }
}
