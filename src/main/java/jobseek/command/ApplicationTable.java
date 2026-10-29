package jobseek.command;

import jobseek.model.Application;
import jobseek.model.ApplicationList;

import java.util.List;

/**
 * Formats a view of applications while retaining their original list numbers.
 * Search and sorted views use these numbers so other commands still address the same records.
 */
final class ApplicationTable {
    private static final int COMPLETED_WIDTH = "Completed".length();

    private ApplicationTable() {
    }

    /**
     * Returns a numbered table for the given zero-based indices in display order.
     *
     * @param applications Source list, which is never modified.
     * @param indices Zero-based source positions to display.
     * @param heading Text shown above the table.
     * @return Formatted table.
     */
    static String format(ApplicationList applications, List<Integer> indices, String heading) {
        int numberWidth = "No.".length();
        int companyWidth = "Company".length();
        int roleWidth = "Role".length();
        for (int index : indices) {
            Application application = applications.get(index);
            numberWidth = Math.max(numberWidth, Integer.toString(index + 1).length());
            companyWidth = Math.max(companyWidth, application.getCompany().length());
            roleWidth = Math.max(roleWidth, application.getRole().length());
        }

        String rowFormat = "%-" + numberWidth + "s | %-" + COMPLETED_WIDTH + "s | %-"
                + companyWidth + "s | %s";
        StringBuilder table = new StringBuilder(heading).append('\n');
        table.append(String.format(rowFormat, "No.", "Completed", "Company", "Role"));
        table.append('\n').append("-".repeat(numberWidth))
                .append("-+-").append("-".repeat(COMPLETED_WIDTH))
                .append("-+-").append("-".repeat(companyWidth))
                .append("-+-").append("-".repeat(roleWidth));

        for (int index : indices) {
            Application application = applications.get(index);
            table.append('\n').append(String.format(rowFormat, Integer.toString(index + 1),
                    application.getCompletionIcon(), application.getCompany(), application.getRole()));
        }
        return table.toString();
    }
}
