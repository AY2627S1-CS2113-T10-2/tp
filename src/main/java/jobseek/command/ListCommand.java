package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

import java.util.stream.IntStream;

/**
 * Displays all saved applications in a numbered table.
 * The displayed numbers are one-based so they match the numbers users enter for other commands.
 */
public class ListCommand extends Command {
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

        ui.showMessage(ApplicationTable.format(applications,
                IntStream.range(0, applications.size()).boxed().toList(), "Here are your applications:"));
    }
}
