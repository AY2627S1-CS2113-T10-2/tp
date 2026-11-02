package jobseek.command;

import jobseek.model.ApplicationList;
import jobseek.ui.Ui;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that the help text lists every typed command and lines up the descriptions of each list.
 */
public class HelpAlignmentTest {

    private static String helpText() {
        StringBuilder captured = new StringBuilder();
        Ui ui = new Ui() {
            @Override
            public void showMessage(String message) {
                captured.append(message);
            }
        };
        new HelpCommand().execute(new ApplicationList(), ui, null);
        return captured.toString();
    }

    @Test
    public void help_listsEveryTypedCommand() {
        String help = helpText();

        for (String command : new String[] {"list ", "view ", "add ", "edit ", "delete|mark|unmark ", "find ",
            "search ", "sort", "today", "undo", "help", "exit"}) {
            assertTrue(help.contains("  " + command), "help should mention: " + command);
        }
    }

    @Test
    public void help_descriptionsAreAlignedWithinEachSection() {
        String[] lines = helpText().split("\\R");
        int typedColumn = -1;
        int menuColumn = -1;
        boolean inMenu = false;
        for (String line : lines) {
            if (line.startsWith("Numbered menu options")) {
                inMenu = true;
            }
            if (!line.startsWith("  ")) {
                continue;
            }
            int column = line.indexOf(" - ");
            assertTrue(column > 0, "entry without ' - ': " + line);
            if (inMenu) {
                menuColumn = menuColumn == -1 ? column : menuColumn;
                assertEquals(menuColumn, column, "misaligned menu line: " + line);
            } else {
                typedColumn = typedColumn == -1 ? column : typedColumn;
                assertEquals(typedColumn, column, "misaligned typed line: " + line);
            }
        }
    }
}
