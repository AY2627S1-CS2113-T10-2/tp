package jobseek.command;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Shows the applications that satisfy every filter the user gave, e.g. {@code find c/google s/interview}.
 * The command only holds one combined {@link Predicate}, so it does not need to know which filters were used.
 * It only displays results, so nothing is changed or saved.
 */
public class FindCommand extends Command {
    private final Predicate<Application> filter;
    private final String description;

    /**
     * Constructs a FindCommand.
     *
     * @param filter Combined condition an application must satisfy to be shown.
     * @param description Human-readable summary of the conditions, shown above the results.
     */
    public FindCommand(Predicate<Application> filter, String description) {
        assert filter != null : "Filter should not be null";
        this.filter = filter;
        this.description = description != null ? description : "";
    }

    /**
     * Displays matching applications with their original list numbers, so they can still be used
     * with commands such as {@code edit} and {@code delete}.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for displaying results.
     * @param storage Storage instance required by the command interface but not used here.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) {
        if (applications.isEmpty()) {
            ui.showMessage("No applications found.");
            return;
        }

        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < applications.size(); i++) {
            if (filter.test(applications.get(i))) {
                matches.add(i);
            }
        }

        if (matches.isEmpty()) {
            ui.showMessage("No applications found matching " + description + ".");
            return;
        }
        ui.showMessage(ApplicationTable.format(applications, matches,
                matches.size() + " application(s) matching " + description + ":"));
    }
}
