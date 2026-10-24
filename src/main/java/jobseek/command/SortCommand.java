package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Displays applications alphabetically by company, then role, without changing saved order.
 */
public class SortCommand extends Command {
    /**
     * Sorts only a list of source indices. The original application list is left untouched.
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

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < applications.size(); i++) {
            indices.add(i);
        }
        Comparator<Integer> byCompanyAndRole = Comparator
                .comparing((Integer index) -> applications.get(index).getCompany(), String.CASE_INSENSITIVE_ORDER)
                .thenComparing(index -> applications.get(index).getRole(), String.CASE_INSENSITIVE_ORDER)
                .thenComparingInt(index -> index);
        indices.sort(byCompanyAndRole);
        ui.showMessage(ApplicationTable.format(applications, indices,
                "Applications sorted by company, then role:"));
    }
}
