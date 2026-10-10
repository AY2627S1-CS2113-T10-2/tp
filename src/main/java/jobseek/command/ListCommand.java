package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Displays all saved applications in a numbered table.
 * The displayed numbers are one-based so they match the numbers users enter for other commands.
 */
public class ListCommand extends Command {
    private static final int STATUS_WIDTH = "Status".length();

    /**
     * Shows an empty-list message or a table containing each application's status, company, and role.
     * Listing does not change the applications or save them to storage.
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

        int numberWidth = Math.max("No.".length(), Integer.toString(applications.size()).length());
        int companyWidth = "Company".length();
        int roleWidth = "Role".length();
        for (int i = 0; i < applications.size(); i++) {
            Application application = applications.get(i);
            companyWidth = Math.max(companyWidth, application.getCompany().length());
            roleWidth = Math.max(roleWidth, application.getRole().length());
        }

        String rowFormat = "%-" + numberWidth + "s | %-" + STATUS_WIDTH + "s | %-"
                + companyWidth + "s | %s";
        StringBuilder table = new StringBuilder("Here are your applications:\n");
        table.append(String.format(rowFormat, "No.", "Status", "Company", "Role"));
        table.append('\n').append("-".repeat(numberWidth))
                .append("-+-").append("-".repeat(STATUS_WIDTH))
                .append("-+-").append("-".repeat(companyWidth))
                .append("-+-").append("-".repeat(roleWidth));

        for (int i = 0; i < applications.size(); i++) {
            Application application = applications.get(i);
            table.append('\n').append(String.format(rowFormat, Integer.toString(i + 1),
                    application.getStatusIcon(), application.getCompany(), application.getRole()));
        }
        ui.showMessage(table.toString());
    }
}
