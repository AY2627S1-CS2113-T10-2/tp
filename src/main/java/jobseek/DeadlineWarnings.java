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
     * Displays overdue, due-today, and near-term deadlines for active applications.
     *
     * @param applications Applications to inspect.
     * @param ui User interface used to display any warnings.
     */
    public void show(ApplicationList applications, Ui ui) {
        LocalDate today = LocalDate.now();
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
