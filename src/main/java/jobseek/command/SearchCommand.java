package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Shows applications whose company or role contains a search term.
 */
public class SearchCommand extends Command {
    private final String keyword;

    /**
     * Creates a search command for a non-blank term.
     *
     * @param keyword Search term entered by the user.
     */
    public SearchCommand(String keyword) {
        this.keyword = keyword.trim();
    }

    /**
     * Displays case-insensitive partial matches without changing or saving the list.
     *
     * @param applications In-memory applications.
     * @param ui User interface for displaying results.
     * @param storage Unused storage required by the command interface.
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) {
        if (applications.isEmpty()) {
            ui.showMessage("No applications found.");
            return;
        }

        String normalizedKeyword = keyword.toLowerCase(Locale.ROOT);
        List<Integer> matches = new ArrayList<>();
        for (int i = 0; i < applications.size(); i++) {
            Application application = applications.get(i);
            if (application.getCompany().toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                    || application.getRole().toLowerCase(Locale.ROOT).contains(normalizedKeyword)) {
                matches.add(i);
            }
        }
        if (matches.isEmpty()) {
            ui.showMessage("No applications found matching '" + keyword + "'.");
            return;
        }
        ui.showMessage(ApplicationTable.format(applications, matches,
                "Applications matching '" + keyword + "':"));
    }
}
