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
    private static final int COMPLETED_WIDTH = "Completed".length();

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
        int stageWidth = "Stage".length();
        int deadlineWidth = "Deadline".length();
        int portalWidth = "Portal Link".length();
        int emailWidth = "Email".length();
        int notesWidth = "Notes".length();
        for (int index : matchingIndices) {
            Application application = applications.get(index);
            companyWidth = Math.max(companyWidth, application.getCompany().length());
            roleWidth = Math.max(roleWidth, application.getRole().length());
            stageWidth = Math.max(stageWidth, stageText(application).length());
            deadlineWidth = Math.max(deadlineWidth, deadlineText(application).length());
            portalWidth = Math.max(portalWidth, portalLinkText(application).length());
            emailWidth = Math.max(emailWidth, emailText(application).length());
            notesWidth = Math.max(notesWidth, notesText(application).length());
        }

        String rowFormat = "%-" + numberWidth + "s | %-" + COMPLETED_WIDTH + "s | %-"
                + companyWidth + "s | %-" + roleWidth + "s | %-" + stageWidth + "s | %-"
                + deadlineWidth + "s | %-" + portalWidth + "s | %-" + emailWidth + "s | %-"
                + notesWidth + "s";
        StringBuilder table = new StringBuilder("Here are your applications:\n");
        table.append(String.format(rowFormat, "No.", "Completed", "Company", "Role", "Stage",
                "Deadline", "Portal Link", "Email", "Notes"));
        table.append('\n').append("-".repeat(numberWidth))
                .append("-+-").append("-".repeat(COMPLETED_WIDTH))
                .append("-+-").append("-".repeat(companyWidth))
                .append("-+-").append("-".repeat(roleWidth))
                .append("-+-").append("-".repeat(stageWidth))
                .append("-+-").append("-".repeat(deadlineWidth))
                .append("-+-").append("-".repeat(portalWidth))
                .append("-+-").append("-".repeat(emailWidth))
                .append("-+-").append("-".repeat(notesWidth));

        for (int index : matchingIndices) {
            Application application = applications.get(index);
            table.append('\n').append(String.format(rowFormat, Integer.toString(index + 1),
                    application.getCompletionIcon(), application.getCompany(), application.getRole(),
                    stageText(application), deadlineText(application), portalLinkText(application),
                    emailText(application), notesText(application)));
        }

        ui.showMessage(table.toString());
    }

    /** Returns a printable stage label, using a dash when none is recorded. */
    private String stageText(Application application) {
        return application.getRecruitmentStage() == null ? "-" : application.getRecruitmentStage().getLabel();
    }

    /** Returns a printable deadline value, using a dash when none is recorded. */
    private String deadlineText(Application application) {
        return application.getDeadline() == null ? "-" : application.getDeadline().toString();
    }

    /** Returns a printable portal link, using a dash when none is recorded. */
    private String portalLinkText(Application application) {
        String portal = application.getPortalLink();
        return portal == null || portal.isBlank() ? "-" : portal;
    }

    /** Returns a printable contact email, using a dash when none is recorded. */
    private String emailText(Application application) {
        String email = application.getEmail();
        return email == null || email.isBlank() ? "-" : email;
    }

    /** Returns printable notes, using a dash when none is recorded. */
    private String notesText(Application application) {
        String notes = application.getNotes();
        return notes == null || notes.isBlank() ? "-" : notes;
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
