package jobseek;

import jobseek.command.AddCommand;
import jobseek.exception.JobSeekException;
import jobseek.model.Application;
import jobseek.model.ApplicationList;
import jobseek.parser.CommandParser;
import jobseek.storage.Storage;
import jobseek.ui.Ui;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests that "blank" means the same thing in the model, commands, parser, prompts and storage, including
 * Unicode spaces such as U+2003 (EM SPACE) that String.trim() does not remove.
 */
public class BlankValidationTest {
    private static final String EM_SPACE = " ";

    private final PrintStream standardOut = System.out;

    @BeforeEach
    public void silenceOutput() {
        System.setOut(new PrintStream(new ByteArrayOutputStream()));
    }

    @AfterEach
    public void restoreOutput() {
        System.setOut(standardOut);
    }

    @Test
    public void application_blankOrNullCompanyOrRole_isRejectedEvenWithoutAssertions() {
        assertThrows(IllegalArgumentException.class, () -> new Application(EM_SPACE, "Intern"));
        assertThrows(IllegalArgumentException.class, () -> new Application("Acme", "  \t "));
        assertThrows(IllegalArgumentException.class, () -> new Application(null, "Intern"));
        assertThrows(IllegalArgumentException.class, () -> new Application("Acme", null));
    }

    @Test
    public void application_settersRejectBlankValues() {
        Application application = new Application("Acme", "Intern");

        assertThrows(IllegalArgumentException.class, () -> application.setCompany(EM_SPACE));
        assertThrows(IllegalArgumentException.class, () -> application.setRole(""));
        assertEquals("Acme", application.getCompany());
        assertEquals("Intern", application.getRole());
    }

    @Test
    public void application_stripsSurroundingUnicodeSpaces() {
        Application application = new Application(EM_SPACE + "Acme" + EM_SPACE, " Intern ");
        application.setCompany("Beta" + EM_SPACE);

        assertEquals("Beta", application.getCompany());
        assertEquals("Intern", application.getRole());
    }

    @Test
    public void addCommand_emSpaceCompany_isRejectedAndNotAdded() throws Exception {
        ApplicationList applications = new ApplicationList();
        Storage storage = TestStorage.create();

        new AddCommand(EM_SPACE, "Intern").execute(applications, new Ui(new Scanner("")), storage);
        new AddCommand("Acme", EM_SPACE).execute(applications, new Ui(new Scanner("")), storage);

        assertTrue(applications.isEmpty());
    }

    @Test
    public void parser_emSpaceOnlyValues_areTreatedAsEmpty() {
        JobSeekException add = assertThrows(JobSeekException.class,
                () -> CommandParser.parse("add c/" + EM_SPACE + " r/Intern"));
        JobSeekException edit = assertThrows(JobSeekException.class,
                () -> CommandParser.parse("edit 1 r/" + EM_SPACE));

        assertTrue(add.getMessage().contains("'c/' cannot be empty"));
        assertTrue(edit.getMessage().contains("'r/' cannot be empty"));
    }

    @Test
    public void menuPrompt_emSpaceOnlyAnswer_isRejectedAndReprompted() {
        ApplicationList applications = new ApplicationList();
        String input = "1\n" + EM_SPACE + "\nGoogle\n" + EM_SPACE + "\nSWE\n\n\n\n\n0\n";

        new Main(new Ui(new Scanner(input)), TestStorage.create(), applications).run();

        assertEquals(1, applications.size());
        assertEquals("Google", applications.get(0).getCompany());
        assertEquals("SWE", applications.get(0).getRole());
    }

    @Test
    public void typedAdd_withUnicodeSpacePadding_savesAndReloadsTheSameApplication() throws Exception {
        ApplicationList applications = new ApplicationList();
        Storage storage = TestStorage.create();
        new AddCommand("Acme" + EM_SPACE, EM_SPACE + "Intern").execute(applications, new Ui(new Scanner("")),
                storage);

        ApplicationList reloaded = new Storage(storage.getFilePath()).load();

        assertEquals(1, reloaded.size());
        assertEquals("Acme", reloaded.get(0).getCompany());
        assertEquals("Intern", reloaded.get(0).getRole());
    }

    @Test
    public void everyAcceptedApplication_survivesASaveAndReload() throws Exception {
        // The original bug: an application that could be added was skipped as "blank" after restart.
        String[] candidates = {EM_SPACE + "A", "A" + EM_SPACE, " A", "A　", "​A"};
        for (String name : candidates) {
            ApplicationList applications = new ApplicationList();
            Storage storage = TestStorage.create();
            try {
                applications.add(new Application(name, "Role"));
            } catch (IllegalArgumentException rejected) {
                continue;
            }
            storage.save(applications);

            ApplicationList reloaded = new Storage(storage.getFilePath()).load();

            assertEquals(1, reloaded.size(), "accepted name was lost on reload: " + name);
        }
    }
}
