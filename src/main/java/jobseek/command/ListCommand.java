package jobseek.command;

import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

import java.util.ArrayList;

/**
 * Displays saved applications in a numbered table, with optional filtering by completion status.
 * By default, only active (uncompleted) applications are shown.
 */
public class ListCommand extends Command {
    private static final int STATUS_WIDTH = "Status".length();

    /**
     * Filter criteria for displaying applications.
     */
    public enum FilterType {
        ACTIVE,
        COMPLETED,
        ALL
    }

    private final FilterType filterType;

    /**
     * Constructs a default ListCommand that filters for active (uncompleted) applications.
     */
    public ListCommand() {
        this(FilterType.ACTIVE);
    }

    /**
     * Constructs a ListCommand with a specified FilterType.
     *
     * @param filterType Filter criteria (ACTIVE, COMPLETED, or ALL).
     */
    public ListCommand(FilterType filterType) {
        this.filterType = filterType != null ? filterType : FilterType.ACTIVE;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    /**
     * Parses the optional argument following 'list' into a corresponding ListCommand instance.
     *
     * @param args The optional arguments string (e.g. "", "completed", "all").
     * @return A ListCommand configured with the requested filter.
     * @throws JobSeekException If the filter argument is unrecognized.
     */
    public static ListCommand parse(String args) throws JobSeekException {
        if (args == null || args.trim().isEmpty()) {
            return new ListCommand(FilterType.ACTIVE);
        }

        String trimmed = args.trim().toLowerCase();
        switch (trimmed) {
        case "completed":
            return new ListCommand(FilterType.COMPLETED);
        case "all":
            return new ListCommand(FilterType.ALL);
        default:
            throw new JobSeekException("Invalid list filter: '" + args.trim()
                    + "'. Usage: list [completed|all]");
        }
    }

    /**
     * Shows a table containing applications matching the filter criteria.
     * Preserves each application's original one-based index from the list.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for displaying the list.
     * @param storage Storage instance required by the command interface but not used here.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) {
        if (applications.isEmpty()) {
            ui.showMessage("No applications found.");
            return;
        }

        ArrayList<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            if (matchesFilter(app)) {
                matchingIndices.add(i);
            }
        }

        if (matchingIndices.isEmpty()) {
            switch (filterType) {
            case COMPLETED:
                ui.showMessage("No completed applications found.");
                break;
            case ACTIVE:
                ui.showMessage("No active applications found.");
                break;
            case ALL:
            default:
                ui.showMessage("No applications found.");
                break;
            }
            return;
        }

        int numberWidth = Math.max("No.".length(), Integer.toString(applications.size()).length());
        int companyWidth = "Company".length();
        int roleWidth = "Role".length();
        for (int index : matchingIndices) {
            Application app = applications.get(index);
            companyWidth = Math.max(companyWidth, app.getCompany().length());
            roleWidth = Math.max(roleWidth, app.getRole().length());
        }

        String rowFormat = "%-" + numberWidth + "s | %-" + STATUS_WIDTH + "s | %-"
                + companyWidth + "s | %s";
        StringBuilder table = new StringBuilder("Here are your applications:\n");
        table.append(String.format(rowFormat, "No.", "Status", "Company", "Role"));
        table.append('\n').append("-".repeat(numberWidth))
                .append("-+-").append("-".repeat(STATUS_WIDTH))
                .append("-+-").append("-".repeat(companyWidth))
                .append("-+-").append("-".repeat(roleWidth));

        for (int index : matchingIndices) {
            Application app = applications.get(index);
            table.append('\n').append(String.format(rowFormat, Integer.toString(index + 1),
                    app.getStatusIcon(), app.getCompany(), app.getRole()));
        }

        ui.showMessage(table.toString());
    }

    private boolean matchesFilter(Application app) {
        switch (filterType) {
        case COMPLETED:
            return app.isCompleted();
        case ACTIVE:
            return !app.isCompleted();
        case ALL:
        default:
            return true;
        }
    }
}
