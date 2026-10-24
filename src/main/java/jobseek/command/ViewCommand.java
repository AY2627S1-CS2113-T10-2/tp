package jobseek.command;

import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.storage.Storage;
import jobseek.ui.Ui;

/**
 * Handles displaying detailed information about a single job application in a card format.
 */
public class ViewCommand extends Command {
    private static final String DIVIDER = "==================================================";
    private static final String SUB_DIVIDER = "--------------------------------------------------";
    private final int targetIndex;

    /**
     * Constructs a ViewCommand with the target 0-based index.
     *
     * @param targetIndex 0-based index of the application in the application list.
     */
    public ViewCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    public int getTargetIndex() {
        return targetIndex;
    }

    /**
     * Parses the arguments string from a 'view INDEX' command into a ViewCommand instance.
     *
     * @param args The user argument string following the command word.
     * @return A ViewCommand targeting the specified 0-based application index.
     * @throws JobSeekException If the index argument is missing, non-numeric, or less than or equal to 0.
     */
    public static ViewCommand parse(String args) throws JobSeekException {
        if (args == null || args.trim().isEmpty()) {
            throw new JobSeekException("Please provide a valid positive application index. Usage: view INDEX");
        }

        try {
            int oneBasedIndex = Integer.parseInt(args.trim());
            if (oneBasedIndex <= 0) {
                throw new JobSeekException("Please provide a valid positive application index. "
                        + "Usage: view INDEX");
            }
            return new ViewCommand(oneBasedIndex - 1);
        } catch (NumberFormatException e) {
            throw new JobSeekException("Please provide a valid positive application index. Usage: view INDEX");
        }
    }

    /**
     * Executes the view command by retrieving the application and displaying its detailed card.
     *
     * @param applications In-memory list of applications.
     * @param ui User interface for displaying the card.
     * @param storage Storage instance (not modified by view).
     * @throws JobSeekException If the target index is out of bounds (greater than list size).
     */
    @Override
    public void execute(ApplicationList applications, Ui ui, Storage storage) throws JobSeekException {
        if (targetIndex < 0 || targetIndex >= applications.size()) {
            throw new JobSeekException("Application index " + (targetIndex + 1)
                    + " does not exist. (Total applications: " + applications.size() + ")");
        }

        Application app = applications.get(targetIndex);
        String completedText = app.isCompleted() ? "Yes" : "No";

        String card = DIVIDER + "\n"
                + "Application Details [Index: " + (targetIndex + 1) + "]\n"
                + SUB_DIVIDER + "\n"
                + "Company:   " + app.getCompany() + "\n"
                + "Role:      " + app.getRole() + "\n"
                + "Status:    " + app.getStatus() + "\n"
                + "Completed: " + completedText + "\n"
                + DIVIDER;

        ui.showMessage(card);
    }
}
